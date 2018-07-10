package com.example.android.maizapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    CoreDatabase mData;
    EditText mEditTextPurchase;
    EditText mEditTextPrice;
    Button mButtonAddPurchase;
    Button mButtonYes;
    Button mButtonNo;
    Cursor mCursor;
    SharedPreferences sharedPreferences;
    SQLiteDatabase mSqLiteDatabase;
    final static String TAG = "MainActivity";
    int currentMaizID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mData = new CoreDatabase(this);
        mEditTextPurchase = findViewById(R.id.edit_purchase);
        mEditTextPrice = findViewById(R.id.edit_price);
        mButtonAddPurchase = findViewById(R.id.button_add_purchase);
        Spinner spinner = findViewById(R.id.spinner_members);

        sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        currentMaizID = sharedPreferences.getInt("currentMaizID", currentMaizID);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, membersList());
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                final String memberName = adapterView.getItemAtPosition(i).toString();
                showPurchasesList(memberName);

                mButtonAddPurchase.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String purchase = mEditTextPurchase.getText().toString().trim();
                        String price = mEditTextPrice.getText().toString().trim();

                        if(!purchase.matches("") && !price.matches("") && !(Double.parseDouble(price) < 1.0)){
                            mSqLiteDatabase = mData.getReadableDatabase();
                            mCursor = mSqLiteDatabase.rawQuery("SELECT * FROM Member WHERE MemberName = '" + memberName + "' AND MaizID = '"+ currentMaizID +"'", null);

                            while (mCursor.moveToNext()) {
                                mData.insertDataPurchaseTable(mCursor.getInt(0), currentMaizID, mEditTextPurchase.getText().toString(), mEditTextPrice.getText().toString());
                            }
                            showPurchasesList(memberName);

                            mEditTextPurchase.setText("");
                            mEditTextPrice.setText("");
                        } else {
                            Toast.makeText(MainActivity.this, "The purchase or price is INVALID", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.item_add_remove_member:
                intent = new Intent(this, ModificationActivity.class);
                this.startActivity(intent);
                break;

            case R.id.item_calculate:
                calculate(false);
                break;

            case R.id.item_end_session:
                endSession(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MainActivity.this, MaizActivity.class);
        startActivity(intent);
    }

    public void showPurchasesList(String memberName) {
        mData = new CoreDatabase(this);
        SQLiteDatabase db = mData.getWritableDatabase();
        mCursor = db.rawQuery("SELECT * FROM Purchase WHERE MemberID IN (SELECT _id FROM Member WHERE MemberName = '" + memberName + "' AND MaizID = '"+ currentMaizID +"')", null);

        ListView listView = findViewById(R.id.list_purchases);
        ListCursorAdapter adapter = new ListCursorAdapter(this, mCursor);
        listView.setAdapter(adapter);
    }

    public void showDeletedPurchasesList() {
        mData = new CoreDatabase(this);
        SQLiteDatabase db = mData.getWritableDatabase();
        mCursor = db.rawQuery("SELECT * FROM Purchase WHERE MaizID = '"+ currentMaizID +"'", null);

        ListView listView = findViewById(R.id.list_purchases);
        ListCursorAdapter listAdapter = new ListCursorAdapter(this, mCursor);
        listAdapter.notifyDataSetChanged();
        listView.setAdapter(listAdapter);
    }

    public void showResultList(Dialog dialog){
        mData = new CoreDatabase(this);
        SQLiteDatabase db = mData.getWritableDatabase();
        mCursor = db.rawQuery("SELECT * FROM Member WHERE MaizID = '"+ currentMaizID +"'", null);

        ListView listView = dialog.findViewById(R.id.list_result);
        ResultCursorAdapter resultAdapter = new ResultCursorAdapter(this, mCursor, currentMaizID);
        listView.setAdapter(resultAdapter);

        TextView tvAVR = dialog.findViewById(R.id.text_avr);
        tvAVR.setText( "Average = " + String.valueOf(resultAdapter.getAverage()));
    }

    public void deleteAllPurchases(){
        mData.deleteAllPurchases(currentMaizID);
        mData.closeDB();
    }

    public ArrayList<String> membersList() {
        mData = new CoreDatabase(this);
        SQLiteDatabase db = mData.getWritableDatabase();
        mCursor = db.rawQuery("SELECT MemberName FROM Member WHERE MaizID = '"+ currentMaizID +"'", null);

        ArrayList<String> members = new ArrayList<>();
        while (mCursor.moveToNext()) {
            members.add(mCursor.getString(0));
        }
        return members;
    }

    private void endSession(final boolean isDelete){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.activity_alert_dialog_main);
        mButtonYes = dialog.findViewById(R.id.button_yes);
        mButtonNo = dialog.findViewById(R.id.button_no);
        dialog.setTitle("Custom Dialog");
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        mButtonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                calculate(isDelete);
            }
        });

        mButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void calculate(final boolean isDelete){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.activity_result_dialog);
        dialog.setTitle("Custom Dialog");
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        showResultList(dialog);

        Button declineButton = dialog.findViewById(R.id.declineButton);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDelete) {
                    deleteAllPurchases();
                    showDeletedPurchasesList();
                }
                dialog.dismiss();
            }
        });
    }

}
