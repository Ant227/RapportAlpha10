package com.example.rapportalpha10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {


    private ImageButton postCommentButton;
    private EditText CommentInputText;
    private RecyclerView CommentsList;


    private DatabaseReference UsersRef, PostsRef;
    private FirebaseAuth mAuth;


    private String Post_Key;
    private String CurrentUserId;

    private long CountComment = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Post_Key = getIntent().getExtras().get("PostKey").toString();



        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(Post_Key).child("Comments");
        CurrentUserId= mAuth.getCurrentUser().getUid();




        CommentsList = (RecyclerView) findViewById(R.id.comment_list);
        CommentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentsList.setLayoutManager(linearLayoutManager);

        CommentInputText = (EditText) findViewById(R.id.comments_input);
        postCommentButton =(ImageButton) findViewById(R.id.post_comment_btn);


        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsersRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists())
                        {
                            String userName = dataSnapshot.child("A_name").getValue().toString();
                            String profilePic = dataSnapshot.child("profileimage").getValue().toString();

                            validateComment(userName, profilePic);


                            CommentInputText.setText("");
                        }
                        else
                        {
                            Toast.makeText(CommentActivity.this, "Somethings went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();

        Query SortPostsInDecendingOrderComment = PostsRef.orderByChild("countComments");

        FirebaseRecyclerAdapter<Comments,CommentViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Comments, CommentViewHolder>
                (
                        Comments.class,
                        R.layout.all_coments_layout,
                        CommentViewHolder.class,
                        SortPostsInDecendingOrderComment


                )
        {
            @Override
            protected void populateViewHolder(CommentViewHolder commentViewHolder, Comments comments, int i)
            {
                commentViewHolder.setUsername(comments.getUsername());
                commentViewHolder.setComment(comments.getComment());
                commentViewHolder.setDate(comments.getDate());
                commentViewHolder.setTime(comments.getTime());
                commentViewHolder.setProfileimage(getApplicationContext() , comments.getProfileimage());


            }
        };
        CommentsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder
    {

        View mView ;



        public CommentViewHolder(@NonNull View itemView)
        {
            super(itemView);

            mView = itemView;

        }


        public void setUsername(String username)
        {
            TextView myUserName = (TextView) mView.findViewById(R.id.comment_user_name);
           if(username.equals("Ant"))
           {
               myUserName.setText("Developer." +username);
           }
           else
           {
               myUserName.setText("Dr." +username);
           }
        }

        public void setComment(String comment)
        {
            TextView myComment = (TextView) mView.findViewById(R.id.comment_text);
            myComment.setText(comment);
        }

        public void setTime(String time)
        {
            TextView myTime = (TextView) mView.findViewById(R.id.comment_time);
            myTime.setText("Time : "+time);
        }

        public void setDate(String date)
        {
            TextView myDate = (TextView) mView.findViewById(R.id.comment_date);
            myDate.setText("Date  "+date);

        }

        public void setProfileimage(Context ctx ,String profileimage)
        {
            CircleImageView proImg = (CircleImageView) mView.findViewById(R.id.comment_profileImage);
            Picasso.with(ctx).load(profileimage).placeholder(R.drawable.sample_profile).into(proImg);
        }

    }







    private void validateComment(String userName, String profilePic)
    {
        PostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    CountComment = dataSnapshot.getChildrenCount();
                }
                else
                {
                   CountComment = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        })   ;

        String commentText = CommentInputText.getText().toString();

        if(TextUtils.isEmpty(commentText))
        {
            Toast.makeText(this, "Please write text to Comment ...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String  saveCurrentDate = currentDate.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCurrentTime = currentTime.format(calForTime.getTime());

            Random myRandom = new Random();

            final String myRandomString = String.valueOf(myRandom.nextLong());

            final String RandomKey = CurrentUserId + "   " + myRandomString;

            HashMap commentMap = new HashMap();
            commentMap.put("uid",CurrentUserId);
            commentMap.put("comment",commentText);
            commentMap.put("date",saveCurrentDate);
            commentMap.put("time",saveCurrentTime);
            commentMap.put("username",userName);
            commentMap.put("profileimage",profilePic);
            commentMap.put("countComments",CountComment);
            PostsRef.child(RandomKey).updateChildren(commentMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(CommentActivity.this, "You have commented Successfully", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(CommentActivity.this, "Error Occurred: Try Again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CommentActivity.this, (CharSequence) e, Toast.LENGTH_SHORT).show();
                }
            });


        }


    }
}
