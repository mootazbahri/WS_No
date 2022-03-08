package com.almerys.generali.wsnoemisation.security;

import org.keycloak.representations.adapters.config.AdapterConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties
public class KeycloaksConfiguration {

  private List<AdapterConfig> keycloaks = new ArrayList<>();

  public List<AdapterConfig> getKeycloaks() {
    return keycloaks;
  }

}
