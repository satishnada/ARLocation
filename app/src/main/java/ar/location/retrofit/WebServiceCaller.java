package ar.location.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import ar.location.model.CommonResponse;
import ar.location.model.Directions;
import ar.location.utility.AppConstant;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public class WebServiceCaller {

    private static ApiInterface apiInterface;

    public static ApiInterface getClient() {
        if (apiInterface == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okclient = new OkHttpClient();
            Gson gson = new GsonBuilder().setLenient().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

            Retrofit client = new Retrofit.Builder()
                    .baseUrl(WebUtility.BASE_URL)
                    .client(okclient.newBuilder()
                            .connectTimeout(100, TimeUnit.SECONDS)
                            .readTimeout(100, TimeUnit.SECONDS)
                            .writeTimeout(100, TimeUnit.SECONDS)
                            .addInterceptor(logging).build())
                    .addConverterFactory(GsonConverterFactory.create(gson)).build();

            apiInterface = client.create(ApiInterface.class);
        }

        return apiInterface;
    }

    public interface GetDirectionInterface {

        @POST(WebUtility.GET_DIRECTION)
        Call<Directions> getDirections(@Query(AppConstant.origin) String origin,
                                       @Query(AppConstant.destination) String destination,
                                       @Query(AppConstant.sensor) boolean sensor,
                                       @Query(AppConstant.mode) String mode,
                                       @Query(AppConstant.key) String apiKey,
                                       @Query(AppConstant.alternatives) boolean alternatives);
    }

    public interface ApiInterface {

        @GET()
        Call<ResponseBody> getAddressFromLatLong(@Url() String url);
    }
}
