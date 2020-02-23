package com.capgroup.dcip.capitalconnect.suggestion;

import lombok.Data;

@Data
public class Scope {
	private String scope;
	// other properties not required = can be added if we need additional filters/exclusion ids
	private long rowLimit = 10;
	private boolean hasHits = false;
	
	public Scope(String scope) {
		this.scope = scope;
	}
}
