package eu.berdosi.app.heartbeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import eu.berdosi.app.heartbeat.api.ApiClient;
import eu.berdosi.app.heartbeat.api.ApiInterface;
import eu.berdosi.app.heartbeat.api.response.BaseDao;
import eu.berdosi.app.heartbeat.api.response.UserDAO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin(etEmail.getText().toString(), etPassword.getText().toString());
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doRegister(etEmail.getText().toString(), etPassword.getText().toString());
            }
        });

    }

    public void doLogin(String email, String password){
        ApiInterface apiClient = new ApiClient().getClient(ApiClient.BASE_URL).create(ApiInterface.class);
        apiClient.login(email,password).enqueue(new Callback<BaseDao<UserDAO>>() {
            @Override
            public void onResponse(Call<BaseDao<UserDAO>> call, Response<BaseDao<UserDAO>> response) {
                if(response.body().getCode() == 1){

                    Intent intent = new Intent(LoginActivity.this, DiagnosaActivity.class);
                    intent.putExtra("uId", response.body().getData().user_id);
                    intent.putExtra("email", response.body().getData().email);
                    startActivity(intent);
                    finish();
                }else {

                }
            }

            @Override
            public void onFailure(Call<BaseDao<UserDAO>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void doRegister(String email, String password){
        ApiInterface apiClient = new ApiClient().getClient(ApiClient.BASE_URL).create(ApiInterface.class);
        apiClient.register(email,password,"aa","123").enqueue(new Callback<BaseDao<String>>() {
            @Override
            public void onResponse(Call<BaseDao<String>> call, Response<BaseDao<String>> response) {
                if(response.body().getCode() == 1){

                    Toast.makeText(LoginActivity.this, "Register Berhasil Silahkan Login",
                            Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseDao<String>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}