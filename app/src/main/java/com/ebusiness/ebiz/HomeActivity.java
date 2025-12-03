package com.ebusiness.ebiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

/**
 * Risk Manager 홈 화면 Activity
 * HTML 디자인을 기반으로 구현된 Risk Manager 앱의 메인 홈 화면
 */
public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    // UI Components - CSS Design
    private TextView logoEmoji;
    private TextView mainTitle;
    private TextView subtitle;

    // Feature Cards (LinearLayout in CSS design)
    private LinearLayout featureCard1;
    private LinearLayout featureCard2;
    private LinearLayout featureCard3;

    // CTA Buttons
    private Button btnNewProject;
    private Button btnViewHistory;

    // Info Banner (LinearLayout in CSS design)
    private LinearLayout infoBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // UI 초기화
        initializeViews();
        setupClickListeners();

        Log.d(TAG, "Risk Manager HomeActivity created successfully");
    }

    /**
     * UI 컴포넌트 초기화
     */
    private void initializeViews() {
        // Hero Section - CSS Design
        logoEmoji = findViewById(R.id.logo_emoji);
        mainTitle = findViewById(R.id.main_title);
        subtitle = findViewById(R.id.subtitle);

        // Feature Cards - LinearLayout in CSS design
        featureCard1 = findViewById(R.id.feature_card_1);
        featureCard2 = findViewById(R.id.feature_card_2);
        featureCard3 = findViewById(R.id.feature_card_3);

        // CTA Buttons
        btnNewProject = findViewById(R.id.btn_new_project);
        btnViewHistory = findViewById(R.id.btn_view_history);

        // Info Banner - LinearLayout in CSS design
        infoBanner = findViewById(R.id.info_banner);

        Log.d(TAG, "All CSS-based UI components initialized");
    }

    /**
     * 클릭 리스너 설정
     */
    private void setupClickListeners() {
        // Feature Card 1: 최적 2개의 모델 제공
        featureCard1.setOnClickListener(v -> {
            Log.d(TAG, "Feature card 1 clicked: 최적 2개의 모델 제공");
            Toast.makeText(this, "FMEA 방식 분석 기능", Toast.LENGTH_SHORT).show();
            onFeatureCard1Clicked();
        });

        // Feature Card 2: AI 실행 조언
        featureCard2.setOnClickListener(v -> {
            Log.d(TAG, "Feature card 2 clicked: AI 실행 조언");
            Toast.makeText(this, "AI 기반 리스크 분석", Toast.LENGTH_SHORT).show();
            onFeatureCard2Clicked();
        });

        // Feature Card 3: 보고서 & 이력
        featureCard3.setOnClickListener(v -> {
            Log.d(TAG, "Feature card 3 clicked: 보고서 & 이력");
            Toast.makeText(this, "분석 결과 보고서", Toast.LENGTH_SHORT).show();
            onFeatureCard3Clicked();
        });

        // Primary Button: 새 프로젝트 분석하기
        btnNewProject.setOnClickListener(v -> {
            Log.d(TAG, "New project button clicked");
            Toast.makeText(this, "새 프로젝트 분석을 시작합니다", Toast.LENGTH_SHORT).show();
            onNewProjectClicked();
        });

        // Secondary Button: 분석 이력 보기
        btnViewHistory.setOnClickListener(v -> {
            Log.d(TAG, "View history button clicked");
            Toast.makeText(this, "분석 이력을 확인합니다", Toast.LENGTH_SHORT).show();
            onViewHistoryClicked();
        });

        // Info Banner
        infoBanner.setOnClickListener(v -> {
            Log.d(TAG, "Info banner clicked");
            Toast.makeText(this, "프로젝트 리스크에 대한 통계 정보", Toast.LENGTH_LONG).show();
            onInfoBannerClicked();
        });

        // Logo Emoji (CSS Design)
        logoEmoji.setOnClickListener(v -> {
            Log.d(TAG, "Logo emoji clicked");
            Toast.makeText(this, "📊 Risk Manager v1.0", Toast.LENGTH_SHORT).show();
            showAppInfo();
        });
    }

    /**
     * Feature Card 1 클릭 처리: FMEA 방식 분석
     */
    private void onFeatureCard1Clicked() {
        Log.d(TAG, "Navigating to FMEA analysis feature");

        // TODO: FMEA 분석 화면으로 이동
        // Intent intent = new Intent(this, FmeaAnalysisActivity.class);
        // startActivity(intent);

        showFeatureComingSoon("FMEA 방식 분석");
    }

    /**
     * Feature Card 2 클릭 처리: AI 실행 조언
     */
    private void onFeatureCard2Clicked() {
        Log.d(TAG, "Navigating to AI advisory feature");

        // TODO: AI 조언 화면으로 이동
        // Intent intent = new Intent(this, AiAdvisoryActivity.class);
        // startActivity(intent);

        showFeatureComingSoon("AI 실행 조언");
    }

    /**
     * Feature Card 3 클릭 처리: 보고서 & 이력
     */
    private void onFeatureCard3Clicked() {
        Log.d(TAG, "Navigating to reports and history");

        // TODO: 보고서 및 이력 화면으로 이동
        // Intent intent = new Intent(this, ReportsActivity.class);
        // startActivity(intent);

        showFeatureComingSoon("보고서 & 이력");
    }

    /**
     * 새 프로젝트 분석 버튼 클릭 처리
     */
    private void onNewProjectClicked() {
        Log.d(TAG, "Starting new project analysis");

        // 새 프로젝트 화면으로 이동
        Intent intent = new Intent(this, NewProjectActivity.class);
        startActivity(intent);

        // 부드러운 전환 애니메이션
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    /**
     * 분석 이력 보기 버튼 클릭 처리
     */
    private void onViewHistoryClicked() {
        Log.d(TAG, "Opening analysis history");

        // TODO: 분석 이력 화면으로 이동
        // Intent intent = new Intent(this, AnalysisHistoryActivity.class);
        // startActivity(intent);

        Toast.makeText(this, "분석 이력 기능 - 준비 중", Toast.LENGTH_LONG).show();
    }

    /**
     * 정보 배너 클릭 처리
     */
    private void onInfoBannerClicked() {
        Log.d(TAG, "Opening project statistics info");

        // 프로젝트 실패 통계에 대한 상세 정보 표시
        showStatisticsInfo();
    }

    /**
     * 기능 준비 중 메시지 표시
     */
    private void showFeatureComingSoon(String featureName) {
        String message = featureName + " 기능이 곧 출시됩니다.\n" +
                        "업데이트를 기다려주세요.";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 프로젝트 통계 정보 표시
     */
    private void showStatisticsInfo() {
        String statisticsInfo = "📊 프로젝트 리스크 통계\n\n" +
                               "• 70% 프로젝트 예산 초과\n" +
                               "• 평균 27% 일정 지연\n" +
                               "• 리스크 분석 시 성공률 85% 향상\n\n" +
                               "Risk Manager로 프로젝트를\n" +
                               "성공적으로 관리하세요!";

        Toast.makeText(this, statisticsInfo, Toast.LENGTH_LONG).show();
    }

    /**
     * 앱 정보 표시 (로고 클릭 시)
     */
    private void showAppInfo() {
        String appInfo = "Risk Manager v1.0\n\n" +
                        "실패를 전제로 한 프로젝트 분석으로\n" +
                        "비용 낭비를 줄이는 스마트 솔루션\n\n" +
                        "© 2024 E-Business Solutions";

        Toast.makeText(this, appInfo, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "HomeActivity resumed");

        // 화면 활성화 시 추가 작업이 필요한 경우 여기에 구현
        refreshHomeData();
    }

    /**
     * 홈 화면 데이터 새로고침
     */
    private void refreshHomeData() {
        Log.d(TAG, "Refreshing home screen data");

        // TODO: 실시간 데이터 업데이트가 필요한 경우 구현
        // 예: 최근 분석 결과, 알림 등
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "HomeActivity paused");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "HomeActivity destroyed");
    }
}
