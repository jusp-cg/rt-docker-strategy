package com.capgroup.dcip.sand.company;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Company implements Comparable<Company> {
	private long id;
	private String name;
	private String shortName;
	private int companyType;
	private List<Security> securities;

	public Optional<Security> primarySecurity(){
		return securities.stream().filter(x->x.isPrimaryFlag()).findAny();
	}

	@Override
	public int compareTo(Company o) {
		int result = Integer.compare(companyType, o.companyType);
		return result == 0 ? name.compareTo(o.name) : result;
	}
}
