package com.example.android.maizapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    CoreDatabase mData;
    SQLiteDatabase mSqLiteDatabase;
    Cursor mCursor;
    //Thread splashTread;

    /*public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();
        moveToNextActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSupportActionBar().hide();
        moveToNextActivity();
    }

    private void moveToNextActivity(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /*mData = new CoreDatabase(SplashScreenActivity.this);
                mSqLiteDatabase = mData.getReadableDatabase();
                mCursor = mSqLiteDatabase.rawQuery("SELECT MaizName FROM Maiz", null);

                Intent intent;
                if (mCursor.getCount() == 0)
                    intent = new Intent(SplashScreenActivity.this, MaizActivity.class);
                else
                    intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                mData.closeDB();
                mCursor.close();*/

                Intent intent = new Intent(SplashScreenActivity.this, MaizActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    /*private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        RelativeLayout layout = findViewById(R.id.layout_splash);
        layout.clearAnimation();
        layout.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = findViewById(R.id.image_logo);
        iv.clearAnimation();
        iv.startAnimation(anim);

        moveToNextActivity();
    }*/


}
