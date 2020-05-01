package com.bouldersmart.RetrifitInterface;

import com.bouldersmart.model.AddApproachResponseModel;
import com.bouldersmart.model.AddCommentResponseModel;
import com.bouldersmart.model.AddLocationResponseModel;
import com.bouldersmart.model.AddRouteResponseModel;
import com.bouldersmart.model.CommentResponseModel;
import com.bouldersmart.model.CommonResponseModel;
import com.bouldersmart.model.ListApproachResponseModel;
import com.bouldersmart.model.LocationResponseModel;
import com.bouldersmart.model.LoginResponseModel;
import com.bouldersmart.model.PurchaseVoucherResponseModel;
import com.bouldersmart.model.RouteResponseModel;
import com.bouldersmart.model.VoucherResponseModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface GetDataService {

    @FormUrlEncoded
    @POST("login")
    Call<LoginResponseModel> login(@Field("email") String email,
                                   @Field("password") String password,
                                   @Field("device_type") String device_type,
                                   @Field("device_token") String device_token);

    @FormUrlEncoded
    @POST("signUp")
    Call<LoginResponseModel> signUp(@Field("fname") String fname,
                                    @Field("lname") String lname,
                                    @Field("email") String email,
                                    @Field("password") String password,
                                    @Field("is_manual_email") String is_manual_email,
                                    @Field("is_fb") String is_fb,
                                    @Field("fb_id") String fb_id,
                                    @Field("device_token") String device_token,
                                    @Field("device_type") String device_type);

    @FormUrlEncoded
    @POST("resendConfirm")
    Call<CommonResponseModel> resendConfirm(@Field("email") String email,
                                            @Field("fb_id") String fb_id);

    @FormUrlEncoded
    @POST("logOut")
    Call<CommonResponseModel> logOut(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("isRegister")
    Call<LoginResponseModel> isRegister(@Field("is_fb") String is_fb,
                                        @Field("fb_id") String fb_id,
                                        @Field("email") String email,
                                        @Field("device_type") String device_type,
                                        @Field("device_token") String device_token);

    @FormUrlEncoded
    @POST("forgotPassword")
    Call<CommonResponseModel> forgotPassword(@Field("email") String email);

    @FormUrlEncoded
    @POST("getLocations")
    Call<LocationResponseModel> getLocations(@Field("user_id") String user_id);

    @Multipart
    @POST("addLocation")
    Call<AddLocationResponseModel> addLocation(@Part MultipartBody.Part cover_image,
                                               @Part("user_id") RequestBody user_id,
                                               @Part("location_name") RequestBody location_name,
                                               @Part("adress") RequestBody adress,
                                               @Part("lat") RequestBody lat,
                                               @Part("lng") RequestBody lng);

    @FormUrlEncoded
    @POST("deleteLocation")
    Call<CommonResponseModel> deleteLocation(@Field("user_id") String user_id,
                                             @Field("location_id") String location_id);

    @FormUrlEncoded
    @POST("getRoutes")
    Call<RouteResponseModel> getRoutes(@Field("user_id") String user_id,
                                       @Field("location_id") String location_id);

    @Multipart
    @POST("addRoute")
    Call<AddRouteResponseModel> addRoute(@Part MultipartBody.Part route_image,
                                         @Part("user_id") RequestBody user_id,
                                         @Part("location_id") RequestBody location_id,
                                         @Part("route_name") RequestBody route_name,
                                         @Part("grade_opinion") RequestBody grade_opinion,
                                         @Part("ratting") RequestBody ratting);

    @FormUrlEncoded
    @POST("deleteRoute")
    Call<CommonResponseModel> deleteRoute(@Field("user_id") String user_id,
                                          @Field("route_id") String route_id);

    @FormUrlEncoded
    @POST("giveRouteRatting")
    Call<AddCommentResponseModel> giveRouteRatting(@Field("user_id") String user_id,
                                                   @Field("route_id") String route_id,
                                                   @Field("ratting") String ratting,
                                                   @Field("comments") String comments);

    @FormUrlEncoded
    @POST("getComments")
    Call<CommentResponseModel> getComments(@Field("route_id") String route_id,
                                           @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("getapproach")
    Call<ListApproachResponseModel> getapproach(@Field("location_id") String location_id,
                                                @Field("user_id") String user_id);


    @Multipart
    @POST("addApproach")
    Call<AddApproachResponseModel> addApproach(@Part MultipartBody.Part approach_image,
                                               @Part("user_id") RequestBody user_id,
                                               @Part("location_id") RequestBody location_id,
                                               @Part("date") RequestBody date,
                                               @Part("session_time") RequestBody session_time);

    @FormUrlEncoded
    @POST("deleteApproach")
    Call<CommonResponseModel> deleteApproach(@Field("user_id") String user_id,
                                          @Field("approach_id") String approach_id);

    @FormUrlEncoded
    @POST("getVouchers")
    Call<VoucherResponseModel> getVouchers(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("purchaseVouchers")
    Call<PurchaseVoucherResponseModel> purchaseVouchers(@Field("user_id") String user_id,
                                                        @Field("voucher_id") String voucher_id,
                                                        @Field("points") String points);

    @FormUrlEncoded
    @POST("giveRouteRattingOnly")
    Call<CommonResponseModel> giveRouteRattingOnly(@Field("user_id") String user_id,
                                                   @Field("route_id") String route_id,
                                                   @Field("ratting") String ratting);
}