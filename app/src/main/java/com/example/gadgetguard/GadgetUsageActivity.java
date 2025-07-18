package com.example.gadgetguard;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class GadgetUsageActivity extends AppCompatActivity {

    private EditText editTextGadget, editTextHours;
    private Button btnSavePredict;
    private RecyclerView usageRecyclerView;

    private FirebaseFirestore db;
    private String userId;

    private GadgetUsageAdapter adapter;
    private List<Object> displayItems = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gadget_usage);

        editTextGadget = findViewById(R.id.editTextGadget);
        editTextHours = findViewById(R.id.editTextHours);
        btnSavePredict = findViewById(R.id.btnSavePredict);
        usageRecyclerView = findViewById(R.id.recyclerViewUsage);
        usageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        btnSavePredict.setOnClickListener(v -> saveUsageEntry());

        loadUsageData();
    }

    private void saveUsageEntry() {
        String gadget = editTextGadget.getText().toString().trim();
        String time = editTextHours.getText().toString().trim();

        if (TextUtils.isEmpty(gadget) || TextUtils.isEmpty(time)) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        GadgetUsageEntry entry = new GadgetUsageEntry(gadget, time, currentDate);

        db.collection("GadgetUsage")
                .document(userId)
                .collection("entries")
                .add(entry)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Usage saved", Toast.LENGTH_SHORT).show();
                    editTextGadget.setText("");
                    editTextHours.setText("");
                    loadUsageData(); // Refresh RecyclerView
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error saving usage", Toast.LENGTH_SHORT).show());
    }

    private void loadUsageData() {
        db.collection("GadgetUsage")
                .document(userId)
                .collection("entries")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, List<GadgetUsageEntry>> grouped = new LinkedHashMap<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String app = doc.getString("app");
                        String time = doc.getString("time")+"hr";
                        String date = doc.getString("date");

                        GadgetUsageEntry entry = new GadgetUsageEntry(app, time, date);

                        if (!grouped.containsKey(date)) {
                            grouped.put(date, new ArrayList<>());
                        }
                        grouped.get(date).add(entry);
                    }

                    displayItems.clear();
                    for (String date : grouped.keySet()) {
                        displayItems.add(date);
                        displayItems.addAll(grouped.get(date));
                    }

                    adapter = new GadgetUsageAdapter(displayItems);
                    usageRecyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show());
    }
}
