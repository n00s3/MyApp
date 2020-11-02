package com.example.sign;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.lang.Thread.sleep;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    String TAG = "LocationMap";
    private GoogleMap mMap;
    final Geocoder geocoder = new Geocoder(this);
    String address_lat;
    String laltitude;
    String longitude;

    private FirebaseUser user;
    private FirebaseFirestore db;
    private ArrayList<String> pathList = new ArrayList<>();

    private ArrayList<User> arrayList;
    LatLng xy;
    int flag=0;
    int check=0;

    RelativeLayout loderLayout;
    LocationManager lm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationmap);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        user = FirebaseAuth.getInstance().getCurrentUser();
        //mymark.icon(BitmapDescriptorFactory.fromBitmap(R.drawable.ic_location_on_black_24dp));

        if (check==0) {
            loderLayout = findViewById(R.id.loaderll);
            loderLayout.setVisibility(View.VISIBLE);
        }



        //현재위치 받아오기
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
        }

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
 //       lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                3500, // 통지사이의 최소 시간간격 (miliSecond)
                0, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                3500, // 통지사이의 최소 시간간격 (miliSecond)
                0, // 통지사이의 최소 변경거리 (m)
                mLocationListener);


//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()/
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        findViewById(R.id.button_check1).setOnClickListener(onClickListener);


    }
    public void location_load(){


    }


    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {


            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            Log.d(TAG, "onLocationChanged, location:" + location);
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            xy = new LatLng(latitude, longitude);

            //파이어베이스 유저 정보 받아오기
            arrayList = new ArrayList<>();

            db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(user.getUid());
            Log.d("유저ID", "유저ID : " + user.getUid());


            String xy1 = Double.toString(xy.latitude);
            String xy2 = Double.toString(xy.longitude);

            //위치 업데이트
            docRef.update("latitude", xy1,"longitude",xy2);




            db.collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            double lat;
                            double lon;
                            double result;
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("파이어스토어", document.getId() + " => " + document.getData());

                                    //같은 아이디일 경우 패스
                                    if(document.getId().equals(user.getUid())) {
                                        continue;
                                    }
                                    else {
                                        Log.d("파이어스토어1" ,document.getId() +" /// "+user.getUid());

                                        lat = Double.parseDouble(document.getData().get("latitude").toString());
                                        lon = Double.parseDouble(document.getData().get("longitude").toString());
                                        result = distance(xy.latitude, xy.longitude, lat, lon, "meter");

                                        //거리가 1000m 이내인 경우
                                        if (result <= 1000) {
                                            arrayList.add(new User(
                                                    document.getData().get("name").toString(),
                                                    document.getData().get("phone").toString(),
                                                    document.getData().get("photoUrl").toString(),
                                                    document.getData().get("latitude").toString(),
                                                    document.getData().get("longitude").toString()));
                                        }
                                    }

                                }//end for

                             //값 불러왔으면 마커추가
                                if(flag!=2) {
                                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                            .findFragmentById(R.id.map);
                                    mapFragment.getMapAsync(LocationActivity.this);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

            if (check==0) {
                loderLayout.setVisibility(View.GONE);
                check=1;
            }

        }

        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };


    //구글지도 Fragment
    @Override
    public void onMapReady(GoogleMap googleMap) {
        int list_size = arrayList.size();
        double lat, lon;
        LatLng target;

        Log.d("위치","리스트사이즈"+list_size);

        MarkerOptions mymark = new MarkerOptions();
        mymark.position(xy)
                .title("내 위치")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        CircleOptions circle1KM = new CircleOptions().center(xy) //원점
                .radius(1000)      //반지름 단위 : m
                .strokeWidth(0f)  //선너비 0f : 선없음
                .fillColor(Color.parseColor("#880000ff")); //배경색


        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        // Add a marker in Sydney, Australia, and move the camera.
        //LatLng def = new LatLng(37.532600, 127.024612);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Seoul"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (flag==1)
            mMap.clear();
        else if(flag==0)
            flag=1;



        mMap.addMarker(mymark.position(xy).title("내 위치"));
        mMap.addCircle(circle1KM);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(xy, 15));

        for(int i=0; i<list_size; i++) {
            Log.d("arraylist", "arraylist : "+arrayList.get(i).getName());
            lat = Double.parseDouble(arrayList.get(i).getLatitude());
            lon = Double.parseDouble(arrayList.get(i).getLongitude());
            target = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(target).title(arrayList.get(i).getName()).zIndex(i));
        }

        //기본 줌 서울
        //LatLng xy = new LatLng(37.532600, 127.024612);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(xy, 16));


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                /*
                List<Address> alist =null;
                mMap.addMarker(new MarkerOptions().position(latLng).title("잃어버린 위치"));

                String[] temp = latLng.toString().replace("lat/lng:","").split(",");
                laltitude = temp[0].replace("(", "");
                longitude = temp[1].replace(")", "");

                try {
                    alist = geocoder.getFromLocation(latLng.latitude,latLng.longitude, 10);
                }catch (IOException e) {
                    e.printStackTrace();
                }
                address_lat=alist.get(0).getAddressLine(0);
                //toastmsg(alist.get(0).getAddressLine(0));
                toastmsg(alist.get(0).getAdminArea());

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                */
            }
        });
    }


    //버튼 리스너
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_check1:
                    lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    lm.removeUpdates(mLocationListener);



                    flag=2;
                    finish();
                    return;
            }
        }
    };

    public void toastmsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.removeUpdates(mLocationListener);
            flag=2;
            finish();
            return;
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if(unit == "meter"){
            dist = dist * 1609.344;
        }

        return (dist);
    }


    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public void mOnPopupClick(Marker marker){
        //데이터 담아서 팝업(액티비티) 호출
        //String id = marker.getId().replaceAll("[^0-9]", "");
        int list_id = (int) marker.getZIndex();

        Log.d("마커", "마커ID  : "+list_id);


        Intent intent = new Intent(this, Popup_locationActivity.class);
        //if(FLAG.equals(""))
        intent.putExtra("info_name", arrayList.get(list_id).getName());
        intent.putExtra("info_photo", arrayList.get(list_id).getPhotoUrl());
        //intent.putExtra("key",arrayList.get(list_id))
        startActivityForResult(intent, 1);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        if ("내 위치".equals(marker.getTitle()))
            toastmsg("내 위치");
        else
            mOnPopupClick(marker);
        return false;
    }
}
