package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quiz.Util.Prefs;
import com.example.quiz.data.AnswerListAsyncResponse;
import com.example.quiz.data.QuestionBank;
import com.example.quiz.model.Question;
import com.example.quiz.model.Score;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView questionTextView;
    private TextView questionCounterTextView;
    private Button trueButton;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private Button falseButton;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;
    private CardView cardView;
    private Score score;
    private TextView scoreTextView;
    private static final String MESSAGE_ID = "MESSAGE_PREF";
    private TextView highScoreTextView;
    private int scoreCounter = 0;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        questionTextView = findViewById(R.id.questionTextView);
        questionCounterTextView = findViewById(R.id.counterText);
        trueButton = findViewById(R.id.trueButton);
        nextButton = findViewById(R.id.nextButton);
        prevButton = findViewById(R.id.prevButton);
        falseButton = findViewById(R.id.falseButton);
        scoreTextView = findViewById(R.id.scoreTextView);
        highScoreTextView = findViewById(R.id.highScoreTextView);

        score = new Score();
        prefs = new Prefs(MainActivity.this);
        currentQuestionIndex = prefs.getState();

        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                questionTextView.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                questionCounterTextView.setText((currentQuestionIndex + 1)+"/"+questionList.size());
            }
        });

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        SharedPreferences getSharedData = getSharedPreferences(MESSAGE_ID,MODE_PRIVATE);
        String value = getSharedData.getString("score",""+score);
        highScoreTextView.setText("High Score : "+prefs.getHighScore());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.prevButton :
                    if(currentQuestionIndex == 0){
                        Toast.makeText(MainActivity.this, "First Question", Toast.LENGTH_SHORT).show();
                    }else{
                        currentQuestionIndex = (currentQuestionIndex - 1)% questionList.size();
                    }
                    updateQuestion();
                break;
            case R.id.nextButton:
                    currentQuestionIndex = (currentQuestionIndex + 1)% questionList.size();
                    updateQuestion();
                    prefs.saveHighScore(scoreCounter);

                break;
            case R.id.trueButton:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.falseButton:
                checkAnswer(false);
                updateQuestion();
                break;

        }
    }


    private void checkAnswer(boolean userChooseCorrect) {
        boolean answerIsTrue = questionList.get(currentQuestionIndex).isAnswerTrue();
        if(answerIsTrue == userChooseCorrect){
            fadeView();
            addPoints();
        }else{
            shakeAnimation();

            deductPoints();
        }
        scoreTextView.setText(String.valueOf("Current score : "+score.getScore()));

    }

    private void addPoints(){
        scoreCounter += 100;
        score.setScore(scoreCounter);
    }

    private void deductPoints(){
        scoreCounter -= 100;
        if (scoreCounter > 0){
            score.setScore(scoreCounter);
        }else{
            scoreCounter = 0;
            score.setScore(scoreCounter);
        }
    }

    private void updateQuestion() {
        questionCounterTextView.setText((currentQuestionIndex + 1)+"/"+questionList.size());
        questionTextView.setText(questionList.get(currentQuestionIndex).getAnswer());
    }

    private void fadeView(){
        cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0F,0.0F);

        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake_animation);
        cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
               goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onPause() {
        prefs.saveHighScore(score.getScore());
        prefs.setState(currentQuestionIndex);
        super.onPause();
    }

    private void goNext(){
        currentQuestionIndex = (currentQuestionIndex + 1)% questionList.size();
        updateQuestion();
    }
}
