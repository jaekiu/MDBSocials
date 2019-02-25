/** Represents the "Login" screen.
 * @author: Jacqueline Zhang
 * @date: 02/23/2019
 */

package com.jackie.mdbsocials;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private Button signUpBtn;
    private EditText email;
    private EditText password;
    private Button login;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Prevents keyboard from popping up when activity launches.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mAuth = FirebaseAuth.getInstance();
        signUpBtn = findViewById(R.id.signUpBtn);
        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.pwEditText);
        login = findViewById(R.id.loginBtn);

        // https://stackoverflow.com/questions/43599638/firebase-signinwithemailandpassword-and-createuserwithemailandpassword-not-worki
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    Log.d("nani", "onAuthStateChanged:signed_in");
                    updateUI();
                }else{
                    Log.d("nani", "onAuthStateChanged:signed_out");
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        signUpBtn.setOnClickListener(this);
        login.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    public void loginUsers(String email, String password) {
        Log.d("nani", "status: i'm in");

        if (email == null || password == null) {
            Toast.makeText(this, "Please enter your email and password!", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please make sure your email and password aren't empty!", Toast.LENGTH_LONG).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("nani", "status: " + task.isSuccessful());
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Yay", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("lol", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Incorrect username or password.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    public void updateUI() {
        Intent i = new Intent(this, FeedActivity.class);
        startActivity(i);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUpBtn:
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
                break;
            case R.id.loginBtn:
                loginUsers(email.getText().toString(), password.getText().toString());
                break;
        }
    }
}
