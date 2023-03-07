package com.example.fypapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fypapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    //Views
        EditText mEmailEt, mPasswordEt;
                TextView notHaveAccountTv, mRecoverPassTv;
                Button mLoginBtn;

    //Declare an instance of firebaseAuth
    private FirebaseAuth mAuth;

            //Progress dialog
            ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            //Action bar and its title
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Login");
            //Enable back button
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);

            //Initialise firebaseAuth instance
            mAuth = FirebaseAuth.getInstance();

            //init
            mEmailEt = findViewById(R.id.emailEt);
            mPasswordEt = findViewById(R.id.passwordEt);
            notHaveAccountTv = findViewById(R.id.nothave_accountTv);
            mRecoverPassTv= findViewById(R.id.recoverPassTv);
            mLoginBtn = findViewById(R.id.loginBtn);

            //Login button click
            mLoginBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
            //Input data
            String email = mEmailEt.getText().toString();
            String passw = mPasswordEt.getText().toString().trim();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //invalid email pattern set error
            mEmailEt.setError("Invalid email");
            mEmailEt.setFocusable(true);
            }else {
            //valid email pattern
            loginUser(email, passw);
            }
            }
            });
            //Not have account
            notHaveAccountTv.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
            }
            });
            //recover password
            mRecoverPassTv.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
            showRecoverPasswordDialog();
            }
            });

            //init progress dialog
            pd = new ProgressDialog(this);

            }
    private void showRecoverPasswordDialog() {
            //Alertdialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Recover Password");
            //set linear layout
            LinearLayout linearLayout = new LinearLayout(this);
            //Views to set in dialog
            EditText emailEt = new EditText(this);
            emailEt.setHint("Email");
            emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

            //set min width of a editview to fit a text of n 'M' letters regardless of the actual text extension and text size
            emailEt.setMinEms(16);

            linearLayout.addView(emailEt);
            linearLayout.setPadding(10,10,10,10);

            builder.setView(linearLayout);

            //Buttons recover
            builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
            String email = emailEt.getText().toString().trim();
            beginRecovery(email);
            }
            });
            //Buttons cancel
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            }
            });
            //show dialog
            builder.create().show();
            }

    private void beginRecovery(String email) {
            pd.setMessage("Sending email..");
            pd.show();
            mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
    @Override
    public void onComplete(@NonNull Task<Void> task) {
            pd.dismiss();
            if (task.isSuccessful()) {
            Toast.makeText(LoginActivity.this, "Email sent",Toast.LENGTH_SHORT).show();
            }
            else {
            Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            }
            }).addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception e) {
            pd.dismiss();
            //Get and show proper error message
            Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            });

            }

    private void loginUser(String email, String passw){
            //Show progress dialog
            pd.setMessage("Logging in..");
            pd.show();
            mAuth.signInWithEmailAndPassword(email, passw)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
            //dismiss progress dialog
            pd.dismiss();
            //sign in success, update UI with the signed in users info
            FirebaseUser user = mAuth.getCurrentUser();
            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
            finish();
            } else {
            //dismiss progress dialog
            pd.dismiss();
            //If sign in fails
            Toast.makeText(LoginActivity.this, "Authentication failed",
            Toast.LENGTH_SHORT).show();
            }
            }
            }).addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception e) {
            //dismiss progress dialog
            pd.dismiss();
            //error, get and show error message
            Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            });

            }

    @Override
    public boolean onSupportNavigateUp() {
            onBackPressed();
            return super.onSupportNavigateUp();
            }
}