package com.ebusiness.ebiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

public class LoadingActivity extends AppCompatActivity {
    private static final String TAG = "LoadingActivity";

    // Backend API Configuration
    private static final String BASE_URL = "https://ebizapi.zeabur.app";
    private static final String QUESTIONS_ENDPOINT = "/api/v1/analyze/questions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // HTTP Client
    private OkHttpClient httpClient;

    // Session data from previous activity
    private String sessionId;
    private String projectTitle;
    private String projectDescription;
    private String projectBudget;

    private Handler loadingHandler;
    private Runnable loadingRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        initializeHttpClient();

        // read session and project data
        extractSessionData();

        // If we have a sessionId, start questions API call
        if (sessionId != null && !sessionId.isEmpty()) {
            startQuestionsApiCall();
        } else {
            Toast.makeText(this, "세션 ID가 없습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "No session_id available after intent and prefs fallback");
            finish();
        }
    }

    private void initializeHttpClient() {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    private void extractSessionData() {
        Intent intent = getIntent();
        sessionId = intent.getStringExtra("session_id");
        projectTitle = intent.getStringExtra("project_title");
        projectDescription = intent.getStringExtra("project_description");
        projectBudget = intent.getStringExtra("project_budget");

        if (sessionId == null || sessionId.isEmpty()) {
            // fallback to SharedPreferences
            SharedPreferences prefs = getSharedPreferences("ebiz_prefs", MODE_PRIVATE);
            sessionId = prefs.getString("session_id", null);
            if (projectTitle == null) projectTitle = prefs.getString("project_title", null);
            if (projectDescription == null) projectDescription = prefs.getString("project_description", null);
            if (projectBudget == null) projectBudget = prefs.getString("project_budget", null);
        }

        Log.d(TAG, "Session ID: " + sessionId);
        Log.d(TAG, "Project Title: " + projectTitle);
    }

    private void startQuestionsApiCall() {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("session_id", sessionId);

            RequestBody body = RequestBody.create(requestBody.toString(), JSON);
            String fullUrl = BASE_URL + QUESTIONS_ENDPOINT;

            Request request = new Request.Builder()
                    .url(fullUrl)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("User-Agent", "RiskManager-Android/1.0")
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Questions API request failed", e);
                    runOnUiThread(() -> {
                        Toast.makeText(LoadingActivity.this, "질문 생성 중 네트워크 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                        finish();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Questions API response success: " + responseBody);

                        // pass raw response to QuestionsActivity
                        Intent intent = new Intent(LoadingActivity.this, QuestionsActivity.class);
                        intent.putExtra("session_id", sessionId);
                        intent.putExtra("questions_response", responseBody);
                        intent.putExtra("project_title", projectTitle);
                        intent.putExtra("project_description", projectDescription);
                        intent.putExtra("project_budget", projectBudget);

                        runOnUiThread(() -> {
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        });

                    } else {
                        Log.e(TAG, "Questions API failed with code: " + response.code() + ", response: " + responseBody);
                        runOnUiThread(() -> {
                            Toast.makeText(LoadingActivity.this, "서버에서 질문 생성에 실패했습니다. (코드: " + response.code() + ")", Toast.LENGTH_LONG).show();
                            finish();
                        });
                    }
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "JSON creation error", e);
            Toast.makeText(this, "요청 생성 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingHandler != null && loadingRunnable != null) {
            loadingHandler.removeCallbacks(loadingRunnable);
        }
    }
}
