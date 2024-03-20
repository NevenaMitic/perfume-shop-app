package com.example.perfumeshop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.perfumeshop.HomeFragment;
import com.example.perfumeshop.R;
import com.example.perfumeshop.activities.user.LoginActivity;
import com.example.perfumeshop.activities.user.ProfileActivity;
import com.example.perfumeshop.models.MyCartModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {
    Fragment homeFragment;
    Toolbar toolbar;
    FirebaseAuth auth;
    BottomNavigationView nav;
    FirebaseFirestore firestore;
    private int cartQuantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        //Toolbar
        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        nav = findViewById(R.id.nav);

        homeFragment = new HomeFragment();
        loadFragment(homeFragment);

        //Bottom toolbar
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.profile) {
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                if (id == R.id.home) {
                    return true;
                }
                if (id == R.id.about) {
                    startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                if (id == R.id.logout) {
                    showLogoutConfirmationDialog();
                    return true;
                }
                return false;
            }
        });
    }
    //Dialog for Logging out
    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                auth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                overridePendingTransition(0, 0);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }
    private void loadFragment(Fragment homeFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_container, homeFragment);
        transaction.commit();
    }

    //Toolbar cart badge
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.cart);
        View actionView = menuItem.getActionView();


        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            }
        });

        return true;
    }

        @Override
        protected void onResume() {
            super.onResume();
            // Update cart count on resume
            updateCartCount();
        }

        private void updateCartCount() {
            // Fetch the total count of items in the cart
            firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                    .collection("User")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            int totalCount = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                MyCartModel cartItem = document.toObject(MyCartModel.class);
                                if (cartItem != null) {
                                    totalCount += Integer.parseInt(cartItem.getTotalQuantity());
                                }
                            }
                            // Update the badge counter with the total count
                            updateBadgeCounter(totalCount);
                        } else {
                            Log.e("MainActivity", "Error fetching cart items: ", task.getException());
                        }
                    });
            }

        private void updateBadgeCounter(int count) {
            MenuItem menuItem = toolbar.getMenu().findItem(R.id.cart);
            View actionView = menuItem.getActionView();
            TextView badgeCounter = actionView.findViewById(R.id.badge_counter);

            badgeCounter.setText(String.valueOf(count));

        // Set visibility based on count
        if (count > 0) {
            badgeCounter.setVisibility(View.VISIBLE);
        } else {
            badgeCounter.setVisibility(View.GONE);
        }
    }
}


