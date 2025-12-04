package com.ebusiness.ebiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 새 프로젝트 입력 화면
 * 사용자가 프로젝트 정보를 입력하여 리스크 분석을 시작하는 화면
 */
public class NewProjectActivity extends AppCompatActivity {
    private static final String TAG = "NewProjectActivity";

    // Backend API Configuration
    private static final String BASE_URL = "https://ebizapi.zeabur.app";
    private static final String INITIAL_ENDPOINT = "/api/v1/analyze/initial";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // HTTP Client
    private OkHttpClient httpClient;

    // UI Components
    private ImageButton btnBack;
    private EditText projectTitle;
    private EditText textarea;
    private EditText container;
    private Button input; // 미정 버튼 (가장 오른쪽 버튼)
    private Button button;

    // Form validation
    private boolean isBudgetUnknown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);

        initializeViews();
        initializeHttpClient();
        setupClickListeners();
        setupTextWatchers();
        checkFormValidity();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        projectTitle = findViewById(R.id.project_title);
        textarea = findViewById(R.id.textarea);
        container = findViewById(R.id.container);
        input = findViewById(R.id.input); // 미정 버튼
        button = findViewById(R.id.button);
    }

    private void initializeHttpClient() {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    private void setupClickListeners() {
        // 뒤로 가기 버튼
        btnBack.setOnClickListener(v -> finish());

        // 예산 미정 버튼 (input)
        input.setOnClickListener(v -> {
            isBudgetUnknown = !isBudgetUnknown;

            if (isBudgetUnknown) {
                input.setSelected(true);
                container.setText("");
                container.setEnabled(false);
                Toast.makeText(this, "예산이 미정으로 설정되었습니다", Toast.LENGTH_SHORT).show();
            } else {
                input.setSelected(false);
                container.setEnabled(true);
            }

            checkFormValidity();
        });

        // 제출 버튼
        button.setOnClickListener(v -> {
            if (validateForm()) {
                submitProjectInfo();
            }
        });
    }

    private void setupTextWatchers() {
        TextWatcher formWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                checkFormValidity();
            }
        };

        projectTitle.addTextChangedListener(formWatcher);
        textarea.addTextChangedListener(formWatcher);
        container.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty() && isBudgetUnknown) {
                    isBudgetUnknown = false;
                    input.setSelected(false);
                }
                checkFormValidity();
            }
        });
    }

    private void checkFormValidity() {
        boolean titleFilled = !projectTitle.getText().toString().trim().isEmpty();
        boolean descriptionFilled = !textarea.getText().toString().trim().isEmpty();
        boolean budgetFilled = !container.getText().toString().trim().isEmpty() || isBudgetUnknown;

        boolean isFormValid = titleFilled && descriptionFilled && budgetFilled;

        button.setEnabled(isFormValid);

        // 버튼 색상과 텍스트 색상을 동적으로 변경
        if (isFormValid) {
            button.setAlpha(1.0f);
        } else {
            button.setAlpha(0.6f);
        }
    }

    private boolean validateForm() {
        String title = projectTitle.getText().toString().trim();
        String description = textarea.getText().toString().trim();
        String budget = container.getText().toString().trim();

        if (title.isEmpty()) {
            projectTitle.setError("프로젝트 제목을 입력해주세요");
            projectTitle.requestFocus();
            return false;
        }

        if (description.isEmpty()) {
            textarea.setError("프로젝트 설명을 입력해주세요");
            textarea.requestFocus();
            return false;
        }

        if (budget.isEmpty() && !isBudgetUnknown) {
            container.setError("예산을 입력하거나 미정을 선택해주세요");
            container.requestFocus();
            return false;
        }

        return true;
    }

    private void submitProjectInfo() {
        String title = projectTitle.getText().toString().trim();
        String description = textarea.getText().toString().trim();
        String budget = isBudgetUnknown ? "미정" : container.getText().toString().trim() + "만원";

        Toast.makeText(this, "리스크 분석을 시작합니다...", Toast.LENGTH_SHORT).show();
        button.setEnabled(false); // Disable button during request

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("business_name", title);
            jsonBody.put("business_description", description);
            jsonBody.put("project_budget", budget);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create JSON for initial request", e);
            Toast.makeText(this, "요청 데이터 생성에 실패했습니다.", Toast.LENGTH_LONG).show();
            button.setEnabled(true);
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + INITIAL_ENDPOINT)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", "RiskManager-Android/1.0")
                .build();
        
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Initial analysis API request failed", e);
                runOnUiThread(() -> {
                    Toast.makeText(NewProjectActivity.this, "분석 시작에 실패했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    button.setEnabled(true);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body() != null ? response.body().string() : "";
                
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Initial analysis API failed with code: " + response.code() + ", response: " + responseBody);
                    String errorMessage = "서버에서 분석 시작에 실패했습니다. (코드: " + response.code() + ")";
                    try {
                        JSONObject errorJson = new JSONObject(responseBody);
                        errorMessage = errorJson.getString("detail");
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to parse error response", e);
                    }
                    final String finalErrorMessage = errorMessage;
                    runOnUiThread(() -> {
                        Toast.makeText(NewProjectActivity.this, finalErrorMessage, Toast.LENGTH_LONG).show();
                        button.setEnabled(true);
                    });
                    return;
                }

                try {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    final String sessionId = jsonResponse.getString("session_id");

                    runOnUiThread(() -> {
                        // Save session ID and project info to SharedPreferences for later use
                        SharedPreferences prefs = getSharedPreferences("ebiz_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("session_id", sessionId);
                        editor.putString("project_title", title);
                        editor.putString("project_description", description);
                        editor.putString("project_budget", budget);
                        editor.apply();

                        // QuestionsActivity로 세션 ID와 프로젝트 정보를 전달
                        Intent intent = new Intent(NewProjectActivity.this, QuestionsActivity.class);
                        intent.putExtra("session_id", sessionId);
                        intent.putExtra("project_title", title);
                        intent.putExtra("project_description", description);
                        intent.putExtra("project_budget", budget);

                        startActivity(intent);

                        // 부드러운 전환 애니메이션
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish(); // Close this activity
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse session_id from response: " + responseBody, e);
                    runOnUiThread(() -> {
                        Toast.makeText(NewProjectActivity.this, "서버 응답 처리 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                        button.setEnabled(true);
                    });
                }
            }
        });
    }
}
