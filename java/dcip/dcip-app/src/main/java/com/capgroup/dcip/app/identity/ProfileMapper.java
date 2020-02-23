package com.capgroup.dcip.app.identity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.capgroup.dcip.app.entity.TemporalEntityMapper;
import com.capgroup.dcip.domain.identity.Profile;

@Mapper(config = TemporalEntityMapper.class)
public interface ProfileMapper {
	@Mappings({ @Mapping(target = "initials", source = "user.initials"),
			@Mapping(target="userName", source="user.name"),
			@Mapping(target = "role", source = "role.name"),
			@Mapping(target = "investmentUnit", source = "user.investmentUnit.name"),
			@Mapping(target = "investmentUnitId", source = "user.investmentUnit.id"),
			@Mapping(target = "userId", source="user.id")
	})
	ProfileModel toProfileModel(Profile profile);

	Iterable<ProfileModel> toProfileModels(Iterable<Profile> profiles);
}
