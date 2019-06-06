package com.example.phonebook_practice;




import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    DatabaseHelper myDb;
    EditText editName, editNum, editEmail;
    Button btnSave, btnCancel;
    FloatingActionButton floatAct;
    ContactCursorAdapter mCursorAdapter;
    ListView mListView;
    private MenuItem itemDelete, itemEdit;
    private SearchView editSearch;
    private FrameLayout noData;
    long selectid;
    NoContact ns;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        editSearch = findViewById(R.id.search);
        editSearch.setOnQueryTextListener(this);
        myDb = new DatabaseHelper(MainActivity.this);
        floatAct = findViewById(R.id.add_contact_id);
        mListView = findViewById(R.id.my_list_view);
        final Cursor cursor = myDb.getAllContacts();
        mCursorAdapter = new ContactCursorAdapter(MainActivity.this, cursor);
        mListView.setAdapter(mCursorAdapter);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        noData = findViewById(R.id.noDataFrame);
        ns = new NoContact();

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                itemDelete.setVisible(true);
                itemEdit.setVisible(true);
                selectid = id;
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();

                String ID = String.valueOf(id);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                TextView phone = view.findViewById(R.id.phone_num_id);
                intent.setData(Uri.parse("tel:" + phone.getText().toString()));
                startActivity(intent);
            }
        });

        floatAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder addNewContact = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View view1 = inflater.inflate(R.layout.add_new_contact_dialog, null, true);
                editName = view1.findViewById(R.id.Name);
                editNum = view1.findViewById(R.id.phone);
                editEmail = view1.findViewById(R.id.Email);
                btnSave = view1.findViewById(R.id.save);
                btnCancel = view1.findViewById(R.id.cancel);
                addNewContact.setView(view1);
                final AlertDialog d = addNewContact.create();
                d.show();

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = editName.getText().toString();
                        String phone = editNum.getText().toString();
                        String email = editEmail.getText().toString();

                        if (name.isEmpty()) {
                            editName.setError("name cannot be empty");
                            return;
                        }
                        if (phone.isEmpty()) {
                            editNum.setError("phone cannot be empty");
                            return;
                        }

                        boolean result = myDb.addContact(name, phone, email);
                        if (result == true) {
                            d.dismiss();
                            Cursor cs = myDb.getAllContacts();
                            mCursorAdapter.changeCursor(cs);
                            CheckData();
                            Toast.makeText(MainActivity.this, "Contact added to DB", Toast.LENGTH_SHORT).show();
                        } else {
                            d.dismiss();
                            Toast.makeText(MainActivity.this, "Contact not added DB", Toast.LENGTH_SHORT).show();
                            CheckData();
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        itemDelete = menu.findItem(R.id.delete_menu);
        itemEdit = menu.findItem(R.id.edit_menu);

        itemDelete.setVisible(false);
        itemEdit.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.delete_menu:
                AlertDialog.Builder dialogue = new AlertDialog.Builder(MainActivity.this);
                dialogue.setMessage("Are you sure you want to delete ? ");
                dialogue.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedContact = String.valueOf(selectid);
                        myDb.deleteContact(selectedContact);
                        Cursor cs = myDb.getAllContacts();
                        mCursorAdapter.changeCursor(cs);
                        CheckData();
                        Toast.makeText(MainActivity.this, "Deleting", Toast.LENGTH_SHORT).show();
                        itemDelete.setVisible(false);
                        itemEdit.setVisible(false);
                    }
                });
                dialogue.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                dialogue.show();
                return true;

            case  R.id.edit_menu:
                final AlertDialog.Builder editDialogue = new AlertDialog.Builder(MainActivity.this);
                View editView = getLayoutInflater().inflate(R.layout.update_contact_dialogue, null);
                editDialogue.setView(editView);
                final AlertDialog ad = editDialogue.create();
                Button update = editView.findViewById(R.id.update);
                Button cancel = editView.findViewById(R.id.cancel);
                final EditText name = editView.findViewById(R.id.Name);
                final EditText phone = editView.findViewById(R.id.phone);
                final EditText email = editView.findViewById(R.id.Email);

                final String userId = String.valueOf(selectid);
                Cursor cs = myDb.getAAllContactById(userId);
                if (cs.getCount()>=1){
                    cs.moveToFirst();
                    String _name = cs.getString(cs.getColumnIndexOrThrow(DatabaseHelper.CONTACT_NAME));
                    String _phone = cs.getString(cs.getColumnIndexOrThrow(DatabaseHelper.CONTACT_NO));
                    String _email = cs.getString(cs.getColumnIndexOrThrow(DatabaseHelper.CONTACT_EMAIL));

                    name.setText(_name);
                    phone.setText(_phone);
                    email.setText(_email);
                }
                cs.close();
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDb.updateContact(userId, name.getText().toString(), phone.getText().toString(),
                                email.getText().toString());
                        Cursor editCursor = myDb.getAllContacts();
                        mCursorAdapter.changeCursor(editCursor);
                        CheckData();
                        Toast.makeText(MainActivity.this, "Contact Updated Successfully", Toast.LENGTH_SHORT).show();
                        itemDelete.setVisible(false);
                        itemEdit.setVisible(false);
                        ad.dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ad.dismiss();
                        Toast.makeText(MainActivity.this, "Update Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                ad.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        Cursor cs = myDb.searchContact(query);
        mCursorAdapter.changeCursor(cs);
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Cursor cs = myDb.searchContact(newText);
                mCursorAdapter.changeCursor(cs);
                CheckData();
            }
        });
        return false;
    }

    public void CheckData() {
        if (mListView.getAdapter().getCount() == 0){
            if (ns.getView() == null) { //check to mk sure fragment is empty b4 adding
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.noDataFrame, ns)
                        .commit();

            }
        }
        else if (ns.getView() != null && mListView.getAdapter().getCount() > 0){
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(ns)
                        .commit();
        }
    }
}