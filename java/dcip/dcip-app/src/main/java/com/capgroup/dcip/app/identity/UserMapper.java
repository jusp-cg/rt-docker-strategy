package com.capgroup.dcip.app.identity;

import com.capgroup.dcip.app.common.LinkModel;
import com.capgroup.dcip.app.common.ToLongLink;
import com.capgroup.dcip.app.common.UtilityLinkMapper;
import com.capgroup.dcip.app.entity.TemporalEntityMapper;
import com.capgroup.dcip.domain.identity.User;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Mapping between a User and its corresponding DTO (UserModel)
 */
@Mapper(config = TemporalEntityMapper.class)
public abstract class UserMapper {
    @Autowired
    private ProfileService profileService;

    public UserModel map(User model) {
        return map(model, true);
    }

    @Mapping(target = "investmentUnit", qualifiedBy = {UtilityLinkMapper.class, ToLongLink.class})
    @Mapping(target = "initials", source = "initials")
    public abstract UserModel map(User user, @Context Boolean fetchProfiles);

    /**
     * Optimized to retrieve all the profiles for all the users
     */
    public List<UserModel> mapAll(Iterable<User> users) {
        // get all the profiles for all the users
        Map<Long, List<LinkModel<Long>>> profiles =
                profileService.findAllProfileLinksByUserIds(StreamSupport.stream(users.spliterator(), false).map(User::getId).collect(Collectors.toList()));

        return StreamSupport.stream(users.spliterator(), false).map(x -> map(x, false)).map(userModel -> {
            userModel.setProfiles(profiles.getOrDefault(userModel.getId(), Collections.emptyList()));
            return userModel;
        }).collect(Collectors.toList());
    }

    @AfterMapping
    protected void afterMapping(User user, @MappingTarget UserModel model, @Context Boolean fetchProfiles) {
        if (fetchProfiles == null || fetchProfiles) {
            List<LinkModel<Long>> profiles = new ArrayList<>();
            profileService.findAllProfileLinksByUserId(user.getId()).forEach(profiles::add);
            model.setProfiles(profiles);
        }
    }
}
