package com.capgroup.dcip.app.reference.capital_system;

import lombok.Data;

@Data
public class AccountModel {
	private long id;
	private String name;
	private boolean isError = false;
	
	public static AccountModel CreateError(long id) {
		AccountModel result = new AccountModel();
		result.id = id;
		result.name = "#ERROR#";
		result.isError = true;
		return result;
	}
	
	public static AccountModel CreateUnknown(long id) {
		AccountModel result = new AccountModel();
		result.id = id;
		result.name = "#UNKNOWN#";
		result.isError = true;
		return result;
	}
}
