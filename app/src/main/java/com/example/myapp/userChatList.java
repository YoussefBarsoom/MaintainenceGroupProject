package com.example.myapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.myapp.ui.notifications.NotificationsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link userChatList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class userChatList extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String MY_PREFS_NAME = "MyPrefsFile";
    public Integer userID=1;
    private DatabaseReference mDatabase;
    public ArrayList<String> new_messages = new ArrayList<String>();

    public ArrayList<Integer> chatList = new ArrayList<Integer>();
    // TODO: Rename and change types of parameters
    private View binding;
    private LinearLayout chatBox;
    public ArrayList<String> all_Chat = new ArrayList<String>();
    private LayoutInflater mInflater;

    public userChatList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment userChatList.
     */
    // TODO: Rename and change types and number of parameters
    public static userChatList newInstance(String param1, String param2) {
        userChatList fragment = new userChatList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        binding = inflater.inflate(R.layout.fragment_user_chat_list, container, false);
        ;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference myRef_mess = FirebaseDatabase.getInstance().getReference("chatRelation");
        chatBox = (LinearLayout) binding.findViewById(R.id.contactList);

        SharedPreferences prefs = binding.getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getInt("userID", 0); //0 is the default value.
        myRef_mess.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    View vi = inflater.inflate(R.layout.contact_chat_box,chatBox, false);
                    TextView txt = (TextView) vi.findViewById(R.id.nameDisplay);
                    Log.d("Debuff",data.getKey().toString()+" JI ");

                    if(Integer.parseInt(data.child("0").getValue().toString()) == (userID)) {
                        chatList.add(Integer.parseInt(data.child("1").getValue().toString()));
                        Log.d("Debuff",data.getKey().toString()+" JI ");

                        vi.setOnClickListener(new View.OnClickListener()
                        {
                            public void onClick(View v) {
                                AppCompatActivity activity = (AppCompatActivity) binding.getContext();
                                Fragment myFragment = new NotificationsFragment();
                                Bundle bundle = new Bundle();
                                bundle.putInt("contactID", Integer.parseInt(data.getKey().toString()));
                                Log.d("Debuff","  JI");
                                myFragment.setArguments(bundle);
                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.chatContainer,myFragment).addToBackStack(null).commit();
                        }

                        });

                        final DatabaseReference myRef_user = FirebaseDatabase.getInstance().getReference("Users");
                        myRef_user.child(data.child("1").getValue().toString()).child("username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {

                                if (!task.isSuccessful()) {
                                    Log.d("firebase", "Error getting data", task.getException());
                                }
                                else {
                                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                    txt.setText( String.valueOf(task.getResult().getValue()));
                                    chatBox.addView(vi);

                                }
                            }
                        });
                    }
                    else if(Integer.parseInt(data.child("1").getValue().toString()) == (userID)){
                        chatList.add(Integer.parseInt(data.child("0").getValue().toString()));
                        vi.setOnClickListener(new View.OnClickListener()
                        {
                            public void onClick(View v) {
                                AppCompatActivity activity = (AppCompatActivity) binding.getContext();
                                Fragment myFragment = new NotificationsFragment();
                                Bundle bundle = new Bundle();
                                bundle.putInt("contactID", Integer.parseInt(data.getKey().toString()));
                                Log.d("Debuff","  JI");
                                myFragment.setArguments(bundle);
                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.chatContainer,myFragment).addToBackStack(null).commit();
                            }
                        });
                        final DatabaseReference myRef_user = FirebaseDatabase.getInstance().getReference("Users");
                        myRef_user.child(data.child("0").getValue().toString()).child("username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {

                                if (!task.isSuccessful()) {
                                    Log.d("firebase", "Error getting data", task.getException());
                                }
                                else {
                                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                    txt.setText( String.valueOf(task.getResult().getValue()));
                                    chatBox.addView(vi);

                                }
                            }
                        });
                    }


                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });


        //imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        return binding;
    }

    public void getChatList()
    {

        final DatabaseReference myRef_mess = FirebaseDatabase.getInstance().getReference("chatRelation");
        myRef_mess.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if(Integer.parseInt(data.child("0").getValue().toString()) == (userID)) {

                    chatList.add(Integer.parseInt(data.child("1").getValue().toString()));
                    }
                    else if(Integer.parseInt(data.child("1").getValue().toString()) == (userID)){
                        chatList.add(Integer.parseInt(data.child("0").getValue().toString()));

                    }
                    //   view.setBackgroundResource(R.drawable.chat_base_pink1x);

                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }
}