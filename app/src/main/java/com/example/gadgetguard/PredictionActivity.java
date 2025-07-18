package com.example.gadgetguard;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.io.OutputStream;
import java.util.*;

public class PredictionActivity extends AppCompatActivity {

    private EditText etGadget, etHours;
    private Button btnAdd, btnExportPDF;
    private TextView resultText;
    private TableLayout tableLayout;
    private PieChart pieChart;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;
    private List<PieEntry> pieEntries = new ArrayList<>();
    private List<Integer> pieColors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        etGadget = findViewById(R.id.etGadget);
        etHours = findViewById(R.id.etHours);
        btnAdd = findViewById(R.id.btnAdd);
        btnExportPDF = findViewById(R.id.btnExportPDF);
        resultText = findViewById(R.id.resultText);
        tableLayout = findViewById(R.id.tableLayout);
        pieChart = findViewById(R.id.pieChart);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        btnAdd.setOnClickListener(v -> addGadgetUsage());
        btnExportPDF.setOnClickListener(v -> exportToPDF());

        loadUsageData();
    }

    private void addGadgetUsage() {
        String gadget = etGadget.getText().toString().trim();
        String hoursStr = etHours.getText().toString().trim();

        if (gadget.isEmpty() || hoursStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        float hours = Float.parseFloat(hoursStr);
        String prediction = (hours >= 5) ? "Addicted" : "Healthy";

        Map<String, Object> data = new HashMap<>();
        data.put("gadget", gadget);
        data.put("hours", hours);
        data.put("result", prediction);

        db.collection("users").document(userId)
                .collection("Gadget_usage")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                    resultText.setText("Prediction: " + prediction);
                    etGadget.setText("");
                    etHours.setText("");
                    loadUsageData();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadUsageData() {
        db.collection("users").document(userId)
                .collection("Gadget_usage")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tableLayout.removeAllViews();
                    pieEntries.clear();
                    pieColors.clear();

                    addTableHeader();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String gadget = doc.getString("gadget");
                        double hours = doc.getDouble("hours");
                        String result = doc.getString("result");

                        addTableRow(gadget, hours, result);
                        pieEntries.add(new PieEntry((float) hours, gadget));
                        pieColors.add(ColorTemplate.COLORFUL_COLORS[pieEntries.size() % ColorTemplate.COLORFUL_COLORS.length]);
                    }

                    updatePieChart();
                });
    }

    private void addTableHeader() {
        TableRow header = new TableRow(this);
        addTextToRow(header, "Gadget");
        addTextToRow(header, "Hours");
        addTextToRow(header, "Result");
        tableLayout.addView(header);
    }

    private void addTableRow(String gadget, double hours, String result) {
        TableRow row = new TableRow(this);
        addTextToRow(row, gadget);
        addTextToRow(row, String.valueOf(hours));
        addTextToRow(row, result);
        tableLayout.addView(row);
    }

    private void addTextToRow(TableRow row, String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(8, 8, 8, 8);
        tv.setTextColor(Color.BLACK);
        row.addView(tv);
    }

    private void updatePieChart() {
        PieDataSet dataSet = new PieDataSet(pieEntries, "Usage per Gadget");
        dataSet.setColors(pieColors);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(30f);
        pieChart.setTransparentCircleRadius(35f);
        pieChart.setCenterText("Usage Chart");
        pieChart.setCenterTextSize(18f);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        pieChart.invalidate();
    }

    private void exportToPDF() {
        PdfDocument document = new PdfDocument();
        Paint paint = new Paint();
        Paint linePaint = new Paint();
        linePaint.setColor(Color.LTGRAY);
        linePaint.setStrokeWidth(1f);

        int pageWidth = 595;
        int pageHeight = 842;
        int xMargin = 40;
        int y = 100;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.apic);
        Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, 80, 80, false);
        canvas.drawBitmap(scaledLogo, pageWidth / 2f - 40, 20, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(20f);
        paint.setFakeBoldText(true);
        canvas.drawText("GadgetGuard - Usage Report", pageWidth / 2f, 120, paint);

        paint.setFakeBoldText(false);
        paint.setTextSize(14f);
        paint.setTextAlign(Paint.Align.LEFT);

        y = 150;
        canvas.drawLine(xMargin, y - 15, pageWidth - xMargin, y - 15, linePaint);
        canvas.drawText("Gadget", xMargin, y, paint);
        canvas.drawText("Hours", xMargin + 200, y, paint);
        canvas.drawText("Result", xMargin + 350, y, paint);
        y += 10;
        canvas.drawLine(xMargin, y, pageWidth - xMargin, y, linePaint);
        y += 20;

        for (int i = 1; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            String gadget = ((TextView) row.getChildAt(0)).getText().toString();
            String hours = ((TextView) row.getChildAt(1)).getText().toString();
            String result = ((TextView) row.getChildAt(2)).getText().toString();

            canvas.drawText(gadget, xMargin, y, paint);
            canvas.drawText(hours, xMargin + 200, y, paint);
            canvas.drawText(result, xMargin + 350, y, paint);
            y += 30;
            canvas.drawLine(xMargin, y - 10, pageWidth - xMargin, y - 10, linePaint);
        }

        paint.setTextSize(12f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Generated by GadgetGuard | Dev: $ }-{ @ }-{", pageWidth / 2f, pageHeight - 50, paint);
        canvas.drawText("Date: " + new Date().toString(), pageWidth / 2f, pageHeight - 30, paint);

        document.finishPage(page);

        String fileName = "GadgetUsageReport_" + System.currentTimeMillis() + ".pdf";
        ContentResolver resolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
        contentValues.put(MediaStore.Downloads.IS_PENDING, 1);

        Uri collection = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        }
        Uri pdfUri = resolver.insert(collection, contentValues);

        try {
            OutputStream os = resolver.openOutputStream(pdfUri);
            document.writeTo(os);
            os.close();

            contentValues.clear();
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0);
            resolver.update(pdfUri, contentValues, null, null);

            Toast.makeText(this, "PDF exported to Downloads!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        document.close();
    }
}
