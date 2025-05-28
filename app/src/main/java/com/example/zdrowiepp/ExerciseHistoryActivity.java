package com.example.zdrowiepp;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExerciseHistoryActivity extends AppCompatActivity {

    private ListView historyListView;
    private ExerciseHistoryAdapter historyAdapter;

    private DatabaseHelper dbHelper;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_history);

        historyListView = findViewById(R.id.historyListView);

        historyListView = findViewById(R.id.historyListView);

        try (DatabaseHelper db = new DatabaseHelper(this)) {
            Date startOfWeek = getStartOfWeek();
            Date endOfWeek = getEndOfWeek();
            List<ExerciseHistoryItem> historyList = db.getExerciseHistoryBetween(startOfWeek, endOfWeek);

            if (historyList.isEmpty()) {
                Toast.makeText(this, "Brak wykonanych ćwiczeń w tym tygodniu", Toast.LENGTH_SHORT).show();
            }

            historyAdapter = new ExerciseHistoryAdapter(this, historyList);
            historyListView.setAdapter(historyAdapter);

            Button btnExportPdf = findViewById(R.id.btnExportPdf);

            dbHelper = new DatabaseHelper(this);
            userId = MyApp.getUserId(getApplicationContext());

            int[] last7DaysSteps = new int[7];
            List<StepEntry> entries = StepEntry.getLast7Days(dbHelper, userId);
            for (int i = 0; i < last7DaysSteps.length; i++) last7DaysSteps[i] = 0;
            int startIndex = last7DaysSteps.length - entries.size();
            for (int i = 0; i < entries.size(); i++) {
                last7DaysSteps[startIndex + i] = entries.get(entries.size() - 1 - i).getCount();
            }

            btnExportPdf.setOnClickListener(v -> {
                if (historyList != null && !historyList.isEmpty()) {
                    generatePdf(this,historyList, last7DaysSteps);
                } else {
                    Toast.makeText(this, "Brak danych do eksportu", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private Date getStartOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date getEndOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    private List<Bitmap> getLastPhotos(Context context, int limit) {
        List<Bitmap> photos = new ArrayList<>();
        String[] projection = {MediaStore.Images.Media._ID};

        String selection = MediaStore.Images.Media.RELATIVE_PATH + " LIKE ?";
        String[] selectionArgs = new String[]{"Pictures/ZdrowiePP/Raporty%"};

        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        )) {
            if (cursor != null) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int count = 0;

                while (cursor.moveToNext() && count < limit) {
                    long id = cursor.getLong(idColumn);
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                    if (bitmap != null) {
                        photos.add(bitmap);
                        count++;
                    } else {
                        Log.e("PDF", "Nie udało się wczytać bitmapy dla URI: " + imageUri.toString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return photos;
    }



    public void generatePdf(Context context, List<ExerciseHistoryItem> historyList, int[] stepsData) {
        Document document = new Document();

        try {
            File pdfDir = new File(context.getExternalFilesDir(null), "PDFs");
            if (!pdfDir.exists()) pdfDir.mkdir();

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File pdfFile = new File(pdfDir, "historia_cwiczen_" + timestamp + ".pdf");
            FileOutputStream fos = new FileOutputStream(pdfFile);
            PdfWriter.getInstance(document, fos);

            document.open();
            BaseFont baseFont = BaseFont.createFont("assets/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font entryFont = new Font(baseFont, 12, Font.NORMAL);

            document.add(new Paragraph("Historia ćwiczeń (bieżący tydzień)", titleFont));
            document.add(new Paragraph("\n"));

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

            for (ExerciseHistoryItem item : historyList) {
                String line = "- " + item.getExerciseName() +
                        "\n  Data: " + sdf.format(item.getDate()) +
                        "\n  Serie: " + item.getSets() +
                        ", Czas: " + item.getHours() + "h " + item.getMinutes() + "min\n";
                document.add(new Paragraph(line, entryFont));
                document.add(new Paragraph("\n"));
            }

            if (stepsData != null) {
                Bitmap chartBitmap = ChartHelper.createStepsChartBitmap(stepsData, context);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                chartBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image chartImage = Image.getInstance(stream.toByteArray());

                chartImage.scaleToFit(300, 300);
                document.add(chartImage);
            }
            document.newPage();
            document.add(new Paragraph("\nOstatnie zdjęcia:", titleFont));

            List<Bitmap> lastPhotos = getLastPhotos(context, 3);

            if(lastPhotos.isEmpty())
            Log.e("PDF","Brak zdjęć!");

            for (Bitmap bitmap : lastPhotos) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                Image image = Image.getInstance(stream.toByteArray());

                image.scaleToFit(300, 300);
                image.setSpacingBefore(10);
                try {
                    document.add(image);
                }
                catch (Exception e)
                {
                    Log.e("PDF","Błąd przy dodawaniu zdjęcia: " + e.getMessage());
                }
            }

            Toast.makeText(context, "PDF zapisany: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Błąd przy generowaniu PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            document.close();
        }
    }
}
