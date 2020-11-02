package com.example.sign;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PostViewActivity extends FragmentActivity implements OnMapReadyCallback {
    String TAG="Post";
    private GoogleMap mMap;
    final Geocoder geocoder = new Geocoder(this);
    double laltitude;
    double longitude;

    private ImageView profileImageVIew;
    private ArrayList<String> pathList = new ArrayList<>();
    private LinearLayout parent;
    String key1;


    DocumentSnapshot documentname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postview);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //인텐트 데이터 받아오기
        final Intent intent = getIntent();
        String key = intent.getStringExtra("publisher");
        key1=key;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getUid().equals(key1)) {
            CardView imgview = findViewById(R.id.img_card);
            imgview.setVisibility(View.GONE);
        }







        //자유게시판일 경우
        if(intent.getStringExtra("category").contains("free") || intent.getStringExtra("category").equals("MyPage") ) {
            TextView txtname = findViewById(R.id.textView6);
            txtname.setVisibility(View.GONE);//이름
            TextView txtage = findViewById(R.id.txt_info);
            txtage.setVisibility(View.GONE);//나이
            TextView txtage1 = findViewById(R.id.textView8);
            txtage1.setVisibility(View.GONE);//나이
            //CardView imgview = findViewById(R.id.img_card);
            //imgview.setVisibility(View.GONE);
            LinearLayout ll = findViewById(R.id.layout_temp);
            ll.setVisibility(View.GONE);
        } else if (intent.getStringExtra("category").contains("protect")) {
            TextView txtstr = findViewById(R.id.textView8);
            txtstr.setText("찾은 위치");
            TextView txtname = findViewById(R.id.textView6);
            txtname.setVisibility(View.GONE);//이름
            TextView txtage = findViewById(R.id.txt_info);
            txtage.setVisibility(View.GONE);//나이
        }


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(key);

        /*
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    documentname = task.getResult();
                    if(document != null) {
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            String[] temp = document.getData().toString().split(",");
                            temp[0] = temp[0].replace("{phone=","");        //휴대폰
                            temp[2] = temp[2].replace("name=","").replace("}","");  //이름
                            temp[3] = temp[3].replace("photoUrl=","").replace("}","").trim();  //사진



                            TextView text_publisher = findViewById(R.id.Userinfo_txt);
                            text_publisher.setText("   "+temp[2]);
                            TextView text_publisher2 = findViewById(R.id.Userinfo_txt2);
                            text_publisher2.setText("    "+temp[0]);


                            Log.d("태그", "이름:"+temp[2]);
                            Log.d("태그", "번호:"+temp[0]);
                            Log.d("태그", "사진:"+temp[3]);

                            profileImageVIew = findViewById(R.id.profile_image);
                            Glide.with(PostViewActivity.this).load(temp[3]).centerCrop().override(100, 100).into(profileImageVIew);


                            //TextView text_publisher2 = findViewById(R.id.text_publisher2);
                            //text_publisher2.setText("전화번호 : "+temp[0]);

                        } else {
                            Log.d(TAG, "No such document");
                            //startAct(MemberActivity.class);
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
*/
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User usr = documentSnapshot.toObject(User.class);

                TextView text_publisher = findViewById(R.id.Userinfo_txt);
                text_publisher.setText("   "+usr.getName());

                String temp = usr.getPhotoUrl();
                profileImageVIew = findViewById(R.id.profile_image);
                Glide.with(PostViewActivity.this).load(temp).centerCrop().override(100, 100).into(profileImageVIew);
            }
        });



        if(intent.getStringExtra("category").equals("free")!=true) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        findViewById(R.id.button_check1).setOnClickListener(onClickListener);
        parent = findViewById(R.id.contentsLayout); //동적 추가될 레이어 초기화

        String profilePath =  intent.getStringExtra("imgurl");
        pathList.add(profilePath);      //이미지 경로 받아옴
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView imageView = new ImageView(PostViewActivity.this);
        imageView.setLayoutParams(layoutParams);
        Glide.with(this).load(profilePath).override(1000).into(imageView);
        parent.addView(imageView);
        //

        //address_lat = intent.getStringExtra("address_lat");
        if(intent.getStringExtra("laltitude").equals("null")!=true) {
            laltitude = Double.parseDouble(intent.getStringExtra("laltitude"));
            longitude = Double.parseDouble(intent.getStringExtra("longitude"));
        }



        String info = intent.getStringExtra("pet_name") + " (" +intent.getStringExtra("pet_sex") + ", " + intent.getStringExtra("pet_age")+")";
        TextView text_info = findViewById(R.id.txt_info);
        text_info.setText(info);

        TextView text_title = findViewById(R.id.text_title1);
        text_title.setText(intent.getStringExtra("title"));
        TextView text_contents = findViewById(R.id.text_title2);
        text_contents.setText(intent.getStringExtra("contents"));



        findViewById(R.id.sms_img).setOnClickListener(onClickListener);

    }



    //구글지도 Fragment
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng def = new LatLng(laltitude, longitude);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Seoul"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //기본 줌 서울

        mMap.addMarker(new MarkerOptions().position(def).title("잃어버린 위치"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(def));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(def, 18));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //toastmsg(address_lat);
                //mMap.addMarker(new MarkerOptions().position(latLng).title("잃어버린 위치"));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });
    }

    //버튼 리스너
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_check1:
                    //toastmsg(temp);

                    finish();
                    break;
                case R.id.sms_img:
                    mOnPopupClick();
                    break;
            }
        }
    };

    public void mOnPopupClick(){
        //데이터 담아서 팝업(액티비티) 호출
        TextView textView1 = findViewById(R.id.text_title1);
        //TextView textView2 = findViewById(R.id.Userinfo_txt2);
        TextView textView3 = findViewById(R.id.Userinfo_txt);


        Intent intent = new Intent(this, Popup_smsActivity.class);
        intent.putExtra("title", textView1.getText().toString());
        //intent.putExtra("number", textView2.getText().toString());
        intent.putExtra("name", textView3.getText().toString());
        intent.putExtra("publisher", key1);
        startActivityForResult(intent, 1);
    }

    public void toastmsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
