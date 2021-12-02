package com.example.myapp.ui.rate;

import static android.content.Context.MODE_PRIVATE;
import static com.example.myapp.Login.MY_PREFS_NAME;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

public class RateFragment extends Fragment {
    private FeedViewModel feedViewModel;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private static final String MY_PREFS_NAME = "MyPrefsFile";
    public Integer userID=3;
    public ArrayList<String> new_task = new ArrayList<String>();

    private View binding;
    ScrollView scrollView;
    private int num_task=0;
    LinearLayout taskBox;
    LayoutInflater mInflater ;
    View rootView;
    RatingBar rBar ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.fragment_rate, container, false);
         mInflater=inflater;

        get_newTasks();
        return rootView;
    }

    public void get_newTasks()
    {
        SharedPreferences prefs = rootView.getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getInt("userID", 0);
        final DatabaseReference myRef_mess = FirebaseDatabase.getInstance().getReference("Posts");
        Log.d("Tasks2",userID.toString());

        myRef_mess.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Log.d("Tasks2",userID.toString());
                    if(Integer.parseInt(data.child("UserID").getValue().toString())==userID)
                    {
                        Log.d("Tasks",data.child("assignedTo").getValue().toString());

                        taskBox = (LinearLayout) rootView.findViewById(R.id.task2Box);
                        View view = mInflater.inflate(R.layout.rating_template, taskBox, false);
                        TextView usertxt = (TextView) view.findViewById(R.id.usernamePost);
                        TextView datetxt = (TextView) view.findViewById(R.id.datePost);
                        datetxt.setText(data.child("Date").getValue().toString());
                        //usertxt.setText(data.child("username").getValue().toString());
                        TextView desctxt = (TextView) view.findViewById(R.id.descriptionPost);
                        desctxt.setText(data.child("Description").getValue().toString());
                        TextView costTxt = (TextView) view.findViewById(R.id.costBox);
                        if(data.child("cost").getValue().toString().compareTo("-1")!=0)
                        {
                            costTxt.setText(data.child("cost").getValue().toString()+"$");
                        }
                        else{
                            costTxt.setText("Not yet");
                        }
                        TextView stattxt = (TextView) view.findViewById(R.id.statusPost);
                        stattxt.setText(data.child("Status").getValue().toString());
                        RatingBar rBar = (RatingBar) view.findViewById(R.id.ratingBar);
                        Button btn = (Button) view.findViewById(R.id.rateBtn);

                        if(Float.parseFloat(data.child("rating").getValue().toString())==-1&&(data.child("Status").getValue().toString().compareTo("Resolved")==0))
                        {

                            btn.setOnClickListener(new View.OnClickListener() {
                                                       @Override
                                                       public void onClick(View v) {
                                                           int numStars = rBar.getNumStars();
                                                           float getRating = rBar.getRating();
                                                           rBar.setVisibility(View.GONE);
                                                           myRef_mess.child(data.getKey().toString()).child("rating").setValue(getRating);


                                                       }
                                                   }
                            );
                        }
                        else
                        {
                            rBar.setVisibility(View.GONE);
                            btn.setVisibility(View.GONE);
                        }

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
                        //view.add
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