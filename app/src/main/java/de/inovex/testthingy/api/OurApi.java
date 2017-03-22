package de.inovex.testthingy.api;

import io.reactivex.Completable;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface OurApi {
    @POST("login")
    Completable login(@Body TokenRequest tokenRequest);

    @GET("someresponse")
    Observable<SomeResponse> getSomeResponse();
}
