package com.example.phonebook_practice;



import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private EditText editName, editPhone, editEmail;
    private Button btnSave, btnCancel;
    ContactDBHelper contactDBHelper;
    ContactCursorAdapter contactCursorAdapter;
    ListView listView;
    private MenuItem itemDelete, editItem;
    Menu menu;
    long selectid;









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            fab = findViewById(R.id.add_contact_id);
            contactDBHelper = new ContactDBHelper(MainActivity.this);
            listView = findViewById(R.id.my_list_view);
            final Cursor cs = contactDBHelper.getAllContactas();
            contactCursorAdapter = new ContactCursorAdapter(MainActivity.this, cs);
            listView.setAdapter(contactCursorAdapter);
           // itemDelete = findViewById(R.id.delete_menu);
           // editItem = findViewById(R.id.edit_menu);
            listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);


            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder  addContact = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    final View view1 = inflater.inflate(R.layout.add_new_contact_dialog, null, true);
                    editName = view1.findViewById(R.id.Name);
                    editPhone = view1.findViewById(R.id.phone);
                    editEmail = view1.findViewById(R.id.Email);
                    btnSave = view1.findViewById(R.id.save);
                    btnCancel = view1.findViewById(R.id.cancel);
                    addContact.setView(view1);
                    final AlertDialog d = addContact.create();
                    d.show();
                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String name = editName.getText().toString();
                            String phone = editPhone.getText().toString();
                            String email = editEmail.getText().toString();
                            if (name.isEmpty()){
                                editName.setError("Name cannot be empty");
                                return;
                            }
                            if (phone.isEmpty()){
                                editPhone.setError("phone cannot be empty");
                                return;
                            }
                            boolean result = contactDBHelper.insertData(name, phone, email);
                            if (result == true){
                                d.dismiss();
                                Cursor cs = contactDBHelper.getAllContactas();
                                contactCursorAdapter.changeCursor(cs);
                                Toast.makeText(MainActivity.this, "contact added to DB", Toast.LENGTH_SHORT).show();
                            }else {
                                d.dismiss();
                                Toast.makeText(MainActivity.this, "error adding data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            d.dismiss();
                        }
                    });
                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    itemDelete.setVisible(true);
                    editItem.setVisible(true);
                    selectid = id;
                    return true;
                }
            });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        itemDelete = menu.findItem(R.id.delete_menu);
        editItem = menu.findItem(R.id.edit_menu);

        itemDelete.setVisible(false);
        editItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_menu:
                AlertDialog.Builder editContacts = new AlertDialog.Builder(MainActivity.this);
                View editView = getLayoutInflater().inflate(R.layout.update_contact_dialogue, null, true);
                editContacts.setView(editView);
                final AlertDialog alertDialog = editContacts.create();
                Button update = editView.findViewById(R.id.update);
                Button cancel = editView.findViewById(R.id.cancel);
                final EditText name = editView.findViewById(R.id.Name);
                final  EditText phone = editView.findViewById(R.id.phone);
                final EditText email = editView.findViewById(R.id.Email);

                final String userId = String.valueOf(selectid);
                Cursor cs = contactDBHelper.getAllContactsById(userId);
                if (cs.getCount() >= 1){
                    cs.moveToFirst();
                    String _name = cs.getString(cs.getColumnIndexOrThrow(ContactDBHelper.CONTACT_NAME));
                    String _phone = cs.getString(cs.getColumnIndexOrThrow(ContactDBHelper.CONTACT_NO));
                    String _email = cs.getString(cs.getColumnIndexOrThrow(ContactDBHelper.CONTACT_EMAIL));

                    name.setText(_name);
                    phone.setText(_phone);
                    email.setText(_email);
                }
                cs.close();
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactDBHelper.UpdateContacts(userId, name.getText().toString(),
                                phone.getText().toString(), email.getText().toString());
                        Cursor cursor = contactDBHelper.getAllContactas();
                        contactCursorAdapter.changeCursor(cursor);
                        Toast.makeText(MainActivity.this, "Contacts Updated Succesfully", Toast.LENGTH_SHORT).show();
                        editItem.setVisible(false);
                        itemDelete.setVisible(false);
                        alertDialog.dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Update cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}