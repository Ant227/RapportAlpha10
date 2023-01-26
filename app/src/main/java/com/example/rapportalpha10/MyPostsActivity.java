package com.example.rapportalpha10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostsActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView myPostList;

    private  FirebaseAuth mAuth;
    private  String CurrentUserID;
    private DatabaseReference PostsRef,  LikeRef;
    Boolean LikeChecker = false;
    Boolean isCheck = false;

    private String PersonUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        PersonUserID = getIntent().getExtras().get("person_user_id").toString();

        FirebaseRelated();
        SettingUpToolbar();
        LayoutManagerMyPosts();

        DisplayMyOwnPost();

    }

    private void DisplayMyOwnPost()
    {
        Query myPostsQuery = PostsRef.orderByChild("uid")
                .startAt(PersonUserID).endAt(PersonUserID + "\uf8ff");

        FirebaseRecyclerAdapter<Posts, MainActivity.PostsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Posts, MainActivity.PostsViewHolder>
                        (
                                Posts.class,
                                R.layout.all_post_layout,
                                MainActivity.PostsViewHolder.class,
                                myPostsQuery
                        )
                {
                    @Override
                    protected void populateViewHolder(final MainActivity.PostsViewHolder viewHolder, final Posts model, int position)
                    {



                        final String postKey = getRef(position).getKey();
                        viewHolder.setFullname(model.getUsername());
                        viewHolder.setTime(model.getTime());
                        viewHolder.setJob(model.getJob());
                        viewHolder.setCity(model.getCity());
                        viewHolder.setDate(model.getDate());
                        viewHolder.setDescription(model.getDescription());
                        viewHolder.setPostimage(getApplicationContext(), model.getPostimage());
                        viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());


                        viewHolder.setLikeButtonStatus(postKey);

                        viewHolder.image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String person_user_id = model.getUid();
                                Intent PersonIntent = new Intent(MyPostsActivity.this , PersonActivity.class);
                                PersonIntent.putExtra("person_user_id", person_user_id);
                                startActivity(PersonIntent);
                            }
                        });

                        viewHolder.username.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String person_user_id = model.getUid();
                                Intent PersonIntent = new Intent(MyPostsActivity.this , PersonActivity.class);
                                PersonIntent.putExtra("person_user_id", person_user_id);
                                startActivity(PersonIntent);

                            }
                        });

                        viewHolder.CommentPostButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SendUserToComentsActivity();

                            }

                            private void SendUserToComentsActivity()
                            {
                                Intent commentsIntent = new Intent(MyPostsActivity.this,CommentActivity.class);
                                commentsIntent.putExtra("PostKey",postKey);
                                startActivity(commentsIntent);
                            }
                        });



                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent clickPostIntent = new Intent(MyPostsActivity.this,ClickPostActivity.class);
                                clickPostIntent.putExtra("postkey" , postKey);
                                startActivity(clickPostIntent);
                            }
                        });


                        viewHolder.LikePostButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                LikeChecker = true;

                                LikeRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                        if(LikeChecker.equals(true))
                                        {
                                            if(dataSnapshot.child(postKey).hasChild(CurrentUserID))
                                            {
                                                LikeRef.child(postKey).child(CurrentUserID).removeValue();
                                                LikeChecker = false;
                                            }
                                            else
                                            {
                                                LikeRef.child(postKey).child(CurrentUserID).setValue(true);
                                                LikeChecker = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        });




                        viewHolder.PostDescriptionSeeMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (isCheck) {
                                    viewHolder.PostDescriptionSeeMore.setMaxLines(5000);
                                    isCheck = false;
                                } else {
                                    viewHolder.PostDescriptionSeeMore.setMaxLines(4);

                                    isCheck = true;
                                }


                            }
                        });




                    }
                };
        myPostList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class MyPostsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        ImageButton LikePostButton, CommentPostButton;
        TextView DisplayNoOfLikes;
        int countLikes;
        String currentUserId;
        DatabaseReference LikesRef;
        TextView PostDescriptionSeeMore;
        LinearLayout PostColor;

        CircleImageView image;
        TextView username;


        public MyPostsViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;

            LikePostButton = (ImageButton) mView.findViewById(R.id.voteButton);
            CommentPostButton = (ImageButton) mView.findViewById(R.id.commentButton);
            DisplayNoOfLikes = (TextView) mView.findViewById(R.id.voteCount);
            PostDescriptionSeeMore = (TextView) mView.findViewById(R.id.post_description);
            PostColor = (LinearLayout) mView.findViewById(R.id.post_Color);

            LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            username = (TextView) mView.findViewById(R.id.post_user_name);

        }
        public  void setLikeButtonStatus(final String postkey)
        {
            LikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.child(postkey).hasChild(currentUserId))
                    {
                        countLikes = (int) dataSnapshot.child(postkey).getChildrenCount();
                        LikePostButton.setImageResource(R.drawable.ic_upvote);
                        if(countLikes == 1 )
                        {
                            DisplayNoOfLikes.setText(Integer.toString(countLikes)+ " Like");
                        }
                        else  if(countLikes == 0)
                        {
                            DisplayNoOfLikes.setText("Like");
                        }
                        else
                        {
                            DisplayNoOfLikes.setText(Integer.toString(countLikes)+ " Likes");
                        }

                    }
                    else
                    {
                        countLikes = (int) dataSnapshot.child(postkey).getChildrenCount();
                        LikePostButton.setImageResource(R.drawable.disvotexml);
                        DisplayNoOfLikes.setText(Integer.toString(countLikes));
                        if(countLikes == 1 )
                        {
                            DisplayNoOfLikes.setText(Integer.toString(countLikes)+ " Like");
                        }
                        else  if(countLikes == 0)
                        {
                            DisplayNoOfLikes.setText("Like");
                        }
                        else
                        {
                            DisplayNoOfLikes.setText(Integer.toString(countLikes)+ " Likes");
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }



        public void setProfileimage(Context ctx, String profileimage)
        {

            Picasso.with(ctx).load(profileimage).into(image);
        }


        public void setFullname(String fullname)
        {

            username.setText("Dr."+fullname);

        }

        public void setJob(String job)
        {
            TextView PostTime = (TextView) mView.findViewById(R.id.post_user_job);
            PostTime.setText(job);
        }

        public void setCity(String city)
        {
            TextView PostTime = (TextView) mView.findViewById(R.id.post_user_city);
            PostTime.setText(city);
        }

        public void setTime(String time)
        {
            TextView PostTime = (TextView) mView.findViewById(R.id.post_time);
            PostTime.setText("    " + time);
        }

        public void setDate(String date)
        {
            TextView PostDate = (TextView) mView.findViewById(R.id.post_date);
            PostDate.setText( date);
        }

        public void setDescription(String description)
        {
            TextView PostDescription = (TextView) mView.findViewById(R.id.post_description);
            PostDescription.setText(description);
        }

        public void setPostimage(Context ctx1,  String postimage)
        {
            ImageView PostImage = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx1).load(postimage).into(PostImage);
        }
    }

    private void LayoutManagerMyPosts()
    {
        myPostList = (RecyclerView) findViewById(R.id.my_all_post_list);
        myPostList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myPostList.setLayoutManager(linearLayoutManager);

    }

    private void FirebaseRelated()
    {
        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        LikeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
    }

    private void SettingUpToolbar()
    {
        mToolbar = findViewById(R.id.my_post_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Posts");
    }
}
