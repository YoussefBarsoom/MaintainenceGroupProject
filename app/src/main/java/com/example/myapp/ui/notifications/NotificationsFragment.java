package com.example.myapp.ui.notifications;

import static android.content.Context.MODE_PRIVATE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.example.myapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationsFragment extends Fragment {
    private static final String  MY_PREFS_NAME = "MyPrefsFile";
    private DatabaseReference mDatabase;
public Integer userID=0;
    public ArrayList<String> new_messages = new ArrayList<String>();
public Integer chatGrpID =1;


    private NotificationsViewModel notificationsViewModel;
    private View binding;
    private LinearLayout chatBox;
    public ArrayList<String> all_Chat = new ArrayList<String>();
    private LayoutInflater mInflater;
private int num_message=0;
    ScrollView scrollView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        binding = inflater.inflate(R.layout.fragment_notifications, container, false);

        Bundle bundle = getArguments();
            chatGrpID = bundle.getInt("contactID", 0);
            Log.d("Bundle",chatGrpID+"");

        Log.d("Bundle",chatGrpID+"");

        //Log.d("a","STUFF" + mDatabase.child("messages").child("1").child("0").child("message").get().toString());
        ;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference myRef_mess = FirebaseDatabase.getInstance().getReference("messages").child(chatGrpID.toString());
        scrollView = (ScrollView) binding.findViewById(R.id.global_chat) ;
        SharedPreferences prefs = binding.getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getInt("userID", 0);
        Log.d("Debug1",userID+"");
        Log.d("Debug1",userID+"");

        myRef_mess.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    chatBox = (LinearLayout) binding.findViewById(R.id.chat_sent);
                    View vi = inflater.inflate(R.layout.message_sent,chatBox, false);
                    TextView txt = (TextView) vi.findViewById(R.id.textView2);
                    if(Integer.parseInt(data.child("sender").getValue().toString()) == (userID)) {

                    }
                    else{

                        vi.setBackgroundColor(0xFF307a8c);

                    }
                    //   view.setBackgroundResource(R.drawable.chat_base_pink1x);
                    txt.setText(data.child("message").getValue().toString());
                    chatBox.addView(vi);
                    num_message=Integer.parseInt(data.getKey());
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
        Button sendButton = (Button)binding.findViewById(R.id.send);
        mInflater = inflater;
        scrollView.fullScroll(View.FOCUS_DOWN);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                executeSend(v);
                scrollView.fullScroll(View.FOCUS_DOWN);
                    imm.hideSoftInputFromWindow(binding.getWindowToken(), 0);

            }
        });
        updateChat();

        //imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        return binding;
    }

    public void send() {
        final DatabaseReference myRef_chat = FirebaseDatabase.getInstance().getReference("messages").child(chatGrpID.toString());
        //getLastValue();
        EditText tc = (EditText) binding.findViewById(R.id.editMess);
        String message = tc.getText().toString();
        chatBox = (LinearLayout) binding.findViewById(R.id.chat_sent);
        View view = mInflater.inflate(R.layout.message_sent, chatBox, false);
       // view.setBackgroundColor(0xFF307a8c);

        TextView txt = (TextView) view.findViewById(R.id.textView2);
         txt.setText(message);
      //  view.setBackgroundResource(R.drawable.chat_base1x);
        chatBox.addView(view);
        all_Chat.add(message);
        String currentDate = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        myRef_chat.child(new Integer(num_message+1).toString()).child("message").setValue(message);
        myRef_chat.child(new Integer(num_message+1).toString()).child("sender").setValue(userID.toString());
        myRef_chat.child(new Integer(num_message+1).toString()).child("time").setValue(currentTime.toString());
        myRef_chat.child(new Integer(num_message+1).toString()).child("date").setValue(currentDate.toString());

        num_message++;
    }
    public void get_newMessage()
    {

        final DatabaseReference myRef_mess = FirebaseDatabase.getInstance().getReference("messages").child(chatGrpID.toString());
        DatabaseReference newRef = myRef_mess.orderByKey().limitToLast(1).getRef();

        myRef_mess.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if((Integer.parseInt(data.getKey().toString()))>num_message)
                    {
                        new_messages.add(data.child("message").getValue().toString());

                    }}

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }
    public void postMessage() {
        while (!new_messages.isEmpty()) {
            chatBox = (LinearLayout) binding.findViewById(R.id.chat_sent);
            View view = mInflater.inflate(R.layout.message_sent, chatBox, false);
            view.setBackgroundColor(0xFF307a8c);
            TextView txt = (TextView) view.findViewById(R.id.textView2);
            txt.setText(new_messages.get(0));
            new_messages.remove(0);
            chatBox.addView(view);
            num_message++;
        }

        new_messages.clear();

    }
    public void updateChat()
    {
        Log.d("UPDATE","chat");
        get_newMessage();
        postMessage();
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
                get_newMessage();
                if(!new_messages.isEmpty()) {
                    postMessage();
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
                Log.d("CHAT" ,new_messages.toString()+"ARRAY");
                handler.postDelayed(this,3500);
            }
        }, 1000);

    }


    public void executeSend(View view) {
        //mInflater = LayoutInflater.from(binding);
        send();
        EditText tc = (EditText) binding.findViewById(R.id.editMess);
        tc.setText("");
    }
public View viewTest;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {



    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}