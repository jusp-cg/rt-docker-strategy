package com.capgroup.dcip.webapi.controllers.version;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/version")
public class VersionController {

    @Value("${application.name}")
    private String applicationName;

    @Value("${build.version}")
    private String buildVersion;

    @Value("${build.timestamp}")
    private String buildDateTime;

    @Value("${app.java.version}")
    private String appJavaVersion;

    @GetMapping
    public Map<String, String> version() {
        Map<String, String> versionMap = new HashMap<>(4);
        versionMap.put("buildVersion", buildVersion);
        versionMap.put("applicationName", applicationName);
        versionMap.put("buildDateTime", buildDateTime);
        versionMap.put("appJavaVersion", appJavaVersion);

        return versionMap;
    }
}
