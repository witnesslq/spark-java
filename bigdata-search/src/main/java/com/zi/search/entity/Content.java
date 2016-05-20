package com.zi.search.entity;

/**
 * Created by twt on 2016/5/10.
 */
public class Content {
    private String question_body;
    private String question_body_html;
    private String answer_analysis;

    public String getQuestion_body() {
        return question_body;
    }

    public String getQuestion_body_html() {
        return question_body_html;
    }

    public String getAnswer_analysis() {
        return answer_analysis;
    }

    public void setQuestion_body(String question_body) {
        this.question_body = question_body;
    }

    public void setQuestion_body_html(String question_body_html) {
        this.question_body_html = question_body_html;
    }

    public void setAnswer_analysis(String answer_analysis) {
        this.answer_analysis = answer_analysis;
    }

    @Override
    public String toString() {
        return "Content{" +
                "question_body='" + question_body + '\'' +
                ", question_body_html='" + question_body_html + '\'' +
                ", answer_analysis='" + answer_analysis + '\'' +
                '}';
    }
}
