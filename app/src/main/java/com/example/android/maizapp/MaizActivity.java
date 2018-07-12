package com.example.android.maizapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MaizActivity extends AppCompatActivity {

    TextView mTextViewAddMaiz;
    ImageView mImageViewArrow;
    EditText mEditTextMaizName;
    EditText mEditTextMaizCount;
    Button mButtonCreate;
    ListView listView;
    CoreDatabase mData;
    SQLiteDatabase mSQLiteDatabase;
    ImageButton mImageButton;
    Cursor mCursor;
    ArrayAdapter<String> arrayAdapter;

    final static String TAG = "MaizActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maiz);

        listView = findViewById(R.id.list_maiz);
        mTextViewAddMaiz = findViewById(R.id.text_add_maiz);
        mImageViewArrow = findViewById(R.id.image_arrow);
        mImageButton = findViewById(R.id.image_button);
        mData = new CoreDatabase(this);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, maizList());

        if (!maizList().isEmpty()) {
            listView.setAdapter(arrayAdapter);
        } else {
            mTextViewAddMaiz.setVisibility(View.VISIBLE);
            mImageViewArrow.setVisibility(View.VISIBLE);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String maizName = (String) listView.getItemAtPosition(i);
                setCurrentMaizID(maizName);
                Intent intent;
                if(membersList(getCurrentMaizID(maizName)).isEmpty()){
                    intent = new Intent(MaizActivity.this, MemberActivity.class);
                } else {
                    intent = new Intent(MaizActivity.this, MainActivity.class);
                }
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String maizName = listView.getItemAtPosition(i).toString();
                AlertDialog.Builder alert = new AlertDialog.Builder(MaizActivity.this);
                alert.setTitle("ALERT!");
                alert.setMessage("Are you sure to delete "+ maizName + " Maiz?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mData.deleteFromPurchaseTable(getCurrentMaizID(maizName));
                        mData.deleteFromMembersTable(getCurrentMaizID(maizName));
                        mData.deleteFromMaizTable(maizName);
                        mData.closeDB();
                        if (!maizList().isEmpty()) {
                            listView.setAdapter(new ArrayAdapter<>(MaizActivity.this, android.R.layout.simple_list_item_1, maizList()));
                        } else {
                            listView.setAdapter(new ArrayAdapter<>(MaizActivity.this, android.R.layout.simple_list_item_1, maizList()));
                            mTextViewAddMaiz.setVisibility(View.VISIBLE);
                            mImageViewArrow.setVisibility(View.VISIBLE);
                        }
                        dialogInterface.dismiss();
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                alert.show();
                return true;
            }
        });

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MaizActivity.this);
                dialog.setContentView(R.layout.activity_add_maiz_dialog);

                mEditTextMaizName = dialog.findViewById(R.id.edit_miz_name);
                mEditTextMaizCount = dialog.findViewById(R.id.edit_members_count);
                mButtonCreate = dialog.findViewById(R.id.button_create);

                dialog.show();

                mButtonCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String maizName = mEditTextMaizName.getText().toString().trim();
                        String maizCount = mEditTextMaizCount.getText().toString().trim();

                        if (!maizName.matches("") && !maizCount.matches("") && !(Double.parseDouble(maizCount) < 1.0)) {
                            mData.insertDataMaizTable(mEditTextMaizName.getText().toString(), mEditTextMaizCount.getText().toString());
                            mData.closeDB();
                            mTextViewAddMaiz.setVisibility(View.INVISIBLE);
                            mImageViewArrow.setVisibility(View.INVISIBLE);

                            showMaizList();

                            dialog.dismiss();

                        } else {
                            Toast.makeText(MaizActivity.this, "The Maiz name or member count is INVALID", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }

    public void setCurrentMaizID(String maizName) {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("currentMaizID", getCurrentMaizID(maizName));
        editor.apply();
    }

    public int getCurrentMaizID(String maizName){
        mSQLiteDatabase = mData.getReadableDatabase();
        mCursor = mSQLiteDatabase.rawQuery("SELECT _id FROM Maiz WHERE MaizName = '" + maizName + "' ", null);
        mCursor.moveToNext();
        return mCursor.getInt(0);
    }

    private ArrayList<String> maizList() {
        mData = new CoreDatabase(this);
        SQLiteDatabase db = mData.getWritableDatabase();
        mCursor = db.rawQuery("SELECT MaizName FROM Maiz", null);

        ArrayList<String> maizs = new ArrayList<>();
        while (mCursor.moveToNext()) {
            maizs.add(mCursor.getString(0));
        }
        return maizs;
    }

    private ArrayList<String> membersList(int maizID) {
        mData = new CoreDatabase(this);
        SQLiteDatabase db = mData.getWritableDatabase();
        mCursor = db.rawQuery("SELECT * FROM Member WHERE MaizID = '"+ maizID +"'" , null);

        ArrayList<String> members = new ArrayList<>();
        while (mCursor.moveToNext()) {
            members.add(mCursor.getString(2));
        }
        return members;
    }

    private void showMaizList() {
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, maizList());
        listView.setAdapter(arrayAdapter);
    }
}
