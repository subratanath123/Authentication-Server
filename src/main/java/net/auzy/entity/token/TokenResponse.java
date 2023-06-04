package net.auzy.entity.token;

import net.auzy.entity.idp.IdpProvider;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

public class TokenResponse implements Serializable {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private int expiresIn;

    @JsonProperty
    private IdpProvider idpProvider;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("id_token")
    private String idToken;

    private Date issuedTime;

    //for special case usage
    // like in swagger filter
    private String email;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getIdToken() {
        return idToken;
    }

    public IdpProvider getIdpProvider() {
        return idpProvider;
    }

    public void setIdpProvider(IdpProvider idpProvider) {
        this.idpProvider = idpProvider;
    }

    public void setIssuedTime(Date issuedTime) {
        this.issuedTime = issuedTime;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }


    public Date getIssuedTime() {
        return issuedTime;
    }

    public void setIssuedTime() {
        this.issuedTime = new Date();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isTokenExpired() {
        return getIssuedTime()
                .toInstant()
                .plusSeconds(expiresIn)
                .isBefore(new Date().toInstant());
    }

}
