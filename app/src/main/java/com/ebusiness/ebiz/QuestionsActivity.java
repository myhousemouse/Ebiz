package com.ebusiness.ebiz;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 구체화 질문 화면
 * 더 정확한 리스크 분석을 위한 추가 질문들을 표시하고 답변을 수집하는 화면
 */
public class QuestionsActivity extends AppCompatActivity {
    private static final String TAG = "QuestionsActivity";

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

        initializeViews();
        setupClickListeners();
        initializeQuestions();
        renderQuestions();
        checkFormValidity();
    }

    private void initializeViews() {
        questionsContainer = findViewById(R.id.questionsContainer);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnSubmit = findViewById(R.id.btnSubmit);

        answers = new HashMap<>();
    }

    private void setupClickListeners() {
        // 이전 버튼
        btnPrevious.setOnClickListener(v -> {
            finish();
        });

        // 제출 버튼
        btnSubmit.setOnClickListener(v -> {
            if (validateAnswers()) {
                submitAnswers();
            }
        });
    }

    private void initializeQuestions() {
        // Mock data - 실제로는 API에서 받아올 데이터
        questions = new ArrayList<>();
        questions.add(new Question("q1", "Logic Model", "이 사업에 투자할 수 있는 초기 자본은 얼마인가요?"));
        questions.add(new Question("q2", "Logic Model", "현재 보유한 기술 역량 또는 관련 경험은 어느 정도인가요?"));
        questions.add(new Question("q3", "Logic Model", "목표 고객층이 명확히 정의되어 있나요?"));
        questions.add(new Question("q4", "Logic Model", "경쟁사 분석을 수행했으며, 차별화 포인트가 있나요?"));
        questions.add(new Question("q5", "Logic Model", "향후 6개월 내 최소 기능 제품(MVP)을 출시할 수 있나요?"));
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
        // @+id/background_ = question card container
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
        questionNum.setTextColor(getColor(R.color.button_enabled)); // FF3B4E
        LinearLayout.LayoutParams numParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        numParams.setMarginEnd(dpToPx(7));
        questionNum.setLayoutParams(numParams);

        // Question text - android:id="@+id/label_"
        TextView questionText = new TextView(this);
        questionText.setId(View.generateViewId());
        questionText.setText(question.questionText);
        questionText.setTextSize(14);
        questionText.setTextColor(getColor(R.color.text_primary));
        questionText.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        questionHeader.addView(questionNum);
        questionHeader.addView(questionText);

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
        // 답변 데이터 준비
        StringBuilder submissionData = new StringBuilder("답변이 제출되었습니다!\n\n");

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            String answer = answers.get(question.questionId);
            submissionData.append("Q").append(i + 1).append(": ").append(answer).append("\n");
        }

        Toast.makeText(this, "분석을 완료했습니다!", Toast.LENGTH_SHORT).show();

        // 분석 결과 화면으로 이동
        Intent intent = new Intent(this, ReportActivity.class);

        // 프로젝트 정보 전달
        intent.putExtra("project_title", getIntent().getStringExtra("project_title"));
        intent.putExtra("project_description", getIntent().getStringExtra("project_description"));
        intent.putExtra("project_budget", getIntent().getStringExtra("project_budget"));

        // 답변 데이터 전달 (실제 구현에서는 session_id를 통해 백엔드에서 가져옴)
        intent.putExtra("answers", submissionData.toString());

        startActivity(intent);

        // 부드러운 전환 애니메이션
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        finish();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    // Question 데이터 클래스
    private static class Question {
        String questionId;
        String method;
        String questionText;

        Question(String questionId, String method, String questionText) {
            this.questionId = questionId;
            this.method = method;
            this.questionText = questionText;
        }
    }
}
