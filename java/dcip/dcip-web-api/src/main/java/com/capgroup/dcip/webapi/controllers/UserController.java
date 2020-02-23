package com.capgroup.dcip.webapi.controllers;

import com.capgroup.dcip.app.common.PropertyModel;
import com.capgroup.dcip.app.identity.UserModel;
import com.capgroup.dcip.app.identity.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/dcip/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Iterable<UserModel> get(@RequestParam(value = "userInitials", required = false) String userInitials,
                                   @RequestParam(value="applicationRoleId", required = false) Long applicationRoleId) {
        return userService.findAll(userInitials, applicationRoleId);
    }

    @GetMapping("/{userId}")
    public UserModel getUser(@PathVariable("userId") long userId) {
        return userService.findById(userId);
    }

    @GetMapping("/{userId}/on-behalf-of")
    public Iterable<UserModel> getOnBehalfOf(@PathVariable("userId") long userId){
        return userService.findOnBehalfOfForUser(userId);
    }

    @GetMapping("/{userId}/properties")
    public Iterable<PropertyModel> getProperties(@PathVariable("userId") long userId,
                                                 @RequestParam(value = "path", required = false) String path) {
        return path == null ? userService.findPropertiesFor(userId) :
                userService.findPropertiesFor(userId, path);
    }

    @GetMapping("/{userId}/properties/**")
    public PropertyModel getProperty(@PathVariable("userId") long userId,
                                     HttpServletRequest request) {

        return userService.getProperty(userId, getPropertyPath(request));
    }

    private String getPropertyPath(HttpServletRequest request) {
        String uri = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return new AntPathMatcher().extractPathWithinPattern(pattern, uri);
    }

    @PostMapping("/{userId}/properties/**")
    public PropertyModel postProperty(@PathVariable("userId") long userId,
                                      HttpServletRequest request,
                                      @RequestBody PropertyModel propertyModel) {
        return userService.createOrUpdateProperty(userId, new PropertyModel(getPropertyPath(request),
                propertyModel.getValue()));
    }

    @PutMapping("/{userId}/properties/**")
    public PropertyModel putProperty(@PathVariable("userId") long userId,
                                      HttpServletRequest request,
                                      @RequestBody PropertyModel propertyModel) {
        return userService.createOrUpdateProperty(userId, new PropertyModel(getPropertyPath(request),
                propertyModel.getValue()));
    }

}
