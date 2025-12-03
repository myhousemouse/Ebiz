package com.ebusiness.ebiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 분석 중 로딩 화면
 * 프로젝트 정보를 분석하는 동안 보여지는 로딩 화면
 */
public class LoadingActivity extends AppCompatActivity {
    private static final String TAG = "LoadingActivity";

    // UI Components
    private ImageButton btnBack;
    private TextView projectNameDisplay; // some_id에 해당하는 TextView

    // Loading simulation
    private Handler loadingHandler;
    private Runnable loadingRunnable;
    private static final int LOADING_DURATION_MIN = 3000; // 3초
    private static final int LOADING_DURATION_MAX = 5000; // 5초

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        initializeViews();
        setupClickListeners();
        displayProjectInfo();
        startLoadingSimulation();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        projectNameDisplay = findViewById(R.id.some_id); // android:id="@+id/some_id"
    }

    private void setupClickListeners() {
        // 뒤로 가기 버튼
        btnBack.setOnClickListener(v -> {
            // 로딩 중단하고 이전 화면으로 돌아가기
            if (loadingHandler != null && loadingRunnable != null) {
                loadingHandler.removeCallbacks(loadingRunnable);
            }
            finish();
        });
    }

    private void displayProjectInfo() {
        // Intent에서 프로젝트 정보 가져오기
        Intent intent = getIntent();
        String projectTitle = intent.getStringExtra("project_title");

        // android:id="@+id/some_id"에 프로젝트명 표시
        if (projectTitle != null && !projectTitle.isEmpty()) {
            projectNameDisplay.setText("\"" + projectTitle + "\"");
        } else {
            projectNameDisplay.setText("\"프로젝트명\"");
        }
    }

    private void startLoadingSimulation() {
        // 3-5초 후 다음 화면으로 이동 (실제로는 API 호출 완료 시)
        int loadingDuration = LOADING_DURATION_MIN +
            (int) (Math.random() * (LOADING_DURATION_MAX - LOADING_DURATION_MIN));

        loadingHandler = new Handler();
        loadingRunnable = new Runnable() {
            @Override
            public void run() {
                onLoadingComplete();
            }
        };

        loadingHandler.postDelayed(loadingRunnable, loadingDuration);
    }

    private void onLoadingComplete() {
        // AI 질문 화면으로 이동
        Intent intent = new Intent(this, QuestionsActivity.class);

        // 프로젝트 정보 전달
        intent.putExtra("project_title", getIntent().getStringExtra("project_title"));
        intent.putExtra("project_description", getIntent().getStringExtra("project_description"));
        intent.putExtra("project_budget", getIntent().getStringExtra("project_budget"));

        startActivity(intent);

        // 부드러운 전환 애니메이션
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 핸들러 정리
        if (loadingHandler != null && loadingRunnable != null) {
            loadingHandler.removeCallbacks(loadingRunnable);
        }
    }
}
