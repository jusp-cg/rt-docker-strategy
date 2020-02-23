package com.capgroup.dcip.app.reference.capital_system;

import static com.capgroup.dcip.util.Lazy.lazy;

import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.capgroup.dcip.cmps.api.PortfolioApi;
import com.capgroup.dcip.cmps.model.Portfolio.CategoryEnum;
import com.capgroup.dcip.cmps.model.Portfolio.InvestmentPortfolioTypeCodeEnum;
import com.capgroup.dcip.cmps.model.PortfoliosResponse;

/**
 * Adapter for the Cmps Account API and the DCIP Account service
 */
@Service
public class CmpsAccountService implements AccountService {

	private static Logger LOGGER = LoggerFactory.getLogger(CmpsAccountService.class);

	@Value("${cmps.account.concurrent-threads}")
	private int concurrency = 3;

	PortfolioApi portfolioApi;
	AccountLoader accountLoader;
	CmpsAccountMapper accountMapper;
	Supplier<ForkJoinPool> threadPool = lazy(() -> new ForkJoinPool(concurrency));

	public interface AccountLoader {
		Optional<AccountModel> findById(long id);
	}

	/**
	 * This class is required because of the proxy mechanism of spring. The
	 * CmpsAccountService cannot invoke an internal method and make use of the
	 * Cacheable annotation
	 */
	@Component
	public static class AccountLoaderImpl implements AccountLoader {
		PortfolioApi portfolioApi;
		CmpsAccountMapper accountMapper;

		@Autowired
		public AccountLoaderImpl(PortfolioApi portfolioApi, CmpsAccountMapper accountMapper) {
			this.portfolioApi = portfolioApi;
			this.accountMapper = accountMapper;
		}

		@Cacheable("Account")
		public Optional<AccountModel> findById(long id) {
			PortfoliosResponse response = portfolioApi.portfolioGetPortfolio(id, null, null, null, null);
			if (response == null || response.getPortfolios() == null)
				return Optional.empty();
			return response.getPortfolios().stream().map(x -> accountMapper.toAccountModel(x)).findAny();
		}
	}

	@Autowired
	public CmpsAccountService(PortfolioApi portfolioApi, AccountLoader accountLoader, CmpsAccountMapper mapper) {
		this.portfolioApi = portfolioApi;
		this.accountLoader = accountLoader;
		this.accountMapper = mapper;
	}

	private PortfoliosResponse executeRequest(String startsWith) {
		long startTime = 0;
		if (LOGGER.isDebugEnabled()) {
			startTime = System.currentTimeMillis();
		}

		// make a request to CMPS to get the portfolios that meet the requirements
		PortfoliosResponse response = portfolioApi.portfolioGetPortfolios(null,
				InvestmentPortfolioTypeCodeEnum.AA.getValue(), CategoryEnum.INVESTMENT.getValue(), startsWith, 4,
				null, null, null, null, null, null);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Time to call CMPS with query:{} took {}ms", startsWith,
					System.currentTimeMillis() - startTime);
		}

		return response;
	}

	@Override
	public Stream<AccountModel> find(String startsWith) {

		PortfoliosResponse response = executeRequest(startsWith);

		// if NO_CONTENT then an empty result
		if (response == null || response.getPortfolios() == null) {
			return Stream.empty();
		}

		return accountMapper.toAccountModelsFromPortfolio(response.getPortfolios().stream());
	}

	@Override
	public Optional<AccountModel> findById(long id) {
		return accountLoader.findById(id);
	}

	public Stream<AccountModel> findByIds(Stream<Long> ids) {
		// use its own thread pool to prevent using the common pool and to control
		// concurrency
		try {
			return threadPool.get().submit(() -> ids.parallel().map(x -> {
				try {
					return findById(x).orElse(AccountModel.CreateUnknown(x));
				} catch (Throwable thr) {
					LOGGER.error("Retrieving account for:" + x, thr);
					return AccountModel.CreateError(x);
				}
			})).get();
		} catch (Exception exc) {
			LOGGER.error("Retrieving accounts:" + ids.toString());
			return ids.map(AccountModel::CreateError);
		}
	}
}
