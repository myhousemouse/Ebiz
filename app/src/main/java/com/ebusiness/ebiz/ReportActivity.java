package com.ebusiness.ebiz;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 분석 결과 화면 (report.html 구현)
 * 백엔드 API 연동을 통한 리스크 분석 결과 표시
 */
public class ReportActivity extends AppCompatActivity {
    private static final String TAG = "ReportActivity";

    // UI Components - 백엔드 연동 매핑
    private TextView projectName;
    private TextView riskScore; // android:id="@+id/some_id" - overall_risk_score → RPN 위험도
    private ProgressBar riskChart; // android:id="@+id/svg" - 원형 차트
    private TextView severity; // severity → 심각도
    private TextView occurrence; // occurrence → 발생도
    private TextView detection; // detection → 검출도
    private TextView totalLoss; // total_expected_loss → 현금손실액 시뮬레이션 금액
    private TextView aiSummary; // ai_recommendations → AI 조언
    private LinearLayout actionSteps; // 전문가 매칭 실행 계획
    private ProgressBar progressTime, progressCapex, progressOpex;
    private Button saveButton, newAnalysisButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        initializeViews();
        setupClickListeners();
        loadAnalysisResult();
    }

    private void initializeViews() {
        projectName = findViewById(R.id.project_name);
        riskScore = findViewById(R.id.some_id); // RPN 위험도 점수
        riskChart = findViewById(R.id.svg); // 원형 차트
        severity = findViewById(R.id.severity);
        occurrence = findViewById(R.id.occurrence);
        detection = findViewById(R.id.detection);
        totalLoss = findViewById(R.id.total_loss);
        aiSummary = findViewById(R.id.ai_summary);
        actionSteps = findViewById(R.id.action_steps);

        progressTime = findViewById(R.id.progress_time);
        progressCapex = findViewById(R.id.progress_capex);
        progressOpex = findViewById(R.id.progress_opex);

        saveButton = findViewById(R.id.save_button);
        newAnalysisButton = findViewById(R.id.button);
    }

    private void setupClickListeners() {
        // 저장 버튼
        saveButton.setOnClickListener(v -> {
            Toast.makeText(this, "보고서 저장 기능이 곧 제공됩니다.", Toast.LENGTH_SHORT).show();
        });

        // 새로운 분석 시작 버튼
        newAnalysisButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // 외부 연계 카드는 레이아웃에서 직접 클릭 가능하도록 설정됨
    }

    private void loadAnalysisResult() {
        // 프로젝트 정보 가져오기
        Intent intent = getIntent();
        String projectTitle = intent.getStringExtra("project_title");
        if (projectTitle != null) {
            projectName.setText("\"" + projectTitle + "\"");
        }

        // TODO: 실제 구현에서는 백엔드 API 호출
        // String sessionId = intent.getStringExtra("session_id");
        // fetchAnalysisResultFromAPI(sessionId);

        // 현재는 Mock 데이터 사용
        useMockData();
    }

    private void useMockData() {
        // Mock 분석 결과 데이터 (실제로는 백엔드에서 받아올 데이터)
        MockAnalysisResult mockData = createMockData();
        renderAnalysisResult(mockData);
    }

    private MockAnalysisResult createMockData() {
        MockAnalysisResult data = new MockAnalysisResult();
        data.businessName = "헬스케어 앱 런칭";
        data.overallRiskScore = 42.5;
        data.severity = 5;
        data.occurrence = 6;
        data.detection = 7;
        data.totalExpectedLoss = 45000000;
        data.timeCost = 13500000;
        data.directInvestment = 11250000;
        data.personnelCost = 11250000;
        data.executiveSummary = "초기 자본과 기술 역량을 고려할 때 MVP를 빠르게 출시하되, AI 기능은 단계적으로 고도화하는 전략이 필요";

        // AI 추천사항
        data.aiRecommendations = new String[]{
            "1단계: 핵심 기능 MVP 개발 - 3개월 내 기본 다이어트 플래너 출시",
            "2단계: 사용자 피드백 수집 - 베타 테스터 100명 확보 및 개선",
            "3단계: AI 기능 고도화 - 맞춤형 추천 알고리즘 개발 착수"
        };

        return data;
    }

    private void renderAnalysisResult(MockAnalysisResult data) {
        // 1. RPN 위험도 표시 (android:id="@+id/some_id")
        int riskScoreInt = (int) data.overallRiskScore;
        riskScore.setText(String.valueOf(riskScoreInt));

        // 2. 원형 차트 애니메이션 (android:id="@+id/svg")
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(riskChart, "progress", 0, riskScoreInt);
        progressAnimator.setDuration(1500);
        progressAnimator.start();

        // 3. OSD 값 표시 - 백엔드 연동 매핑
        severity.setText(String.valueOf(data.severity)); // severity → 심각도
        occurrence.setText(String.valueOf(data.occurrence)); // occurrence → 발생도
        detection.setText(String.valueOf(data.detection)); // detection → 검출도

        // 4. 현금 손실액 표시 (total_expected_loss → 현금손실액 시뮬레이션 금액)
        totalLoss.setText("₩" + String.format("%,d", data.totalExpectedLoss));

        // 5. 비용 분류 프로그레스 바 애니메이션
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            int timePercent = (int) ((double) data.timeCost / data.totalExpectedLoss * 100);
            int capexPercent = (int) ((double) data.directInvestment / data.totalExpectedLoss * 100);
            int opexPercent = (int) ((double) data.personnelCost / data.totalExpectedLoss * 100);

            ObjectAnimator.ofInt(progressTime, "progress", 0, timePercent).setDuration(1000).start();
            ObjectAnimator.ofInt(progressCapex, "progress", 0, capexPercent).setDuration(1000).start();
            ObjectAnimator.ofInt(progressOpex, "progress", 0, opexPercent).setDuration(1000).start();
        }, 300);

        // 6. AI 전문가 조언 표시 (ai_recommendations → AI 조언)
        aiSummary.setText(data.executiveSummary);

        // 7. 실행 계획 단계 렌더링 (전문가 매칭)
        renderActionSteps(data.aiRecommendations);
    }

    private void renderActionSteps(String[] recommendations) {
        actionSteps.removeAllViews();

        for (int i = 0; i < recommendations.length; i++) {
            View stepView = createActionStepView(i + 1, recommendations[i]);
            actionSteps.addView(stepView);
        }
    }

    private View createActionStepView(int stepNumber, String recommendation) {
        View stepView = LayoutInflater.from(this).inflate(R.layout.action_step_item, actionSteps, false);

        TextView stepNumberView = stepView.findViewById(R.id.step_number);
        TextView stepTitle = stepView.findViewById(R.id.step_title);

        stepNumberView.setText(String.valueOf(stepNumber));
        stepTitle.setText(recommendation);

        return stepView;
    }

    // Mock 데이터 클래스
    private static class MockAnalysisResult {
        String businessName;
        double overallRiskScore;
        int severity, occurrence, detection;
        int totalExpectedLoss;
        int timeCost, directInvestment, personnelCost;
        String executiveSummary;
        String[] aiRecommendations;
    }
}
