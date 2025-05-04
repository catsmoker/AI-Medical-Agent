package com.catsmoker.aimedicalagent;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UpgradeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        Button subscribeButton = findViewById(R.id.subscribeButton);
        subscribeButton.setOnClickListener(v -> {
            // Save subscription status
            getSharedPreferences("app_prefs", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isSubscribed", true)
                    .apply();

            Toast.makeText(this, "Subscribed successfully!", Toast.LENGTH_LONG).show();
            finish(); // Return to MainActivity
        });
    }
}
