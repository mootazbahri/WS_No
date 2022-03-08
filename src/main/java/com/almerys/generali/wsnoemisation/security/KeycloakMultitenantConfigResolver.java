package com.almerys.generali.wsnoemisation.security;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

public class KeycloakMultitenantConfigResolver implements KeycloakConfigResolver {

  private Logger logger = LoggerFactory.getLogger(KeycloakMultitenantConfigResolver.class);

  private Map<String, KeycloakDeployment> keycloakDeployments;

  public KeycloakMultitenantConfigResolver(Map<String, KeycloakDeployment> keycloakDeployments) {
    this.keycloakDeployments = keycloakDeployments;
  }

  private String extractRealm(HttpFacade.Request facade) {
    List<String> authHeaders = facade.getHeaders("Authorization");
    if (authHeaders == null || authHeaders.isEmpty()) {
      return null;
    }

    String tokenString = null;
    for (String authHeader : authHeaders) {
      String[] split = authHeader.trim()
                                 .split("\\s+");
      if (split == null || split.length != 2) {
        continue;
      }
      if (!split[0].equalsIgnoreCase("Bearer")) {
        continue;
      }
      tokenString = split[1];
    }

    if (tokenString == null) {
      return null;
    }

    try {
      JWSInput jwsInput = new JWSInput(tokenString);
      AccessToken token = jwsInput.readJsonContent(AccessToken.class);
      String issuer = token.getIssuer();
      String[] splittedIssuer = issuer.split("/");
      return splittedIssuer[splittedIssuer.length - 1];
    } catch (JWSInputException e) {
      logger.info("Invalid bearer token", e);
    }
    return null;
  }

  @Override
  public KeycloakDeployment resolve(HttpFacade.Request facade) {
    String realm = extractRealm(facade);

    if (StringUtils.isEmpty(realm)) {
      realm = "anonymous";
    }

    return keycloakDeployments.get(realm);
  }
}