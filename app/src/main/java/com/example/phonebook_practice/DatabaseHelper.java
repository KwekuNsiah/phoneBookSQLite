package com.example.phonebook_practice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "PhoneBook.db";
    public static final String TABLE = "PhoneBook";
    public static final String ID = "_id";
    public static final String CONTACT_NAME = "Name";
    public static final String CONTACT_NO = "Phone";
    public static final String CONTACT_EMAIL = "Email";

    public DatabaseHelper( Context context) {
        super(context, DATABASE_NAME, null, 11);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       db.execSQL(" create table " + TABLE
       + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT NOT NULL, Phone TEXT NOT NULL, Email TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public boolean addContact(String name, String phone, String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACT_NAME, name);
        contentValues.put(CONTACT_NO, phone);
        contentValues.put(CONTACT_EMAIL, email);
        long res =  db.insert(TABLE, null, contentValues);
        if (res == -1 ){
            return false;
        }else {
            return true;
        }
    }

    public Cursor getAllContacts(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE, null);
        return result;
    }

    public boolean updateContact(String id, String name, String phone, String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(CONTACT_NAME, name);
        contentValues.put(CONTACT_NO, phone);
        contentValues.put(CONTACT_EMAIL, email);
        db.update(TABLE, contentValues, " _id = ? ", new String[]{id});
        return true;
    }

    public Cursor getAAllContactById(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from " + TABLE + " where _id = ?";
        Cursor data = db.rawQuery(query, new String[] {id});
        return data;
    }

    public Integer deleteContact(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE, " _id = ?", new String[]{id});
    }

    public Cursor searchContact(String queryText){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[1];
        args[0] = "%"+queryText+"%";
        String query = "select * from PhoneBook where Name Like ? ";
        Cursor cursor = db.rawQuery(query,args);
        return cursor;
    }
}
