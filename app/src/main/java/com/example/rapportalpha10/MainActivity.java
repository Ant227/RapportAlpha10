package com.example.rapportalpha10;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity   {

    private RelativeLayout addPostBtn;
    private RelativeLayout ProfileBtn;

    private ImageView addPostBtn1;
    private ImageView ProfileBtn1;



    private RecyclerView postList;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, PostRef ,  LikeRef;
    private DataSnapshot lastResult ;


    private static final int TOTAL_ITEMS_TO_LOAD = 5;
    private int mCurrentPost = 0;

    String currentUserId;
    Boolean LikeChecker = false;

    Boolean isCheck = false;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        addPostBtn = findViewById(R.id.add_new_post_btn);
        ProfileBtn = findViewById(R.id.post_profile_setting_btn);

        addPostBtn1 = findViewById(R.id.add_new_post_btn1);
        ProfileBtn1 = findViewById(R.id.post_profile_setting_btn1);


        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        UsersRef.keepSynced(true);
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        PostRef.keepSynced(true);
        LikeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        LikeRef.keepSynced(true);


        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToPostActivity();
            }
        });

        ProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToProfileActivity();
            }
        });


        addPostBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToPostActivity();
            }
        });

        ProfileBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToProfileActivity();
            }
        });


        postList = (RecyclerView) findViewById(R.id.all_user_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);
        DisplayAllUsersPosts();

    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }



    private void sendUserToProfileActivity()
    {
        Intent intentProfile = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intentProfile);
    }


    private void sendUserToPostActivity()
    {
        Intent intentPost = new Intent(MainActivity.this,PostActivity.class);
        startActivity(intentPost);
    }


    private void DisplayAllUsersPosts()
    {


        Query  SortPostsInDecendingOrder;

        if(lastResult == null)
        {
              SortPostsInDecendingOrder = PostRef.orderByChild("counter").limitToFirst(3);
         }
        else
        {
            SortPostsInDecendingOrder = PostRef.orderByChild("counter").limitToFirst(3);
        }



        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Posts, PostsViewHolder>
                        (
                                Posts.class,
                                R.layout.all_post_layout,
                                PostsViewHolder.class,
                                SortPostsInDecendingOrder  )
                {
                    @Override
                    protected void populateViewHolder(final PostsViewHolder viewHolder, final Posts model, int position)
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
                               Intent PersonIntent = new Intent(MainActivity.this , PersonActivity.class);
                               PersonIntent.putExtra("person_user_id", person_user_id);
                               startActivity(PersonIntent);
                           }
                       });

                       viewHolder.username.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {

                               String person_user_id = model.getUid();
                               Intent PersonIntent = new Intent(MainActivity.this , PersonActivity.class);
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
                                Intent commentsIntent = new Intent(MainActivity.this,CommentActivity.class);
                                commentsIntent.putExtra("PostKey",postKey);
                                startActivity(commentsIntent);
                            }
                        });



                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent clickPostIntent = new Intent(MainActivity.this,ClickPostActivity.class);
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
                                            if(dataSnapshot.child(postKey).hasChild(currentUserId))
                                            {
                                                LikeRef.child(postKey).child(currentUserId).removeValue();
                                                LikeChecker = false;
                                            }
                                            else
                                            {
                                                LikeRef.child(postKey).child(currentUserId).setValue(true);
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
        postList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder
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


        public PostsViewHolder(View itemView)
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



        public void setProfileimage(final Context ctx, final String profileimage)
        {

            Picasso.with(ctx).load(profileimage).networkPolicy(NetworkPolicy.OFFLINE).into(image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                        Picasso.with(ctx).load(profileimage).into(image);
                }
            });
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



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            SendUserToLoginActivity();
        }
        else
        {
            CheckUserExistence();
        }
    }
    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }

    private void CheckUserExistence()
    {
        final String currentuser_id = mAuth.getCurrentUser().getUid();
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(currentuser_id))
                {
                    SendUserToSetupActivity();
                }
            }

            private void SendUserToSetupActivity()
            {
                Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(setupIntent);
                finish();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
