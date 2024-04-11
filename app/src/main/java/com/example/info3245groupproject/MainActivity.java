package  com.example.info3245groupproject;

import android.app.Dialog;
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

import com.example.info3245groupproject.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<String> listItems = new ArrayList<>();
    ListView files;
    ArrayAdapter<String> list;
    public File root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Dialog dialog = new Dialog(MainActivity.this);
        //default add button
        listItems.add("ADD NEW");

        files = findViewById(R.id.ListView1);
        list = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        files.setAdapter(list);
        files.setClickable(true);
        files.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFromList = files.getItemAtPosition(position).toString();
            if ("ADD NEW".equals(selectedFromList)) {
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
                        dialog.dismiss();
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid input. Please avoid special characters or leaving it blank.", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                // TODO Open next activity with file name
                Intent detailIntent = new Intent(MainActivity.this, Game1.class);
                detailIntent.putExtra("fileName", selectedFromList);
                startActivity(detailIntent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Thread getFiles = new Thread(GetFiles);
        getFiles.start();
    }

    private boolean CheckUserInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false; // Check for non-empty
        }
        // Check for invalid characters. Adjust the list according to your requirements.
        String invalidChars = "/\\:?\"<>|*";
        for (char c : invalidChars.toCharArray()) {
            if (input.indexOf(c) != -1) {
                return false;
            }
        }
        return true;
    }

    private Runnable GetFiles = new Runnable() {
        @Override
        public void run() {
            root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/StatCalcfiles");
            if (!root.exists()) {
                if (!root.mkdir()) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to make directory", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Directory made", Toast.LENGTH_SHORT).show());
                }
            }
            File[] filesArray = root.listFiles();
            if (filesArray != null) {
                for (File f : filesArray) {
                    String fileName = f.getName();
                    // Ensure you update the UI on the UI thread
                    runOnUiThread(() -> {
                        listItems.add(fileName);
                        list.notifyDataSetChanged();
                    });
                }
            }
        }
    };
}


