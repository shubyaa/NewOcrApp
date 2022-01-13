package com.example.newocrapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.BitmapCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.newocrapp.Database.DatabaseHelper;
import com.example.newocrapp.Model.Document;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FileActivity extends AppCompatActivity {
    private ImageView imageView;
    private ImageButton back, delete, edit_title;
    private Button copy, share, save;
    private EditText extracted_text, file_name;
    private String name, text, date;
    private float size;
    private Bitmap image;
    private Bitmap bitmap;
    private byte[] bytes;
    private String currentDateandTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        imageView = findViewById(R.id.image);
        back = findViewById(R.id.back);
        delete = findViewById(R.id.delete);
        copy = findViewById(R.id.copy_button);
        share = findViewById(R.id.share);
        extracted_text = findViewById(R.id.extracted_text);
        file_name = findViewById(R.id.file_name);
        save = findViewById(R.id.save);
        edit_title = findViewById(R.id.edit_Title);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        currentDateandTime = sdf.format(new Date());

        Document document = new Document(file_name.getText().toString(), currentDateandTime, 0, image, text);

        if (getIntent().hasExtra("name")) {
            name = getIntent().getStringExtra("name");
            bytes = getIntent().getByteArrayExtra("image");
            text = getIntent().getStringExtra("text");
            size = getIntent().getFloatExtra("size", 0);
            date = getIntent().getStringExtra("date");

            file_name.setText(name);

            image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            Glide.with(FileActivity.this).load(image)
                    .apply(new RequestOptions().override(250, 250))
                    .placeholder(R.drawable.common_google_signin_btn_icon_dark)
                    .centerCrop()
                    .into(imageView);

            extracted_text.setText(text);

            document.setName(name);
            document.setDate(date);
            document.setImage(bytes);
            document.setSize(size);
            document.setText(text);

            Log.e("error101", name + " " + text);
        } else {
            bytes = getIntent().getByteArrayExtra("image");
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);


            recognizeText(bitmap);

            document.setName(file_name.getText().toString());
            document.setDate(currentDateandTime);
            document.setSize(bytes.length);
            document.setText(extracted_text.getText().toString());

            document.setImage(bytes);
            Log.e("byteL", bitmap.toString());

            Glide.with(this).load(bitmap)
                    .apply(new RequestOptions().override(250, 250))
                    .centerCrop()
                    .into(imageView);

        }


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyText();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (extracted_text.getText().toString().equals("")) {
                    Toast.makeText(FileActivity.this, "It's empty", Toast.LENGTH_SHORT).show();
                } else {
                    shareText(extracted_text.getText().toString());
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                saveData(document);
            }
        });

        edit_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file_name.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(file_name, InputMethodManager.SHOW_IMPLICIT);

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                deleteData(document);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void copyText() {
        ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("data", extracted_text.getText().toString());

        manager.setPrimaryClip(data);
        if (manager.hasPrimaryClip()) {
            Toast.makeText(FileActivity.this, "Copied!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(FileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareText(String text) {
        Intent sendIntent = new Intent();
        sendIntent.setType("text/*");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);

        Intent shareIntent = Intent.createChooser(sendIntent, "Share this file using :-");
        startActivity(shareIntent);
    }

    private void deleteData(Document document){
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.alert_dialog, null, false);

        Button exit = view1.findViewById(R.id.exit);
        Button cancel = view1.findViewById(R.id.cancel);
        TextView dialog_text = view1.findViewById(R.id.delete_text);
        TextView dialog_title = view1.findViewById(R.id.delete_title);

        exit.setText("Yes");
        cancel.setText("No");
        dialog_text.setText("You sure you want to delete ?");
        dialog_title.setText("Delete ?");

        Dialog dialog = new Dialog(FileActivity.this);
        dialog.setContentView(view1);
        dialog.create();

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
                String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmap, "Title", null);

                if (getIntent().hasExtra("name")) {
                    if (helper.checkFile(document)) {
                        helper.deleteFile(document);

                        Intent intent = new Intent(FileActivity.this, HomeActivity.class);
                        startActivity(intent);

                        dialog.dismiss();
                        finish();

                    } else {
                        Toast.makeText(FileActivity.this, "File Does not exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    document.setName(file_name.getText().toString());
                    document.setDate(currentDateandTime);
                    document.setImage(bytes);
                    document.setSize((float) bytes.length);
                    document.setText(extracted_text.getText().toString());

                    if (helper.checkFile(document)) {
                        helper.deleteFile(document);

                        Intent intent = new Intent(FileActivity.this, HomeActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                        finish();
                    } else {
                        Toast.makeText(FileActivity.this, "File Does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }


        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void saveData(Document document){
        DatabaseHelper helper = new DatabaseHelper(FileActivity.this);

        if (file_name.getText().toString().equals("")) {
            Toast.makeText(FileActivity.this, "Please add title", Toast.LENGTH_SHORT).show();
        } else {
            if (getIntent().hasExtra("name")) {
                Log.i("open =>", "opened from list");

                if (helper.checkFile(document)){
                    Log.e("FileExists","Exits a file ?");

                    LayoutInflater inflater = LayoutInflater.from(FileActivity.this);
                    View view1 = inflater.inflate(R.layout.alert_dialog, null, false);

                    Button exit = view1.findViewById(R.id.exit);
                    Button cancel = view1.findViewById(R.id.cancel);
                    TextView dialog_text = view1.findViewById(R.id.delete_text);
                    TextView dialog_title = view1.findViewById(R.id.delete_title);

                    dialog_text.setText("This file already exists ! \n Update the file?");
                    dialog_title.setText("File Already exists !");
                    exit.setText("Yes");
                    cancel.setText("No");

                    Dialog dialog = new Dialog(FileActivity.this);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setContentView(view1);
                    dialog.create();

                    exit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            document.setText(extracted_text.getText().toString());
                            helper.updateFile(document);
                            dialog.dismiss();

                            Intent intent = new Intent(FileActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                }else {
                    String path = MediaStore.Images.Media.insertImage(FileActivity.this.getContentResolver(), bitmap, "Title", null);

                    document.setName(file_name.getText().toString());
                    document.setDate(currentDateandTime);
                    document.setImage(bytes);
                    document.setSize((float) bytes.length);
                    document.setText(extracted_text.getText().toString());

                    helper.addFile(document);
                }

            } else {

                document.setName(file_name.getText().toString());
                document.setDate(currentDateandTime);
                document.setImage(bytes);
                document.setSize((float) bytes.length);
                document.setText(extracted_text.getText().toString());

                if (helper.checkFile(document)){
                    Toast.makeText(FileActivity.this, "File Already exists !", Toast.LENGTH_SHORT).show();
                }else {
                    helper.addFile(document);
                }
            }
        }
    }

    private void recognizeText(Bitmap bitmap) {

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        recognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        String result = firebaseVisionText.getText();
                        extracted_text.setText(result);
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String result = "";
                        extracted_text.setText(result);
                    }
                });
    }
}