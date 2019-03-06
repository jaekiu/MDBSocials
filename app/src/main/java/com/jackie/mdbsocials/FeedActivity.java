/** Represents the "Feed" screen.
 * @author: Jacqueline Zhang
 * @date: 02/24/2019
 */

package com.jackie.mdbsocials;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static com.jackie.mdbsocials.FirebaseUtils.getFirebaseUser;
import static com.jackie.mdbsocials.FirebaseUtils.getSocialsDatabaseRef;

public class FeedActivity extends AppCompatActivity {
    /** Represents the RecyclerView. */
    private RecyclerView _socialsView;

    /** Represents all the socials. */
    private ArrayList _socials;

    /** Firebase-related variables.*/
    private DatabaseReference _mDatabase;
    private FirebaseUser _user;

    /** Toolbar-related variables. */
    private DrawerLayout _drawerLayout;
    private TextView _navText;

    /** Sets up activity. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        // Sets up toolbar.
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        _drawerLayout = findViewById(R.id.drawer_layout);;
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        setUpDrawer();

        // Initialization of items.
        _socials = new ArrayList<>();
        _socialsView = findViewById(R.id.socialsView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        _socialsView.setLayoutManager(layoutManager);
        final FeedAdapter adapter = new FeedAdapter(this, _socials);
        _socialsView.setAdapter(adapter);
        _mDatabase = getSocialsDatabaseRef();

        // Realtime database retrieval.
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("nani", "starting data change");
                ArrayList<Social> newSocials = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uid = snapshot.getKey();
                    String name = snapshot.child("name").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    String date = snapshot.child("date").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    int interested = Integer.valueOf(snapshot.child("interested").getValue().toString());
                    // int interested = snapshot.child("interested").getValue(Integer.class);
                    Social s = new Social(uid, name, description, date, email, interested);
                    newSocials.add(s);
                    Log.d("nani", "event name: " + name);
                }
                // Need to reverse the list because Firebase doesn't have a descending ordering function.
                // https://stackoverflow.com/questions/34156996/firebase-data-desc-sorting-in-android
                Collections.reverse(newSocials);
                _socials.clear();
                _socials.addAll(newSocials);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR", "loadPost:onCancelled", databaseError.toException());
            }
        };
        _mDatabase.orderByChild("timestamp").addValueEventListener(postListener);


    }

    /** Creates all the menu options for the Drawer. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed_menu, menu);
        return true;
    }

    /** Handles selection of options for the Drawer. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent i = new Intent(this, SocialActivity.class);
                startActivity(i);
                return true;
            case android.R.id.home:
                _drawerLayout.openDrawer(GravityCompat.START);
                _user = getFirebaseUser();
                _navText = findViewById(R.id.nav_welcome);
                _navText.setText("Hi " + _user.getDisplayName() + "!");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** Sets up the Drawer for the navigation bar. */
    void setUpDrawer() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        _drawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        switch (menuItem.getItemId()) {
                            case R.id.nav_create:
                                Intent i1 = new Intent(FeedActivity.this, SocialActivity.class);
                                startActivity(i1);
                                break;
                            case R.id.nav_analytics:
                                Intent i3 = new Intent(FeedActivity.this, AnalyticsActivity.class);
                                startActivity(i3);
                                break;
                            case R.id.nav_sign_out:
                                Intent i2 = new Intent(FeedActivity.this, LoginActivity.class);
                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(FeedActivity.this, "You are now signed out.", Toast.LENGTH_SHORT).show();
                                startActivity(i2);
                                break;
                        }

                        return true;
                    }
                });

    }
}
