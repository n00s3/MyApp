package com.example.sign;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

public class PostUpdateActivity extends FragmentActivity{
    int flag=0;
    String TAG="Post";
    final Geocoder geocoder = new Geocoder(this);
    String address_lat="default";
    String laltitude;
    String longitude;

    private FirebaseUser user;
    private ArrayList<String> pathList = new ArrayList<>();
    private LinearLayout parent;
    private int pathCount,successCount;
    private String docID;

    String pet_sex;
    String category;
    String publisher;
    String imgurl;


    TextView txttitle;
    TextView txtcontents;
    TextView txtname;
    TextView txtage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //액티비티 데이터복구
        if(savedInstanceState != null) {
            address_lat = savedInstanceState.getString("adl");
            laltitude = savedInstanceState.getString("lal");
            longitude = savedInstanceState.getString("lon");
        }




        /*
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        */


        parent = findViewById(R.id.contentsLayout); //동적 추가될 레이어 초기화
        findViewById(R.id.button_check).setOnClickListener(onClickListener);
        findViewById(R.id.button_imgadd).setOnClickListener(onClickListener);
        findViewById(R.id.button_geo).setOnClickListener(onClickListener);

        //카테고리 받아오기
        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        toastmsg(category);

        publisher = intent.getStringExtra("publisher");
        docID = intent.getStringExtra("docID");
        txttitle = findViewById(R.id.txttitle);
        txttitle.setText(intent.getStringExtra("title"));   //제목
        txtcontents = findViewById(R.id.txtcontents);
        txtcontents.setText(intent.getStringExtra("contents"));   //내용
        txtname = findViewById(R.id.txtname);
        txtname.setText(intent.getStringExtra("pet_name"));

        txtage = findViewById(R.id.txtage);
        txtname.setText(intent.getStringExtra("pet_age"));
        Button button_geo = findViewById(R.id.button_geo);
        address_lat=intent.getStringExtra("address");
        laltitude=intent.getStringExtra("laltitude");
        longitude=intent.getStringExtra("longitude");

        RadioGroup rg = (RadioGroup) findViewById(R.id.radiorg);
        RadioButton rb0 = (RadioButton) findViewById(R.id.radio_male);
        RadioButton rb1 = (RadioButton) findViewById(R.id.radio_female);

        if (intent.getStringExtra("pet_sex").equals("암컷")) {
            rb1.setChecked(true);
        } else {
            rb0.setChecked(true);
        }

        final ViewGroup.LayoutParams layoutParams;
        imgurl = intent.getStringExtra("imgurl");
        if (imgurl != null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            ImageView imageView = new ImageView(PostUpdateActivity.this);
            imageView.setLayoutParams(layoutParams);
            Glide.with(this).load(imgurl).override(1000).into(imageView);
            parent.addView(imageView);
        } else {
            imgurl="null";
        }


        //게시판 분류 Visibility
        //자유게시판일 경우
        if(category.contains("free")) {
            txtname.setVisibility(View.GONE);//이름
            txtname.setText(intent.getStringExtra("pet_name"));

            txtage.setVisibility(View.GONE);//나이
            txtage.setText(intent.getStringExtra("pet_age"));


            button_geo.setVisibility(View.GONE);//위치등록버튼
            RadioGroup radioGroup = findViewById(R.id.radiorg);
            radioGroup.setVisibility(View.GONE);//성별체크
        }
        else if (category.contains("protect")) {
            button_geo.setText("찾은 위치 등록");
            txtname.setVisibility(View.GONE);//이름
            txtage.setVisibility(View.GONE);//나이
        }

