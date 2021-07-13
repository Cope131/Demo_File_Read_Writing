package com.myapplicationdev.android.demofilereadwriting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String DEBUG_TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 101;
    // File
    private String folderLocPath;
    private File dataFile;

    // Views
    private Button writeBtn, readBtn;
    private TextView contentTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        folderLocPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyFolder";
        initViews();
        askPermission();
    }

    private void initViews() {
        writeBtn = findViewById(R.id.write_button);
        readBtn = findViewById(R.id.read_button);
        contentTV = findViewById(R.id.content_text_view);
        writeBtn.setOnClickListener(this);
        readBtn.setOnClickListener(this);
    }

    private void askPermission() {
        if (!checkPermission() && android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        }
    }

    private boolean checkPermission() {
        int writeExtPermission
                = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return writeExtPermission == PermissionChecker.PERMISSION_GRANTED;
    }


    private void createDir() {
        Log.d(DEBUG_TAG, folderLocPath);
        File folder = new File(folderLocPath);
        if (!folder.exists()) {
            boolean isCreated = folder.mkdir();
            Log.d(DEBUG_TAG, isCreated ? "Folder Created" : "Folder Not Created");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.read_button:
                read();
                break;
            case R.id.write_button:
                write();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createDir();
                Toast.makeText(MainActivity.this, "MyFolder Created Successfully", Toast.LENGTH_SHORT).show();
            }  else {
                Toast.makeText(MainActivity.this, "Write External Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void read() {
        if (dataFile != null && dataFile.exists()) {
            StringBuilder sb = new StringBuilder();
            try {
                FileReader reader = new FileReader(dataFile);
                BufferedReader br = new BufferedReader(reader);

                String line = br.readLine();
                while (line != null) {
                    sb.append(line + "\n");
                    line = br.readLine();
                }
                br.close();
                reader.close();
                Log.d(DEBUG_TAG, "data.txt Content: " + sb.toString());
                contentTV.setText(sb.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void write() {
        dataFile = new File(folderLocPath, "data.txt");
        Log.d(DEBUG_TAG, folderLocPath + dataFile.getAbsolutePath());
        try {
            FileWriter writer = new FileWriter(dataFile, true);
            writer.write("Hello World" + "\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to write!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}