package io.taptalk.taptalklive.API.Service;

import io.taptalk.taptalklive.API.Model.ResponseModel.TTLBaseResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLRequestAccessTokenResponse;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface TTLRefreshTokenApiService {
    @POST("auth/access_token/refresh")
    Observable<TTLBaseResponse<TTLRequestAccessTokenResponse>> refreshAccessToken(@Header("Authorization") String refreshToken);
}
