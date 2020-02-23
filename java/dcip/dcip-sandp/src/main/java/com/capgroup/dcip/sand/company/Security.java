package com.capgroup.dcip.sand.company;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Security {
	private long id;
	private String name;
	private boolean primaryFlag;
	private List<TradingItem> tradingItems;

	public Optional<TradingItem> primaryTradingItem(){
		return tradingItems.stream().filter(x->x.isPrimaryFlag()).findAny();
	}
}
