package me.chrishughes.meetupeventslight.service;

import me.chrishughes.meetupeventslight.model.OAuthToken;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface OAuthService {

    @FormUrlEncoded
    @POST("oauth2/access")
    Call<OAuthToken> requestTokenForm(
            @Field("code")String code,
            @Field("client_id")String client_id,
            @Field("client_secret")String client_secret,
            @Field("redirect_uri")String redirect_uri,
            @Field("grant_type")String grant_type);

}
