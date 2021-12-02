package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity
{

    private EditText eEmail;
    private EditText ePassword;
    private Button eLogin;
public boolean flagValid;

    final private String testEmail = "admin";
    final private String testPassword = "password";
   public static final String MY_PREFS_NAME = "MyPrefsFile";

    private boolean isValid ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        eEmail = findViewById(R.id.etEmail);
        ePassword = findViewById(R.id.etPassword);
        eLogin = findViewById(R.id.btnLogin);

        eLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String inputEmail = eEmail.getText().toString();
                String inputPassword = ePassword.getText().toString();

                if(inputEmail.isEmpty() || inputPassword.isEmpty())
                {
                    Toast.makeText(Login.this, "Please enter all the details correctly!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    validate(inputEmail, inputPassword);
//                    isValid = flagValid;

                }
            }
        });
    }
    private boolean validate(String in_email, String in_password)
    {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("Users");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.child("username").getValue().toString().compareTo(in_email)==0) {
                        Log.d("LoGIN","hi1");

                        if(data.child("password").getValue().toString().compareTo(in_password)==0) {
                            flagValid=true;
                            Log.d("Success",flagValid+" ");
                            Intent intent = new Intent(Login.this, MainActivity.class);
                          //  Intent i = new Intent(CurrentActivity.this, NewActivity.class);
                            intent.putExtra("userID",data.getKey());
                            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                            editor.putInt("userID",Integer.parseInt(data.getKey()));
                            editor.putString("type",data.child("type").getValue().toString());

                            editor.apply();
                            startActivity(intent);
                        }
                        else
                        {

                            flagValid=false;

                        }
                    }
                }
                if(!flagValid) {
                    Toast.makeText(Login.this, "Incorrect credentials entered!", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

        return flagValid;
    }
}

 class MyAsyncTask extends AsyncTask<String, Void, Void> {
     public boolean flagValid = false;

     @Override
    protected Void doInBackground(String... params) {
        validate(params[0],params[1]);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {

    }

     private boolean validate(String in_email, String in_password)
     {

         FirebaseDatabase database = FirebaseDatabase.getInstance();
         DatabaseReference dbRef = database.getReference("Users");

         dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {

                 for (DataSnapshot data : dataSnapshot.getChildren()) {
                     if(data.child("username").getValue().toString().compareTo(in_email)==0) {
                         Log.d("LoGIN","hi1");

                         if(data.child("password").getValue().toString().compareTo(in_password)==0) {
                             flagValid=true;
                             Log.d("Success",flagValid+" ");

                             Log.d("LoGIN","hi2");
                         }
                         else
                         {
                             flagValid=false;

                         }
                     }
                 }
             }


             @Override
             public void onCancelled(DatabaseError databaseError) {
             }

         });

         return flagValid;
     }
}