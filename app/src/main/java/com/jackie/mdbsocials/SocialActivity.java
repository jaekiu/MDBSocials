/** Represents the "Create a Social" screen.
 * @author: Jacqueline Zhang
 * @date: 02/24/2019
 */

package com.jackie.mdbsocials;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocialActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton _imgBtn;
    private EditText _nameText;
    private EditText _dateText;
    private EditText _descText;
    private TextView _upload;
    private Button _createBtn;
    private Uri _eventImg;

    // Firebase variable
    private FirebaseStorage _storage;
    private FirebaseDatabase _database;
    private DatabaseReference _myRef;
    private FirebaseUser _user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        // Prevents keyboard from popping up when activity launches.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // Initializing variables
        _imgBtn = findViewById(R.id.imgBtn);
        _nameText = findViewById(R.id.nameText);
        _dateText = findViewById(R.id.dateText);
        _descText = findViewById(R.id.descText);
        _upload = findViewById(R.id.uploadText);
        _createBtn = findViewById(R.id.createBtn);
        _storage = FirebaseStorage.getInstance();
        _database = FirebaseDatabase.getInstance();
        _myRef = _database.getReference("socials");
        _user = FirebaseAuth.getInstance().getCurrentUser();
        _imgBtn.setOnClickListener(this);
        _createBtn.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if(requestCode == 1 && resultCode == RESULT_OK) {
            _eventImg = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), _eventImg);
                //_imgBtn.setImageBitmap(bitmap);
                Glide.with(this).load(bitmap).centerCrop().into(_imgBtn);
                _imgBtn.setBackgroundTintList((getResources().getColorStateList(android.R.color.transparent)));
                _upload.setTextColor(getResources().getColor(android.R.color.transparent));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Please upload a valid image.", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Please upload a valid image.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgBtn:
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i,1);
                break;
            case R.id.createBtn:
                // Check if there's a valid event name. Must declare as final in order to access in OnSuccessListener
                final String eventName = _nameText.getText().toString();
                if (eventName == null || eventName.equals("")) {
                    Toast.makeText(SocialActivity.this, "Please enter an event name.", Toast.LENGTH_LONG).show();
                    return;
                }

                // Check if there's a valid event date.
                final String date = _dateText.getText().toString();
                if (date == null || date.equals("")) {
                    Toast.makeText(SocialActivity.this, "Please enter a valid date.", Toast.LENGTH_LONG).show();
                    return;
                }
                Pattern p = Pattern.compile("^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$");
                Matcher m = p.matcher(date);
                if (!m.find()) {
                    Toast.makeText(SocialActivity.this, "Please input the date in mm/dd/yyyy format.", Toast.LENGTH_LONG).show();
                    return;
                }

                // Check if there's a valid description.
                final String description = _descText.getText().toString();
                if (description == null || description.equals("")) {
                    Toast.makeText(SocialActivity.this, "Please enter a description.", Toast.LENGTH_LONG).show();
                    return;
                }

                // Check if there's a valid event image.
                if (_eventImg == null) {
                    Toast.makeText(SocialActivity.this, "Please upload a valid image!", Toast.LENGTH_LONG).show();
                    return;
                }
                // Generate a unique name for the image file that will be used as our key for our JSON.
                // https://stackoverflow.com/questions/38768576/in-firebase-when-using-push-how-do-i-get-the-unique-id-and-store-in-my-databas
                final String imgKey = _myRef.push().getKey();

                // Create a storage reference from our app
                StorageReference storageRef = _storage.getReference();

                // Create a reference to "GUID.jpg"
                StorageReference imgRef = storageRef.child(imgKey + ".jpg");
                imgRef.putFile(_eventImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String email = _user.getEmail();
                        // wow this kept giving me a nullpointerexception and it's the most jank reason too..
                        // https://stackoverflow.com/questions/44264041/firebase-onchildadded-is-null-at-first-time
                        _myRef.child(imgKey).child("interested").setValue(0);
                        _myRef.child(imgKey).child("name").setValue(eventName);
                        _myRef.child(imgKey).child("date").setValue(date);
                        _myRef.child(imgKey).child("description").setValue(description);
                        _myRef.child(imgKey).child("timestamp").setValue(ServerValue.TIMESTAMP);
                        _myRef.child(imgKey).child("email").setValue(email);
                        Log.d("nani", "Stored reference complete!");

                        // Creation of a new social: complete!
                        // Navigate back to the Feed.
                        Intent i = new Intent(SocialActivity.this, FeedActivity.class);
                        startActivity(i);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SocialActivity.this, "Please upload a valid image!", Toast.LENGTH_LONG).show();
                    }
                });
                break;
        }
    }
}
