package com.example.sign;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import static java.lang.Thread.sleep;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    int flag = 0;
    int flag1=0;
    String TAG = "Post";
    private GoogleMap mMap;
    final Geocoder geocoder = new Geocoder(this);
    String address_lat;
    String laltitude;
    String longitude;

    private FirebaseUser user;
    private ArrayList<String> pathList = new ArrayList<>();
    LatLng xy;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                0, // 통지사이의 최소 시간간격 (miliSecond)
                0, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                0, // 통지사이의 최소 시간간격 (miliSecond)
                0, // 통지사이의 최소 변경거리 (m)
                mLocationListener);





        findViewById(R.id.button_check1).setOnClickListener(onClickListener);

    }
    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            Log.d("test", "onLocationChanged, location:" + location);
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            xy = new LatLng(latitude, longitude);


            if(flag1==0) {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(MapActivity.this);
            }

            if(flag1==0)
                flag1=1;
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
        mMap = googleMap;
        // Add a marker in Sydney, Australia, and move the camera.
        //LatLng def = new LatLng(37.532600, 127.024612);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Seoul"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //현재위치
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(xy, 16));

        //기본 줌 서울
        //LatLng xy = new LatLng(37.532600, 127.024612);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(xy, 16));


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
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
                flag=1;
            }
        });
    }

    //버튼 리스너
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_check1:
                    if (flag==1) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("address", address_lat);
                        resultIntent.putExtra("laltitude", laltitude);
                        resultIntent.putExtra("longitude", longitude);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                        break;
                    }
                    else {
                        toastmsg("위치를 선택해주세요");
                        break;
                    }
            }
        }
    };

    public void toastmsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (flag==1) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("address", address_lat);
            resultIntent.putExtra("laltitude", laltitude);
            resultIntent.putExtra("longitude", longitude);
            setResult(RESULT_OK, resultIntent);
            finish();
            return;
        }
        else {
            toastmsg("위치를 선택해주세요");
            return;
        }
    }


}
