package com.example.zdrowiepp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;

public class SharePdfActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<File> pdfFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_pdf);

        listView = findViewById(R.id.pdfListView);
        pdfFiles = new ArrayList<>();

        File pdfDir = new File(getExternalFilesDir(null), "PDFs");
        if (pdfDir.exists()) {
            File[] files = pdfDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".pdf")) {
                        pdfFiles.add(file);
                    }
                }
            }
        }

        ArrayList<String> fileNames = new ArrayList<>();
        for (File file : pdfFiles) {
            fileNames.add(file.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> shareFile(pdfFiles.get(position)));
    }

    private void shareFile(File file) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent chooser = Intent.createChooser(intent, "Udostępnij plik PDF");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        } else {
            Toast.makeText(this, "Brak aplikacji do udostępniania plików", Toast.LENGTH_SHORT).show();
        }
    }
}
