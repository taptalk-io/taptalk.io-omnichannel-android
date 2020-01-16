package io.taptalk.taptalklive.API.Service;

import io.taptalk.taptalklive.API.Model.RequestModel.TTLCreateUserRequest;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCreateUserResponse;
import retrofit2.http.Body;
import retrofit2.http.Header;
import rx.Observable;

import io.taptalk.taptalklive.API.Model.ResponseModel.TTLBaseResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLRequestAccessTokenResponse;
import retrofit2.http.POST;

public interface TTLApiService {
    @POST("auth/access_token/request")
    Observable<TTLBaseResponse<TTLRequestAccessTokenResponse>> requestAccessToken(@Header("Authorization") String authTicket);

    @POST("client/user/create")
    Observable<TTLBaseResponse<TTLCreateUserResponse>> createUser(@Body TTLCreateUserRequest request);
}
