package com.norpactech.fw.factory.config.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

  private static final String COGNITO_GROUPS_CLAIM = "cognito:groups";

  @Override
  public Collection<GrantedAuthority> convert(Jwt jwt) {

    List<String> groups = jwt.getClaimAsStringList(COGNITO_GROUPS_CLAIM);

    if (groups == null || groups.isEmpty()) {
      return Collections.emptyList();
    }

    return groups.stream()
      .map(group -> new SimpleGrantedAuthority("ROLE_" + group.toUpperCase()))
      .collect(Collectors.toList());
  }
}
