package com.example.sign;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static android.support.constraint.Constraints.TAG;

public class MessageChatAdapter extends RecyclerView.Adapter<MessageChatAdapter.GalleryViewHolder> {
    private ArrayList<Message> mDataset;
    private ArrayList<String> mDataset1;
    private Activity activity;
    private LinearLayout parent;
    private String key;
    private FirebaseFirestore db;

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        GalleryViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public MessageChatAdapter(Activity activity, ArrayList<Message> myDataset, String key) {
        mDataset = myDataset;
        this.activity = activity;
        this.key = key;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msgchat, parent, false);

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
        cardView.setCardElevation(0);

        /*
        final ImageView profile_img = cardView.findViewById(R.id.pro_img);

        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(key);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User usr = documentSnapshot.toObject(User.class);

                String temp = usr.getPhotoUrl();
                temp = temp.trim();
                Glide.with(activity).load(temp).centerCrop().override(1000, 1000).into(profile_img);
            }
        });
        */





        //제목
        final TextView title_txt = cardView.findViewById(R.id.title_txt);
        final TextView time_txt = cardView.findViewById(R.id.time_txt);


        title_txt.setText(mDataset.get(position).getMsg());
        //time_txt.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mDataset.get(position).getTime()));
        time_txt.setText(new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(mDataset.get(position).getTime()));



        if(mDataset.get(position).getSend().equals(key)) {  //자신과 다르면
            title_txt.setGravity(Gravity.LEFT);
            time_txt.setGravity(Gravity.LEFT);
        }
        else {                                              // 자신일때
            title_txt.setGravity(Gravity.RIGHT);
            time_txt.setGravity(Gravity.RIGHT);
            //profile_img.setVisibility(View.GONE);
        }









        //뷰 클릭리스너
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }



    private void post_update(String docID) {



    }

    @Override
    public int getItemCount() {
        return mDataset.size();
        //return mDataset1.size();
    }

    public void toastmsg(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }
}