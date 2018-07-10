package com.example.android.maizapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class creates the database of the Maiz groups.
 * @author Ahmed Yousef
 */

public class CoreDatabase extends SQLiteOpenHelper {


    final static private String DB_NAME = "MaizApp";
    final static private String DB_MAIZ_TABLE = "Maiz";
    final static private String DB_MEMBER_TABLE = "Member";
    final static private String DB_PURCHASE_TABLE = "Purchase";
    final static private int DB_VER = 4;

    Context context;
    SQLiteDatabase mSQLiteDatabase;
    Cursor mCursor;

    /**
     * This constructs the database to the SQLiteOpenHelper - super class - to create the database.
     * @param context
     */

    public CoreDatabase(Context context) {
        super(context, DB_NAME, null, DB_VER);
        this.context = context;
    }

    /**
     *This method creates the tables of the database.
     * @param sqLiteDatabase executes SQLite queries.
     */

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + DB_MAIZ_TABLE + "( _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, MaizName TEXT, MemberCount INTEGER);");
        sqLiteDatabase.execSQL("create table " + DB_MEMBER_TABLE + "( _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, MaizID INTEGER, MemberName TEXT,  TotalPaid REAL, FOREIGN KEY (MaizID) REFERENCES Maiz(_id));");
        sqLiteDatabase.execSQL("create table " + DB_PURCHASE_TABLE + "( _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, MemberID INTEGER, MaizID INTEGER, PurchasesName TEXT, Price REAL, FOREIGN KEY (MemberID) REFERENCES Member(_id), FOREIGN KEY (MaizID) REFERENCES Maiz(_id));");
        Log.i("CoreDatabase", "Table Created");
    }

    /**
     * This method drops the tables of the database if the version of the database changed
     * @param sqLiteDatabase executes SQLite queries.
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB_MAIZ_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB_MEMBER_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB_PURCHASE_TABLE);
        onCreate(sqLiteDatabase);
    }

    /**
     * Executes inserting query to insert data to database
     * @param maizName refers to Maiz name
     * @param memberCount refers to member count in the Maiz
     */
    public void insertDataMaizTable(String maizName, String memberCount) {
        mSQLiteDatabase = getWritableDatabase();
        mSQLiteDatabase.execSQL("INSERT INTO " + DB_MAIZ_TABLE + " (MaizName, MemberCount) VALUES('" + maizName + "','" + memberCount + "');");
    }

    /**
     * Executes inserting query to insert data to database
     * @param maizID refers to MaizID which is a foreign key in Member table.
     * @param memberName refers to member name.
     */
    public void insertDataMembersTable(int maizID, String memberName) {
        mSQLiteDatabase = getWritableDatabase();
        mSQLiteDatabase.execSQL("INSERT INTO  " + DB_MEMBER_TABLE + " (MaizID, MemberName) VALUES('" + maizID + "','" + memberName + "');");
    }

    /**
     * Executes inserting query to insert data to database
     * @param memberID references to MemberID which is a foreign key in Purchase table.
     * @param purchaseName refers to purchase name.
     * @param price refers to price name.
     */
    public void insertDataPurchaseTable(int memberID, int maizID, String purchaseName, String price) {
        mSQLiteDatabase = getWritableDatabase();
        mSQLiteDatabase.execSQL("INSERT INTO " + DB_PURCHASE_TABLE + " (MemberID, MaizID, PurchasesName, Price) values('" + memberID + "','" + maizID + "','" + purchaseName + "', '" + price +"');");
    }

    public int readFromMaizTable(int currentMaizID){
        mSQLiteDatabase = getReadableDatabase();
        mCursor = mSQLiteDatabase.rawQuery("SELECT MemberCount FROM Maiz WHERE _id = '"+ currentMaizID +"'", null);
        mCursor.moveToFirst();
        int memberCount = mCursor.getInt(0);
        mCursor.close();
        return memberCount;
    }

    public void updateDataMaizTable(int instantMemberCount, int currentMaizID){
        mSQLiteDatabase = getWritableDatabase();
        mSQLiteDatabase.execSQL("UPDATE Maiz SET MemberCount = '" + instantMemberCount + "' WHERE _id = '" + currentMaizID + "' ");
    }

    public void deleteFromMembersTable(String memberName, int currentMaizID){
        mSQLiteDatabase = getWritableDatabase();
        mSQLiteDatabase.execSQL("DELETE FROM Member WHERE MemberName = '" + memberName + "' AND  MaizID = '" + currentMaizID + "' ");
    }

    /**
     * Executes deleting query to delete data from database
     * @param memberName refers to member name.
     */
    public void deleteFromPurchaseTable(String memberName, int maizID){
        mSQLiteDatabase = getWritableDatabase();
        mSQLiteDatabase.execSQL("DELETE FROM " + DB_PURCHASE_TABLE + " WHERE MemberID IN (SELECT _id FROM Member WHERE MemberName = '" + memberName +"' AND MaizID = '"+ maizID +"')");
    }

    /**
     * Executes deleting query to delete data from database
     */
    public void deleteAllPurchases(int maizID){
        mSQLiteDatabase = getWritableDatabase();
        mSQLiteDatabase.execSQL("DELETE FROM " + DB_PURCHASE_TABLE + " WHERE MaizID = '"+ maizID +"'");
    }

    /**
     * Closes database if it's opened.
     */
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
