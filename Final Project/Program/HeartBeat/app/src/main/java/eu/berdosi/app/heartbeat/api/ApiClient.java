package eu.berdosi.app.heartbeat.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static String BASE_URL = "https://ppg-heartrate.000webhostapp.com/";
    private Retrofit mApi = null;

    public Retrofit getClient(String baseUrl){
        if(mApi == null){
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.connectTimeout(20, TimeUnit.SECONDS);

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);

            builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request().newBuilder()
                            .addHeader("Accept","application/json")
                            .build();
                    return chain.proceed(request);
                }
            });

            OkHttpClient client = builder.build();

            mApi = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .baseUrl(baseUrl)
                    .build();

        }
        return mApi;
    }
}
