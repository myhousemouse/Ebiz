package com.ebusiness.ebiz;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 새 프로젝트 입력 화면
 * 사용자가 프로젝트 정보를 입력하여 리스크 분석을 시작하는 화면
 */
public class NewProjectActivity extends AppCompatActivity {
    private static final String TAG = "NewProjectActivity";

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

        // 프로젝트 정보를 LoadingActivity로 전달
        Intent intent = new Intent(this, LoadingActivity.class);
        intent.putExtra("project_title", title);
        intent.putExtra("project_description", description);
        intent.putExtra("project_budget", budget);

        Toast.makeText(this, "리스크 분석을 시작합니다", Toast.LENGTH_SHORT).show();

        startActivity(intent);

        // 부드러운 전환 애니메이션
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
