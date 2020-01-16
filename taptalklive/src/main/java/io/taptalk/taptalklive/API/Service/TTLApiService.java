package io.taptalk.taptalklive.API.Service;

import rx.Observable;

import io.taptalk.taptalklive.API.Model.ResponseModel.TTLBaseResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLRequestAccessTokenResponse;
import retrofit2.http.POST;

public interface TTLApiService {
    @POST("auth/access_token/request")
    Observable<TTLBaseResponse<TTLRequestAccessTokenResponse>> requestAccessToken();
}
