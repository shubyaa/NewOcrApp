package com.example.newocrapp.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.newocrapp.Model.Document;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "my_ocr.db";
    private static final int DATABASE_VERSION = 1;

    private final String TABLE_NAME = "ScanImages";
    private final String COLUMN_ID = "_id";
    private final String COLUMN_TITLE = "document_name";
    private final String COLUMN_IMAGE = "image";
    private final String COLUMN_DATE = "date";
    private final String COLUMN_SIZE = "size";
    private final String COLUMN_TEXT = "text";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query =
                "CREATE TABLE " + TABLE_NAME + " ("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_TITLE + " TEXT, "
                        + COLUMN_DATE + " TEXT, "
                        + COLUMN_IMAGE + " TEXT, "
                        + COLUMN_SIZE + " REAL, "
                        + COLUMN_TEXT + " TEXT)";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void addFile(Document document) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_TITLE, document.getName());
        contentValues.put(COLUMN_DATE, document.getDate());
        contentValues.put(COLUMN_SIZE, document.getSize());
        contentValues.put(COLUMN_IMAGE, document.getImage());
        contentValues.put(COLUMN_TEXT, document.getText());

        long result = database.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
        }
        database.close();
    }

    public void deleteFile(Document document) {
        SQLiteDatabase database = this.getWritableDatabase();

        long result = database.delete(TABLE_NAME, "document_name=?", new String[]{document.getName()});

        if (result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
        }
        database.close();
    }

    public Document openFile(String name) {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = " + name;
        Cursor cursor = database.rawQuery(query, null);
        ArrayList<Document> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            Document document1 = new Document(cursor.getString(1), cursor.getString(2), cursor.getFloat(4), cursor.getBlob(3), cursor.getString(5));
            list.add(document1);
        }
        database.close();
        cursor.close();
        return list.get(0);
    }

    public ArrayList<Document> showFiles() {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);
        ArrayList<Document> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            Document document1 = new Document(cursor.getString(1), cursor.getString(2), cursor.getFloat(4), cursor.getBlob(3), cursor.getString(5));
            list.add(document1);
        }

        Log.i("ListA", String.valueOf(cursor.getCount()));
        cursor.close();
        database.close();
        return list;
    }

    public void updateFile(Document document) {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME +
                " SET " + COLUMN_DATE + " = '" + document.getDate() +"', "+
                COLUMN_TEXT + " = '" + document.getText() +"', "+
                COLUMN_IMAGE + " = '" + document.getImage() +"', "+
                COLUMN_SIZE + " = '" + document.getSize() +
                "' WHERE " + COLUMN_TITLE + " = '" + document.getName() + "'";

        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_TITLE, document.getName());
        contentValues.put(COLUMN_DATE, document.getDate());
        contentValues.put(COLUMN_SIZE, document.getSize());
        contentValues.put(COLUMN_IMAGE, document.getImage());
        contentValues.put(COLUMN_TEXT, document.getText());

        long result = database.update(TABLE_NAME, contentValues, COLUMN_TITLE+" = '"+document.getName()+"'", null);
        Log.e("textValues", document.getText());

        if (result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
        }
        database.close();
//        try {
//            database.execSQL(query);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        database.close();
    }

    public boolean checkFile(Document document) {
        boolean result = false;
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = \"" + document.getName() + "\"";
        Cursor cursor = database.rawQuery(query, null);
        ArrayList<Document> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            Document document1 = new Document(cursor.getString(1), cursor.getString(2), cursor.getFloat(4), cursor.getBlob(3), cursor.getString(5));
            list.add(document1);
        }
        if (list.isEmpty()){
            result = false;
        }else {
            result = list.get(0).getName().equals(document.getName());
            Log.e("checkFile", list.get(0).getName());
        }
        cursor.close();
        database.close();

        return result;
    }
}
