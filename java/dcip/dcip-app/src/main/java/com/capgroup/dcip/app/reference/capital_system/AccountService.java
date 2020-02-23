package com.capgroup.dcip.app.reference.capital_system;

import java.util.Optional;
import java.util.stream.Stream;

public interface AccountService {
	Stream<AccountModel> find(String startsWith);
	
	Optional<AccountModel> findById(long id);
	
	Stream<AccountModel> findByIds(Stream<Long> ids);
}
