package com.example.sign;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import static android.support.constraint.Constraints.TAG;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.GalleryViewHolder> {
    private ArrayList<PostInfo> mDataset;
    private ArrayList<String> mDataset1;
    private Activity activity;
    private LinearLayout parent;
    private String category;
    private FirebaseFirestore db;
    private String user_name[];

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        GalleryViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public MessageAdapter(Activity activity, ArrayList<String> myDataset1, String category) {
        mDataset1 = myDataset1;
        this.activity = activity;
        this.category = category;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg, parent, false);

        final GalleryViewHolder galleryViewHolder = new GalleryViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        return galleryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final GalleryViewHolder holder, final int position) {


        CardView cardView = holder.cardView;


        /*
        if(mDataset.get(position).getCategory().contains("MyPage")) {
            //
        } else {
            img1.setVisibility(View.GONE);
            img2.setVisibility(View.GONE);
        }*/


        //제목
        final TextView title_txt = cardView.findViewById(R.id.title_txt);
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(mDataset1.get(position));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //user_name[] = document.getData().get("name").toString();
                        title_txt.setText(document.getData().get("name").toString()+ "님의 메시지");
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });










        //뷰 클릭리스너
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                //Toast.makeText(context, mDataset.get(position).getAddress_lat(), Toast.LENGTH_SHORT).show();
                //Intent it = new Intent(context, PostViewActivity.class);
                //it.putExtra("list", mDataset);



                String key;
                key = mDataset1.get(position);


                context.startActivity(new Intent(context, MessageChatActivity.class).putExtra("key", key)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            }
        });

        /*
        //삭제 버튼
        cardView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {      //삭제
            @Override
            public void onClick(View v) {
                PostInfo post;
                post = mDataset.get(position);

                post_delete(post.getDocID(), post.getTitle());
                Context context = v.getContext();
                context.startActivity(new Intent(context, MyPageActivity.class).putExtra("title", post.getTitle()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });



        //수정버튼
        cardView.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {    //수정 넘기기
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                PostInfo post;
                post = mDataset.get(position);

                context.startActivity(new Intent(context, PostUpdateActivity.class).putExtra("publisher", post.getPublisher())
                        .putExtra("title", post.getTitle())
                        .putExtra("pet_name", post.getPet_name())
                        .putExtra("pet_sex", post.getPet_sex())
                        .putExtra("pet_age", post.getPet_age())
                        .putExtra("address", post.getAddress_lat())
                        .putExtra("laltitude", post.getLaltitude())
                        .putExtra("longitude", post.getLongitude())
                        .putExtra("imgurl", post.getContents().get(1).toString())
                        .putExtra("contents", post.getContents().get(0).toString())
                        .putExtra("category", post.getCategory().toString())
                        .putExtra("docID", post.getDocID())
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        */


    }
    private void post_update(String docID) {



    }

    @Override
    public int getItemCount() {
        //return mDataset.size();
        return mDataset1.size();
    }

    public void toastmsg(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }
}