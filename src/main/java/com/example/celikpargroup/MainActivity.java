package com.example.celikpargroup;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private EditText phoneNumberEditText;
    private EditText trackingCodeEditText;
    private Button trackButton;
    private Button saveButton;
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private List<FollowedCustomer> followedCustomers = new ArrayList<>();
    private FollowedCustomerAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneNumberEditText = findViewById(R.id.phone_number_edittext);
        trackingCodeEditText = findViewById(R.id.tracking_code_edittext);
        trackButton = findViewById(R.id.track_button);
        saveButton = findViewById(R.id.save_button);

        TrackingCodeDbHelper dbHelper = new TrackingCodeDbHelper(this);
        db = dbHelper.getWritableDatabase();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = phoneNumberEditText.getText().toString();
                String trackingCode = trackingCodeEditText.getText().toString();

                ContentValues values = new ContentValues();
                values.put(TrackingCodeDbHelper.TrackingCodeContract.TrackingCodeEntry.COLUMN_PHONE_NUMBER, phoneNumber);
                values.put(TrackingCodeDbHelper.TrackingCodeContract.TrackingCodeEntry.COLUMN_TRACKING_CODE, trackingCode);

                long newRowId = db.insert(TrackingCodeDbHelper.TrackingCodeContract.TrackingCodeEntry.TABLE_NAME, null, values);

                if (newRowId == -1) {
                    Toast.makeText(MainActivity.this, "Hata oluştu! Veri eklenemedi.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Veri eklendi!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = phoneNumberEditText.getText().toString();
                String trackingCode = trackingCodeEditText.getText().toString();

                String url = "https://api.kargomnerede.com.tr/v1/shipment?code=" + trackingCode;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Hata oluştu!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        response.body().close();

                        try {
                            JSONObject jsonObject = new JSONObject(responseData);

                            if (jsonObject.getBoolean("success")) {
                                JSONArray dataArray = jsonObject.getJSONArray("data");

                                if (dataArray.length() > 0) {
                                    String kargoDurumu = dataArray.getJSONObject(0).getString("status");
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(phoneNumber, null, kargoDurumu, null, null);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this, "Mesaj gönderildi!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this, "Geçersiz takip kodu!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "Geçersiz takip kodu!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            response.close();
                        }
                    }



                })
             ;   }
            })
        ;}
    }


