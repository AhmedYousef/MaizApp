package com.example.android.maizapp;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class implements adding or deleting members from database
 *
 * @author Ahmed Yousef
 */
public class ModificationActivity extends AppCompatActivity {

    EditText mEditTextAddMember;
    Button mButtonAdd;
    Button mButtonYes;
    Button mButtonNo;
    LinearLayout mLayoutContainer;
    CoreDatabase mData;
    SQLiteDatabase mSQLiteDatabase;
    Cursor mCursor;
    final static String TAG = "MemberActivity";
    int instantMemberCount = 0;
    int currentMaizID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modification);

        mEditTextAddMember = findViewById(R.id.edit_add_member);
        mButtonAdd = findViewById(R.id.button_add_member);
        mLayoutContainer = findViewById(R.id.layout_container);
        mData = new CoreDatabase(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.i(TAG,"onCreate");

        final SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        currentMaizID = sharedPreferences.getInt("currentMaizID", currentMaizID);

        showAllMembers(View.VISIBLE);

        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String addMember = mEditTextAddMember.getText().toString().trim();
                //Validation process.
                if (!addMember.matches("")) {
                    //Increase the instant member count by 1.
                    instantMemberCount++;

                    //Insert new member to the database.
                    mData.insertDataMembersTable(currentMaizID, mEditTextAddMember.getText().toString());
                    mData.closeDB();

                    //Update current member list.
                    showAddedMember(View.VISIBLE);
                } else {
                    Toast.makeText(ModificationActivity.this, "You can't add empty member", Toast.LENGTH_SHORT).show();
                }
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
                instantMemberCount--;

                ((LinearLayout) addView.getParent()).removeView(addView);

                String memberName = memberTextView.getText().toString();
                mData.deleteFromMembersTable(memberName, currentMaizID);
                mData.closeDB();
            }
        });

        mLayoutContainer.addView(addView);
        mEditTextAddMember.setText("");
    }

    public void showAllMembers(int visibility) {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mSQLiteDatabase = mData.getReadableDatabase();
        mCursor = mSQLiteDatabase.rawQuery("SELECT MemberName FROM Member WHERE MaizID = '"+ currentMaizID +"'", null);

        while (mCursor.moveToNext()) {
            final View addView = layoutInflater.inflate(R.layout.row, null);

            final TextView memberTextView = addView.findViewById(R.id.text_member);
            memberTextView.setText(mCursor.getString(0));

            Button buttonRemove = addView.findViewById(R.id.button_remove);
            buttonRemove.setVisibility(visibility);
            buttonRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(ModificationActivity.this);
                    dialog.setContentView(R.layout.activity_alert_dialog_modification);
                    mButtonYes = dialog.findViewById(R.id.button_yes);
                    mButtonNo = dialog.findViewById(R.id.button_no);
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);

                    mButtonYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            instantMemberCount--;

                            ((LinearLayout) addView.getParent()).removeView(addView);
                            String memberName = memberTextView.getText().toString();

                            mSQLiteDatabase = mData.getWritableDatabase();
                            mCursor = mSQLiteDatabase.rawQuery("SELECT * FROM Purchase WHERE MaizID = '"+ currentMaizID +"'", null);
                            while (mCursor.moveToNext())
                                mData.deleteFromPurchaseTable(memberName, currentMaizID);
                            mData.deleteFromMembersTable(memberName, currentMaizID);
                            mData.closeDB();

                            dialog.dismiss();
                        }
                    });

                    mButtonNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
            });
            mLayoutContainer.addView(addView);
        }
        mCursor.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        //Get the member count from database and update instantMemberCount with it.
        mSQLiteDatabase = mData.getReadableDatabase();
        mCursor = mSQLiteDatabase.rawQuery("SELECT MemberCount FROM Maiz WHERE _id = '" + currentMaizID + "'", null);
        mCursor.moveToFirst();
        instantMemberCount = mCursor.getInt(0);
        mData.closeDB();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mData.updateDataMaizTable(instantMemberCount, currentMaizID);
        mData.closeDB();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}
