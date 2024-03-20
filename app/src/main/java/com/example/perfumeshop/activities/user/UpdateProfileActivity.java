package com.example.perfumeshop.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.perfumeshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;


public class UpdateProfileActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    Toolbar toolbar;
    TextView textViewAuthenticated;
    private String userOldEmail, userNewEmail, userPass;
    Button buttonUpdateEmail;
    ProgressBar progressBar;
    private EditText editTextNewEmail, editTextPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        //Toolbar
        toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        progressBar = findViewById(R.id.progressBar);
        editTextPass = findViewById(R.id.editText_update_email_verify_password);
        editTextNewEmail = findViewById(R.id.editText_update_email_new);
        textViewAuthenticated = findViewById(R.id.textView_update_email_authenticated);
        buttonUpdateEmail = findViewById(R.id.button_update_email);
        
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        
        buttonUpdateEmail.setEnabled(false);
        editTextNewEmail.setEnabled(false);
        
        //Set old email ID
        userOldEmail = user.getEmail();
        TextView textViewOldEmail = findViewById(R.id.textView_update_email_old);
        textViewOldEmail.setText(userOldEmail);

        if (user == null){
            Toast.makeText(UpdateProfileActivity.this, "Something went wrong! User's details not available", Toast.LENGTH_LONG).show();
        } else {
            reAuthenticate(user);
        }
    }
    //Verify user before updating email
    private void reAuthenticate(FirebaseUser user) {
        Button buttonVerifyUser = findViewById(R.id.button_authenticate_user);
        buttonVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userPass = editTextPass.getText().toString();
                if(TextUtils.isEmpty(userPass)){
                    Toast.makeText(UpdateProfileActivity.this, "Required password", Toast.LENGTH_SHORT).show();
                    editTextPass.setError("Please enter your password to verify");
                    editTextPass.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail, userPass);

                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()){
                               progressBar.setVisibility(View.GONE);
                               Toast.makeText(UpdateProfileActivity.this, "Password has been verified.", Toast.LENGTH_SHORT).show();

                               //Show that user is verified
                               textViewAuthenticated.setText("You are verified. You can update your email now.");
                               editTextNewEmail.setEnabled(true);
                               editTextPass.setEnabled(false);
                               buttonVerifyUser.setEnabled(false);
                               buttonUpdateEmail.setEnabled(true);
                               buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdateProfileActivity.this,
                                       R.color.beige));

                               buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       userNewEmail = editTextNewEmail.getText().toString();
                                       if(TextUtils.isEmpty(userNewEmail)) {
                                           Toast.makeText(UpdateProfileActivity.this, "New Email is required.", Toast.LENGTH_SHORT).show();
                                           editTextNewEmail.setError("Please enter new Email.");
                                           editTextNewEmail.requestFocus();
                                       } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()) {
                                           Toast.makeText(UpdateProfileActivity.this, "Please enter valid Email address.", Toast.LENGTH_SHORT).show();
                                           editTextNewEmail.setError("Please enter valid Email address.");
                                           editTextNewEmail.requestFocus();
                                       } else if (userOldEmail.matches(userNewEmail)) {
                                           Toast.makeText(UpdateProfileActivity.this, "New Email cannot be same as old Email!", Toast.LENGTH_SHORT).show();
                                           editTextNewEmail.setError("New Email cannot be same as old Email!");
                                           editTextNewEmail.requestFocus();
                                       }else {
                                           progressBar.setVisibility(View.VISIBLE);
                                           updateEmail(user);
                                       }
                                   }
                               });
                           } else {
                               try {
                                   throw task.getException();
                               } catch (FirebaseAuthInvalidCredentialsException e) {
                                   Toast.makeText(UpdateProfileActivity.this, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show();
                               } catch (Exception e){
                                   Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }
                        }
                    });
                }
            }
        });
    }

    //Updating email to firebase
    private void updateEmail(@NonNull FirebaseUser user) {
        user.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               if (task.isComplete()){
                   user.sendEmailVerification();
                   Toast.makeText(UpdateProfileActivity.this, "Email has been updated. Please Verify your new Email!", Toast.LENGTH_SHORT).show();
                   startActivity(new Intent(UpdateProfileActivity.this, ProfileActivity.class));
                   finish();
               } else {
                   try {
                       throw task.getException();
                   }catch (Exception e) {
                       Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                   }
               }
               progressBar.setVisibility(View.GONE);
            }
        });
    }

}