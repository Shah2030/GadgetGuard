package com.example.gadgetguard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class SplashActivity extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView icon = findViewById(R.id.appIcon);
        TextView name = findViewById(R.id.appName);
        TextView dev = findViewById(R.id.devName);

        executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(SplashActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                finish(); // Exit if cancelled or failed
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("GadgetGuard Login")
                .setSubtitle("Biometric authentication required")
                .setNegativeButtonText("Cancel")
                .build();

        new Handler().postDelayed(() -> {
            BiometricManager biometricManager = BiometricManager.from(this);
            if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                    == BiometricManager.BIOMETRIC_SUCCESS) {
                biometricPrompt.authenticate(promptInfo);
            } else {
                Toast.makeText(this, "Biometric not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, 1500);
    }
}
