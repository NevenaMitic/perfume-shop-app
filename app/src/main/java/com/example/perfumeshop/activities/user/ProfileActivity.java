package com.example.perfumeshop.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perfumeshop.R;
import com.example.perfumeshop.activities.AboutActivity;
import com.example.perfumeshop.activities.CartActivity;
import com.example.perfumeshop.activities.MainActivity;
import com.example.perfumeshop.adapters.AddressAdapter;
import com.example.perfumeshop.models.AddressModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements AddressAdapter.SelectedAddress {
    Button addAddress;
    RecyclerView recyclerView;
    private List<AddressModel> addressModelList;
    private AddressAdapter addressAdapter;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    Toolbar toolbar;
    String mAAddress = "";
    Button updateBtn;
    TextView textViewWelcome, textViewEmail;
    String fullName, email;
    BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Toolbar
        toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            firestore = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();

            recyclerView = findViewById(R.id.address_recycler);
            addAddress = findViewById(R.id.add_address_btn);

            updateBtn = findViewById(R.id.update_btn);
            textViewWelcome = findViewById(R.id.textView_show_welcome);
            textViewEmail = findViewById(R.id.textView_show_email);

         //Bottom Toolbar
        nav = findViewById(R.id.nav);
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.profile){
                    return true;
                }
                if (id == R.id.home){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                if (id == R.id.cart){
                    startActivity(new Intent(getApplicationContext(), CartActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                if (id == R.id.about){
                    startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                if (id == R.id.logout){
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    overridePendingTransition(0,0);
                    auth.signOut();
                    return true;
                }
                return false;
            }
        });

            if(user == null){
                Toast.makeText(ProfileActivity.this, "Something went wrong! User's details are not available.",
                        Toast.LENGTH_LONG).show();
            }else {
                showUserProfile(user);
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            addressModelList = new ArrayList<>();
            addressAdapter = new AddressAdapter(addressModelList, getApplicationContext(), this);
            recyclerView.setAdapter(addressAdapter);

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, AddAddressActivity.class));
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, UpdateProfileActivity.class));
            }
        });


            firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                        .collection("Info").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                        AddressModel addressModel = doc.toObject(AddressModel.class);
                                        addressModelList.add(addressModel);
                                        addressAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
        }

    private void showUserProfile(FirebaseUser user) {
        if (user != null) {
            fullName = user.getDisplayName();
            email = user.getEmail();

            if (fullName != null && !fullName.isEmpty()) {
                textViewWelcome.setText("Hello, " + fullName);
            }
            if (email != null) {
                textViewEmail.setText(email);
            }
        } else {
            Toast.makeText(ProfileActivity.this, "User's details are not available.", Toast.LENGTH_LONG).show();
        }
    }
    @Override
        public void setAddress(String address) {
            mAAddress = address;
        }
}


