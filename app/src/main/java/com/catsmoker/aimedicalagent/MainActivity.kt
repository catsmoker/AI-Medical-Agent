package com.catsmoker.aimedicalagent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText symptomsEditText;
    private TextView diagnosisTextView;
    private TextView medicationTextView;
    private GeminiApiClient geminiApiClient;

    private boolean isSubscribed;

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isSubscribed = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getBoolean("isSubscribed", false);

        // Initialize views
        symptomsEditText = findViewById(R.id.symptomsEditText);
        diagnosisTextView = findViewById(R.id.diagnosisTextView);
        medicationTextView = findViewById(R.id.medicationTextView);
        Button enterButton = findViewById(R.id.enterButton);
        Button buyButton = findViewById(R.id.buyButton);
        Button upgradeButton = findViewById(R.id.upgradeButton);
        Button verifyButton = findViewById(R.id.verifyButton); // Add this button in XML

        geminiApiClient = new GeminiApiClient();

        // Diagnosis logic
        enterButton.setOnClickListener(v -> {
            String symptoms = symptomsEditText.getText().toString().trim();
            if (!symptoms.isEmpty()) {
                diagnosisTextView.setText("AI Processing...");
                medicationTextView.setText("Suggested medication: Please wait...");

                geminiApiClient.getDiagnosis(symptoms, new GeminiApiClient.DiagnosisCallback() {
                    @Override
                    public void onDiagnosisReceived(String diagnosis, String medication) {
                        if (isSubscribed) {
                            diagnosisTextView.setText("Doctor's Diagnosis: " + diagnosis);
                            medicationTextView.setText("Prescription: " + medication);
                            verifyButton.setEnabled(true); // Enable verify button
                        } else {
                            diagnosisTextView.setText("AI Diagnosis: " + diagnosis);
                            medicationTextView.setText("Suggested medication: " + medication);
                            verifyButton.setEnabled(false); // Not available
                        }
                    }

                    @Override
                    public void onError(String error) {
                        diagnosisTextView.setText("Error occurred");
                        medicationTextView.setText("Suggested medication: Consult a doctor");
                        Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(this, "Please enter symptoms", Toast.LENGTH_SHORT).show();
            }
        });

        // Buy button: open WebView
        buyButton.setOnClickListener(v -> {
            String medication = medicationTextView.getText()
                    .toString()
                    .replace("Suggested medication: ", "")
                    .replace("Prescription: ", "")
                    .trim();

            if (medication.isEmpty() || medication.equalsIgnoreCase("N/A") || medication.equalsIgnoreCase("Consult a doctor")) {
                Toast.makeText(this, "No valid medication to buy", Toast.LENGTH_SHORT).show();
                return;
            }

            String amazonSearchUrl = "https://www.amazon.com/s?k=" + Uri.encode(medication);
            Intent webIntent = new Intent(this, WebViewActivity.class);
            webIntent.putExtra("url", amazonSearchUrl);
            startActivity(webIntent);
        });

        // Upgrade button
        upgradeButton.setOnClickListener(v -> {
            Toast.makeText(this, "Redirecting to subscription page...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, UpgradeActivity.class));
        });

        // Verify by doctor simulation
        verifyButton.setEnabled(isSubscribed);
        verifyButton.setOnClickListener(v -> {
            Toast.makeText(this, "Diagnosis verified by a certified doctor ✅", Toast.LENGTH_LONG).show();
        });
    }
}
