package com.example.komal.firebase3;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1000;
    Button btnadd;
    EditText etvar;
    ArrayList<String> notes;
    ListView lv;
    ArrayAdapter<String> arrayAdapter;
    FirebaseUser firebaseUser;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnadd = findViewById(R.id.btnadd);
        etvar = findViewById(R.id.etvar);
        lv = findViewById(R.id.listView);

        notes = new ArrayList<>();

                       arrayAdapter= new ArrayAdapter<>(
                this,
                R.layout.item_row,
                R.id.tv,
                notes
        );
        lv.setAdapter(arrayAdapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
          addListener();

        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                    new AuthUI.IdpConfig.GitHubBuilder().build(),
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.PhoneBuilder().build(),
                                    new AuthUI.IdpConfig.AnonymousBuilder().build()))
                            .build(),
                    RC_SIGN_IN);
        }
    }


        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
            if (requestCode == RC_SIGN_IN) {

                IdpResponse response = IdpResponse.fromResultIntent(data);

                // Successfully signed in
                if (resultCode == RESULT_OK) {
                  addListener();


                    finish();
                } else {
                    // Sign in failed
                    if (response == null) {
                        // User pressed back button

                        return;
                    }

                    if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {

                        return;
                    }


                }
            }
        }

        public  void addListener(){
            final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
            btnadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String note = etvar.getText().toString();
                    // FirebaseDatabase.getInstance().getReference().push().setValue(note);
                    dbref.child("note").push().setValue(note);
                    // dbref.child("todo").push().setValue(note);
                }
            });


            dbref.child("note").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String data = dataSnapshot.getValue(String.class);
                    notes.add(data);
                    arrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

    }

