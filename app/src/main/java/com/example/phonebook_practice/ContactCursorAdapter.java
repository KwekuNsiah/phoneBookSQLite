package com.example.phonebook_practice;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ContactCursorAdapter extends CursorAdapter {
    private Context _context;

    public ContactCursorAdapter(Context context, Cursor c) {
        super(context, c);
        _context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(_context).inflate(R.layout.contact_view, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = view.findViewById(R.id.name_id);
        TextView phone = view.findViewById(R.id.phone_num_id);
        TextView email = view.findViewById(R.id.email_id);

        String Name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CONTACT_NAME));
        String Phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CONTACT_NO));
        String Email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CONTACT_EMAIL));

        name.setText(Name);
        phone.setText(Phone);
        email.setText(Email);
    }
}
