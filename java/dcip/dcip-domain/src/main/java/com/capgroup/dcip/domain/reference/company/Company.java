package com.capgroup.dcip.domain.reference.company;

/*
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Company extends Entity {
	public static final long TYPE_ID = 14;
	
	private static final long serialVersionUID = -6622552643154767288L;

	private List<Security> securities = new ArrayList<Security>();
	private String name;
	private String shortName;
	private boolean isPublic;
	private boolean isActive;

	public Company(long id) {
		super(id);
	}
	
	public Optional<Security> getPrimarySecurity() {
		return securities.stream().filter(x -> x.isPrimary()).findFirst();
	}
}
*/