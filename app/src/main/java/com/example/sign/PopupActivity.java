package com.example.sign;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class PopupActivity extends Activity {

    TextView txtText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //화면 회전잠금
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        //UI 객체생성
        //txtText = (TextView)findViewById(R.id.txtText);

        //데이터 가져오기
        Intent intent = getIntent();
        String data = intent.getStringExtra("option");
        //txtText.setText(data);

        RadioGroup rg = (RadioGroup) findViewById(R.id.rd_geo);
        RadioButton rb = (RadioButton) findViewById(R.id.radio_all);
        rb.setChecked(true);


    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        //버튼 체크
        RadioGroup rg = (RadioGroup) findViewById(R.id.rd_geo);
        int id = rg.getCheckedRadioButtonId();
        RadioButton rb = (RadioButton) findViewById(id);

        String geo = "";

            geo = rb.getText().toString();
            //데이터 전달하기
            Intent intent = new Intent();
            intent.putExtra("result", geo);
            setResult(RESULT_OK, intent);

            //액티비티(팝업) 닫기
            finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}