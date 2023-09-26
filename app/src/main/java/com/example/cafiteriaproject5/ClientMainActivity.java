package com.example.cafiteriaproject5;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

/*
side: client
action: a core for all the fragments in the client side.
contains the replace fragment function
xml file: activity_client_main.xml
 */
public class ClientMainActivity extends AppCompatActivity {

    MaterialToolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_main);

        //start the background music
        startService(new Intent(this, MusicService.class));

        mAuth = FirebaseAuth.getInstance();

        //The main fragment is HomeFragment
        replaceFragment(new HomeFragment());

        toolbar = findViewById(R.id.topAppbar);
        drawerLayout = findViewById(R.id.drawer_layout);
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
                    case R.id.nav_home:
                        replaceFragment(new HomeFragment());
                        break;

                    case R.id.nav_shopping_cart:
                        replaceFragment(new ShoppingFragment());
                        break;

                    case R.id.nav_add:
                        replaceFragment(new WantedProductsFragment());
                        break;

                    case R.id.nav_person:
                        replaceFragment(new ProfileFragment());
                        break;
                    case R.id.nav_logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(ClientMainActivity.this, MainActivity.class));

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