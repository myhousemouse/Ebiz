package com.ebusiness.ebiz;

import java.util.List;

/**
 * 질문 데이터 모델 클래스
 * 백엔드 API 응답에서 받아오는 질문 정보를 저장
 */
public class Question {
    public final String questionId;
    public final String method;
    public final String questionText;
    public final String questionType;
    public final List<String> choices;

    public Question(String questionId, String method, String questionText, String questionType, List<String> choices) {
        this.questionId = questionId;
        this.method = method;
        this.questionText = questionText;
        this.questionType = questionType;
        this.choices = choices;
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionId='" + questionId + '\'' +
                ", method='" + method + '\'' +
                ", questionText='" + questionText + '\'' +
                ", questionType='" + questionType + '\'' +
                ", choices=" + choices +
                '}';
    }
}
