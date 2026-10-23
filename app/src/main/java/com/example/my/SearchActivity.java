package com.example.my;

import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity
        implements TextToSpeech.OnInitListener {

    private ScrollView scrollView;

    private TextView resultTextView;

    private ImageView searchEcoBuddyAvatar;

    private ImageView btnReloadAudio;

    private TextToSpeech textToSpeech;

    private boolean isTTSReady = false;

    private String textToSpeak = "";

    private final Handler scrollHandler = new Handler();

    private boolean autoScrolling = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        scrollView = findViewById(R.id.scrollView);

        resultTextView = findViewById(R.id.resultTextView);

        searchEcoBuddyAvatar =
                findViewById(R.id.search_ecobuddy_avatar);

        btnReloadAudio =
                findViewById(R.id.btn_reload_audio);

        textToSpeech =
                new TextToSpeech(this, this);

        String keyword =
                getIntent().getStringExtra("object_name");

        if (keyword != null) {

            searchObject(keyword);

        } else {

            resultTextView.setText(
                    "No object selected."
            );

        }

        btnReloadAudio.setOnClickListener(v -> {

            if (isTTSReady &&
                    !textToSpeak.isEmpty()) {

                speak(textToSpeak);

            }

        });

    }

    /*--------------------------------------------------
            SEARCH OBJECT
     --------------------------------------------------*/

    private void searchObject(String keyword) {

        try {

            JSONArray objects =
                    new JSONArray(loadJSON());

            keyword =
                    keyword.toLowerCase().trim();

            StringBuilder displayBuilder =
                    new StringBuilder();

            StringBuilder speechBuilder =
                    new StringBuilder();

            boolean exactFound = false;

            boolean categoryFound = false;

            /*------------------------------------
                STEP 1
                Exact Object Search
             ------------------------------------*/

            for (int i = 0;
                 i < objects.length();
                 i++) {

                JSONObject item =
                        objects.getJSONObject(i);

                String name =
                        item.getString("name");

                if (name.equalsIgnoreCase(keyword)) {

                    exactFound = true;

                    appendObjectDetails(
                            displayBuilder,
                            item
                    );

                    speechBuilder.append(
                            buildSpeech(item)
                    );

                    break;

                }

            }

            if (exactFound) {

                resultTextView.setText(
                        displayBuilder.toString()
                );

                textToSpeak =
                        speechBuilder.toString();

                if (isTTSReady) {

                    speak(textToSpeak);

                }

                return;

            }

            /*------------------------------------
                STEP 2
                Category Search
             ------------------------------------*/

            for (int i = 0;
                 i < objects.length();
                 i++) {

                JSONObject item =
                        objects.getJSONObject(i);

                String category =
                        item.optString(
                                "category",
                                ""
                        );

                if (category.equalsIgnoreCase(keyword)) {

                    categoryFound = true;

                    appendObjectDetails(
                            displayBuilder,
                            item
                    );

                    speechBuilder.append(
                            buildSpeech(item)
                    );

                    speechBuilder.append(" ");

                }

            }

            if (categoryFound) {

                resultTextView.setText(
                        displayBuilder.toString()
                );

                textToSpeak =
                        speechBuilder.toString();

                if (isTTSReady) {

                    speak(textToSpeak);

                }

                return;

            }
                        /*------------------------------------
                STEP 3
                Keyword / Fuzzy Search
             ------------------------------------*/

            for (int i = 0;
                 i < objects.length();
                 i++) {

                JSONObject item =
                        objects.getJSONObject(i);

                String name =
                        item.getString("name");

                String category =
                        item.optString(
                                "category",
                                ""
                        );

                if (isSimilar(keyword, name)
                        || isSimilar(keyword, category)) {

                    appendObjectDetails(
                            displayBuilder,
                            item
                    );

                    speechBuilder.append(
                            buildSpeech(item)
                    );

                    speechBuilder.append(" ");

                    categoryFound = true;

                }

            }

            if (categoryFound) {

                resultTextView.setText(
                        displayBuilder.toString()
                );

                textToSpeak =
                        speechBuilder.toString();

                if (isTTSReady) {

                    speak(textToSpeak);

                }

            } else {

                resultTextView.setText(

                        "❌ Object not found.\n\n"

                                + "Try searching another object."

                );

            }

        } catch (Exception e) {

            e.printStackTrace();

            Toast.makeText(
                    this,
                    e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

        }

    }

    /*------------------------------------
        Display One Object
     ------------------------------------*/

    private void appendObjectDetails(
            StringBuilder builder,
            JSONObject item) throws Exception {

        String name =
                item.getString("name");

        boolean recyclable =
                item.getBoolean("recyclable");

        String recyclingRate =
                item.getString("recyclingRate");

        String description =
                item.getString("description");

        String howToRecycle =
                item.getString("howToRecycle");

        String creativeIdea =
                item.getString("creativeIdea");

        String interestingFact =
                item.getString("interestingFact");

        builder.append("🌍 ")
                .append(name)

                .append("\n\n")

                .append("♻ Recyclable : ")
                .append(recyclable ? "Yes" : "No")

                .append("\n\n")

                .append("📊 Recycling Rate : ")
                .append(recyclingRate)

                .append("\n\n")

                .append("📝 Description\n\n")
                .append(description)

                .append("\n\n")

                .append("♻ How To Recycle\n\n")
                .append(howToRecycle)

                .append("\n\n")

                .append("💡 Creative Idea\n\n")
                .append(creativeIdea)

                .append("\n\n")

                .append("🌟 Interesting Fact\n\n")
                .append(interestingFact)

                .append("\n\n")

                .append("--------------------------------------")

                .append("\n\n");

    }

    /*------------------------------------
        EcoBuddy Speech
     ------------------------------------*/

    private String buildSpeech(
            JSONObject item) throws Exception {

        return "Now let's learn about "
                + item.getString("name")
                + ". "
                + item.getString("description")
                + ". "
                + item.getString("howToRecycle")
                + ". "
                + item.getString("creativeIdea")
                + ". "
                + item.getString("interestingFact")
                + ". ";

    }
        /*------------------------------------
        Load JSON
     ------------------------------------*/

    private String loadJSON() {

        try {

            InputStream is =
                    getAssets().open("object.json");

            int size = is.available();

            byte[] buffer =
                    new byte[size];

            is.read(buffer);

            is.close();

            return new String(
                    buffer,
                    StandardCharsets.UTF_8
            );

        } catch (Exception e) {

            e.printStackTrace();

            return "";

        }

    }

    /*------------------------------------
        Fuzzy Search
     ------------------------------------*/

    private boolean isSimilar(
            String search,
            String objectName) {

        search =
                search.toLowerCase().trim();

        objectName =
                objectName.toLowerCase().trim();

        // Exact match
        if (objectName.equals(search))
            return true;

        // Contains
        if (objectName.contains(search))
            return true;

        if (search.contains(objectName))
            return true;

        // Word matching
        String[] words =
                objectName.split(" ");

        for (String word : words) {

            if (word.contains(search))
                return true;

            if (distance(search, word) <= 2)
                return true;

        }

        // Full string fuzzy
        return distance(search, objectName) <= 3;

    }

    /*------------------------------------
        Levenshtein Distance
     ------------------------------------*/

    private int distance(String a,
                         String b) {

        int[][] dp =
                new int[a.length() + 1][b.length() + 1];

        for (int i = 0;
             i <= a.length();
             i++)

            dp[i][0] = i;

        for (int j = 0;
             j <= b.length();
             j++)

            dp[0][j] = j;

        for (int i = 1;
             i <= a.length();
             i++) {

            for (int j = 1;
                 j <= b.length();
                 j++) {

                int cost =
                        (a.charAt(i - 1)
                                == b.charAt(j - 1))
                                ? 0 : 1;

                dp[i][j] = Math.min(

                        Math.min(
                                dp[i - 1][j] + 1,
                                dp[i][j - 1] + 1
                        ),

                        dp[i - 1][j - 1] + cost

                );

            }

        }

        return dp[a.length()][b.length()];

    }

    /*------------------------------------
        Auto Scroll
     ------------------------------------*/

    private final Runnable autoScrollRunnable =
            new Runnable() {

                @Override
                public void run() {

                    if (autoScrolling) {

                        scrollView.smoothScrollBy(
                                0,
                                4
                        );

                        scrollHandler.postDelayed(
                                this,
                                70
                        );

                    }

                }

            };

    private void startAutoScroll() {

        autoScrolling = true;

        scrollHandler.post(
                autoScrollRunnable
        );

    }

    private void stopAutoScroll() {

        autoScrolling = false;

        scrollHandler.removeCallbacks(
                autoScrollRunnable
        );

    }

    /*------------------------------------
        Speak
     ------------------------------------*/

    private void speak(String text) {

        if (!isTTSReady)
            return;

        textToSpeech.stop();

        textToSpeech.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "EcoBuddyVoice"
        );

    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result =
                    textToSpeech.setLanguage(
                            Locale.US
                    );

            if (result ==
                    TextToSpeech.LANG_MISSING_DATA
                    ||
                    result ==
                            TextToSpeech.LANG_NOT_SUPPORTED) {

                Toast.makeText(
                        this,
                        "English not supported.",
                        Toast.LENGTH_SHORT
                ).show();

                return;

            }

            isTTSReady = true;

            textToSpeech.setSpeechRate(0.90f);

            textToSpeech.setPitch(1.0f);

            textToSpeech.setOnUtteranceProgressListener(

                    new UtteranceProgressListener() {

                        @Override
                        public void onStart(String utteranceId) {

                            runOnUiThread(() ->
                                    startAutoScroll());

                        }

                        @Override
                        public void onDone(String utteranceId) {

                            runOnUiThread(() ->
                                    stopAutoScroll());

                        }

                        @Override
                        public void onError(String utteranceId) {

                            runOnUiThread(() ->
                                    stopAutoScroll());

                        }

                    });

            if (!textToSpeak.isEmpty()) {

                speak(textToSpeak);

            }

        }

    }

    @Override
    protected void onDestroy() {

        stopAutoScroll();

        if (textToSpeech != null) {

            textToSpeech.stop();

            textToSpeech.shutdown();

            textToSpeech = null;

        }

        super.onDestroy();

    }

}