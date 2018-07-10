package com.example.android.maizapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ListCursorAdapter extends CursorAdapter {

    public ListCursorAdapter(Context context, Cursor cursor) {
        super(context , cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_purchase, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvPurchases = view.findViewById(R.id.text_purchase);
        TextView tvPrice = view.findViewById(R.id.text_price);

        String purchases = cursor.getString(cursor.getColumnIndexOrThrow("PurchasesName"));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow("Price"));

        tvPurchases.setText(purchases);
        tvPrice.setText(String.valueOf(price));
    }
}
