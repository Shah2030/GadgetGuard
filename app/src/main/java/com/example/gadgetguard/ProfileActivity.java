package com.example.gadgetguard;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth auth;
    TableLayout gadgetTable;
    TextView userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        gadgetTable = findViewById(R.id.gadgetTable);
        userEmail = findViewById(R.id.userEmail);

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userEmail.setText("Logged in as: " + user.getEmail());
            loadUserGadgetData(user.getUid());
        }
    }

    private void loadUserGadgetData(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        List<Map<String, Object>> gadgetUsage = (List<Map<String, Object>>) document.get("gadgetUsage");
                        if (gadgetUsage != null) {
                            for (int i = 0; i < gadgetUsage.size(); i++) {
                                Map<String, Object> item = gadgetUsage.get(i);

                                TableRow row = new TableRow(this);
                                row.addView(makeCell(String.valueOf(i + 1)));
                                row.addView(makeCell(item.get("gadgetName").toString()));
                                row.addView(makeCell(item.get("hoursUsed").toString()));
                                row.addView(makeCell((Boolean) item.get("addicted") ? "Yes" : "No"));
                                gadgetTable.addView(row);
                            }
                        }
                    }
                });
    }

    private TextView makeCell(String text) {
        TextView cell = new TextView(this);
        cell.setText(text);
        cell.setPadding(8, 8, 8, 8);
        return cell;
    }
}
