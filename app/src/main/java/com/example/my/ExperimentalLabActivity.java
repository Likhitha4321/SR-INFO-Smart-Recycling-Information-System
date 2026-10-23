package com.example.my;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import java.util.Locale;

public class ExperimentalLabActivity extends AppCompatActivity {

    private String selectedObject = "Unknown Object";

    private LottieAnimationView lottieEcoBuddy;
    private TextView tvEcoBuddyMessage;

    private LottieAnimationView lottieExamRobot;
    private TextView tvExamMagnifier;

    private TextView tvObjectName;
    private TextView tvExaminationStatus;
    private TextView tvExaminationAnimation;

    private TextView tvResultTitle;
    private TextView tvResult;

    private TextToSpeech textToSpeech;
    private boolean isTtsReady = false;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private ObjectAnimator robotMoveAnimator;
    private ObjectAnimator magnifierMoveAnimator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_experimental_lab);

        initializeViews();
        receiveSelectedObject();
        prepareScreen();
        setupEcoBuddy();
        setupExamAnimation();
        setupBackHandling();
    }


    // ---------------------------------------------------------
    // INITIALIZE VIEWS
    // ---------------------------------------------------------

    private void initializeViews() {

        lottieEcoBuddy = findViewById(R.id.lottieEcoBuddy);
        tvEcoBuddyMessage = findViewById(R.id.tvEcoBuddyMessage);

        lottieExamRobot = findViewById(R.id.lottieExamRobot);
        tvExamMagnifier = findViewById(R.id.tvExamMagnifier);

        tvObjectName = findViewById(R.id.tvObjectName);
        tvExaminationStatus = findViewById(R.id.tvExaminationStatus);
        tvExaminationAnimation = findViewById(R.id.tvExaminationAnimation);

        tvResultTitle = findViewById(R.id.tvResultTitle);
        tvResult = findViewById(R.id.tvResult);
    }


    // ---------------------------------------------------------
    // RECEIVE SELECTED OBJECT
    // ---------------------------------------------------------

    private void receiveSelectedObject() {

        if (getIntent() != null &&
                getIntent().hasExtra("selected_object")) {

            selectedObject =
                    getIntent().getStringExtra("selected_object");

            if (selectedObject == null ||
                    selectedObject.trim().isEmpty()) {

                selectedObject = "Unknown Object";
            }
        }

        tvObjectName.setText(selectedObject);

        setObjectEmoji(selectedObject);
    }


    // ---------------------------------------------------------
    // OBJECT EMOJI
    // ---------------------------------------------------------

    private void setObjectEmoji(String object) {

        String emoji;

        switch (object) {

            case "Paper":
                emoji = "📄";
                break;

            case "Plastic Bottle":
                emoji = "🧴";
                break;

            case "Glass Bottle":
                emoji = "🍾";
                break;

            case "Battery":
                emoji = "🔋";
                break;

            case "Metal Can":
                emoji = "🥫";
                break;

            case "Plastic Bag":
                emoji = "🛍️";
                break;

            default:
                emoji = "♻️";
                break;
        }

        tvExaminationAnimation.setText(emoji);
    }


    // ---------------------------------------------------------
    // PREPARE SCREEN
    // ---------------------------------------------------------

    private void prepareScreen() {

        tvResultTitle.setVisibility(View.GONE);
        tvResult.setVisibility(View.GONE);

        tvExaminationStatus.setText(
                "Preparing scientific examination..."
        );

        tvEcoBuddyMessage.setText(
                "Hello! 🔬\n"
                        + "I will examine this object and identify "
                        + "its material, components and recycling properties."
        );
    }


    // ---------------------------------------------------------
    // ECO BUDDY
    // ---------------------------------------------------------

    private void setupEcoBuddy() {

        try {

            lottieEcoBuddy.setAnimation("robot_wave.json");

            lottieEcoBuddy.setRepeatCount(
                    LottieDrawable.INFINITE
            );

            lottieEcoBuddy.playAnimation();

        } catch (Exception e) {
            e.printStackTrace();
        }


        textToSpeech = new TextToSpeech(
                this,
                status -> {

                    if (status == TextToSpeech.SUCCESS) {

                        int result = textToSpeech.setLanguage(
                                Locale.US
                        );

                        isTtsReady =
                                result != TextToSpeech.LANG_MISSING_DATA
                                        &&
                                        result != TextToSpeech.LANG_NOT_SUPPORTED;

                        if (isTtsReady) {

                            textToSpeech.setSpeechRate(0.95f);

                            speak(
                                    "Hello. I will examine the "
                                            + selectedObject
                                            + " and identify its material properties."
                            );
                        }
                    }
                }
        );


        handler.postDelayed(
                this::startExamination,
                3000
        );
    }


    // ---------------------------------------------------------
    // EXAMINATION ROBOT ANIMATION
    // ---------------------------------------------------------

    private void setupExamAnimation() {

        try {

            lottieExamRobot.setAnimation(
                    "robot_wave.json"
            );

            lottieExamRobot.setRepeatCount(
                    LottieDrawable.INFINITE
            );

            lottieExamRobot.playAnimation();

        } catch (Exception e) {
            e.printStackTrace();
        }


        // Robot moves slightly toward the object

        robotMoveAnimator =
                ObjectAnimator.ofFloat(
                        lottieExamRobot,
                        View.TRANSLATION_X,
                        0f,
                        -18f,
                        0f,
                        18f,
                        0f
                );

        robotMoveAnimator.setDuration(3200);

        robotMoveAnimator.setRepeatCount(
                ObjectAnimator.INFINITE
        );

        robotMoveAnimator.setInterpolator(
                new AccelerateDecelerateInterpolator()
        );

        robotMoveAnimator.start();


        // Magnifying glass movement

        magnifierMoveAnimator =
                ObjectAnimator.ofFloat(
                        tvExamMagnifier,
                        View.TRANSLATION_X,
                        -12f,
                        12f,
                        -12f
                );

        magnifierMoveAnimator.setDuration(1800);

        magnifierMoveAnimator.setRepeatCount(
                ObjectAnimator.INFINITE
        );

        magnifierMoveAnimator.setInterpolator(
                new AccelerateDecelerateInterpolator()
        );

        magnifierMoveAnimator.start();
    }


    // ---------------------------------------------------------
    // START EXAMINATION
    // ---------------------------------------------------------

    private void startExamination() {

        tvExaminationStatus.setText(
                "🔬 Examination started..."
        );

        speak(
                "Examination started."
        );


        // STEP 2

        handler.postDelayed(
                () -> {

                    tvExaminationStatus.setText(
                            "🔍 Examining the object..."
                    );

                    speak(
                            "Examining the "
                                    + selectedObject
                                    + "."
                    );

                },
                3000
        );


        // STEP 3

        handler.postDelayed(
                () -> {

                    tvExaminationStatus.setText(
                            "🧪 Studying the material..."
                    );

                    speak(
                            "Studying material composition "
                                    + "and physical properties."
                    );

                },
                6000
        );


        // STEP 4

        handler.postDelayed(
                () -> {

                    tvExaminationStatus.setText(
                            "✓ Examination Complete"
                    );

                    displayExperimentResult();

                },
                9000
        );
    }


    // ---------------------------------------------------------
    // DISPLAY RESULT
    // ---------------------------------------------------------

    private void displayExperimentResult() {

        tvResultTitle.setVisibility(View.VISIBLE);
        tvResult.setVisibility(View.VISIBLE);

        tvResult.setText(
                getExperimentResult(selectedObject)
        );

        speak(
                getVoiceResult(selectedObject)
        );
    }


    // =========================================================
    // SCIENTIFIC INFORMATION FOR ALL OBJECTS
    // =========================================================

    private String getExperimentResult(String object) {

        switch (object) {


            // =================================================
            // PLASTIC BOTTLE
            // =================================================

            case "Plastic Bottle":

                return

                        "🧪 MATERIAL COMPOSITION\n\n"

                                + "Polymer: PET "
                                + "(Polyethylene Terephthalate)\n"
                                + "Carbon: ~62.5%\n"
                                + "Hydrogen: ~4.2%\n"
                                + "Oxygen: ~33.3%\n\n"


                                + "🔬 MAIN COMPONENTS\n\n"

                                + "• Bottle body → PET plastic\n"
                                + "• Cap → Usually PP or HDPE\n"
                                + "• Label → Paper or plastic film\n"
                                + "• Adhesive → Polymer-based adhesive\n\n"


                                + "⚛ PARTICLE / MOLECULAR LEVEL\n\n"

                                + "• Polymer chains\n"
                                + "• Carbon atoms\n"
                                + "• Hydrogen atoms\n"
                                + "• Oxygen atoms\n\n"


                                + "⚙ PHYSICAL PROPERTIES\n\n"

                                + "• Lightweight\n"
                                + "• Transparent / semi-transparent\n"
                                + "• Strong and flexible\n"
                                + "• Resistant to moisture\n\n"


                                + "♻ RECYCLING\n\n"

                                + "Commonly recyclable as PET (#1), "
                                + "depending on the local recycling system.";


            // =================================================
            // PAPER
            // =================================================

            case "Paper":

                return

                        "🧪 MATERIAL COMPOSITION\n\n"

                                + "Main polymer: Cellulose\n"
                                + "Primary source: Plant fibers\n"
                                + "Carbon: ~44%\n"
                                + "Hydrogen: ~6%\n"
                                + "Oxygen: ~50%\n\n"


                                + "🔬 MAIN COMPONENTS\n\n"

                                + "• Cellulose fibers\n"
                                + "• Hemicellulose\n"
                                + "• Lignin\n"
                                + "• Fillers and coatings may be present\n\n"


                                + "⚛ PARTICLE / MOLECULAR LEVEL\n\n"

                                + "• Cellulose polymer chains\n"
                                + "• Carbon atoms\n"
                                + "• Hydrogen atoms\n"
                                + "• Oxygen atoms\n\n"


                                + "⚙ PHYSICAL PROPERTIES\n\n"

                                + "• Lightweight\n"
                                + "• Flexible\n"
                                + "• Absorbent\n"
                                + "• Tearable\n\n"


                                + "♻ RECYCLING\n\n"

                                + "Usually recyclable when clean and dry. "
                                + "Recycling depends on local collection rules.";


            // =================================================
            // GLASS BOTTLE
            // =================================================

            case "Glass Bottle":

                return

                        "🧪 MATERIAL COMPOSITION\n\n"

                                + "Main material: Soda-lime glass\n"
                                + "Primary component: Silica (SiO₂)\n"
                                + "Silica: ~70–74%\n"
                                + "Sodium oxide: ~12–16%\n"
                                + "Calcium oxide: ~5–12%\n"
                                + "Other components: small amounts\n\n"


                                + "🔬 MAIN COMPONENTS\n\n"

                                + "• Silica\n"
                                + "• Sodium oxide\n"
                                + "• Calcium oxide\n"
                                + "• Small amounts of other compounds\n\n"


                                + "⚛ PARTICLE / MOLECULAR LEVEL\n\n"

                                + "• Silicon atoms\n"
                                + "• Oxygen atoms\n"
                                + "• Sodium ions\n"
                                + "• Calcium ions\n\n"


                                + "⚙ PHYSICAL PROPERTIES\n\n"

                                + "• Hard\n"
                                + "• Transparent or colored\n"
                                + "• Brittle\n"
                                + "• Resistant to moisture\n"
                                + "• Chemically stable\n\n"


                                + "♻ RECYCLING\n\n"

                                + "Glass bottles can often be recycled "
                                + "through glass collection systems.";


            // =================================================
            // BATTERY
            // =================================================

            case "Battery":

                return

                        "🧪 MATERIAL COMPOSITION\n\n"

                                + "Composition depends on battery type.\n\n"

                                + "For a common alkaline battery:\n"
                                + "• Zinc: ~20–30%\n"
                                + "• Manganese dioxide: ~30–40%\n"
                                + "• Electrolyte and other materials: remainder\n\n"

                                + "⚠ Exact composition varies by "
                                + "battery chemistry and manufacturer.\n\n"


                                + "🔬 MAIN COMPONENTS\n\n"

                                + "• Positive electrode\n"
                                + "• Negative electrode\n"
                                + "• Electrolyte\n"
                                + "• Separator\n"
                                + "• Metal casing\n\n"


                                + "⚛ PARTICLE / MOLECULAR LEVEL\n\n"

                                + "• Metal atoms and ions\n"
                                + "• Electrolyte ions\n"
                                + "• Electrode materials\n"
                                + "• Chemical compounds\n\n"


                                + "⚙ PHYSICAL PROPERTIES\n\n"

                                + "• Stores chemical energy\n"
                                + "• Converts chemical energy into electrical energy\n"
                                + "• Contains reactive materials\n"
                                + "• Different battery types have different chemistry\n\n"


                                + "♻ RECYCLING\n\n"

                                + "Do NOT place batteries in normal household "
                                + "recycling. Use an appropriate battery "
                                + "collection or recycling point.";


            // =================================================
            // METAL CAN
            // =================================================

            case "Metal Can":

                return

                        "🧪 MATERIAL COMPOSITION\n\n"

                                + "Main material: Metal\n"
                                + "Common types: Aluminium or steel\n\n"

                                + "For aluminium cans:\n"
                                + "• Aluminium: typically ~97–99%\n"
                                + "• Other metals and coating materials: remainder\n\n"

                                + "For steel cans:\n"
                                + "• Iron: majority of the metal\n"
                                + "• Carbon and other alloying elements: small amounts\n\n"

                                + "⚠ Exact composition depends on can type.\n\n"


                                + "🔬 MAIN COMPONENTS\n\n"

                                + "• Metal body\n"
                                + "• Protective coating\n"
                                + "• Inner lining\n"
                                + "• Printed label or coating\n\n"


                                + "⚛ PARTICLE / MOLECULAR LEVEL\n\n"

                                + "• Metal atoms\n"
                                + "• Alloying elements\n"
                                + "• Surface coating compounds\n\n"


                                + "⚙ PHYSICAL PROPERTIES\n\n"

                                + "• Strong\n"
                                + "• Lightweight for its strength\n"
                                + "• Conductive\n"
                                + "• Resistant to impact\n\n"


                                + "♻ RECYCLING\n\n"

                                + "Metal cans are commonly recyclable. "
                                + "Empty and rinse the can before recycling.";


            // =================================================
            // PLASTIC BAG
            // =================================================

            case "Plastic Bag":

                return

                        "🧪 MATERIAL COMPOSITION\n\n"

                                + "Main material: Plastic polymer\n"
                                + "Common polymers: HDPE or LDPE\n\n"

                                + "HDPE / LDPE are mainly made of:\n"
                                + "• Carbon: ~85.7%\n"
                                + "• Hydrogen: ~14.3%\n\n"

                                + "⚠ Actual bag composition may vary "
                                + "because additives and pigments can be present.\n\n"


                                + "🔬 MAIN COMPONENTS\n\n"

                                + "• Polymer film\n"
                                + "• Color pigments may be present\n"
                                + "• Additives\n"
                                + "• Printing or surface coatings may be present\n\n"


                                + "⚛ PARTICLE / MOLECULAR LEVEL\n\n"

                                + "• Polymer chains\n"
                                + "• Carbon atoms\n"
                                + "• Hydrogen atoms\n"
                                + "• Additive molecules\n\n"


                                + "⚙ PHYSICAL PROPERTIES\n\n"

                                + "• Lightweight\n"
                                + "• Flexible\n"
                                + "• Water resistant\n"
                                + "• Durable\n\n"


                                + "♻ RECYCLING\n\n"

                                + "Recycling depends on the local collection system. "
                                + "Reuse the bag when possible.";


            // =================================================
            // DEFAULT
            // =================================================

            default:

                return

                        "🧪 MATERIAL COMPOSITION\n\n"

                                + "Information not available.\n\n"

                                + "🔬 MAIN COMPONENTS\n\n"

                                + "Information not available.\n\n"

                                + "⚛ PARTICLE / MOLECULAR LEVEL\n\n"

                                + "Information not available.\n\n"

                                + "⚙ PHYSICAL PROPERTIES\n\n"

                                + "Information not available.\n\n"

                                + "♻ RECYCLING\n\n"

                                + "Information not available.";
        }
    }


    // =========================================================
    // VOICE RESULT
    // =========================================================

    private String getVoiceResult(String object) {

        switch (object) {

            case "Plastic Bottle":

                return "The bottle is mainly made from PET plastic. "
                        + "Its typical elemental composition is about "
                        + "sixty three percent carbon, "
                        + "four percent hydrogen and "
                        + "thirty three percent oxygen. "
                        + "It can commonly be recycled as PET number one.";

            case "Paper":

                return "Paper is mainly made from cellulose fibers. "
                        + "Cellulose contains approximately "
                        + "forty four percent carbon, "
                        + "six percent hydrogen and "
                        + "fifty percent oxygen by mass.";

            case "Glass Bottle":

                return "The bottle is mainly made from soda-lime glass. "
                        + "Silica is the major component, "
                        + "usually around seventy to seventy four percent. "
                        + "Glass bottles can often be recycled.";

            case "Battery":

                return "A battery contains electrodes, an electrolyte, "
                        + "a separator and a metal casing. "
                        + "Its exact composition depends on the battery type.";

            case "Metal Can":

                return "A metal can is commonly made from aluminium or steel. "
                        + "Aluminium cans are mostly aluminium, "
                        + "while steel cans are mainly iron with other elements.";

            case "Plastic Bag":

                return "A plastic bag is commonly made from HDPE or LDPE. "
                        + "These polymers contain approximately "
                        + "eighty six percent carbon and "
                        + "fourteen percent hydrogen by mass.";

            default:

                return "I could not find detailed information "
                        + "for this object.";
        }
    }


    // =========================================================
    // TEXT TO SPEECH
    // =========================================================

    private void speak(String message) {

        if (isTtsReady &&
                textToSpeech != null) {

            textToSpeech.speak(
                    message,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "SR_INFO_EXAMINATION"
            );
        }
    }


    // =========================================================
    // BACK BUTTON
    // =========================================================

    private void setupBackHandling() {

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        finish();
                    }
                }
        );
    }


    // =========================================================
    // CLEAN UP
    // =========================================================

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(null);


        if (robotMoveAnimator != null) {
            robotMoveAnimator.cancel();
        }


        if (magnifierMoveAnimator != null) {
            magnifierMoveAnimator.cancel();
        }


        if (lottieEcoBuddy != null) {
            lottieEcoBuddy.cancelAnimation();
        }


        if (lottieExamRobot != null) {
            lottieExamRobot.cancelAnimation();
        }


        if (textToSpeech != null) {

            textToSpeech.stop();
            textToSpeech.shutdown();
        }


        super.onDestroy();
    }
}