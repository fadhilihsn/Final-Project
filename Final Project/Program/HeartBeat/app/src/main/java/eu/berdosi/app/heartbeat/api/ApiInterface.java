package eu.berdosi.app.heartbeat.api;

import java.util.List;

import eu.berdosi.app.heartbeat.api.response.BaseDao;
import eu.berdosi.app.heartbeat.api.response.DiagnosticDAO;
import eu.berdosi.app.heartbeat.api.response.SignalDAO;
import eu.berdosi.app.heartbeat.api.response.UserDAO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("api/GetAllSignal.php")
    Call<BaseDao<List<SignalDAO>>> getAllSignal();

    @GET("api/GetAllSignal.php")
    Call<BaseDao<List<SignalDAO>>> getAllSignal(@Query("user_id") String user_id);

    @GET("api/GetSignal.php")
    Call<BaseDao<SignalDAO>> getSignal(@Query("signal_id") String signal_id);

    @POST("api/Register.php")
    @FormUrlEncoded
    Call<BaseDao<String>> register(
            @Field("email") String email,
            @Field("password") String password,
            @Field("nama") String nama,
            @Field("phone") String phone
    );

    @POST("api/Login.php")
    @FormUrlEncoded
    Call<BaseDao<UserDAO>> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @POST("api/DoInputSignal.php")
    @FormUrlEncoded
    Call<BaseDao<SignalDAO>> doInputSignal(
            @Field("user_id") String user_id,
            @Field("pulse") String pulse,
            @Field("cycles") String cycles,
            @Field("duration") String duration,
            @Field("signals") String signals
    );

    @POST("api/DoInputDiagnostic.php")
    @FormUrlEncoded
    Call<BaseDao<String>> doInputDiagnosa(
            @Field("user_id") String user_id,
            @Field("perokok") int perokok,
            @Field("diabetes") int diabetes,
            @Field("kolestrol") int kolestrol,
            @Field("hipertensi") int hipertensi
    );

    @GET("api/GetDiagnostic.php")
    Call<BaseDao<List<DiagnosticDAO>>> getDiagnosa(
            @Query("user_id") String user_id
    );
}
