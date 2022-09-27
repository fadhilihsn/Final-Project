package eu.berdosi.app.heartbeat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import eu.berdosi.app.heartbeat.api.ApiClient;
import eu.berdosi.app.heartbeat.api.ApiInterface;
import eu.berdosi.app.heartbeat.api.response.BaseDao;
import eu.berdosi.app.heartbeat.api.response.DiagnosticDAO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiagnosaActivity extends AppCompatActivity {
    RelativeLayout rlLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosa);

        Button btnSave = findViewById(R.id.btnSave);
        RadioGroup rb1 = findViewById(R.id.radioGroup1);
        RadioGroup rb2 = findViewById(R.id.radioGroup2);
        RadioGroup rb3 = findViewById(R.id.radioGroup3);
        RadioGroup rb4 = findViewById(R.id.radioGroup4);
         rlLoading = findViewById(R.id.layoutLoading);

        rlLoading.setVisibility(View.VISIBLE);

        String user_id = getIntent().getStringExtra("uId");
        String email = getIntent().getStringExtra("email");

        checkDiagnosa(user_id,email);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doInputDiagnostic(
                        user_id,
                        email,
                        rb1.getCheckedRadioButtonId() == R.id.rbYes1,
                        rb2.getCheckedRadioButtonId() == R.id.rbYes2,
                        rb3.getCheckedRadioButtonId() == R.id.rbYes3,
                        rb4.getCheckedRadioButtonId() == R.id.rbYes4
                );

            }
        });
    }

    public void checkDiagnosa(String user_id, String email){
        ApiInterface apiClient = new ApiClient().getClient(ApiClient.BASE_URL).create(ApiInterface.class);
        apiClient.getDiagnosa(user_id)
                .enqueue(new Callback<BaseDao<List<DiagnosticDAO>>>() {
            @Override
            public void onResponse(Call<BaseDao<List<DiagnosticDAO>>> call, Response<BaseDao<List<DiagnosticDAO>>> response) {
                rlLoading.setVisibility(View.GONE);

                if (response.body().getCode() == 1) {
                    if(!response.body().getData().isEmpty()){
                        Intent intent = new Intent(DiagnosaActivity.this, MainActivity.class);
                        intent.putExtra("uId", user_id);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<BaseDao<List<DiagnosticDAO>>> call, Throwable t) {
                rlLoading.setVisibility(View.GONE);

                Toast.makeText(DiagnosaActivity.this, "Get Diagnostic failed. Please Login Again...",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(DiagnosaActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void doInputDiagnostic(String user_id, String email, boolean isPerokok, boolean isDiabetes, boolean isKolestrol, boolean isHipertensi) {
        ApiInterface apiClient = new ApiClient().getClient(ApiClient.BASE_URL).create(ApiInterface.class);
        apiClient.doInputDiagnosa(
                user_id,
                isPerokok ? 1 : 0,
                isDiabetes ? 1 : 0,
                isKolestrol ? 1 : 0,
                isHipertensi ? 1 : 0
        ).enqueue(new Callback<BaseDao<String>>() {
            @Override
            public void onResponse(Call<BaseDao<String>> call, Response<BaseDao<String>> response) {
                if (response.body().getCode() == 1) {

                    Toast.makeText(DiagnosaActivity.this, "Input Diagnostic Berhasil",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(DiagnosaActivity.this, MainActivity.class);
                    intent.putExtra("uId", user_id);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(DiagnosaActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseDao<String>> call, Throwable t) {
                Toast.makeText(DiagnosaActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}