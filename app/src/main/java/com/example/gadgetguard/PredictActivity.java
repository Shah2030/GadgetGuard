package com.example.gadgetguard;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class PredictActivity extends AppCompatActivity {

    private Interpreter tflite;
    private EditText inputHours, inputGadget;
    private TextView resultText;
    private Button btnPredict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict);

        inputHours = findViewById(R.id.inputHours);
        inputGadget = findViewById(R.id.inputGadget);
        resultText = findViewById(R.id.resultText);
        btnPredict = findViewById(R.id.btnPredict);

        try {
            tflite = new Interpreter(loadModelFile());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Model loading failed", Toast.LENGTH_LONG).show();
        }

        btnPredict.setOnClickListener(v -> {
            try {
                float hours = Float.parseFloat(inputHours.getText().toString());
                float gadgetTime = Float.parseFloat(inputGadget.getText().toString());

                float[][] input = new float[1][2]; // if model expects 2 inputs
                input[0][0] = hours;
                input[0][1] = gadgetTime;

                float[][] output = new float[1][1];

                tflite.run(input, output);

                resultText.setText(output[0][0] > 0.5 ? "Addicted: Yes" : "Addicted: No");

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Prediction error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd("gadget_addiction_model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}
