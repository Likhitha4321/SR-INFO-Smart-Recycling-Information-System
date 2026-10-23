package com.example.my;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etSearch;

    // Search icon only
    private ImageView btnSearch;

    private ImageView imgEcoBuddy;

    private LinearLayout layoutPlastic;
    private LinearLayout layoutPaper;
    private LinearLayout layoutMetal;
    private LinearLayout layoutGlass;
    private LinearLayout layoutBattery;

    private LinearLayout layoutGame;
    private LinearLayout layoutLab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        setupSearchButton();
        setupKeyboardSearch();
        setupEcoBuddy();
        setupPopularSearches();
        setupGame();
        setupRecyclingLab();

    }

    private void initializeViews() {

        etSearch = findViewById(R.id.etSearch);

        btnSearch = findViewById(R.id.btnSearch);

        imgEcoBuddy = findViewById(R.id.imgEcoBuddy);

        layoutPlastic = findViewById(R.id.layoutPlastic);
        layoutPaper = findViewById(R.id.layoutPaper);
        layoutMetal = findViewById(R.id.layoutMetal);
        layoutGlass = findViewById(R.id.layoutGlass);
        layoutBattery = findViewById(R.id.layoutBattery);

        layoutGame = findViewById(R.id.layoutGame);
        layoutLab = findViewById(R.id.layoutLab);

    }

    //===================================
    // Search Icon
    //===================================

    private void setupSearchButton() {

        btnSearch.setOnClickListener(v -> performSearch());

    }

    //===================================
    // Keyboard Search
    //===================================

    private void setupKeyboardSearch() {

        etSearch.setOnEditorActionListener((TextView v,
                                            int actionId,
                                            KeyEvent event) -> {

            boolean enterPressed =
                    event != null
                            && event.getAction() == KeyEvent.ACTION_DOWN
                            && event.getKeyCode() == KeyEvent.KEYCODE_ENTER;

            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || enterPressed) {

                performSearch();
                return true;

            }

            return false;

        });

    }
    //===================================
    // Perform Search
    //===================================

    private void performSearch() {

        String objectName = etSearch.getText().toString().trim();

        if (TextUtils.isEmpty(objectName)) {

            Toast.makeText(
                    this,
                    "Please enter an object",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Opens SearchActivity
        openSearchActivity(objectName);

    }

    //===================================
    // EcoBuddy
    //===================================

    private void setupEcoBuddy() {

        imgEcoBuddy.setOnClickListener(v ->

                Toast.makeText(
                        this,
                        "👋 Hi! I'm EcoBuddy.\nLet's recycle together!",
                        Toast.LENGTH_LONG
                ).show()

        );

    }

    //===================================
    // Popular Searches
    //===================================

    private void setupPopularSearches() {

        layoutPlastic.setOnClickListener(v ->
                openSearchActivity("Plastic Bottle"));

        layoutPaper.setOnClickListener(v ->
                openSearchActivity("Paper"));

        layoutMetal.setOnClickListener(v ->
                openSearchActivity("Aluminum Can"));

        layoutGlass.setOnClickListener(v ->
                openSearchActivity("Glass Bottle"));

        layoutBattery.setOnClickListener(v ->
                openSearchActivity("Battery"));

    }

    //===================================
    // Recycling Game
    //===================================

    private void setupGame() {

        layoutGame.setOnClickListener(v -> {

            Toast.makeText(this, "Game Clicked", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, EcoChallengeActivity.class);

            startActivity(intent);

        });

    }
    //===================================
    // Recycling Lab
    //===================================

    private void setupRecyclingLab() {

        layoutLab.setOnClickListener(v -> {

            // Safe launch intent
            Intent intent = new Intent(
                    MainActivity.this,
                    RecyclingLabActivity.class
            );

            startActivity(intent);

        });

    }

    //===================================
    // Open SearchActivity
    //===================================

    private void openSearchActivity(String objectName) {

        Intent intent = new Intent(
                MainActivity.this,
                SearchActivity.class
        );

        intent.putExtra("object_name", objectName);

        startActivity(intent);

    }

}