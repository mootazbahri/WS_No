package com.almerys.generali.wsnoemisation.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties
public class PolicyConfiguration {
    private final Map<String, List<String>> policy = new HashMap<>();

    public Map<String, List<String>> getPolicy() {
        return policy;
    }
}
