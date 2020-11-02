package com.example.sign;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostActivity extends FragmentActivity{
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


    String pet_sex;
    String category;


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

        //카테고리 받아오기
        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        toastmsg(category);

        //자유게시판일 경우
        if(category.contains("free")) {
            TextView txtname = findViewById(R.id.txtname);
            txtname.setVisibility(View.GONE);//이름
            TextView txtage = findViewById(R.id.txtage);
            txtage.setVisibility(View.GONE);//나이
            Button button_geo = findViewById(R.id.button_geo);
            button_geo.setVisibility(View.GONE);//위치등록버튼
            RadioGroup radioGroup = findViewById(R.id.radiorg);
            radioGroup.setVisibility(View.GONE);//성별체크
        }
        else if (category.contains("protect")) {
            Button button_geo = findViewById(R.id.button_geo);
            button_geo.setText("찾은 위치");
            TextView txtname = findViewById(R.id.txtname);
            txtname.setVisibility(View.GONE);//이름
            txtname.setText("null");
            TextView txtage = findViewById(R.id.txtage);
            txtage.setVisibility(View.GONE);//나이
            txtage.setText("null");

        }



        parent = findViewById(R.id.contentsLayout); //동적 추가될 레이어 초기화
        findViewById(R.id.button_check).setOnClickListener(onClickListener);
        findViewById(R.id.button_imgadd).setOnClickListener(onClickListener);
        findViewById(R.id.button_geo).setOnClickListener(onClickListener);
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

                    ImageView imageView = new ImageView(PostActivity.this);
                    imageView.setLayoutParams(layoutParams);
                    Glide.with(this).load(profilePath).override(1000).into(imageView);
                    parent.addView(imageView);
                    flag=1;
                    //이미지뷰 추가


                    EditText editText = new EditText(PostActivity.this);
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
                    if(flag==0 && category.equals("free")!=true) { ;
                        toastmsg("사진을 선택해주세요.");
                        break;
                    }

                    RadioGroup rg = (RadioGroup) findViewById(R.id.radiorg);
                    int id = rg.getCheckedRadioButtonId();
                    if(id==-1 && category.equals("free")!=true) {
                        toastmsg("성별을 체크해주세요.");
                        break;
                    }
                    RadioButton rb = (RadioButton) findViewById(id);

                    if(category.equals("free")!=true)
                        pet_sex = rb.getText().toString();

                    if(category.equals("free")!=true)
                        storageUpload();
                    else
                        storageUpload_free();

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



        if (title.length() >0 && pet_name.length()>0 && pet_age.length()>0) {
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
                } else {        //이미지 뷰일때
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
                } else {        //이미지 뷰일때
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

    public void toastmsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void startAct(Class c, String media, int requestCode) {
        Intent intent = new Intent(this,  c);
        intent.putExtra("media", media);
        startActivityForResult(intent, requestCode);
    }
}
