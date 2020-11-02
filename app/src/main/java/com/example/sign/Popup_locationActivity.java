package com.example.sign;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class Popup_locationActivity extends Activity {

    private ImageView profileImageVIew;
    TextView txtText1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //화면 회전잠금
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_location_popup);

        //UI 객체생성

        //데이터 가져오기
        Intent intent = getIntent();
        String data_name = intent.getStringExtra("info_name");
        String data_photo = intent.getStringExtra("info_photo");

        txtText1 = (TextView)findViewById(R.id.text1);
        txtText1.setText(data_name);


        profileImageVIew = findViewById(R.id.profileImageView);
        Glide.with(this).load(data_photo).centerCrop().override(500).into(profileImageVIew);

    }

    public void mOnSend(View v){


    }

    //확인 버튼 클릭
    public void mOnClose(View v){
            //데이터 전달하기
            //Intent intent = new Intent();
            //intent.putExtra("result", "test");
            //setResult(RESULT_OK, intent);

            //액티비티(팝업) 닫기
            finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게

        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            finish();
            //return false;
        }
        return true;
    }

    /*
    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
    */
}