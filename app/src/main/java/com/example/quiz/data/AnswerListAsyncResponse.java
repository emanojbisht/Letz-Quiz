package com.example.quiz.data;

import com.example.quiz.model.Question;

import java.util.ArrayList;

public interface AnswerListAsyncResponse {

    void processFinished(ArrayList<Question> questionArrayList);

}
