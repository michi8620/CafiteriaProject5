package com.example.cafiteriaproject5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminMainActivity extends AppCompatActivity {
    MaterialToolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    String userType = " ";

    //TODO: לבדוק אם המשתמש כבר מחובר

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        replaceFragment(new HomeAdminFragment());

        toolbar = findViewById(R.id.topAppbar);
        drawerLayout = findViewById(R.id.drawer_layout_admin);
        navigationView = findViewById(R.id.navigation_view);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

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
                        replaceFragment(new HomeAdminFragment());
                        break;

                    case R.id.nav_shopping_edit:
                        replaceFragment(new EditAdminFragment());
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
                                        Toast.makeText(AdminMainActivity.this, "document does not exist", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(AdminMainActivity.this, "task failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        //the page only for admins, checking the type.
                        if(userType.equals("admin")){
                            replaceFragment(new AdminOnlyFragment());
                        }
                        else{
                            Toast.makeText(AdminMainActivity.this, "זהו מסך למנהלים", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case R.id.nav_person:
                        replaceFragment(new ProfileAdminFragment());
                        break;
                    case R.id.nav_logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(AdminMainActivity.this, MainActivity.class));

                    default:
                        return true;
                }
                return true;

            }
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }
}