package com.capgroup.dcip.sand.company;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TradingItem {
	private long id;
	private boolean primaryFlag;
	private String symbolTicker;
	private List<Symbol> symbols;
	
	public TradingItem(long id, boolean primaryFlag, String symbolTicker) {
		this.id= id;
		this.primaryFlag = primaryFlag;
		this.symbolTicker = symbolTicker;
	}
}
