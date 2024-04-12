package com.example.info3245groupproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<String> listItems = new ArrayList<>();
    ListView files;
    ArrayAdapter<String> list;
    public File root;
    public Dialog dlgAddNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        dlgAddNew = new Dialog(MainActivity.this);
        listItems.add("ADD NEW");

        files = findViewById(R.id.ListView1);
        list = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        files.setAdapter(list);
        files.setClickable(true);
        files.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFromList = files.getItemAtPosition(position).toString();
            if ("ADD NEW".equals(selectedFromList)) {
                handleAddNewFile(dlgAddNew);
            } else {
                Intent detailIntent = new Intent(MainActivity.this, GameMenu.class);
                detailIntent.putExtra("fileName", selectedFromList);
                startActivity(detailIntent);
            }
        });

        files.setOnItemLongClickListener((parent, view, position, id) -> {
            if (position == 0) {
                Toast.makeText(MainActivity.this, "This item cannot be deleted.", Toast.LENGTH_SHORT).show();
                return true;
            }

            String selectedItem = listItems.get(position);
            showDeleteConfirmationDialog(selectedItem, position);
            return true;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Thread getFiles = new Thread(GetFiles);
        getFiles.start();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        dlgAddNew.dismiss();

    }

    private void handleAddNewFile(Dialog dialog) {
        dialog.setContentView(R.layout.add_file_popup_menu);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button create = dialog.findViewById(R.id.btnCreateNewFile);
        EditText txtInput = dialog.findViewById(R.id.editTextUserInputNewFileName);
        dialog.show();
        create.setOnClickListener(v -> {
            String input = txtInput.getText().toString();
            if (CheckUserInput(input)) {
                try {
                    File file = new File(root + "/" + input + ".txt");
                    if (file.createNewFile()) {
                        listItems.add(input);
                        list.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, input + " created.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "File already exists.", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error creating file.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Invalid input. Please avoid special characters or leaving it blank.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean CheckUserInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        String invalidChars = "/\\:?\"<>|*";
        for (char c : invalidChars.toCharArray()) {
            if (input.indexOf(c) != -1) {
                return false;
            }
        }
        return true;
    }

    private void showDeleteConfirmationDialog(String fileName, int position) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Delete File")
                .setMessage("Are you sure you want to delete this file: " + fileName + "?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    File file = new File(root + File.separator + fileName );
                    if (file.delete()) {
                        Toast.makeText(MainActivity.this, fileName + " deleted.", Toast.LENGTH_SHORT).show();
                        listItems.remove(position);
                        list.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MainActivity.this, "Error deleting file.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private Runnable GetFiles = () -> {
        root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/StatCalcfiles");
        if (!root.exists() && !root.mkdirs()) {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to make directory", Toast.LENGTH_SHORT).show());
        }
        File[] filesArray = root.listFiles();
        if (filesArray != null) {
            runOnUiThread(() -> listItems.clear());
            runOnUiThread(() -> listItems.add("ADD NEW"));
            for (File f : filesArray) {
                String fileName = f.getName();
                runOnUiThread(() -> {
                    listItems.add(fileName);
                    list.notifyDataSetChanged();
                });
            }
        }
    };
}
