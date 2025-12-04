package com.ebusiness.ebiz;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * AI 구체화 질문 화면
 * 더 정확한 리스크 분석을 위한 추가 질문들을 표시하고 답변을 수집하는 화면
 */
public class QuestionsActivity extends AppCompatActivity {
    private static final String TAG = "QuestionsActivity";

    // Backend API Configuration
    private static final String BASE_URL = "https://ebizapi.zeabur.app";
    private static final String QUESTIONS_ENDPOINT = "/api/v1/analyze/questions";
    private static final String REPORT_ENDPOINT = "/api/v1/analyze/report";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // HTTP Client
    private OkHttpClient httpClient;

    // Session data from previous activity
    private String sessionId;

    // UI Components
    private LinearLayout questionsContainer;
    private Button btnPrevious;
    private Button btnSubmit;

    // Data
    private List<Question> questions;
    private Map<String, String> answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        initializeHttpClient();
        initializeViews();
        setupClickListeners();
        extractSessionData();

        // 만약 질문 데이터가 없다면 API에서 가져오기
        if (questions == null || questions.isEmpty()) {
            loadQuestionsFromApi();
        }
    }

    private void initializeHttpClient() {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    private void initializeViews() {
        questionsContainer = findViewById(R.id.questionsContainer);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnSubmit = findViewById(R.id.btnSubmit);

        // 답변 저장용 맵 초기화
        answers = new HashMap<>();
    }

    private void extractSessionData() {
        Intent intent = getIntent();
        sessionId = intent.getStringExtra("session_id");
        String questionsResponse = intent.getStringExtra("questions_response");

        if (sessionId == null || sessionId.isEmpty()) {
            Log.e(TAG, "No session_id provided");
            Toast.makeText(this, "세션 정보가 없습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 만약 질문 데이터가 이미 있다면 바로 사용, 없다면 API 호출
        if (questionsResponse != null && !questionsResponse.isEmpty()) {
            Log.d(TAG, "Using provided questions response: " + questionsResponse);
            handleQuestionsApiSuccess(questionsResponse);
        }
    }

    private void setupClickListeners() {
        // 이전 버튼
        btnPrevious.setOnClickListener(v -> finish());

        // 제출 버튼
        btnSubmit.setOnClickListener(v -> {
            if (validateAnswers()) {
                submitAnswers();
            }
        });
    }

    private void loadQuestionsFromApi() {
        // 로딩 상태 표시
        showLoadingState();

        try {
            // JSON 요청 바디 생성
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

            Log.d(TAG, "Requesting questions from: " + fullUrl);
            Log.d(TAG, "Request body: " + requestBody);

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Questions API request failed", e);
                    runOnUiThread(() -> handleQuestionsApiError("네트워크 오류가 발생했습니다. 다시 시도해주세요."));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : "";

                    if (response.isSuccessful()) {
                        Log.d(TAG, "Questions API response success: " + responseBody);
                        runOnUiThread(() -> handleQuestionsApiSuccess(responseBody));
                    } else {
                        Log.e(TAG, "Questions API failed with code: " + response.code() + ", response: " + responseBody);
                        runOnUiThread(() -> handleQuestionsApiError("서버에서 질문을 가져오는데 실패했습니다. (코드: " + response.code() + ")"));
                    }
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "JSON creation error", e);
            handleQuestionsApiError("요청 데이터 생성 중 오류가 발생했습니다.");
        }
    }

    private void showLoadingState() {
        // 기존 질문 컨테이너 숨기기
        questionsContainer.removeAllViews();

        // 로딩 메시지 표시
        TextView loadingText = new TextView(this);
        loadingText.setText("AI가 맞춤형 질문을 생성하고 있습니다...");
        loadingText.setTextSize(16);
        loadingText.setTextColor(getColor(R.color.text_secondary));
        loadingText.setGravity(android.view.Gravity.CENTER);
        loadingText.setPadding(20, 40, 20, 40);

        questionsContainer.addView(loadingText);

        // 버튼 비활성화
        btnSubmit.setEnabled(false);
    }

    private void handleQuestionsApiSuccess(String responseBody) {
        try {
            JSONObject response = new JSONObject(responseBody);

            // 세션 ID 업데이트
            if (response.has("session_id")) {
                sessionId = response.getString("session_id");
            }

            // 응답에서 질문 데이터 추출
            JSONArray questionsArray = response.getJSONArray("questions");
            int totalQuestions = response.getInt("total_questions");

            Log.d(TAG, "Total questions received: " + totalQuestions);

            // 질문 리스트 생성
            questions = new ArrayList<>();
            for (int i = 0; i < questionsArray.length(); i++) {
                JSONObject questionObj = questionsArray.getJSONObject(i);

                String questionId = questionObj.getString("question_id");
                String method = questionObj.getString("method");
                String questionText = questionObj.getString("question_text");
                String questionType = questionObj.optString("question_type", "text");

                // choices 배열 처리 (현재는 사용하지 않지만 향후 확장용)
                List<String> choices = new ArrayList<>();
                if (questionObj.has("choices") && !questionObj.isNull("choices")) {
                    JSONArray choicesArray = questionObj.getJSONArray("choices");
                    for (int j = 0; j < choicesArray.length(); j++) {
                        choices.add(choicesArray.getString(j));
                    }
                }

                questions.add(new Question(questionId, method, questionText, questionType, choices));
            }

            // UI 업데이트
            renderQuestions();
            checkFormValidity();

        } catch (JSONException e) {
            Log.e(TAG, "Questions response parsing error", e);
            handleQuestionsApiError("질문 데이터 처리 중 오류가 발생했습니다.");
        }
    }

    private void handleQuestionsApiError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        Log.e(TAG, "Questions API Error: " + errorMessage);

        // 오류 발생 시 이전 화면으로 돌아가기
        finish();
    }

    private void renderQuestions() {
        questionsContainer.removeAllViews();

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            View questionCard = createQuestionCard(question, i + 1);
            questionsContainer.addView(questionCard);
        }
    }

    private View createQuestionCard(Question question, int questionNumber) {
        // question card container
        LinearLayout cardContainer = new LinearLayout(this);
        cardContainer.setOrientation(LinearLayout.VERTICAL);
        cardContainer.setBackgroundResource(R.drawable.question_card_background);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dpToPx(16));
        cardContainer.setLayoutParams(cardParams);
        cardContainer.setPadding(dpToPx(21), dpToPx(21), dpToPx(21), dpToPx(21));

        // Question header
        LinearLayout questionHeader = new LinearLayout(this);
        questionHeader.setOrientation(LinearLayout.HORIZONTAL);
        questionHeader.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Question number
        TextView questionNum = new TextView(this);
        questionNum.setText("Q" + questionNumber + ".");
        questionNum.setTextSize(16);
        questionNum.setTextColor(getColor(R.color.button_enabled));
        LinearLayout.LayoutParams numParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        numParams.setMarginEnd(dpToPx(7));
        questionNum.setLayoutParams(numParams);

        // Question text
        TextView questionTextView = new TextView(this);
        questionTextView.setId(View.generateViewId());
        questionTextView.setText(question.questionText);
        questionTextView.setTextSize(14);
        questionTextView.setTextColor(getColor(R.color.text_primary));
        questionTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        questionHeader.addView(questionNum);
        questionHeader.addView(questionTextView);

        // Input field
        EditText inputField = new EditText(this);
        inputField.setId(View.generateViewId());
        inputField.setHint("답변을 입력하세요...");
        inputField.setTextSize(14);
        inputField.setTextColor(getColor(R.color.text_primary));
        inputField.setHintTextColor(getColor(R.color.input_hint_color));
        inputField.setBackgroundResource(R.drawable.question_input_background);
        inputField.setSingleLine(false);
        inputField.setMaxLines(5);
        inputField.setMinLines(1);

        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        inputParams.setMargins(0, dpToPx(15), 0, 0);
        inputField.setLayoutParams(inputParams);
        inputField.setPadding(dpToPx(13), dpToPx(13), dpToPx(13), dpToPx(13));

        // Text watcher for answer validation
        inputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                answers.put(question.questionId, s.toString().trim());
                checkFormValidity();
            }
        });

        cardContainer.addView(questionHeader);
        cardContainer.addView(inputField);

        return cardContainer;
    }

    private void checkFormValidity() {
        if (questions == null || questions.isEmpty()) return;

        boolean allAnswered = true;
        for (Question question : questions) {
            String answer = answers.get(question.questionId);
            if (answer == null || answer.isEmpty()) {
                allAnswered = false;
                break;
            }
        }

        btnSubmit.setEnabled(allAnswered);
    }

    private boolean validateAnswers() {
        for (Question question : questions) {
            String answer = answers.get(question.questionId);
            if (answer == null || answer.trim().isEmpty()) {
                Toast.makeText(this, "모든 질문에 답변해주세요", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void submitAnswers() {
        Toast.makeText(this, "답변을 제출하고 있습니다...", Toast.LENGTH_SHORT).show();

        // 버튼 비활성화 및 로딩 상태 표시
        btnSubmit.setEnabled(false);
        btnSubmit.setText("답변 제출 중...");

        try {
            // JSON 요청 바디 생성
            JSONObject requestBody = new JSONObject();
            requestBody.put("session_id", sessionId);

            // 답변 데이터 전달
            JSONArray answersArray = new JSONArray();
            for (Question question : questions) {
                JSONObject answerObj = new JSONObject();
                answerObj.put("question_id", question.questionId);
                answerObj.put("answer", answers.get(question.questionId));
                answersArray.put(answerObj);
            }
            requestBody.put("answers", answersArray);

            RequestBody body = RequestBody.create(requestBody.toString(), JSON);
            String fullUrl = BASE_URL + REPORT_ENDPOINT;

            Request request = new Request.Builder()
                    .url(fullUrl)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("User-Agent", "RiskManager-Android/1.0")
                    .build();

            Log.d(TAG, "Submitting answers to: " + fullUrl);
            Log.d(TAG, "Request body: " + requestBody);

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Submit answers API request failed", e);
                    runOnUiThread(() -> handleSubmitAnswersError("네트워크 오류가 발생했습니다. 다시 시도해주세요."));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : "";

                    if (response.isSuccessful()) {
                        Log.d(TAG, "Submit answers API response success: " + responseBody);
                        runOnUiThread(() -> handleSubmitAnswersSuccess(responseBody));
                    } else {
                        Log.e(TAG, "Submit answers API failed with code: " + response.code() + ", response: " + responseBody);
                        runOnUiThread(() -> handleSubmitAnswersError("서버에서 답변 처리에 실패했습니다. (코드: " + response.code() + ")"));
                    }
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "JSON creation error", e);
            handleSubmitAnswersError("요청 데이터 생성 중 오류가 발생했습니다.");
        }
    }

    private void handleSubmitAnswersSuccess(String responseBody) {
        // ReportActivity로 이동 (분석 결과 화면)
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra("session_id", sessionId);
        intent.putExtra("project_title", getIntent().getStringExtra("project_title"));
        intent.putExtra("project_description", getIntent().getStringExtra("project_description"));
        intent.putExtra("project_budget", getIntent().getStringExtra("project_budget"));
        intent.putExtra("report_response", responseBody); // 리포트 데이터를 직접 전달

        startActivity(intent);
        finish();
    }

    private void handleSubmitAnswersError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        Log.e(TAG, "Submit answers Error: " + errorMessage);

        // 버튼 상태 복원
        btnSubmit.setEnabled(true);
        btnSubmit.setText("최종 분석 시작");
    }

    // dp를 px로 변환하는 유틸리티 메서드
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
