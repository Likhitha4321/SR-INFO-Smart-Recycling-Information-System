package com.example.my;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextView txtObject, txtInfo;
    private ImageView imgBuddy;
    private Button btnListenAgain;
    private TextToSpeech engineVoiceHardwareModule;
    private String vocalPayloadTextString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Map IDs to match activity_result.xml
        txtObject = findViewById(R.id.txtObject);
        txtInfo = findViewById(R.id.txtInfo);
        btnListenAgain = findViewById(R.id.btnListenAgain);
        imgBuddy = findViewById(R.id.imgBuddy);

        String activeItemIntentKey = getIntent().getStringExtra("PASSED_OBJECT_KEY");
        if (activeItemIntentKey == null) activeItemIntentKey = "Plastic Bottle";

        processEcoData(activeItemIntentKey);

        engineVoiceHardwareModule = new TextToSpeech(this, this);

        btnListenAgain.setOnClickListener(v -> speakContent());
    }

    private void processEcoData(String targetName) {
        txtObject.setText(targetName);

        // Logic based on your previous requirements
        String material, disposal, status;
        if (targetName.contains("Bottle")) {
            material = "PET Plastic (Type 1)";
            disposal = "Curbside Recycling Bins";
            status = "ACCEPTED UNCONDITIONALLY";
        } else {
            material = "Composite Organic/Cellulose Base";
            disposal = "Standard Drop-off Site Bin";
            status = "LOCAL VERIFICATION REQUIRED";
        }

        String displayContent = "Material: " + material + "\n\n" +
                "Disposal: " + disposal + "\n\n" +
                "Status: " + status;

        txtInfo.setText(displayContent);

        vocalPayloadTextString = "EcoBuddy update. Target match object details identified: " + targetName
                + ". Material background content is " + material
                + ". Action path: " + disposal;
    }

    private void speakContent() {
        if (engineVoiceHardwareModule != null) {
            engineVoiceHardwareModule.speak(vocalPayloadTextString, TextToSpeech.QUEUE_FLUSH, null, "UtteranceIdRef");
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            engineVoiceHardwareModule.setLanguage(Locale.US);
            speakContent();
        }
    }

    @Override
    protected void onDestroy() {
        if (engineVoiceHardwareModule != null) {
            engineVoiceHardwareModule.stop();
            engineVoiceHardwareModule.shutdown();
        }
        super.onDestroy();
    }
}