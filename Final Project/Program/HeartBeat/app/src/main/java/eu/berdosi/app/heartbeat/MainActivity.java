package eu.berdosi.app.heartbeat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import eu.berdosi.app.heartbeat.api.ApiClient;
import eu.berdosi.app.heartbeat.api.ApiInterface;
import eu.berdosi.app.heartbeat.api.response.BaseDao;
import eu.berdosi.app.heartbeat.api.response.SignalDAO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.Surface;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import com.google.code.regexp.Pattern;
import com.google.code.regexp.Matcher;
import com.google.gson.Gson;

public class MainActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private OutputAnalyzer analyzer;

    private final int REQUEST_CODE_CAMERA = 0;
    public static final int MESSAGE_UPDATE_REALTIME = 1;
    public static final int MESSAGE_UPDATE_FINAL = 2;
    public static final int MESSAGE_CAMERA_NOT_AVAILABLE = 3;

    private static final int MENU_INDEX_NEW_MEASUREMENT = 0;
    private static final int MENU_INDEX_EXPORT_RESULT = 1;
    private static final int MENU_INDEX_EXPORT_DETAILS = 2;

    private boolean justShared = false;

    private String uId = "";
    private String email = "";

    @SuppressLint("HandlerLeak")
    private final Handler mainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (msg.what ==  MESSAGE_UPDATE_REALTIME) {
                ((TextView) findViewById(R.id.textView)).setText(msg.obj.toString());
            }

            if (msg.what == MESSAGE_UPDATE_FINAL) {
                ((EditText) findViewById(R.id.editText)).setText(msg.obj.toString());

                Toast.makeText(MainActivity.this, "End of Measurement", Toast.LENGTH_SHORT).show();

                pushToDB(msg.obj.toString());

                // make sure menu items are enabled when it opens.
                Menu appMenu = ((Toolbar) findViewById(R.id.toolbar)).getMenu();
                appMenu.getItem(MENU_INDEX_EXPORT_RESULT).setVisible(true);
                appMenu.getItem(MENU_INDEX_EXPORT_DETAILS).setVisible(true);
                appMenu.getItem(MENU_INDEX_NEW_MEASUREMENT).setVisible(true);
            }

            if (msg.what == MESSAGE_CAMERA_NOT_AVAILABLE) {
                Log.println(Log.WARN, "camera", msg.obj.toString());

                ((TextView) findViewById(R.id.textView)).setText(
                        R.string.camera_not_found
                );
                analyzer.stop();
            }
        }
    };

    private final CameraService cameraService = new CameraService(this, mainHandler);

    @Override
    protected void onResume() {
        super.onResume();

        analyzer = new OutputAnalyzer(this, findViewById(R.id.graphTextureView), mainHandler);

        TextureView cameraTextureView = findViewById(R.id.textureView2);
        SurfaceTexture previewSurfaceTexture = cameraTextureView.getSurfaceTexture();

        // justShared is set if one clicks the share button.
        if ((previewSurfaceTexture != null) && !justShared) {
            // this first appears when we close the application and switch back
            // - TextureView isn't quite ready at the first onResume.
            Surface previewSurface = new Surface(previewSurfaceTexture);

            // show warning when there is no flash
            if (!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                Snackbar.make(
                        findViewById(R.id.constraintLayout),
                        getString(R.string.noFlashWarning),
                        Snackbar.LENGTH_LONG
                ).show();
            }

            // hide the new measurement item while another one is in progress in order to wait
            // for the previous one to finish
            ((Toolbar) findViewById(R.id.toolbar)).getMenu().getItem(MENU_INDEX_NEW_MEASUREMENT).setVisible(false);

            cameraService.start(previewSurface);
            analyzer.measurePulse(cameraTextureView, cameraService);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraService.stop();
        if (analyzer != null) analyzer.stop();
        analyzer = new OutputAnalyzer(this, findViewById(R.id.graphTextureView), mainHandler);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uId = getIntent().getStringExtra("uId");
        email = getIntent().getStringExtra("email");

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CODE_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Snackbar.make(
                        findViewById(R.id.constraintLayout),
                        getString(R.string.cameraPermissionRequired),
                        Snackbar.LENGTH_LONG
                ).show();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.i("MENU", "menu is being prepared");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onPrepareOptionsMenu(menu);
    }

    public void onClickNewMeasurement(MenuItem item) {
        analyzer = new OutputAnalyzer(this, findViewById(R.id.graphTextureView), mainHandler);

        // clear prior results
        char[] empty = new char[0];
        ((EditText) findViewById(R.id.editText)).setText(empty, 0, 0);
        ((TextView) findViewById(R.id.textView)).setText(empty, 0, 0);

        // hide the new measurement item while another one is in progress in order to wait
        // for the previous one to finish
        // Exporting results cannot be done, either, as it would read from the already cleared UI.
        Menu appMenu = ((Toolbar) findViewById(R.id.toolbar)).getMenu();
        appMenu.getItem(MENU_INDEX_NEW_MEASUREMENT).setVisible(false);
        appMenu.getItem(MENU_INDEX_EXPORT_RESULT).setVisible(false);
        appMenu.getItem(MENU_INDEX_EXPORT_DETAILS).setVisible(false);

        TextureView cameraTextureView = findViewById(R.id.textureView2);
        SurfaceTexture previewSurfaceTexture = cameraTextureView.getSurfaceTexture();

        if (previewSurfaceTexture != null) {
            // this first appears when we close the application and switch back
            // - TextureView isn't quite ready at the first onResume.
            Surface previewSurface = new Surface(previewSurfaceTexture);
            cameraService.start(previewSurface);
            analyzer.measurePulse(cameraTextureView, cameraService);
        }
    }

    public void onClickExportResult(MenuItem item) {
        final Intent intent = getTextIntent((String) ((TextView) findViewById(R.id.textView)).getText());
        justShared = true;
        startActivity(Intent.createChooser(intent, getString(R.string.send_output_to)));
    }

    public void onClickExportDetails(MenuItem item) {
//        final Intent intent = getTextIntent(((EditText) findViewById(R.id.editText)).getText().toString());
//        justShared = true;
//        startActivity(Intent.createChooser(intent, getString(R.string.send_output_to)));

        generateCSV(((EditText) findViewById(R.id.editText)).getText().toString());
        justShared = true;
    }

    private Intent getTextIntent(String intentText) {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(
                Intent.EXTRA_SUBJECT,
                String.format(
                        getString(R.string.output_header_template),
                        new SimpleDateFormat(
                                getString(R.string.dateFormat),
                                Locale.getDefault()
                        ).format(new Date())
                ));
        intent.putExtra(Intent.EXTRA_TEXT, intentText);
        return intent;
    }

    // CSV : UserId, UserName, Pulse, Cycles, Duration, timestamp, value
    public void pushToDB(String text){

        String columnString =   "\"UserId\",\"Email\",\"Pulse\",\"Cycles\",\"Duration\",\"Timestamp\",\"Value\"";
        String pulse = "";
        String cycles = "";
        String durations = "";
        List<MeasurementResult> measurements = new ArrayList<>();

        Pattern pattern = Pattern.compile("(Pulse: (?<pulse>.*) \\((?<cycles>.*) cycles in (?<duration>.*) second|((?<timestamp>\\d..*),(?<value>..*)))");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()){
            if(pulse.isEmpty() && cycles.isEmpty() && durations.isEmpty()){
                pulse = matcher.group("pulse");
                cycles = matcher.group("cycles");
                durations = matcher.group("duration");
            }else {
                measurements.add(new MeasurementResult(matcher.group("timestamp"),matcher.group("value")));
            }
        }

        Gson gson = new Gson();
        String json = gson.toJson(measurements);

        Log.e("Result Dao", "pushToDB: "+json);

        ApiInterface apiClient = new ApiClient().getClient(ApiClient.BASE_URL).create(ApiInterface.class);
        apiClient.doInputSignal(uId,pulse,cycles,durations,json).enqueue(new Callback<BaseDao<SignalDAO>>() {
            @Override
            public void onResponse(Call<BaseDao<SignalDAO>> call, Response<BaseDao<SignalDAO>> response) {
                if(response.body().getCode() == 1){
                    Toast.makeText(MainActivity.this, "Berhasil Memasukkan data ke database",
                            Toast.LENGTH_SHORT).show();
                }else {
                    Log.e("PushToDB", "onResponse: "+response.message() );
                    Toast.makeText(MainActivity.this, "Gagal Memasukkan data ke database",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseDao<SignalDAO>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    // CSV : UserId, UserName, Pulse, Cycles, Duration, timestamp, value
    public void generateCSV(String text){

        String columnString =   "\"UserId\",\"Email\",\"Pulse\",\"Cycles\",\"Duration\",\"Timestamp\",\"Value\"";
        String pulse = "";
        String cycles = "";
        String durations = "";
        List<MeasurementResult> measurements = new ArrayList<>();

        Pattern pattern = Pattern.compile("(Pulse: (?<pulse>.*) \\((?<cycles>.*) cycles in (?<duration>.*) second|((?<timestamp>\\d..*),(?<value>..*)))");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()){
            if(pulse.isEmpty() && cycles.isEmpty() && durations.isEmpty()){
                pulse = matcher.group("pulse");
                cycles = matcher.group("cycles");
                durations = matcher.group("duration");
            }else {
                measurements.add(new MeasurementResult(matcher.group("timestamp"),matcher.group("value")));
            }
        }

        String dataString   =   "";
        for (MeasurementResult result: measurements) {
            dataString   +=   "\"" + uId +"\",\"" + email + "\",\"" + pulse + "\",\"" + cycles + "\",\"" + durations + "\",\"" + result.timestamp+ "\",\"" + result.values + "\"" + "\n" ;
        }

        String combinedString = columnString + "\n" + dataString;

        Log.e("Result CSV", "generateCSV: "+ combinedString );

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/csv");
        File data = null;
        try {
            Date dateVal = new Date();
            String filename = dateVal.toString();
            data = File.createTempFile("Report", ".csv");
            FileWriter out = (FileWriter) generateCsvFile(
                    data, combinedString);
            Uri uri = FileProvider.getUriForFile(
                    MainActivity.this,
                    "eu.berdosi.app.heartbeat.provider", //(use your app signature + ".provider" )
                    data);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(shareIntent, "Share Result to"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileWriter generateCsvFile(File sFileName,String fileContent) {
        FileWriter writer = null;

        try {
            writer = new FileWriter(sFileName);
            writer.append(fileContent);
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }finally
        {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return writer;
    }

    class MeasurementResult{
        public String timestamp;
        public String values;
        MeasurementResult(String t, String v){
            timestamp = t;
            values = v;
        }
    }
}


