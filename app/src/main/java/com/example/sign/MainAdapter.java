package com.example.sign;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static android.support.constraint.Constraints.TAG;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.GalleryViewHolder> {
    private ArrayList<PostInfo> mDataset;
    private Activity activity;
    private LinearLayout parent;
    private String category;
    private FirebaseFirestore db;

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        GalleryViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public MainAdapter(Activity activity, ArrayList<PostInfo> myDataset, String category) {
        mDataset = myDataset;
        this.activity = activity;
        this.category = category;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);

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

        ImageView img1 = cardView.findViewById(R.id.delete);
        ImageView img2 = cardView.findViewById(R.id.edit);

        if(mDataset.get(position).getCategory().contains("MyPage")) {
            //
        } else {
            img1.setVisibility(View.GONE);
            img2.setVisibility(View.GONE);
        }


        //제목
        TextView title_txt = cardView.findViewById(R.id.title_txt);
        title_txt.setText(mDataset.get(position).getTitle());

        //올린날짜
        TextView createdAt_txt = cardView.findViewById(R.id.createdAt_txt);
        createdAt_txt.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mDataset.get(position).getCreatedAt()));
        //createdAt_txt.setText(mDataset.get(position).getCreatedAt().toString());

        TextView geo_txt = cardView.findViewById(R.id.txt_geo);
        if(mDataset.get(position).getAddress_lat().equals("default") || mDataset.get(position).getAddress_lat().equals("null")) {
            geo_txt.setText(mDataset.get(position).getContents().get(0));
        } else {
            geo_txt.setText(mDataset.get(position).getAddress_lat());
        }

        TextView tag_txt = cardView.findViewById(R.id.tag_txt);
        //tag_txt.setTextColor();
        if(category.equals("free")) {
            category = "[자유게시판]";
        }
        else if(category.equals("find"))
            category="[찾는중]";
        else if(category.equals("protect"))
            category="[보호중]";
        else if (category.equals("MyPage")) {
            category = "";
        }

        tag_txt.setText(category);




        //뷰 클릭리스너
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                //Toast.makeText(context, mDataset.get(position).getAddress_lat(), Toast.LENGTH_SHORT).show();
                //Intent it = new Intent(context, PostViewActivity.class);
                //it.putExtra("list", mDataset);



                PostInfo post;
                post = mDataset.get(position);

                context.startActivity(new Intent(context, PostViewActivity.class).putExtra("publisher", post.getPublisher())
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
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

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



    }


    private void post_delete(String docID, final String title) {
        db = FirebaseFirestore.getInstance();
        db.collection("posts").document(docID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        toastmsg("<"+title+">이(가) 삭제되었습니다.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                        toastmsg("삭제 실패");
                    }
                });


    }

    private void post_update(String docID) {



    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void toastmsg(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }
}