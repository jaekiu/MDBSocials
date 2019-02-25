/** Represents the "Sign Up" screen.
 * @author: Jacqueline Zhang
 * @date: 02/23/2019
 */

package com.jackie.mdbsocials;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView lady;
    private FirebaseAuth mAuth;
    private Button signUpBtn;
    private EditText email;
    private EditText password;
    private EditText name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        lady = findViewById(R.id.lady);
        Glide.with(this).load(R.drawable.img_3).centerInside().into(lady);

        signUpBtn = findViewById(R.id.signUpBtn_2);
        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.pwEditText);
        name = findViewById(R.id.nameEditText);

        signUpBtn.setOnClickListener(this);


    }

    public void signUpUsers(String email, String password, final String name) {
        if (email == null || password == null || name == null) {
            Toast.makeText(this, "Please enter your name, email, and password!", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please make sure your name, email, and password aren't empty!", Toast.LENGTH_LONG).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, "Please make your password at least 6 characters long!", Toast.LENGTH_LONG).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Yay", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("Yay", "User profile updated.");
                                                }
                                            }
                                        });
                                updateUI();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("No", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }

    }

    public void updateUI() {
        Intent i = new Intent(this, FeedActivity.class);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUpBtn_2:
                signUpUsers(email.getText().toString(), password.getText().toString(), name.getText().toString());

        }
    }
}
