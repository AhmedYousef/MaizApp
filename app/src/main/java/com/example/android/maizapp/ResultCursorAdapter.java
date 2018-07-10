package com.example.android.maizapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ResultCursorAdapter extends CursorAdapter {

    private CoreDatabase mData;
    private SQLiteDatabase mSqLiteDatabase;
    private Cursor mCursor;
    private Context context;
    private SharedPreferences sharedPreferences;
    int currentMaizID;

    public ResultCursorAdapter(Context context, Cursor cursor, int currentMaizID){
        super(context, cursor, 0);
        this.context = context;
        this.currentMaizID = currentMaizID;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_result, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvMember = view.findViewById(R.id.text_member_result);
        TextView tvTotalPurchases = view.findViewById(R.id.text_total_purchases);
        TextView tvResult = view.findViewById(R.id.text_should_pay);

        int memberID = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String memberName = cursor.getString(cursor.getColumnIndexOrThrow("MemberName"));
        double individualTotalPurchases = round(getIndividualTotalPurchases(memberID),1);
        double shouldPay = round(getShouldPay(memberID),1);

        tvMember.setText(memberName);
        tvTotalPurchases.setText(String.valueOf(individualTotalPurchases));
        tvResult.setText(String.valueOf(shouldPay));
    }

    private double getTotalPurchases(int maizID){
        mData = new CoreDatabase(context);
        mSqLiteDatabase = mData.getReadableDatabase();
        mCursor = mSqLiteDatabase.rawQuery("SELECT Price FROM Purchase WHERE MaizID = '"+ maizID +"'", null);

        double total = 0;
        while(mCursor.moveToNext()){
            total += mCursor.getDouble(0);
        }
        mCursor.close();
        return total;
    }

    private double getIndividualTotalPurchases(int memberID){
        mData = new CoreDatabase(context);
        mSqLiteDatabase = mData.getReadableDatabase();
        mCursor = mSqLiteDatabase.rawQuery("SELECT Price FROM Purchase WHERE MemberID IN (SELECT _id FROM Member WHERE _id = '" + memberID + "')", null);

        double individualTotal = 0;
        while(mCursor.moveToNext()){
            individualTotal += mCursor.getDouble(0);
        }
        mCursor.close();
        return individualTotal;
    }

    public int getMemberCount(){
        mData = new CoreDatabase(context);
        mSqLiteDatabase = mData.getReadableDatabase();
        mCursor = mSqLiteDatabase.rawQuery("SELECT MemberCount FROM Maiz WHERE _id = '" + currentMaizID +"'", null);

        int memberCount;
        mCursor.moveToFirst();
        memberCount = mCursor.getInt(0);
        mCursor.close();
        return memberCount;
    }

    private double getShouldPay(int memberID){
        double avr = getTotalPurchases(currentMaizID) / getMemberCount();
        double individualTotal = getIndividualTotalPurchases(memberID);

        return individualTotal - avr;
    }

    public double getAverage(){
        return round(getTotalPurchases(currentMaizID) / getMemberCount(), 1);
    }


    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
