package com.example.my;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class EcoChallengeActivity extends AppCompatActivity
        implements TextToSpeech.OnInitListener {

    //====================================================
    // Header
    //====================================================

    private TextView tvTitle;
    private TextView tvSubtitle;

    //====================================================
    // EcoBuddy
    //====================================================

    private LottieAnimationView lottieEcoBuddy;
    private TextView tvSpeech;

    //====================================================
    // Status
    //====================================================

    private TextView tvPoints;
    private TextView tvStreak;
    private TextView tvBadgeLevel;

    //====================================================
    // Progress
    //====================================================

    private ProgressBar progressBar;
    private TextView tvQuestionNumber;
    private TextView tvScore;

    //====================================================
    // Question
    //====================================================

    private TextView tvCategory;
    private TextView tvDifficulty;
    private TextView tvQuestion;

    //====================================================
    // Options
    //====================================================

    private RadioGroup radioGroupOptions;

    private RadioButton rbOption1;
    private RadioButton rbOption2;
    private RadioButton rbOption3;
    private RadioButton rbOption4;

    //====================================================
    // Result
    //====================================================

    private TextView tvResult;
    private CardView cardExplanation;
    private TextView tvExplanation;

    //====================================================
    // Buttons
    //====================================================

    private Button btnSubmit;
    private Button btnNext;
    private Button btnResumeChallenge;

    //====================================================
    // Tab Views (Bottom Navigation Containers)
    //====================================================

    private LinearLayout layoutQuizTab;
    private LinearLayout layoutMapTab;
    private ScrollView layoutBadgesTab;
    private ScrollView layoutProfileTab;
    private BottomNavigationView bottomNavigation;

    //====================================================
    // Variables
    //====================================================

    private TextToSpeech tts;

    private final ArrayList<Question> questionList =
            new ArrayList<>();

    private int currentQuestion = 0;

    private int score = 0;

    private int streak = 0;

    private int level = 1;

    private boolean answered = false;

    //====================================================
    // Question Model
    //====================================================

    public static class Question {

        String question;

        String[] options;

        int answer;

        String explanation;

        String category;

        String difficulty;

        String speech;

    }

    //====================================================
    // onCreate
    //====================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eco_challenge);

        Toast.makeText(this, "EcoChallenge Opened", Toast.LENGTH_LONG).show();

        initializeViews();

        setupBottomNavigation();

        tts = new TextToSpeech(this, this);

        loadQuestions();

        Collections.shuffle(questionList);

        updateStatus();

        showQuestion();

        setupButtons();

        // Back button callback
        getOnBackPressedDispatcher().addCallback(this,
                new androidx.activity.OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {

                        new AlertDialog.Builder(EcoChallengeActivity.this)
                                .setTitle("Exit Eco Challenge")
                                .setMessage("Are you sure you want to quit?")
                                .setPositiveButton("Yes", (dialog, which) -> finish())
                                .setNegativeButton("No", null)
                                .show();
                    }
                });

    }
    //====================================================
    // Initialize Views
    //====================================================

    private void initializeViews() {

        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);

        lottieEcoBuddy = findViewById(R.id.lottieEcoBuddy);
        tvSpeech = findViewById(R.id.tvSpeech);

        tvPoints = findViewById(R.id.tvPoints);
        tvStreak = findViewById(R.id.tvStreak);
        tvBadgeLevel = findViewById(R.id.tvBadgeLevel);

        progressBar = findViewById(R.id.progressBar);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvScore = findViewById(R.id.tvScore);

        tvCategory = findViewById(R.id.tvCategory);
        tvDifficulty = findViewById(R.id.tvDifficulty);
        tvQuestion = findViewById(R.id.tvQuestion);

        radioGroupOptions = findViewById(R.id.radioGroupOptions);

        rbOption1 = findViewById(R.id.rbOption1);
        rbOption2 = findViewById(R.id.rbOption2);
        rbOption3 = findViewById(R.id.rbOption3);
        rbOption4 = findViewById(R.id.rbOption4);

        tvResult = findViewById(R.id.tvResult);

        cardExplanation = findViewById(R.id.cardExplanation);
        tvExplanation = findViewById(R.id.tvExplanation);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnNext = findViewById(R.id.btnNext);
        btnResumeChallenge = findViewById(R.id.btnResumeChallenge);

        // Tab Containers
        layoutQuizTab = findViewById(R.id.layoutQuizTab);
        layoutMapTab = findViewById(R.id.layoutMapTab);
        layoutBadgesTab = findViewById(R.id.layoutBadgesTab);
        layoutProfileTab = findViewById(R.id.layoutProfileTab);
        bottomNavigation = findViewById(R.id.bottomNavigation);

    }

    //====================================================
    // Setup Bottom Navigation Logic
    //====================================================

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // Hide all tabs first
            layoutQuizTab.setVisibility(View.GONE);
            layoutMapTab.setVisibility(View.GONE);
            layoutBadgesTab.setVisibility(View.GONE);
            layoutProfileTab.setVisibility(View.GONE);

            // Show selected tab container
            if (id == R.id.nav_quiz) {
                layoutQuizTab.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.nav_map) {
                layoutMapTab.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.nav_badges) {
                layoutBadgesTab.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.nav_profile) {
                layoutProfileTab.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });

        // Quick action inside map tab to jump back to quiz
        if (btnResumeChallenge != null) {
            btnResumeChallenge.setOnClickListener(v -> {
                bottomNavigation.setSelectedItemId(R.id.nav_quiz);
            });
        }
    }

    //====================================================
    // Load Questions from JSON
    //====================================================

    private void loadQuestions() {

        questionList.clear();

        try {

            AssetManager assetManager = getAssets();

            InputStream inputStream = assetManager.open("eco_challenges.json");

            int size = inputStream.available();

            byte[] buffer = new byte[size];

            inputStream.read(buffer);

            inputStream.close();

            String json = new String(buffer, "UTF-8");

            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject object = jsonArray.getJSONObject(i);

                Question question = new Question();

                question.question = object.getString("question");

                JSONArray optionsArray = object.getJSONArray("options");

                question.options = new String[4];

                for (int j = 0; j < 4; j++) {

                    question.options[j] = optionsArray.getString(j);

                }

                question.answer = object.getInt("answer");

                question.category = object.getString("category");

                question.difficulty = object.getString("difficulty");

                question.explanation = object.getString("explanation");

                question.speech = object.getString("speech");

                questionList.add(question);

            }

        } catch (Exception e) {

            e.printStackTrace();

            Toast.makeText(this,
                    "Unable to load questions.",
                    Toast.LENGTH_LONG).show();

        }

    }

    //====================================================
    // Display Question
    //====================================================

    private void showQuestion() {

        if (questionList.isEmpty()) {

            Toast.makeText(this,
                    "No questions available.",
                    Toast.LENGTH_SHORT).show();

            return;

        }

        if (currentQuestion >= questionList.size()) {

            finishGame();

            return;

        }

        answered = false;

        Question q = questionList.get(currentQuestion);

        tvQuestion.setText(q.question);

        rbOption1.setText(q.options[0]);
        rbOption2.setText(q.options[1]);
        rbOption3.setText(q.options[2]);
        rbOption4.setText(q.options[3]);

        tvCategory.setText("♻ " + q.category);

        tvDifficulty.setText(q.difficulty);

        tvQuestionNumber.setText(
                "Challenge " +
                        (currentQuestion + 1) +
                        " / " +
                        questionList.size());

        progressBar.setMax(questionList.size());

        progressBar.setProgress(currentQuestion + 1);

        tvScore.setText(score + " pts");

        tvSpeech.setText(q.speech);

        radioGroupOptions.clearCheck();

        resetOptionColors();

        enableOptions(true);

        cardExplanation.setVisibility(View.GONE);

        tvResult.setVisibility(View.GONE);

        btnSubmit.setVisibility(View.VISIBLE);

        btnNext.setVisibility(View.GONE);

        speak(q.speech);

        animateBuddy();

    }

    //====================================================
    // Text To Speech
    //====================================================

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            tts.setLanguage(Locale.US);

            tts.setPitch(0.9f);

            tts.setSpeechRate(0.9f);

        }

    }

    //====================================================
    // Speak
    //====================================================

    private void speak(String text) {

        if (tts == null)
            return;

        tts.stop();

        tts.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "EcoBuddy");

    }

    //====================================================
    // EcoBuddy Animation
    //====================================================

    private void animateBuddy() {

        if (lottieEcoBuddy == null)
            return;

        lottieEcoBuddy.cancelAnimation();

        lottieEcoBuddy.playAnimation();

    }

    //====================================================
    // Reset Option Colors
    //====================================================

    private void resetOptionColors() {

        rbOption1.setBackgroundColor(Color.TRANSPARENT);
        rbOption2.setBackgroundColor(Color.TRANSPARENT);
        rbOption3.setBackgroundColor(Color.TRANSPARENT);
        rbOption4.setBackgroundColor(Color.TRANSPARENT);

    }

    //====================================================
    // Enable / Disable Options
    //====================================================

    private void enableOptions(boolean enable) {

        rbOption1.setEnabled(enable);
        rbOption2.setEnabled(enable);
        rbOption3.setEnabled(enable);
        rbOption4.setEnabled(enable);

    }
    //====================================================
    // Setup Button Clicks
    //====================================================

    private void setupButtons() {

        btnSubmit.setOnClickListener(v -> checkAnswer());

        btnNext.setOnClickListener(v -> {

            currentQuestion++;

            showQuestion();

        });

    }


    //====================================================
    // Check Answer
    //====================================================

    private void checkAnswer() {

        if (answered) {
            return;
        }

        int selectedId = radioGroupOptions.getCheckedRadioButtonId();

        if (selectedId == -1) {

            Toast.makeText(this,
                    "Please select an answer.",
                    Toast.LENGTH_SHORT).show();

            return;

        }

        answered = true;

        enableOptions(false);

        Question q = questionList.get(currentQuestion);

        int selectedAnswer = getSelectedAnswerIndex(selectedId);

        showAnswerColors(selectedAnswer, q.answer);

        cardExplanation.setVisibility(View.VISIBLE);
        tvExplanation.setText(q.explanation);

        tvResult.setVisibility(View.VISIBLE);

        if (selectedAnswer == q.answer) {

            score += 10;

            streak++;

            tvResult.setText("✅ Correct!");

            tvResult.setTextColor(Color.parseColor("#2E7D32"));

            tvSpeech.setText("🎉 Excellent! You got it right.");

            speak("Excellent! You got it right.");

        } else {

            streak = 0;

            tvResult.setText("❌ Wrong Answer");

            tvResult.setTextColor(Color.parseColor("#D32F2F"));

            tvSpeech.setText("😊 Nice try! Let's learn together.");

            speak("Nice try! Let's learn together.");

        }

        updateStatus();

        btnSubmit.setVisibility(View.GONE);

        btnNext.setVisibility(View.VISIBLE);

    }


    //====================================================
    // Get Selected Option Index
    //====================================================

    private int getSelectedAnswerIndex(int selectedId) {

        if (selectedId == R.id.rbOption1)
            return 0;

        if (selectedId == R.id.rbOption2)
            return 1;

        if (selectedId == R.id.rbOption3)
            return 2;

        return 3;

    }


    //====================================================
    // Highlight Answers
    //====================================================

    private void showAnswerColors(int selected, int correct) {

        resetOptionColors();

        RadioButton[] buttons = {

                rbOption1,
                rbOption2,
                rbOption3,
                rbOption4

        };

        // Correct answer - Green
        buttons[correct].setBackgroundColor(
                Color.parseColor("#C8E6C9"));

        // Wrong selected answer - Red
        if (selected != correct) {

            buttons[selected].setBackgroundColor(
                    Color.parseColor("#FFCDD2"));

        }

    }


    //====================================================
    // Update Score & Badge
    //====================================================

    private void updateStatus() {

        tvPoints.setText(String.valueOf(score));

        tvStreak.setText(String.valueOf(streak));

        tvScore.setText(score + " pts");

        level = (score / 50) + 1;

        String badge;

        if (level == 1) {

            badge = "🌱 Beginner";

        } else if (level == 2) {

            badge = "🌿 Eco Learner";

        } else if (level <= 4) {

            badge = "🌳 Eco Warrior";

        } else if (level <= 6) {

            badge = "🌎 Earth Guardian";

        } else {

            badge = "👑 Planet Protector";

        }

        tvBadgeLevel.setText(badge);

    }
    //====================================================
    // Finish Game
    //====================================================

    private void finishGame() {

        if (tts != null) {

            speak("Congratulations! You completed the Eco Challenge.");

        }

        int totalQuestions = questionList.size();

        int correctAnswers = score / 10;

        int accuracy = totalQuestions == 0 ? 0 :
                (correctAnswers * 100) / totalQuestions;

        String message =
                "🏆 Final Score : " + score + " Points\n\n" +
                        "✅ Correct Answers : " + correctAnswers + "/" + totalQuestions + "\n\n" +
                        "📊 Accuracy : " + accuracy + "%\n\n" +
                        "🔥 Best Streak : " + streak + "\n\n" +
                        "🌱 Badge : " + tvBadgeLevel.getText().toString();

        new AlertDialog.Builder(this)
                .setTitle("Eco Challenge Completed 🎉")
                .setMessage(message)

                .setCancelable(false)

                .setPositiveButton("Play Again", (dialog, which) -> {

                    restartGame();

                })

                .setNegativeButton("Exit", (dialog, which) -> {

                    finish();

                })

                .show();

    }

    //====================================================
    // Restart Game
    //====================================================

    private void restartGame() {

        currentQuestion = 0;

        score = 0;

        streak = 0;

        level = 1;

        answered = false;

        Collections.shuffle(questionList);

        updateStatus();

        showQuestion();

    }

    //====================================================
    // Release TTS
    //====================================================

    @Override
    protected void onDestroy() {

        if (tts != null) {

            tts.stop();

            tts.shutdown();

        }

        super.onDestroy();

    }
}