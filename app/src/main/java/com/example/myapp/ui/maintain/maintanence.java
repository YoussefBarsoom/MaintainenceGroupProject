package com.example.myapp.ui.maintain;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.myapp.R;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link maintanence#newInstance} factory method to
 * create an instance of this fragment.
 */
public class maintanence extends Fragment {

    private FeedViewModel feedViewModel;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private static final String MY_PREFS_NAME = "MyPrefsFile";
    public Integer userID=1;
    public ArrayList<String> new_task = new ArrayList<String>();

    private View binding;
    ScrollView scrollView;
    private int num_task=0;
    LinearLayout taskBox;
    LayoutInflater mInflater ;
    public maintanence() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static maintanence newInstance(String param1, String param2) {
        maintanence fragment = new maintanence();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = inflater.inflate(R.layout.fragment_maintanence, container, false);
        ;
        mInflater=inflater;

        SharedPreferences prefs = binding.getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getInt("userID", 0); //0 is the default value.
        Log.d("Tasks2",userID.toString());

        get_newTasks();
        return binding;
    }
    public void get_newTasks()
    {

        final DatabaseReference myRef_mess = FirebaseDatabase.getInstance().getReference("Posts");
        Log.d("Tasks2",userID.toString());

        myRef_mess.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Log.d("Tasks",data.child("assignedTo").getValue().toString());
                    Log.d("Tasks2",userID.toString());
                    if(Integer.parseInt(data.child("assignedTo").getValue().toString())==userID)
                    {
                        taskBox = (LinearLayout) binding.findViewById(R.id.task2Box);
                        View view = mInflater.inflate(R.layout.post_template, taskBox, false);
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
                            final File localFile = File.createTempFile("task"+num_task,".png");
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

                        taskBox.addView(view);
                        num_task++;
                    }}

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }






}
