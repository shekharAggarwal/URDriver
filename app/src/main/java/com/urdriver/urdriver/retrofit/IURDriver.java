package com.urdriver.urdriver.retrofit;

import com.urdriver.urdriver.model.CheckDriverResponse;
import com.urdriver.urdriver.model.Driver;
import com.urdriver.urdriver.model.DriverForAdmin;
import com.urdriver.urdriver.model.DriverRequestModel;
import com.urdriver.urdriver.model.ImageDriver;
import com.urdriver.urdriver.model.Rating;
import com.urdriver.urdriver.model.RequestDataOneWay;
import com.urdriver.urdriver.model.RequestDataRoundWay;
import com.urdriver.urdriver.model.Token;
import com.urdriver.urdriver.model.Trip;
import com.urdriver.urdriver.model.UserDetails;
import com.urdriver.urdriver.model.UserForAdminModel;
import com.urdriver.urdriver.model.cabDetails;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IURDriver {

    @FormUrlEncoded
    @POST("checkdriver.php")
    Call<CheckDriverResponse> CheckDriverExists(@Field("email") String email, @Field("phone") String phone);

    @FormUrlEncoded
    @POST("registerdriver.php")
    Call<Driver> registerNewDriver(@Field("name") String username,
                                   @Field("email") String email,
                                   @Field("phone") String phone,
                                   @Field("password") String password,
                                   @Field("driverImage") String driverImage,
                                   @Field("aadharNumber") String aadharNumber,
                                   @Field("aadharImage") String aadharImage,
                                   @Field("licenseImage") String licenseImage,
                                   @Field("driverStatus") int driverStatus);

    @FormUrlEncoded
    @POST("getdriver.php")
    Call<Driver> getDriverInfo(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("checkingdriver.php")
    Call<CheckDriverResponse> checkDriver(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("uploadadhar.php")
    Call<ImageDriver> uploadAadhar(@Field("image_name") String title, @Field("image") String image);

    @FormUrlEncoded
    @POST("uploadaddhar.php")
    Call<String> uploadAddhar(@Field("phone") String phone, @Field("image") String file);

    @FormUrlEncoded
    @POST("uploadlicensedr.php")
    Call<String> uploadlicensedriver(@Field("phone") String phone, @Field("image") String file);

    @FormUrlEncoded
    @POST("uploadlicense.php")
    Call<ImageDriver> uploadLicense(@Field("image_name") String title, @Field("image") String image);

    @FormUrlEncoded
    @POST("uploadCabImage.php")
    Call<ImageDriver> UploadCabImage(@Field("image_name") String title, @Field("image") String image
            , @Field("driver") String driverPhone);

    @FormUrlEncoded
    @POST("updatedriverpwd.php")
    Call<CheckDriverResponse> updateDriverPassword(@Field("password") String password, @Field("phone") String phone);

    @FormUrlEncoded
    @POST("updatetoken.php")
    Call<String> updateToken(@Field("phone") String phone,
                             @Field("token") String token,
                             @Field("isServerToken") String isServerToken);

    @FormUrlEncoded
    @POST("gettoken.php")
    Call<Token> getToken(@Field("phone") String phone,
                         @Field("isServerToken") String isServerToken);

    @FormUrlEncoded
    @POST("insertnewcab.php")
    Call<String> addNewCab(@Field("cabBrand") String cabBrand,
                           @Field("cabModel") String cabModel,
                           @Field("cabImage") String cabImage,
                           @Field("cabSitting") String cabSitting,
                           @Field("cabType") String cabType,
                           @Field("cabCity") String cabCity,
                           @Field("CabNumber") String CabNumber,
                           @Field("cabDriver") String cabDriver);

    @FormUrlEncoded
    @POST("getrequest.php")
    Call<RequestDataOneWay> getRequestOneWay(@Field("userPhone") String userPhone,
                                             @Field("driverPhone") String driverPhone,
                                             @Field("status") int status,
                                             @Field("code") int code);

    @FormUrlEncoded
    @POST("getrequest.php")
    Call<RequestDataRoundWay> getRequestRoundWay(@Field("userPhone") String userPhone,
                                                 @Field("driverPhone") String driverPhone,
                                                 @Field("status") int status,
                                                 @Field("code") int code);

    @FormUrlEncoded
    @POST("updaterequest.php")
    Call<String> updateRequest(@Field("phoneUser") String phoneUser,
                               @Field("model") String model,
                               @Field("id") int id,
                               @Field("status") int status,
                               @Field("code") int code,
                               @Field("phoneDriver") String phoneDriver);

    @FormUrlEncoded
    @POST("getrequestdata.php")
    Observable<List<RequestDataOneWay>> getRequestDataOneWay(@Field("driverPhone") String driverPhone,
                                                             @Field("date") String date,
                                                             @Field("status") int status,
                                                             @Field("code") int code);

    @FormUrlEncoded
    @POST("getrequestdata.php")
    Observable<List<RequestDataRoundWay>> getRequestDataRoundWay(@Field("driverPhone") String driverPhone,
                                                                 @Field("date") String date,
                                                                 @Field("status") int status,
                                                                 @Field("code") int code);

    @FormUrlEncoded
    @POST("getbookingcount.php")
    Call<String> getCount(@Field("cabDriver") String cabDriver,
                          @Field("code") String code,
                          @Field("date") String date);

    @FormUrlEncoded
    @POST("updatetrip.php")
    Call<String> updateDropMeter(@Field("code") String code,
                                 @Field("id") String id,
                                 @Field("DropMeter") String DropMeter);

    @FormUrlEncoded
    @POST("updatetrip.php")
    Call<String> updateDropTrip(@Field("code") String code,
                                @Field("id") String id,
                                @Field("DropTrip") String DropTrip);

    @FormUrlEncoded
    @POST("updatetrip.php")
    Call<String> updateTripStatusBooking(@Field("code") String code,
                                         @Field("id") String id,
                                         @Field("TripStatus") String TripStatus);

    @FormUrlEncoded
    @POST("updatetrip.php")
    Call<String> updateTripToll(@Field("code") String code,
                                @Field("id") String id,
                                @Field("TripToll") String TripToll);

    @FormUrlEncoded
    @POST("updatetrip.php")
    Call<String> updateFare(@Field("code") String code,
                            @Field("id") String id,
                            @Field("CabFare") String CabFare,
                            @Field("CabTnxId") String CabTnxId);

    @FormUrlEncoded
    @POST("updatetrip.php")
    Call<String> updateTripData(@Field("code") String code,
                                @Field("id") String id,
                                @Field("TripToll") String TripToll,
                                @Field("TripStatus") String TripStatus,
                                @Field("DropTrip") String DropTrip,
                                @Field("DropMeter") String DropMeter,
                                @Field("NightStay") String NightStay);

    @FormUrlEncoded
    @POST("inserttrip.php")
    Call<Trip> insertTrip(@Field("fullName") String fullName,
                          @Field("phoneNumber") String phoneNumber,
                          @Field("email") String email,
                          @Field("sourceAddress") String sourceAddress,
                          @Field("destinationAddress") String destinationAddress,
                          @Field("pickupDate") String pickupDate,
                          @Field("dropDate") String dropDate,
                          @Field("pickupTime") String pickupTime,
                          @Field("source") String source,
                          @Field("destination") String destination,
                          @Field("Cabs") String Cabs,
                          @Field("BookAccount") String BookAccount,
                          @Field("cabFare") String cabFare,
                          @Field("cabDriver") String cabDriver,
                          @Field("cabStatus") String cabStatus,
                          @Field("cabModel") String cabModel,
                          @Field("cabTnxId") String cabTnxId,
                          @Field("StartTrip") String StartTrip,
                          @Field("DropTrip") String DropTrip,
                          @Field("TripStatus") String TripStatus,
                          @Field("PickUpMeter") String PickUpMeter,
                          @Field("DropMeter") String DropMeter,
                          @Field("TripToll") String TripToll,
                          @Field("TripCode") String TripCode);

    @FormUrlEncoded
    @POST("gettrip.php")
    Call<Trip> getTrip(@Field("id") String id);

    @FormUrlEncoded
    @POST("gettripbooking.php")
    Observable<List<Trip>> getTripData(@Field("code") String code,
                                       @Field("date") String date,
                                       @Field("CabDriver") String CabDriver);

    @FormUrlEncoded
    @POST("gettripbooking.php")
    Observable<List<Trip>> getTripData(@Field("code") String code,
                                       @Field("CabDriver") String CabDriver);

    @FormUrlEncoded
    @POST("gettripbooking.php")
    Observable<List<Trip>> getTripDataUser(@Field("code") String code,
                                           @Field("BookAccount") String BookAccount);

    @FormUrlEncoded
    @POST("checkemaildriver.php")
    Call<CheckDriverResponse> checkDriverEmail(@Field("email") String email);

    @FormUrlEncoded
    @POST("updatedriver.php")
    Call<String> updateDriver(@Field("id") String id,
                              @Field("Name") String Name,
                              @Field("oldPhone") String oldPhone,
                              @Field("image") String image,
                              @Field("Phone") String Phone,
                              @Field("Email") String Email);

    @FormUrlEncoded
    @POST("getrating.php")
    Observable<List<Rating>> getRating(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("getcabmodel.php")
    Call<String> getCabModel(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("getcabbydriver.php")
    Call<cabDetails> getcabbydriver(@Field("Phone") String Phone);

    @FormUrlEncoded
    @POST("updatecabdetails.php")
    Call<String> updateCabDetail(@Field("CabType") String CabType,
                                 @Field("CabImage") String CabImage,
                                 @Field("CabCity") String CabCity,
                                 @Field("Phone") String Phone,
                                 @Field("cabBrand") String cabBrand,
                                 @Field("cabModel") String cabModel,
                                 @Field("cabSitting") String cabSitting,
                                 @Field("CabNumber") String CabNumber);

    @FormUrlEncoded
    @POST("getrattingbyphone.php")
    Call<String> getRatingByPhone(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("getuserimage.php")
    Call<String> getUserImage(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("updatedriverstatus.php")
    Call<String> updateDriverStatus(@Field("phone") String phone,
                                    @Field("code") String code,
                                    @Field("arr") String arr);

    @GET("getdriverrequest.php")
    Observable<List<DriverRequestModel>> getRequestDriver();

    @FormUrlEncoded
    @POST("getcabbyphone.php")
    Call<DriverRequestModel> getCabByPhone(@Field("phone") String phone);

    @GET("getalldriver.php")
    Observable<List<DriverRequestModel>> getAllDriver();

    @FormUrlEncoded
    @POST("deletedriver.php")
    Call<String> deleteDriver(@Field("Phone") String phone);

    @FormUrlEncoded
    @POST("getdriverinfoforadmin.php")
    Call<String> getDriverInfoCheck(@Field("phone") String phone, @Field("code") String code);

    @FormUrlEncoded
    @POST("getdriverinfoforadmin.php")
    Call<DriverForAdmin> getDriverInfoForAdmin(@Field("phone") String phone, @Field("code") String code);

    @FormUrlEncoded
    @POST("getuserinfoforadmin.php")
    Observable<List<UserDetails>> getUsersForAdmin(@Field("code") String code);

    @FormUrlEncoded
    @POST("getuserinfoforadmin.php")
    Call<String> getUsersCheckStatus(@Field("phone") String phone, @Field("code") String code);

    @FormUrlEncoded
    @POST("getuserinfoforadmin.php")
    Call<UserDetails> getUsersInfoForAdmin(@Field("phone") String phone, @Field("code") String code);


    @FormUrlEncoded
    @POST("getuserinfoforadmin.php")
    Call<UserForAdminModel> getUsersInfoForAdminStatus(@Field("phone") String phone, @Field("code") String code);

    @FormUrlEncoded
    @POST("checkingforadmin.php")
    Call<String> checkPhoneExist(@Field("phone") String phone, @Field("code") String Code);

    @FormUrlEncoded
    @POST("upload.php")
    Call<ImageDriver> uploadImage(@Field("image_name") String title, @Field("image") String image);

    @FormUrlEncoded
    @POST("delete.php")
    Call<String> DeleteImage(@Field("path") String path);

    @FormUrlEncoded
    @POST("getdriverinfoforadmin.php")
    Call<Trip> getDriverTripDetail(@Field("phone") String phone, @Field("code") String code);

    @FormUrlEncoded
    @POST("getcancelcab.php")
    Observable<List<Trip>> getCancelCab(@Field("code") int code,
                                        @Field("CabDriver") String CabDriver);

    @FormUrlEncoded
    @POST("getcancelcab.php")
    Observable<List<Trip>> getCancelCabForAdmin(@Field("code") int code);


    @FormUrlEncoded
    @POST("cancelbydriver.php")
    Call<String> CancelByDriver(@Field("CabDriver") String CabDriver,
                                @Field("code") String code,
                                @Field("Id") String Id);

    @FormUrlEncoded
    @POST("updatetriptnxid.php")
    Call<String> updateTxnIdTrip(@Field("id") String id,
                                 @Field("CabTnxId") String CabTnxId);

    @FormUrlEncoded
    @POST("getcabadmin.php")
    Observable<List<String>> getCabAdmin(@Field("CabType") String CabType, @Field("cabLocation") String cabLocation, @Field("cabModel") String cabModel);


    @FormUrlEncoded
    @POST("updatedriveronoff.php")
    Call<String> UpdateDriverOnOff(@Field("driverStatus") String driverStatus, @Field("phone") String phone);

}
