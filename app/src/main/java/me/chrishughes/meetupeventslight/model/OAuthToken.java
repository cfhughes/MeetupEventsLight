package me.chrishughes.meetupeventslight.model;

import com.squareup.moshi.Json;

public class OAuthToken {

    @Json(name = "access_token")
    private String accessToken;
    @Json(name = "token_type")
    private String tokenType;
    @Json(name = "expires_in")
    private long expiresIn;
    private long expiredAfterMilli = 0;
    @Json(name = "refresh_token")
    private String refreshToken;

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

  public long getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(long expiresIn) {
    this.expiresIn = expiresIn;
  }

  public long getExpiredAfterMilli() {
    return expiredAfterMilli;
  }

  public void setExpiredAfterMilli(long expiredAfterMilli) {
    this.expiredAfterMilli = expiredAfterMilli;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
