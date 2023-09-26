package com.example.cafiteriaproject5;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/*
side: shminist
action: a core for all the fragments in the shminist side.
contains checking if the shminist is admin while entering "do_not_disturb"
and the replaceFragment function.
xml file: activity_admin_main
 */
public class ShministMainActivity extends AppCompatActivity{
    MaterialToolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    private FirebaseFirestore firestore;

    String userType = " ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shminist_main);

        //start the background music
        startService(new Intent(this, MusicService.class));

        firestore = FirebaseFirestore.getInstance();

        //The main fragment is HomeShministFragment
        replaceFragment(new HomeShministFragment());

        toolbar = findViewById(R.id.topAppbar);
        drawerLayout = findViewById(R.id.drawer_layout_admin);
        navigationView = findViewById(R.id.navigation_view);
        //when you press the three lines icon, it opens the drawer
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //when you press an icon in the drawer, you will be directed to a fragment
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                //מדגיש את האייקון:
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (id)
                {
                    case R.id.nav_schedule:
                        replaceFragment(new HomeShministFragment());
                        break;

                    case R.id.nav_shopping_edit:
                        replaceFragment(new EditShministFragment());
                        break;

                    case R.id.nav_computer:
                        replaceFragment(new BitClientsFragment());
                        break;

                    case R.id.nav_emoji_people:
                        replaceFragment(new SeeWantedFragment());
                        break;

                    case R.id.nav_do_not_disturb:
                        Intent intent = getIntent();
                        String doc = intent.getStringExtra("doc");
                        DocumentReference docRef = firestore.collection("users").document(doc);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        userType = document.getString("type");
                                    } else {
                                        Log.d("AdminMainActivity", "onComplete: document does not exist");
                                    }
                                } else {
                                    Log.d("AdminMainActivity", "onComplete: task failed");
                                }
                            }
                        });
                        //the page only for admins, checking the type.
                        if(userType.equals("admin")){
                            replaceFragment(new AdminOnlyFragment());
                        }
                        else{
                            Toast.makeText(ShministMainActivity.this, "זהו מסך למנהלים", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case R.id.nav_person:
                        replaceFragment(new ProfileShministFragment());
                        break;
                    case R.id.nav_logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(ShministMainActivity.this, MainActivity.class));

                    default:
                        return true;
                }
                return true;

            }
        });
    }

    //the function that replaces the fragments
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }
}