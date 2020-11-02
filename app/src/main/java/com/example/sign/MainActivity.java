package com.example.sign;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private ArrayList<PostInfo> arrayList;
    //private ImageView profileImageVIew;

    int check=0;
    String category="free";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //화면 회전잠금

        //네비게이션 드로어
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);





        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                switch (id) {
                    //자유게시판
                    case R.id.navigation_item_freec:
                        //찾는중
                        //Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        category = "free";
                        postUpdate_cat(category);
                        break;

                    case R.id.navigation_item_attachment:
                        //찾는중
                        //Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        category = "find";
                        //tag_txt.setText("찾는중");
                        postUpdate_cat(category);
                        break;

                    case R.id.navigation_item_images:
                        //보호중
                        //Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        category = "protect";
                        //tag_txt.setText("보호중");
                        postUpdate_cat(category);
                        break;

                    case R.id.navigation_item_location:
                        //산책
                        //Toast.makeText(MainActivity.this, "같이 산책즐기기", Toast.LENGTH_LONG).show();
                        startAct(LocationActivity.class);
                        break;

                    case R.id.nav_sub_menu_item01:
                        Toast.makeText(MainActivity.this, "마이페이지 이동", Toast.LENGTH_LONG).show();
                        startAct(MyPageActivity.class);
                        break;

                    case R.id.nav_sub_menu_item00:
                        Toast.makeText(MainActivity.this, "쪽지함 이동", Toast.LENGTH_LONG).show();
                        startAct(MessageActivity.class);
                        break;

                    case R.id.nav_sub_menu_item02:
                        Toast.makeText(MainActivity.this, "로그아웃", Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                        startAct(login.class);
                        break;

                }
                return true;
            }
        });



        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user = FirebaseAuth.getInstance().getCurrentUser();
        arrayList = new ArrayList<>();


        if (user == null) {
            startAct(login.class);
        } else {
            db = FirebaseFirestore.getInstance();
            final DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document != null) {
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {


                                        User usr = documentSnapshot.toObject(User.class);

                                        View nav_header_view = navigationView.getHeaderView(0);

                                        ImageView nav_header_img = (ImageView) nav_header_view.findViewById(R.id.profile_image);
                                        TextView nav_header_id_text1 = (TextView) nav_header_view.findViewById(R.id.Userinfo_txt);
                                        nav_header_id_text1.setText(user.getEmail());
                                        String temp = usr.getPhotoUrl();
                                        temp = temp.trim();
                                        Glide.with(MainActivity.this).load(temp).centerCrop().override(100, 100).into(nav_header_img);
                                    }
                                });

                            } else {
                                Log.d(TAG, "No such document");
                                startAct(MemberActivity.class);
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

            postUpdate_cat("free");


        }


        findViewById(R.id.floatingActionButton).setOnClickListener(onClickListener);
        //findViewById(R.id.floatingActionButton2).setOnClickListener(onClickListener);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //if(category.equals("free")!=true)
            getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                if(category.equals("free")!=true) {
                    toastmsg("검색 팝업");
                    mOnPopupClick();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //인텐트 값 받기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                //데이터 받기
                check=1;
                String text = data.getStringExtra("result");
                if(text.equals("전체"))
                    check=0;


                postsUpdate(text);
            }
        }
    }

    //팝업띄우기
    public void mOnPopupClick(){
        //데이터 담아서 팝업(액티비티) 호출
        Intent intent = new Intent(this, PopupActivity.class);
        //if(FLAG.equals(""))
        intent.putExtra("option", "all");
        startActivityForResult(intent, 1);
    }



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.floatingActionButton:
                    //toastmsg("글 작성");
                    mystartAct(PostActivity.class, category);
                    break;
            }
        }
    };

    private void startAct(Class c) {
        Intent intent = new Intent(this,  c);
        startActivity(intent);
    }
    private void mystartAct(Class c, String cat) {
        Intent intent = new Intent(this,  c);
        intent.putExtra("category", cat);
        startActivity(intent);
    }

    public void toastmsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    private void postsUpdate(final String geo) {
        if (user != null) {
            CollectionReference collectionReference = db.collection("posts");
            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                arrayList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //카테고리 선택된 값만
                                    if (document.getData().get("category").toString().equals(category)) {

                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        String[] temp = document.getData().get("address_lat").toString().split(" ");
                                        if (check == 0) {
                                            arrayList.add(new PostInfo(
                                                    document.getData().get("title").toString(),
                                                    (ArrayList<String>) document.getData().get("contents"),
                                                    document.getData().get("publisher").toString(),
                                                    document.getData().get("laltitude").toString(),
                                                    document.getData().get("longitude").toString(),
                                                    new Date(document.getDate("createdAt").getTime()),
                                                    document.getData().get("address_lat").toString(),
                                                    document.getData().get("pet_name").toString(),
                                                    document.getData().get("pet_age").toString(),
                                                    document.getData().get("pet_sex").toString(),
                                                    document.getData().get("category").toString()));
                                        } else if (temp[1].equals(geo)) {
                                            arrayList.add(new PostInfo(
                                                    document.getData().get("title").toString(),
                                                    (ArrayList<String>) document.getData().get("contents"),
                                                    document.getData().get("publisher").toString(),
                                                    document.getData().get("laltitude").toString(),
                                                    document.getData().get("longitude").toString(),
                                                    new Date(document.getDate("createdAt").getTime()),
                                                    document.getData().get("address_lat").toString(),
                                                    document.getData().get("pet_name").toString(),
                                                    document.getData().get("pet_age").toString(),
                                                    document.getData().get("pet_sex").toString(),
                                                    document.getData().get("category").toString()));
                                        }
                                    }
                                }
                                RecyclerView recyclerView1 = findViewById(R.id.recyclerView);
                                recyclerView1.setHasFixedSize(true);
                                recyclerView1.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                                RecyclerView.Adapter mAdapter1 = new MainAdapter(MainActivity.this, arrayList, category);
                                recyclerView1.setAdapter(mAdapter1);
                               // MainAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    //처음 모든 게시글 출력
    private void postUpdate_cat(final String category) {
        //게시글 메인 출력
        //게시글 옵션 설정값에 따라 지역 분기
        db.collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            arrayList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                //카테고리 선택된 값만
                                if (document.getData().get("category").toString().equals(category)) {
                                        arrayList.add(new PostInfo(
                                                document.getData().get("title").toString(),
                                                (ArrayList<String>) document.getData().get("contents"),
                                                document.getData().get("publisher").toString(),
                                                document.getData().get("laltitude").toString(),
                                                document.getData().get("longitude").toString(),
                                                new Date(document.getDate("createdAt").getTime()),
                                                document.getData().get("address_lat").toString(),
                                                document.getData().get("pet_name").toString(),
                                                document.getData().get("pet_age").toString(),
                                                document.getData().get("pet_sex").toString(),
                                                document.getData().get("category").toString()));
                                }
                            }
                            RecyclerView recyclerView = findViewById(R.id.recyclerView);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                            RecyclerView.Adapter mAdapter = new MainAdapter(MainActivity.this, arrayList, category);
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}


