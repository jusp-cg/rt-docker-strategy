package com.capgroup.dcip.sand.company;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Symbol {
	private long Id;
	private long symbolTypeId;
	private String symbolValue;
}
