package com.ebusiness.ebiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Risk Manager 메인 액티비티 - 앱 진입점
 * 사용자를 Risk Manager 홈화면으로 안내하는 런처 화면
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView statusTextView;
    private Button homeButton;
    private TextView appTitle;
    private TextView versionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI 컴포넌트 초기화
        initializeViews();
        setupClickListeners();

        // 초기 상태 표시
        updateStatus("Risk Manager 시스템 준비 완료");

        Log.d(TAG, "Risk Manager MainActivity created successfully");
    }

    private void initializeViews() {
        statusTextView = findViewById(R.id.status_text);
        homeButton = findViewById(R.id.home_button);
        appTitle = findViewById(R.id.app_title);
        versionInfo = findViewById(R.id.version_info);

        Log.d(TAG, "UI components initialized");
    }

    private void setupClickListeners() {
        homeButton.setOnClickListener(v -> openRiskManagerHome());

        // 앱 타이틀 클릭 시 정보 표시
        appTitle.setOnClickListener(v -> showAppInfo());

        // 버전 정보 클릭 시 상세 정보 표시
        versionInfo.setOnClickListener(v -> showVersionDetails());
    }

    /**
     * Risk Manager 홈 화면 열기
     */
    private void openRiskManagerHome() {
        Log.d(TAG, "Opening Risk Manager HomeActivity");
        Toast.makeText(this, "Risk Manager를 시작합니다", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

        // 부드러운 전환 애니메이션 (선택사항)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * 앱 정보 표시
     */
    private void showAppInfo() {
        String appInfo = "🛡️ Risk Manager\n\n" +
                        "프로젝트 실패를 예방하는\n" +
                        "스마트 리스크 분석 도구\n\n" +
                        "• FMEA 방식 분석\n" +
                        "• AI 기반 조언\n" +
                        "• 상세 보고서 생성";

        Toast.makeText(this, appInfo, Toast.LENGTH_LONG).show();
        Log.d(TAG, "App info displayed");
    }

    /**
     * 버전 상세 정보 표시
     */
    private void showVersionDetails() {
        String versionDetails = "📱 Risk Manager v1.0.0\n\n" +
                               "빌드: 2024.12.03\n" +
                               "플랫폼: Android\n" +
                               "개발: E-Business Solutions\n\n" +
                               "프로젝트 성공률을 85% 향상시키는\n" +
                               "과학적 리스크 관리 솔루션";

        Toast.makeText(this, versionDetails, Toast.LENGTH_LONG).show();
        Log.d(TAG, "Version details displayed");
    }

    /**
     * 상태 업데이트
     */
    private void updateStatus(String status) {
        if (statusTextView != null) {
            statusTextView.setText("상태: " + status);
            Log.d(TAG, "Status updated: " + status);
        }
    }

    /**
     * 시스템 상태 검사
     */
    private void checkSystemStatus() {
        Log.d(TAG, "Checking system status...");

        // 간단한 시스템 체크 (실제 앱에서는 더 복잡한 검사 수행)
        boolean systemReady = true;

        if (systemReady) {
            updateStatus("모든 시스템 정상 작동 중");
            homeButton.setEnabled(true);
        } else {
            updateStatus("시스템 점검 중...");
            homeButton.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity resumed");

        // 화면 재활성화 시 시스템 상태 재확인
        checkSystemStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "MainActivity paused");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MainActivity destroyed");
    }


}
