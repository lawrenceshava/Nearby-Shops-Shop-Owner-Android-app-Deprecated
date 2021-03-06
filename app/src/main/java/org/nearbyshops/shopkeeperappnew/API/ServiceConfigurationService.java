package org.nearbyshops.shopkeeperappnew.API;

import org.nearbyshops.shopkeeperappnew.Markets.Model.ServiceConfigurationLocal;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by sumeet on 12/3/16.
 */
public interface ServiceConfigurationService {





    @GET("/api/serviceconfiguration")
    Call<ServiceConfigurationLocal> getServiceConfiguration(@Query("latCenter") Double latCenter,
                                                            @Query("lonCenter") Double lonCenter);

//    @PUT("/api/ServiceConfiguration")
//    Call<ResponseBody> putServiceConfiguration(@Header("Authorization") String headers,
//                                               @Body ServiceConfigurationLocal serviceConfiguration);

//
//    // Image Calls
//
//    @POST("/api/ServiceConfiguration/Image")
//    Call<Image> uploadImage(@Header("Authorization") String headers,
//                            @Body RequestBody image);
//
//    @DELETE("/api/ServiceConfiguration/Image/{name}")
//    Call<ResponseBody> deleteImage(@Header("Authorization") String headers,
//                                   @Path("name") String fileName);



}