        else if (category.contains("find")) {
            txtname.setText(intent.getStringExtra("pet_name"));
            txtage.setText(intent.getStringExtra("pet_age"));
        }








    }






    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if (resultCode == Activity.RESULT_OK) {

                    String profilePath = data.getStringExtra("profilePath");
                    pathList.add(profilePath);      //이미지 경로 받아옴
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    ImageView imageView = new ImageView(PostUpdateActivity.this);
                    imageView.setLayoutParams(layoutParams);
                    Glide.with(this).load(profilePath).override(1000).into(imageView);
                    parent.addView(imageView);
                    flag=1;
                    //이미지뷰 추가


                    EditText editText = new EditText(PostUpdateActivity.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    parent.addView(editText);

                    //텍스트 추가
                }
                break;
            }
            case 100: {
                address_lat = data.getStringExtra("address");
                laltitude = data.getStringExtra("laltitude");
                longitude = data.getStringExtra("longitude");
                break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("adl",address_lat);
        outState.putString("lal",laltitude);
        outState.putString("lon",longitude);
        super.onSaveInstanceState(outState);
    }



    //버튼 리스너
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_check:
                    //자유게시판일 경우
                    if (category.contains("free")) {
                        final ArrayList<String> contentsList = new ArrayList<>();
                        contentsList.add(txtcontents.getText().toString()); //내용 추가
                        contentsList.add(imgurl);




                        String new_category = category.replace("MyPage","");
                        PostInfo postInfo = new PostInfo(txttitle.getText().toString(), contentsList, publisher, "null", "null", new Date(),
                                "null", "null", "null", "null", new_category);
                        update(postInfo);

                    } else if (category.contains("find")) {                 //찾기일 경우
                        final ArrayList<String> contentsList = new ArrayList<>();
                        contentsList.add(txtcontents.getText().toString()); //내용 추가
                        contentsList.add(imgurl);

                        //성별
                        RadioGroup rg = (RadioGroup) findViewById(R.id.radiorg);
                        int id = rg.getCheckedRadioButtonId();
                        RadioButton rb = (RadioButton) findViewById(id);
                        pet_sex = rb.getText().toString();

                        String new_category = category.replace("MyPage","");
                        PostInfo postInfo = new PostInfo(txttitle.getText().toString(), contentsList, publisher, laltitude, longitude, new Date(),
                                address_lat, txtname.getText().toString(), txtage.getText().toString(), pet_sex, new_category);
                        update(postInfo);

                    } else {        //보호일 경우
                        final ArrayList<String> contentsList = new ArrayList<>();
                        contentsList.add(txtcontents.getText().toString()); //내용 추가
                        contentsList.add(imgurl);

                        //성별
                        RadioGroup rg = (RadioGroup) findViewById(R.id.radiorg);
                        int id = rg.getCheckedRadioButtonId();
                        RadioButton rb = (RadioButton) findViewById(id);
                        pet_sex = rb.getText().toString();

                        String new_category = category.replace("MyPage","");
                        PostInfo postInfo = new PostInfo(txttitle.getText().toString(), contentsList, publisher, laltitude, longitude, new Date(),
                                address_lat, "null", "null", pet_sex, new_category);
                        update(postInfo);
                    }


                    break;
                case R.id.button_imgadd:
                    startAct(GalleryActivity.class, "image",0);
                    break;
                case R.id.button_geo:
                    startAct(MapActivity.class, "map", 100);
                    break;
            }
        }
    };


    //파이어베이스 업로드

    private void storageUpload() {
        String id = getIntent().getStringExtra("id");
        DocumentReference dr;
        if (id == null) {
            dr = FirebaseFirestore.getInstance().collection("posts").document();
        } else {
            dr = FirebaseFirestore.getInstance().collection("posts").document(id);
        }



        final DocumentReference documentReference = dr;
        final String title = ((EditText)findViewById(R.id.txttitle)).getText().toString();
        final String pet_name = ((EditText)findViewById(R.id.txtname)).getText().toString();
        final String pet_age = ((EditText)findViewById(R.id.txtage)).getText().toString();

        if (title.length() >0 && pet_name.length()>0 && pet_age.length()>0 ) {
            final RelativeLayout loderLayout = findViewById(R.id.loaderll);
            loderLayout.setVisibility(View.VISIBLE);
            final ArrayList<String> contentsList = new ArrayList<>();

            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            for (int i=0; i<parent.getChildCount(); i++) {
                //텍스트 넣기
                Log.d(TAG,"카운트"+parent.getChildCount());
                View view = parent.getChildAt(i);
                if(view instanceof EditText) {
                    String text = ((EditText)view).getText().toString();
                    if(text.length() ==0)
                        text = "null";
                    //로그
                    Log.d(TAG,text);
                    if (text.length()>0) {
                        contentsList.add(text);
                    }
                } else if(view instanceof ImageView){        //이미지 뷰일때
                    contentsList.add(pathList.get(pathCount));      //리스트 셋
                    final StorageReference mountainImagesRef = storageRef.child("users/"+documentReference.getId()+"/"+pathCount+".jpg");
                    try {
                        InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                        StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index",""+(contentsList.size()-1)).build();

                        UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);//
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));

                                mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        contentsList.set(index, uri.toString());
                                        successCount++;

                                        if(pathList.size() == successCount) {
                                            //파이어베이스 게시글 데이터등록
                                            PostInfo postInfo = new PostInfo(title, contentsList, user.getUid(), laltitude, longitude, new Date(), address_lat, pet_name, pet_age, pet_sex, category);
                                            //PostInfo postInfo = new PostInfo(title, contentsList, user.getUid(), new Date());
                                            uploader(postInfo);
                                            finish();
                                            for(int a = 0; a < contentsList.size(); a++){
                                                Log.e("로그: ","콘덴츠: "+contentsList.get(a));
                                            }
                                        }
                                        loderLayout.setVisibility(View.GONE);
                                    }
                                });
                            }
                        });
                    } catch (FileNotFoundException e) {
                        Log.e("로그", "에러:"+e.toString());
                    }
                    pathCount++;
                }
            }
            //메인에 게시글 리스트 추가
            if(pathList.size() ==0) {

            }
        } else {
            Toast.makeText(this, "빈칸 없이 작성해주세요.", Toast.LENGTH_SHORT).show();
        }
    }




    private void storageUpload_free() {
        String id = getIntent().getStringExtra("id");
        DocumentReference dr;
        if (id == null) {
            dr = FirebaseFirestore.getInstance().collection("posts").document();
        } else {
            dr = FirebaseFirestore.getInstance().collection("posts").document(id);
        }


        final DocumentReference documentReference = dr;
        final String title = ((EditText)findViewById(R.id.txttitle)).getText().toString();

        if (title.length() >0) {
            final RelativeLayout loderLayout = findViewById(R.id.loaderll);
            loderLayout.setVisibility(View.VISIBLE);
            final ArrayList<String> contentsList = new ArrayList<>();

            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();


            for (int i=0; i<parent.getChildCount(); i++) {
                //텍스트 넣기
                View view = parent.getChildAt(i);
                Log.d(TAG,"카운트:"+parent.getChildCount());
                if(view instanceof EditText) {
                    String text = ((EditText)view).getText().toString();
                    if(text.length() ==0)
                        text = "null";
                    //로그
                    Log.d(TAG,text);
                    if (text.length()>0) {
                        contentsList.add(text);
                    }
                } else if(view instanceof ImageView) {        //이미지 뷰일때
                    Log.d(TAG,"이미지뷰일때");
                    contentsList.add(pathList.get(pathCount));      //리스트 셋
                    final StorageReference mountainImagesRef = storageRef.child("users/"+documentReference.getId()+"/"+pathCount+".jpg");
                    try {
                        InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                        StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index",""+(contentsList.size()-1)).build();

                        UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);//
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));

                                mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        contentsList.set(index, uri.toString());
                                        successCount++;

                                        if(pathList.size() == successCount) {
                                            //파이어베이스 게시글 데이터등록
                                            String pet_name="null";
                                            String pet_age="null";
                                            String pet_sex="null";
                                            String laltitude = "null";
                                            String longitude ="null";

                                            PostInfo postInfo = new PostInfo(title, contentsList, user.getUid(), laltitude, longitude, new Date(), address_lat, pet_name, pet_age, pet_sex, category);
                                            //PostInfo postInfo = new PostInfo(title, contentsList, user.getUid(), new Date());
                                            uploader(postInfo);
                                            finish();
                                            for(int a = 0; a < contentsList.size(); a++){
                                                Log.e("로그: ","콘덴츠: "+contentsList.get(a));
                                            }
                                        }
                                        loderLayout.setVisibility(View.GONE);
                                    }
                                });
                            }
                        });
                    } catch (FileNotFoundException e) {
                        Log.e("로그", "에러:"+e.toString());
                    }
                    pathCount++;
                }
            }
            //메인에 게시글 리스트 추가
            if(pathList.size() ==0) {
                contentsList.add("null");
                PostInfo postInfo = new PostInfo(title, contentsList, user.getUid(), "null", "null", new Date(), "null", "null", "null", "null", category);
                //PostInfo postInfo = new PostInfo(title, contentsList, user.getUid(), new Date());
                uploader(postInfo);
                finish();
            }
        } else {
            Toast.makeText(this, "빈칸 없이 작성해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploader(PostInfo postInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();     //초기화
        db.collection("posts").add(postInfo);
    }

    private void update(PostInfo postInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();     //초기화
        db.collection("posts").document(docID).set(postInfo);
        finish();
    }

    public void toastmsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void startAct(Class c, String media, int requestCode) {
        Intent intent = new Intent(this,  c);
        intent.putExtra("media", media);
        startActivityForResult(intent, requestCode);
    }
}
