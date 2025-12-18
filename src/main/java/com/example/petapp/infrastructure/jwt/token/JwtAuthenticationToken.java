package com.example.petapp.infrastructure.jwt.token;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    @Getter
    private String token;
    private Object credentials;
    private Object principal;
    @Getter
    private Long profileId;

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public JwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Object principal, Object credentials, Object profileId) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        if (profileId == null) {
            this.profileId = null;
        } else {
            this.profileId = Long.valueOf(profileId.toString());
        }
        setAuthenticated(true);
    }

    public JwtAuthenticationToken(String token) {
        super(null);
        this.token = token;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
