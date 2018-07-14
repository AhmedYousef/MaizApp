package com.example.android.maizapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MemberActivity extends AppCompatActivity {

    EditText mEditTextAddMember;
    Button mButtonAdd;
    Button mButtonSubmit;
    LinearLayout mLayoutContainer;
    CoreDatabase mData;
    SQLiteDatabase mSQLiteDatabase;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    final static String TAG = "MemberActivity";
    int instantMemberCount = 0;
    int memberCount = 0;
    int currentMaizID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditTextAddMember = findViewById(R.id.edit_add_new_member);
        mButtonAdd = findViewById(R.id.button_add_new_member);
        mButtonSubmit = findViewById(R.id.button_submit);
        mLayoutContainer = findViewById(R.id.layout_container2);
        mData = new CoreDatabase(this);


        sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        currentMaizID = sharedPreferences.getInt("currentMaizID", currentMaizID);

        memberCount = mData.readFromMaizTable(currentMaizID);
        mData.closeDB();

        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String addMember = mEditTextAddMember.getText().toString().trim();
                if (!addMember.matches("")) {

                    instantMemberCount++;

                    if (instantMemberCount == memberCount) {
                        setEnable(false, true);
                    } else {
                        setEnable(true, false);
                    }

                    mData.insertDataMembersTable(currentMaizID, mEditTextAddMember.getText().toString());
                    mData.closeDB();

                    showAddedMember(View.VISIBLE);

                } else {
                    Toast.makeText(MemberActivity.this, "You can't add empty member", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MemberActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    public void showAddedMember(int visibility) {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addView = layoutInflater.inflate(R.layout.row, null);

        final TextView memberTextView = addView.findViewById(R.id.text_member);
        memberTextView.setText(mEditTextAddMember.getText().toString());


        Button buttonRemove = addView.findViewById(R.id.button_remove);
        buttonRemove.setVisibility(visibility);
        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEnable(true, false);
                instantMemberCount--;

                ((LinearLayout) addView.getParent()).removeView(addView);
                mSQLiteDatabase = mData.getWritableDatabase();
                String memberName = memberTextView.getText().toString();
                mSQLiteDatabase.execSQL("DELETE FROM Member WHERE MemberName = '" + memberName + "' AND  MaizID = '" + currentMaizID + "' ");
                mData.closeDB();
            }
        });

        mLayoutContainer.addView(addView);
        mEditTextAddMember.setText("");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setEnable(Boolean btnAdd, Boolean btnSubmit) {
        mButtonAdd.setEnabled(btnAdd);
        mButtonSubmit.setEnabled(btnSubmit);
    }

}
