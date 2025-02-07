package com.norpactech.pareto.config.security;

import java.util.Collection;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

  private Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter = new AuthoritiesConverter();
  private String principalClaimName = JwtClaimNames.SUB;

  @Override
  public final AbstractAuthenticationToken convert(Jwt jwt) {
    Collection<GrantedAuthority> authorities = this.jwtGrantedAuthoritiesConverter.convert(jwt);

    String principalClaimValue = jwt.getClaimAsString(this.principalClaimName);
    return new JwtAuthenticationToken(jwt, authorities, principalClaimValue);
  }

  public void setPrincipalClaimName(String principalClaimName) {
    this.principalClaimName = principalClaimName;
  }
}
