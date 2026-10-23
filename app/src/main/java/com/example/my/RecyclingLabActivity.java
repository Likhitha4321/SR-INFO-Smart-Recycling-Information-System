package com.example.my;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import java.util.Locale;

public class RecyclingLabActivity extends AppCompatActivity {

    // ===================================
    // EcoBuddy
    // ===================================

    private LottieAnimationView lottieEcoBuddy;
    private TextView tvEcoBuddyMessage;


    // ===================================
    // Six Object Icons
    // ===================================

    private ImageView imgPaper;
    private ImageView imgPlasticBottle;
    private ImageView imgGlass;
    private ImageView imgBattery;
    private ImageView imgMetal;
    private ImageView imgPlasticBag;


    // ===================================
    // Text To Speech
    // ===================================

    private TextToSpeech textToSpeech;
    private boolean isTtsReady = false;


    // ===================================
    // Selected Object
    // ===================================

    private String selectedObject = "";


    // ===================================
    // Activity Create
    // ===================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recycling_lab);

        initializeViews();

        setupEcoBuddy();

        setupObjects();

        setupBackHandling();
    }


    // ===================================
    // Initialize Views
    // ===================================

    private void initializeViews() {

        // -----------------------------------
        // EcoBuddy
        // -----------------------------------

        lottieEcoBuddy =
                findViewById(R.id.lottieEcoBuddy);

        tvEcoBuddyMessage =
                findViewById(R.id.tvEcoBuddyMessage);


        // -----------------------------------
        // Object Icons
        // -----------------------------------

        imgPaper =
                findViewById(R.id.imgPaper);

        imgPlasticBottle =
                findViewById(R.id.imgPlasticBottle);

        imgGlass =
                findViewById(R.id.imgGlass);

        imgBattery =
                findViewById(R.id.imgBattery);

        imgMetal =
                findViewById(R.id.imgMetal);

        imgPlasticBag =
                findViewById(R.id.imgPlasticBag);
    }


    // ===================================
    // Setup Object Clicks
    // ===================================

    private void setupObjects() {

        // -----------------------------------
        // Paper
        // -----------------------------------

        imgPaper.setOnClickListener(v ->
                openExperimentalLab("Paper"));


        // -----------------------------------
        // Plastic Bottle
        // -----------------------------------

        imgPlasticBottle.setOnClickListener(v ->
                openExperimentalLab("Plastic Bottle"));


        // -----------------------------------
        // Glass Bottle
        // -----------------------------------

        imgGlass.setOnClickListener(v ->
                openExperimentalLab("Glass Bottle"));


        // -----------------------------------
        // Battery
        // -----------------------------------

        imgBattery.setOnClickListener(v ->
                openExperimentalLab("Battery"));


        // -----------------------------------
        // Metal Can
        // -----------------------------------

        imgMetal.setOnClickListener(v ->
                openExperimentalLab("Metal Can"));


        // -----------------------------------
        // Plastic Bag
        // -----------------------------------

        imgPlasticBag.setOnClickListener(v ->
                openExperimentalLab("Plastic Bag"));
    }


    // ===================================
    // EcoBuddy Setup
    // ===================================

    private void setupEcoBuddy() {

        // -----------------------------------
        // Load Robot Animation
        // -----------------------------------

        lottieEcoBuddy.setAnimation("robot_wave.json");

        lottieEcoBuddy.setRepeatCount(
                LottieDrawable.INFINITE
        );

        lottieEcoBuddy.playAnimation();


        // -----------------------------------
        // Initialize Text To Speech
        // -----------------------------------

        textToSpeech = new TextToSpeech(
                this,
                status -> {

                    if (status == TextToSpeech.SUCCESS) {

                        int result =
                                textToSpeech.setLanguage(Locale.US);

                        if (result !=
                                TextToSpeech.LANG_MISSING_DATA
                                &&
                                result !=
                                        TextToSpeech.LANG_NOT_SUPPORTED) {

                            isTtsReady = true;

                            // Robot-like voice
                            textToSpeech.setPitch(0.85f);

                            textToSpeech.setSpeechRate(0.90f);


                            // Welcome message
                            showEcoBuddyMessage(
                                    "Hi! I'm EcoBuddy. " +
                                            "Welcome to the Recycling Lab!"
                            );
                        }
                    }
                }
        );
    }


    // ===================================
    // EcoBuddy Message
    // ===================================

    private void showEcoBuddyMessage(String message) {

        if (tvEcoBuddyMessage != null) {

            tvEcoBuddyMessage.setText(message);
        }

        speak(message);
    }


    // ===================================
    // EcoBuddy Voice
    // ===================================

    private void speak(String message) {

        if (!isTtsReady ||
                textToSpeech == null) {

            return;
        }

        textToSpeech.speak(
                message,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "EcoBuddySpeech"
        );
    }


    // ===================================
    // Open Experimental Lab
    // ===================================

    private void openExperimentalLab(
            String objectName) {

        selectedObject = objectName;


        // -----------------------------------
        // EcoBuddy Selection Message
        // -----------------------------------

        String message =
                "Sure! Let's examine this "
                        + objectName
                        + " and learn about it.";


        showEcoBuddyMessage(message);


        // -----------------------------------
        // Open Experimental Lab
        // -----------------------------------

        new Handler(
                Looper.getMainLooper()
        ).postDelayed(() -> {

            Intent intent = new Intent(
                    RecyclingLabActivity.this,
                    ExperimentalLabActivity.class
            );

            intent.putExtra(
                    "selected_object",
                    selectedObject
            );

            startActivity(intent);

        }, 2200);
    }


    // ===================================
    // Back Button Handling
    // ===================================

    private void setupBackHandling() {

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        showExitDialog();
                    }
                }
        );
    }


    // ===================================
    // Exit Confirmation
    // ===================================

    private void showExitDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Leave Recycling Lab?")
                .setMessage(
                        "Are you sure you want to leave " +
                                "the Recycling Lab?"
                )
                .setPositiveButton(
                        "Yes",
                        (dialog, which) -> finish()
                )
                .setNegativeButton(
                        "Stay",
                        null
                )
                .show();
    }


    // ===================================
    // Activity Destroy
    // ===================================

    @Override
    protected void onDestroy() {

        // Stop EcoBuddy animation
        if (lottieEcoBuddy != null) {

            lottieEcoBuddy.cancelAnimation();
        }


        // Stop and release TTS
        if (textToSpeech != null) {

            textToSpeech.stop();

            textToSpeech.shutdown();

            textToSpeech = null;
        }

        isTtsReady = false;

        super.onDestroy();
    }
}