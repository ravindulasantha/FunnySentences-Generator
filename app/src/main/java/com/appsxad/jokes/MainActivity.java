package com.appsxad.jokes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private TextView textView;
    ProgressDialog progressDialog;
    //unity inter 1
    private String GameID = "4739510";
    private String interPlacement="Interstitial_Android";
    private boolean testMode = false;
    //unity inter 1

    // save and share 1
    Button savebtn;
    Button sharebtn;
    // save and share 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);

        progressDialog.setTitle("Loading...");

        //unity inter 2
        UnityAds.initialize(this, GameID, testMode);
        IUnityAdsListener interListner = new IUnityAdsListener() {
            @Override
            public void onUnityAdsReady(String s) {

            }

            @Override
            public void onUnityAdsStart(String s) {

            }

            @Override
            public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {

            }

            @Override
            public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {

            }
        };
        UnityAds.setListener(interListner);
        UnityAds.load(interPlacement);
        //unity inter 2

        // save and share 2
        savebtn = findViewById(R.id.save_btn);
        sharebtn = findViewById(R.id.share_btn);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  progressDialog.setTitle("Fetching response for ");
                progressDialog.show();*/
                //unity inter 3

                if (UnityAds.isReady(interPlacement))
                {
                    UnityAds.show(MainActivity.this,interPlacement);
                }

                //unity inter 3
                SaveImage();
            }
        });
        sharebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = SaveImage();
                if (file!=null)
                    share(file);
            }
        });
        // save and share 2

        //intenet dialog box 1

        if(!isConnected(this))
        {
            showCustomDialog();
        }

        //intenet dialog box 1

        button = findViewById(R.id.button);
        textView = findViewById(R.id.textview);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Fetching response for ");
                progressDialog.show();
                fetchData();
            }
        });

    }


    //intenet dialog box 2

    private boolean isConnected(MainActivity mainActivity) {

        ConnectivityManager connectivityManager = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);


        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo moblieConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiConn != null && wifiConn.isConnected() || (moblieConn != null && moblieConn.isConnected())))
        {
            return true;
        }
        else
        {
            return false;
        }


    }

    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Please connect to the internet to proceed further")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    //intenet dialog box 2

    private void fetchData()
    {

        String url = "https://v2.jokeapi.dev/joke/Any";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (response.isSuccessful())
                {

                    String resp = response.body().string();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(resp);
                                String Value = jsonObject.getString("joke");
                                textView.setText(Value);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                }

            }
        });

    }

    // save and share 3

    private void share(File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
        {
            uri = FileProvider.getUriForFile(this,getPackageName()+".provider",file);

        }else {
            uri = Uri.fromFile(file);
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Screenshot");
        intent.putExtra(Intent.EXTRA_TEXT,"English Joke App");
        intent.putExtra(Intent.EXTRA_STREAM,uri);

        try {
            startActivity(Intent.createChooser(intent,"Share using"));
        }catch (ActivityNotFoundException e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            sharebtn.performClick();
            // SaveImage();
        }
        else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private File SaveImage() {

        if(!CheckPermission())
            return null;

        try {

            // String path = Environment.getExternalStorageState();
            String path = Environment.getExternalStorageDirectory().toString()+"/AppNAME";
            File fileDir = new File(path);
            if (!fileDir.exists())
                fileDir.mkdir();

            String mPath = path+"/ScreenShot_"+new Date().getTime()+".png";

            Bitmap bitmap = screenShot();
            File file = new File(mPath);
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100, fOut);
            fOut.flush();
            fOut.close();

            Toast.makeText(this, "Image Saved Successfully", Toast.LENGTH_LONG).show();
            return file;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap screenShot() {
        View v = findViewById(R.id.rel);
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;

    }

    private Boolean CheckPermission() {

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1);
            return false;

        }

        return true;

    }
// save and share 3
}