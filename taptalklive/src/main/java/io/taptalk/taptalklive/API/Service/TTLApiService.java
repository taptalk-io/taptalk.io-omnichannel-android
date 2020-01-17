package io.taptalk.taptalklive.API.Service;

import io.taptalk.taptalklive.API.Model.RequestModel.TTLCreateCaseRequest;
import io.taptalk.taptalklive.API.Model.RequestModel.TTLCreateUserRequest;
import io.taptalk.taptalklive.API.Model.RequestModel.TTLRateConversationRequest;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCommonResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCreateCaseResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCreateUserResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetTopicListResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetUserProfileResponse;
import retrofit2.http.Body;
import retrofit2.http.Header;
import rx.Observable;

import io.taptalk.taptalklive.API.Model.ResponseModel.TTLBaseResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLRequestAccessTokenResponse;
import retrofit2.http.POST;

public interface TTLApiService {
    @POST("auth/access_token/request")
    Observable<TTLBaseResponse<TTLRequestAccessTokenResponse>> requestAccessToken(@Header("Authorization") String authTicket);

    @POST("client/topic/get_list")
    Observable<TTLBaseResponse<TTLGetTopicListResponse>> getTopicList();

    @POST("client/user/create")
    Observable<TTLBaseResponse<TTLCreateUserResponse>> createUser(@Body TTLCreateUserRequest request);

    @POST("client/user/get")
    Observable<TTLBaseResponse<TTLGetUserProfileResponse>> getUserProfile();

    @POST("client/case/create")
    Observable<TTLBaseResponse<TTLCreateCaseResponse>> createCase(@Body TTLCreateCaseRequest request);

    @POST("client/case/rate")
    Observable<TTLBaseResponse<TTLCommonResponse>> rateConversation(@Body TTLRateConversationRequest request);

    @POST("logout")
    Observable<TTLBaseResponse<TTLCommonResponse>> logout();
}
