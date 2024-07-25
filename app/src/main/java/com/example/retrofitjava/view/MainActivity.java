package com.example.retrofitjava.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.retrofitjava.R;
import com.example.retrofitjava.adaptor.RecyclerViewAdaptor;
import com.example.retrofitjava.model.CryptoModel;
import com.example.retrofitjava.service.CryptoAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.lang.System;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ArrayList<CryptoModel> cryptoModels;
    private String BASE_URL = "https://www.okx.com/";
    private String apiKey = "***";
    private String apiSecret = "***";
    private String passphrase = "***";
    private Retrofit retrofit;
    private CryptoAPI cryptoAPI;
    RecyclerView recyclerView;
    RecyclerViewAdaptor recyclerViewAdaptor;

    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Instant utcNow = Instant.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .withZone(ZoneOffset.UTC);
                    String utcTimestamp = formatter.format(utcNow);

                    String method = original.method();
                    String requestedPath = original.url().encodedPath();
                    String body = "";

                    if (method.equals("GET")) {
                        Set<String> paramKeys = original.url().queryParameterNames();
                        if (!paramKeys.isEmpty()) {
                            requestedPath += "?";
                            for (String key : paramKeys) {
                                requestedPath += key + "=" + original.url().queryParameter(key) + "&";
                            }
                            requestedPath = requestedPath.substring(0, requestedPath.length() - 1);
                        }
                    }

                    String prehash = utcTimestamp + method + requestedPath + body;
                    String signature = createHmacSHA256Signature(prehash, apiSecret);

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("OK-ACCESS-KEY", apiKey)
                            .header("OK-ACCESS-TIMESTAMP", utcTimestamp)
                            .header("OK-ACCESS-SIGN", signature)
                            .header("OK-ACCESS-PASSPHRASE", passphrase)
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            })
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Gson gson = new GsonBuilder().setLenient().create();
        recyclerView = findViewById(R.id.recyclerView);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        cryptoAPI = retrofit.create(CryptoAPI.class);
        List<CryptoModel.PositionData> cryptoList = new ArrayList<>();
        recyclerViewAdaptor = new RecyclerViewAdaptor(this, cryptoList);
        recyclerView.setAdapter(recyclerViewAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));



        loadData();

    }

    public String createHmacSHA256Signature(String prehash, String secretKey) {
        try {
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256HMAC.init(secretKeySpec);
            byte[] signatureBytes = sha256HMAC.doFinal(prehash.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void loadData() {
        try {
            Call<CryptoModel> call = cryptoAPI.getPositions();
            call.enqueue(new Callback<CryptoModel>() {
                @Override
                public void onResponse(Call<CryptoModel> call, Response<CryptoModel> response) {
                    if (response.isSuccessful()) {

                        Log.d("API_Response", "Response is successful.");
                        CryptoModel positionResponse = response.body();
                        if (positionResponse != null && positionResponse.getData() != null){
                            Log.d("API_Response", "Data is not null.");
                            int size = positionResponse.getData().size();
                            Log.d("API_Response", "Data size: " + size);
                            List<CryptoModel.PositionData> positions = positionResponse.getData();
                            recyclerViewAdaptor.setData(positions);
                            recyclerView.setAdapter(recyclerViewAdaptor);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            Log.d("API_Response", "Adapter updated with " + positions.size() + " items.");
                        }
                    } else {
                        try {
                            Log.e("API_Response", "Response errorBody: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<CryptoModel> call, Throwable t) {

                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

