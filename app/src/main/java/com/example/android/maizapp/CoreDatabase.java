package com.example.android.maizapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CoreDatabase extends SQLiteOpenHelper {

    final static private String DB_NAME = "MaizApp";
    final static private String DB_MAIZ_TABLE = "Maiz";
    final static private String DB_MEMBER_TABLE = "Member";
    final static private String DB_PURCHASE_TABLE = "Purchase";
    final static private int DB_VER = 4;

    private SQLiteDatabase mSQLiteDatabase;

    public CoreDatabase(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + DB_MAIZ_TABLE +
                                "( _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                                " MaizName TEXT," +
                                " MemberCount INTEGER);");

        sqLiteDatabase.execSQL("create table " + DB_MEMBER_TABLE +
                                "( _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                                " MaizID INTEGER," +
                                " MemberName TEXT," +
                                " TotalPaid REAL," +
                                " FOREIGN KEY (MaizID) REFERENCES Maiz(_id));");

        sqLiteDatabase.execSQL("create table " + DB_PURCHASE_TABLE +
                                "( _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                                " MemberID INTEGER," +
                                " MaizID INTEGER," +
                                " PurchasesName TEXT," +
                                " Price REAL," +
                                " FOREIGN KEY (MemberID) REFERENCES Member(_id)," +
                                " FOREIGN KEY (MaizID) REFERENCES Maiz(_id));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB_MAIZ_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB_MEMBER_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB_PURCHASE_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void insertDataMaizTable(String maizName, String memberCount) {
        mSQLiteDatabase = getWritableDatabase();
        mSQLiteDatabase.execSQL("INSERT INTO " + DB_MAIZ_TABLE +
                                " (MaizName, MemberCount) " +
                                " VALUES('" + maizName + "','" + memberCount + "');");
    }

    public void insertDataMembersTable(int maizID, String memberName) {
        mSQLiteDatabase = getWritableDatabase();
        mSQLiteDatabase.execSQL("INSERT INTO  " + DB_MEMBER_TABLE +
                                " (MaizID, MemberName) " +
                                " VALUES('" + maizID + "','" + memberName + "');");
    }

    public void insertDataPurchaseTable(int memberID, int maizID, String purchaseName, String price) {
        mSQLiteDatabase = getWritableDatabase();
        mSQLiteDatabase.execSQL("INSERT INTO " + DB_PURCHASE_TABLE +
                                " (MemberID, MaizID, PurchasesName, Price) " +
                                " VALUES('" + memberID + "','" + maizID + "','" + purchaseName + "', '" + price +"');");
    }

    public int readFromMaizTable(int currentMaizID){
        mSQLiteDatabase = getReadableDatabase();
        Cursor mCursor = mSQLiteDatabase.rawQuery("SELECT MemberCount " +
                                                        "FROM Maiz " +
                                                        "WHERE _id = '"+ currentMaizID +"'", null);
        mCursor.moveToFirst();
        int memberCount = mCursor.getInt(0);
        mCursor.close();
        return memberCount;
    }

    public void updateDataMaizTable(int instantMemberCount, int currentMaizID){
        mSQLiteDatabase = getWritableDatabase();
        mSQLiteDatabase.execSQL("UPDATE Maiz" +
                                " SET MemberCount = '" + instantMemberCount + "' " +
                                " WHERE _id = '" + currentMaizID + "' ");
    }

    public void deleteFromMaizTable(String maizName){
        mSQLiteDatabase = getWritableDatabase();
        mSQLiteDatabase.execSQL("DELETE FROM Maiz " +
                                "WHERE MaizName = '"+ maizName +"'");
    }

    public void deleteFromMembersTable(String memberName, int currentMaizID){
        mSQLiteDatabase = getWritableDatabase();
        mSQLiteDatabase.execSQL("DELETE FROM Member " +
                                "WHERE MemberName = '" + memberName + "' AND  MaizID = '" + currentMaizID + "' ");
    }

    public void deleteFromMembersTable(int currentMaizID){
        mSQLiteDatabase = getWritableDatabase();
        mSQLiteDatabase.execSQL("DELETE FROM Member " +
                                "WHERE MaizID = '" + currentMaizID + "' ");
    }

    public void deleteFromPurchaseTable(String memberName, int currentMaizID){
        mSQLiteDatabase = getWritableDatabase();
        mSQLiteDatabase.execSQL("DELETE FROM " + DB_PURCHASE_TABLE + " " +
                                "WHERE MemberID " +
                                "IN (SELECT _id FROM Member " +
                                    "WHERE MemberName = '" + memberName +"' " +
                                    "AND MaizID = '"+ currentMaizID +"')");
    }

    public void deleteFromPurchaseTable(int currentMaizID){
        mSQLiteDatabase = getWritableDatabase();
        mSQLiteDatabase.execSQL("DELETE FROM " + DB_PURCHASE_TABLE + "" +
                                " WHERE MaizID = '" + currentMaizID + "'");
    }

    public void deleteAllPurchases(int maizID){
        mSQLiteDatabase = getWritableDatabase();
        mSQLiteDatabase.execSQL("DELETE FROM " + DB_PURCHASE_TABLE +
                                " WHERE MaizID = '"+ maizID +"'");
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
