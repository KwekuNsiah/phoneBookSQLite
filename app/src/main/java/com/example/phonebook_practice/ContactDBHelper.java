package com.example.phonebook_practice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "PhoneBook.Db";
    public static final String TABLE_NAME  = "PhoneBook";
    public static final String CONTACT_ID = "_id";
    public static final String CONTACT_NAME = "Name";
    public static final String CONTACT_NO = "Phone";
    public static final String CONTACT_EMAIL = "Email";

    public ContactDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 11);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT NOT NULL, Phone Text NOT NULL, Email TEXT NOT NULL )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" drop table if exists " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String name, String phone, String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACT_NAME, name);
        contentValues.put(CONTACT_NO, phone);
        contentValues.put(CONTACT_EMAIL, email);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1){
            return false;
        }else {
            return true;
        }
    }

    public Cursor getAllContactas(){
        SQLiteDatabase db = this.getWritableDatabase();
       Cursor res =  db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

  public Cursor getAllContactsById(String id) {
      SQLiteDatabase db = this.getWritableDatabase();
      String sql = " select * from " + TABLE_NAME + " where " + CONTACT_ID + " = ? ";
      Cursor data = db.rawQuery(sql, new String[] {id});
      return data;
  }

  public void UpdateContacts(String id, String name, String phone, String email){
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();
      contentValues.put(CONTACT_ID, id);
      contentValues.put(CONTACT_NAME, name);
      contentValues.put(CONTACT_NO, phone);
      contentValues.put(CONTACT_EMAIL, email);
      db.update(TABLE_NAME, contentValues,  CONTACT_ID + " = ?", new String[]{id});
  }


}
