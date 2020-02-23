package com.capgroup.dcip.app.journal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.capgroup.dcip.app.common.LinkMapper;
import com.capgroup.dcip.app.identity.UserService;
import com.capgroup.dcip.infrastructure.repository.UserRepository;
import com.capgroup.dcip.infrastructure.repository.journal.InvestmentReasonRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.capgroup.dcip.app.MappersTestConfig;
import com.capgroup.dcip.app.identity.ProfileMapper;
import com.capgroup.dcip.app.identity.ProfileModel;
import com.capgroup.dcip.app.reference.capital_system.AccountService;
import com.capgroup.dcip.app.reference.company.CompanyModel;
import com.capgroup.dcip.app.reference.company.CompanyService;
import com.capgroup.dcip.domain.identity.Profile;
import com.capgroup.dcip.domain.identity.Role;
import com.capgroup.dcip.domain.identity.User;
import com.capgroup.dcip.domain.journal.InvestmentAction;
import com.capgroup.dcip.domain.journal.InvestmentActionType;
import com.capgroup.dcip.domain.journal.InvestmentType;
import com.capgroup.dcip.infrastructure.repository.journal.InvestmentActionTypeRepository;
import com.capgroup.dcip.infrastructure.repository.journal.InvestmentTypeRepository;
import com.capgroup.dcip.infrastructure.repository.ProfileRepository;

@ContextConfiguration(classes = MappersTestConfig.class, loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@Import(InvestmentActionMapperImpl.class)
public class InvestmentActionMapperTest {

	@MockBean
	private LinkMapper linkMapper;

	@Autowired
	private InvestmentActionMapper investmentActionMapper;

	@MockBean
	private ProfileMapper profileMapper;

	@MockBean
	private AccountService accountService;

	@MockBean
	private CompanyService companyService;

	@MockBean
	private InvestmentTypeRepository investmentTypeRepository;

	@MockBean
	private InvestmentTypeMapper investmentTypeMapper;
	
	@MockBean
	private InvestmentActionTypeRepository investmentActionTypeRepository;
	
	@MockBean
	private ProfileRepository profileRepository;
	
	@MockBean
	private InvestmentActionTypeMapper investmentActionTypeMapper;

	@MockBean
	private InvestmentReasonRepository investmentReasonRepository;

	@MockBean
	private UserRepository userRepository;
	
	private List<InvestmentType> investmentTypeList;

	private InvestmentActionType investmentActionType;

	private Profile profile;

	@Before
	public void init() {
		InvestmentType investmentType1 = new InvestmentType(34, "it name", "it description", true);
		InvestmentType investmentType2 = new InvestmentType(35, "it name", "it description", true);

		investmentTypeList = Arrays.asList(investmentType1, investmentType2);
		investmentActionType = new InvestmentActionType("InvestmentActionTypeN", 1, true);

		User user = new User(1L, "TEST_USER", "TEST_USER", null);
		Role role = new Role(1L, "TEST_ROLE", "TEST_ROLE");
		profile = new Profile(user, role);
	}

	@Test
	public void toInvestmentActionModelTest() {
		InvestmentAction investmentAction = new InvestmentAction(LocalDateTime.now(),
				"PublicComment", "OrderInfo", "ReasonForBuying", investmentActionType, profile, 1L, false, (short) 5);

		investmentTypeList.forEach(investmentAction::addInvestmentType);

		given(accountService.findByIds(any(Stream.class))).willReturn(Stream.empty());
		given(companyService.findById(1l)).willReturn(Optional.of(new CompanyModel()));
		given(profileMapper.toProfileModel(profile))
				.willReturn(new ProfileModel(profile.getUser().getInitials(), profile.getRole().getName(), profile.getUser().getName(), null, 0, 0));

		InvestmentActionModel model = investmentActionMapper.map(investmentAction);

		assertEquals(investmentAction.getActionDate(), model.getActionDate());
		assertEquals(investmentAction.getPublicComment(), model.getPublicComment());
		assertEquals(investmentAction.getOrderInfo(), model.getOrderInfo());
		assertEquals(investmentAction.getReasonForBuying(), model.getReasonForBuying());
		assertEquals(investmentAction.getConvictionLevel(), (Short)model.getConvictionLevel().shortValue());
		assertEquals(investmentAction.isSendMail(), model.isSendMail());

		Collection<InvestmentType> investmentTypes = investmentAction.getInvestmentTypes();
		List<InvestmentTypeModel> investmentTypeModels = model.getInvestmentTypes();

		assertEquals(investmentTypes.size(), investmentTypeModels.size());

		assertEquals(investmentAction.getAnalystProfile().getUser().getInitials(), model.getProfile().getInitials());
		assertEquals(investmentAction.getAnalystProfile().getRole().getName(), model.getProfile().getRole());
// TODO
//		boolean equals = investmentTypes.stream().allMatch(i -> investmentTypeModels.stream()
//				.mapEntity(InvestmentTypeModel::getDescription).anyMatch(j -> j.equals(i.getDescription())));

//		assertTrue(equals);

//		equals = investmentTypes.stream().allMatch(i -> investmentTypeModels.stream().mapEntity(InvestmentTypeModel::getName)
//				.anyMatch(j -> j.equals(i.getName())));

//		assertTrue(equals);

//		equals = investmentTypes.stream().allMatch(i -> investmentTypeModels.stream().mapEntity(InvestmentTypeModel::isPublic)
//				.anyMatch(j -> j.equals(i.isPublic())));

//		assertTrue(equals);
	}
}
