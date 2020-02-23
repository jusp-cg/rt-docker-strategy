package com.capgroup.dcip.webapi.controllers;

import com.capgroup.dcip.app.common.PropertyModel;
import com.capgroup.dcip.app.identity.ProfileModel;
import com.capgroup.dcip.app.identity.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST API for creating/updating/deleting profiles
 */
@RestController
@RequestMapping("api/dcip/profiles")
public class ProfileController {

    private ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService service) {
        this.profileService = service;
    }

    @GetMapping
    public Iterable<ProfileModel> get(@RequestParam(value = "userInitials", required = false) String userInitials,
                                      @RequestParam(value = "role", required = false) String role,
                                      @RequestParam(value = "investmentUnitId", required = false) Long investmentUnitId) {
        return profileService.findAll(userInitials, role, investmentUnitId);
    }

    @GetMapping("/{id}")
    public ProfileModel get(@PathVariable("id") long id) {
        return profileService.findById(id);
    }

    @GetMapping("/{profileId}/properties")
    public Iterable<PropertyModel> getProperties(@PathVariable("profileId") long profileId,
                                                 @RequestParam(value = "path", required = false) String path) {
        return path == null ? profileService.findPropertiesFor(profileId) :
                profileService.findPropertiesFor(profileId, path);
    }

    @GetMapping("/{profileId}/properties/{propertyName}")
    public PropertyModel getProperty(@PathVariable("profileId") long profileId,
                                           @PathVariable("propertyName") String propertyName) {
        return profileService.getProperty(profileId, propertyName);
    }
}
