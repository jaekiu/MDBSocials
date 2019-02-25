/** Represents the detailed version of a Social.
 * @author: Jacqueline Zhang
 * @date: 02/24/2019
 */

package com.jackie.mdbsocials;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private ImageView _img;
    private TextView _nameText;
    private TextView _dateText;
    private TextView _descText;
    private TextView _numInterested;
    private TextView _organizer;
//    private Switch _interestedSwitch;
    private Button _interestedBtn;
    private DatabaseReference _mDatabase;
    private FirebaseUser _user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Sets up toolbar.
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initializing variables.
        _img = findViewById(R.id.img_detail);
        _nameText = findViewById(R.id.eventName_detail);
        _dateText = findViewById(R.id.date_detail);
        _descText = findViewById(R.id.descText_detail);
        _numInterested = findViewById(R.id.interested_detail);
        _organizer = findViewById(R.id.organizer_detail);
//        _interestedSwitch = findViewById(R.id.interested_switch);
        _interestedBtn = findViewById(R.id.interested_button);
        _mDatabase = FirebaseDatabase.getInstance().getReference("socials");
        _user = FirebaseAuth.getInstance().getCurrentUser();

        // Saves state of Switch Toggle button.
//        SharedPreferences sharedPrefs = getSharedPreferences("com.jackie.mdbsocials", MODE_PRIVATE);
//        _interestedSwitch.setChecked(sharedPrefs.getBoolean("Interested State", false));

        // Retrieving extra.
        final Social s = (Social) getIntent().getSerializableExtra("Social");
        final String key = s.getUID();
        _nameText.setText(s.getEventName());
        _dateText.setText(s.getDate());
        _descText.setText(s.getDescription());
        _numInterested.setText(s.getInterested() + " interested");
        _organizer.setText(s.getEmail());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imgRef = storageRef.child(s.getUID() + ".jpg");
        Glide.with(this).load(imgRef).into(_img);


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> interestedUsers = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    interestedUsers.add(snapshot.getValue(String.class));
                }
                if (interestedUsers.contains(_user.getEmail())) {
                    _interestedBtn.setText(Html.fromHtml("✓") + " Interested");
                    _interestedBtn.setTextColor(Color.parseColor("#4cbb17"));

                }
                _interestedBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (interestedUsers.contains(_user.getEmail())) {
                            interestedUsers.remove(_user.getEmail());
                            _mDatabase.child("interested-users").child(key).removeValue();
                            _interestedBtn.setText(Html.fromHtml("☆") + " Interested");
                            _interestedBtn.setTextColor(Color.parseColor("#000000"));
                            s.decrementInterested();
                            _mDatabase.child(key).child("interested").runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                    mutableData.setValue(s.getInterested());
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                    Log.d("Database", "postTransaction:onComplete:" + databaseError);
                                }
                            });
                        } else {
                            interestedUsers.add(_user.getEmail());
                            _mDatabase.child(key).child("interested-users").child(key).setValue(_user.getEmail());
                            _interestedBtn.setText(Html.fromHtml("✓") + " Interested");
                            _interestedBtn.setTextColor(Color.parseColor("#4cbb17"));
                            s.incrementInterested();
                            _mDatabase.child(key).child("interested").runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                    mutableData.setValue(s.getInterested());
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                    Log.d("Database", "postTransaction:onComplete:" + databaseError);
                                }
                            });
                        }
                        _numInterested.setText(s.getInterested() + " interested");
                    }
                });
//                _interestedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
//                        if (isChecked) {
//                            s.incrementInterested();
//                            SharedPreferences.Editor editor = getSharedPreferences("com.jackie.mdbsocials", MODE_PRIVATE).edit();
//                            editor.putBoolean("Interested State", true);
//                            editor.commit();
//                            _mDatabase.runTransaction(new Transaction.Handler() {
//                                @NonNull
//                                @Override
//                                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
//                                    mutableData.setValue(s.getInterested());
//                                    return Transaction.success(mutableData);
//                                }
//
//                                @Override
//                                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
//                                    Log.d("Database", "postTransaction:onComplete:" + databaseError);
//                                }
//                            });
//                        } else {
//                            s.decrementInterested();
//                            SharedPreferences.Editor editor = getSharedPreferences("com.jackie.mdbsocials", MODE_PRIVATE).edit();
//                            editor.putBoolean("Interested State", false);
//                            editor.commit();
//                            _mDatabase.runTransaction(new Transaction.Handler() {
//                                @NonNull
//                                @Override
//                                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
//                                    mutableData.setValue(s.getInterested());
//                                    return Transaction.success(mutableData);
//                                }
//
//                                @Override
//                                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
//                                    Log.d("Database", "postTransaction:onComplete:" + databaseError);
//                                }
//                            });
//                        }
//
//                        _numInterested.setText(s.getInterested() + " interested");
//
//                    }
//                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR", "loadPost:onCancelled", databaseError.toException());
            }
        };
        _mDatabase.child(key).child("interested-users").addValueEventListener(postListener);

    }
}
