package com.example.gadgetguard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    private TextView welcomeText;
    private Button btnTrackUsage, btnPredictAddiction, btnChatbot, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        welcomeText = findViewById(R.id.welcomeText);
        btnTrackUsage = findViewById(R.id.btnTrackUsage);
        btnPredictAddiction = findViewById(R.id.btnPredictAddiction);
        btnChatbot = findViewById(R.id.btnChatbot);
        btnLogout = findViewById(R.id.logoutBtn);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // You can personalize the welcome message if desired
        welcomeText.setText("Welcome Back!");

        btnTrackUsage.setOnClickListener(view ->
                startActivity(new Intent(DashboardActivity.this, GadgetUsageActivity.class)));

        btnPredictAddiction.setOnClickListener(view ->
                startActivity(new Intent(DashboardActivity.this, PredictionActivity.class)));

        btnChatbot.setOnClickListener(view ->
                startActivity(new Intent(DashboardActivity.this, ChatBotActivity.class)));

        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(DashboardActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
