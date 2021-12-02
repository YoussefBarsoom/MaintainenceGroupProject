package com.example.myapp.ui.feed;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapp.R;
import com.example.myapp.databinding.FragmentFeedBinding;
import com.example.myapp.ui.feed.FeedViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FeedFragment extends Fragment {

    private FeedViewModel feedViewModel;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    public ArrayList<String> new_post = new ArrayList<String>();

    private View binding;
    ScrollView scrollView;
    private int num_post=0;
    LinearLayout postBox;
    LayoutInflater mInflater ;

    View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        feedViewModel =
                new ViewModelProvider(this).get(FeedViewModel.class);

        binding = inflater.inflate(R.layout.fragment_feed, container, false);
        mInflater = inflater;

        root = binding;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference myRef_mess = FirebaseDatabase.getInstance().getReference("Posts");
        updatePost();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    public void get_newPosts()
    {

        final DatabaseReference myRef_mess = FirebaseDatabase.getInstance().getReference("Posts");
        DatabaseReference newRef = myRef_mess.orderByKey().limitToLast(1).getRef();

        myRef_mess.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if((Integer.parseInt(data.getKey().toString()))>num_post)
                    {
                        postBox = (LinearLayout) binding.findViewById(R.id.postBox);
                       View view = mInflater.inflate(R.layout.post_template, postBox, false);
                       TextView usertxt = (TextView) view.findViewById(R.id.usernamePost);
                       TextView datetxt = (TextView) view.findViewById(R.id.datePost);
                       datetxt.setText(data.child("Date").getValue().toString());
                        //usertxt.setText(data.child("username").getValue().toString());
                        TextView desctxt = (TextView) view.findViewById(R.id.descriptionPost);
                        desctxt.setText(data.child("Description").getValue().toString());
                        TextView stattxt = (TextView) view.findViewById(R.id.statusPost);
                        stattxt.setText(data.child("Status").getValue().toString());
                        mStorage = FirebaseStorage.getInstance().getReference().child("images/"+data.child("imageName").getValue().toString());
                        try {
                            final File localFile = File.createTempFile("post"+num_post,".png");
                            mStorage.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap= BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                    ((ImageView) view.findViewById(R.id.postImage)).setImageBitmap(bitmap);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        final DatabaseReference myRef_user = FirebaseDatabase.getInstance().getReference("Users");
                        myRef_user.child(data.child("UserID").getValue().toString()).child("username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {

                                if (!task.isSuccessful()) {
                                    Log.d("firebase", "Error getting data", task.getException());
                                }
                                else {
                                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                    usertxt.setText(String.valueOf(task.getResult().getValue()));

                                }
                            }
                        });
                        postBox.addView(view);
                        num_post++;
                    }}

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }

    public void updatePost()
    {
        Log.d("UPDATE","chat");
        final Handler hand = new Handler();
        hand.postDelayed(new Runnable() {
            @Override
            public void run() {
                //sendButton.setClickable(true);

            }
        }, 3500);
        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                get_newPosts();
                if(true) {
                  //  postMessage();
                    //bscrollView.fullScroll(View.FOCUS_DOWN);
                }
               // Log.d("post" ,new_post.toString()+"ARRAY");
                handler.postDelayed(this,3500);
            }
        }, 1000);

    }

}
