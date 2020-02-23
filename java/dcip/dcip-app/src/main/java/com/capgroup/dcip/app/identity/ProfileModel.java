package com.capgroup.dcip.app.identity;

import com.capgroup.dcip.app.entity.TemporalEntityModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProfileModel extends TemporalEntityModel {
	private String initials;
	private String role;
	private String userName;
	private String investmentUnit;
	private long investmentUnitId;
	private long userId;
}
