package com.example.newocrapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newocrapp.Adapters.ListAdapter;
import com.example.newocrapp.Database.DatabaseHelper;
import com.example.newocrapp.Model.Document;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EventListener;
import java.util.function.Predicate;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageButton help, search_button, add_folder, add, github_icon;
    private ArrayList<Document> arrayList;
    private android.widget.SearchView searchView;
    private TextView empty_array_alert, atoz, ztoa, bydate, bysize, last_scan;

    private int PICK_IMAGE = 11;
    private int OPEN_CAM = 12;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        permissionCheck(this, HomeActivity.this);

        recyclerView = findViewById(R.id.item_list);
        help = findViewById(R.id.help);
        add = findViewById(R.id.add);
        search_button = findViewById(R.id.search_button);
        searchView = findViewById(R.id.search_bar);
        empty_array_alert = findViewById(R.id.empty_list);
        add_folder = findViewById(R.id.add_folder);
        github_icon = findViewById(R.id.github_icon);

        atoz = findViewById(R.id.atoz);
        ztoa = findViewById(R.id.ztoa);
        bydate = findViewById(R.id.bydate);
        last_scan = findViewById(R.id.last_scan);
        bysize = findViewById(R.id.bysize);

        DatabaseHelper helper = new DatabaseHelper(this);
        arrayList = helper.showFiles();
        if (arrayList.isEmpty()) {
            Log.e("EmptyList", "Empty Array");
            recyclerView.setVisibility(View.GONE);
            empty_array_alert.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty_array_alert.setVisibility(View.GONE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        ListAdapter adapter = new ListAdapter(arrayList, HomeActivity.this);

        recyclerView.setAdapter(adapter);
        last_scan(adapter, arrayList);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextSubmit(String s) {
                try {
                    if (s.length() != 0) {
                        for (int i = 0; i < arrayList.size(); i++) {
                            if (arrayList.get(i).getName().toLowerCase().contains(s.toLowerCase())) {
                                Log.e("ELESearch", arrayList.get(i).getName());
                                arrayList.removeIf(new Predicate<Document>() {
                                    @Override
                                    public boolean test(Document document) {
                                        return !document.getName().toLowerCase().contains(s.toLowerCase());
                                    }
                                });
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(HomeActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {

                        arrayList = helper.showFiles();
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception ignored) {
                }

                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextChange(String s) {
                try {
                    if (s.length() != 0) {
                        for (int i = 0; i < arrayList.size(); i++) {
                            ArrayList<Document> arrayList1 = new ArrayList<>();

                            if (arrayList.get(i).getName().toLowerCase().contains(s.toLowerCase())) {
                                Document doc = arrayList.get(i);
                                arrayList1.add(doc);
                                adapter.animateTo(arrayList1);


                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(HomeActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {

                        arrayList = helper.showFiles();
                        adapter.animateTo(arrayList);
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception ignored) {
                }
                return false;
            }
        });

        last_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                last_scan(adapter, arrayList);
            }
        });

        atoz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort_ascending(adapter, arrayList);
            }
        });

        ztoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort_descending(adapter, arrayList);
            }
        });

        bysize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort_by_size(adapter, arrayList);
            }
        });

        bydate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort_by_date(adapter, arrayList);
            }
        });

        github_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.github.com/shubyaa"));
                startActivity(intent);
            }
        });

        add_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.help_popup, null, false);

                Dialog dialog = new Dialog(HomeActivity.this);
                dialog.setContentView(popupView);

                WindowManager.LayoutParams window = dialog.getWindow().getAttributes();
                window.gravity = Gravity.TOP | Gravity.END;
                window.x = 150;
                window.y = 110;

                dialog.show();
                dialog.setCanceledOnTouchOutside(true);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, OPEN_CAM);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == OPEN_CAM) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            Intent intent = new Intent(HomeActivity.this, FileActivity.class);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();
            intent.putExtra("image", bytes);
            startActivity(intent);
            finish();

        } else if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {

            Uri img = data.getData();
            try {
                Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), img);
                Intent intent = new Intent(HomeActivity.this, FileActivity.class);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();
                intent.putExtra("image", bytes);
                startActivity(intent);
                finish();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alert_dialog, null, false);

        Dialog dialog = new Dialog(this);
        dialog.setContentView(view);
        dialog.create();

        Button exit = view.findViewById(R.id.exit);
        Button cancel = view.findViewById(R.id.cancel);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void sort_by_date(ListAdapter adapter, ArrayList<Document> list) {
        Collections.sort(arrayList, new Comparator<Document>() {
            @Override
            public int compare(Document document, Document t1) {
                return document.getDate().compareTo(t1.getDate());
            }
        });
        adapter.animateTo(list);
        adapter.notifyDataSetChanged();
    }

    private void sort_by_size(ListAdapter adapter, ArrayList<Document> documents) {
        Collections.sort(arrayList, new Comparator<Document>() {
            @Override
            public int compare(Document document, Document t1) {
                return String.valueOf(document.getSize()).compareTo(String.valueOf(t1.getSize()));
            }
        });

        adapter.animateTo(documents);
        adapter.notifyDataSetChanged();
    }

    private void sort_ascending(ListAdapter adapter, ArrayList<Document> documents) {
        Collections.sort(arrayList, new Comparator<Document>() {
            @Override
            public int compare(Document document, Document t1) {
                return document.getName().compareTo(t1.getName());
            }
        });

        adapter.animateTo(documents);
        adapter.notifyDataSetChanged();
    }

    private void sort_descending(ListAdapter adapter, ArrayList<Document> documents) {
        Collections.sort(arrayList, new Comparator<Document>() {
            @Override
            public int compare(Document document, Document t1) {
                return t1.getName().compareTo(document.getName());
            }
        });

        adapter.animateTo(documents);
        adapter.notifyDataSetChanged();
    }

    private void last_scan(ListAdapter adapter, ArrayList<Document> documents) {
        Collections.sort(arrayList, new Comparator<Document>() {
            @Override
            public int compare(Document document, Document t1) {
                return t1.getDate().compareTo(document.getDate());
            }
        });

        adapter.animateTo(documents);
        adapter.notifyDataSetChanged();
    }


    private void permissionCheck(Context context, Activity activity) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

}