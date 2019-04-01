package com.example.user.spanish;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.spanish.AudioTrains.AudioList;
import com.example.user.spanish.Fragments.FragmentDictionary;
import com.example.user.spanish.Fragments.FragmentGrammar;
import com.example.user.spanish.Fragments.FragmentTrains;
import com.example.user.spanish.Fragments.FragmentWordsSets;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {


    TextView tvName, tvEmail;
    ProgressBar progressBar;
    TextView tvLevel, tvNextLevel;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.pbLevelProgress);
        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.horizontalprogressbar));
        tvLevel = (TextView) findViewById(R.id.tvLevel);
        tvNextLevel = (TextView) findViewById(R.id.tvNextLevel);

        getLevel();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        tvName = (TextView) findViewById(R.id.tvName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        tvName.setText(name);
        tvEmail.setText(intent.getStringExtra("email"));

        Class fragmentClass;
        fragmentClass = FragmentGrammar.class;
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
    }

    private void getLevel() {
        progressBar.setMax(10);
        progressBar.setProgress(0);
        tvLevel.setText("ур.1");
        tvNextLevel.setText("До следующего уровня 10 ед. опыта");
        final Query query = mReference.child("userInfo");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GenericTypeIndicator<Integer> genericTypeIndicator = new GenericTypeIndicator<Integer>() {
                };
                Integer points = dataSnapshot.getValue(genericTypeIndicator);

                int level = 1;
                int nextLevel = 10;
                while (points >= nextLevel) {
                    points = points - nextLevel;
                    level++;
                    nextLevel = nextLevel + 10;
                }

                progressBar.setMax(nextLevel);
                progressBar.setProgress(points);
                tvLevel.setText("ур." + level);
                tvNextLevel.setText("До следующего уровня " + (nextLevel - points) + " ед. опыта");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLevel();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Class fragmentClass;

        if (id == R.id.nav_trains) {
            fragmentClass = FragmentTrains.class;
            startFragment(fragmentClass, item);
        } else if (id == R.id.nav_dict) {
            fragmentClass = FragmentDictionary.class;
            startFragment(fragmentClass, item);

        } else if (id == R.id.nav_signout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_grammar) {
            fragmentClass = FragmentGrammar.class;
            startFragment(fragmentClass, item);
        } else if (id == R.id.nav_words_sets) {
            fragmentClass = FragmentWordsSets.class;
            startFragment(fragmentClass, item);
        } else if (id == R.id.nav_materials) {
            fragmentClass = AudioList.class;
            startFragment(fragmentClass, item);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startFragment(Class fragmentClass, MenuItem item) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        item.setChecked(true);
        setTitle(item.getTitle());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}