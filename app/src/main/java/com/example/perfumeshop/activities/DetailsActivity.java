package com.example.perfumeshop.activities;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;
import com.example.perfumeshop.R;
import com.example.perfumeshop.models.NewProductsModel;
import com.example.perfumeshop.models.PopularProductsModel;
import com.example.perfumeshop.models.ShowAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;

public class DetailsActivity extends AppCompatActivity {

    ImageView detailsImg, addItems,removeItems;
    TextView rating, name, description, price, quantity;
    Toolbar toolbar;
    int totalQuantity = 1;
    int totalPrice = 0;
    Button addToCart;

    NewProductsModel newProductsModel = null;
    PopularProductsModel popularProductsModel = null;

    ShowAllModel showAllModel = null;
    FirebaseAuth auth;
    private FirebaseFirestore firestore;
    float ratingValue;
    int ratingColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Toolbar
        toolbar = findViewById(R.id.details_toolbar);
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

        final Object obj = getIntent().getSerializableExtra("details");

        detailsImg = findViewById(R.id.details_img);
        quantity = findViewById(R.id.quantity);
        name = findViewById(R.id.details_name);
        description = findViewById(R.id.details_description);
        rating = findViewById(R.id.rating);
        price = findViewById(R.id.details_price);

        addItems = findViewById(R.id.add_item);
        removeItems = findViewById(R.id.remove_item);
        addToCart = findViewById(R.id.add_to_cart);

        //myRating
        RatingBar myRating = findViewById(R.id.my_rating);
        myRating.setMax(5);


        if (obj instanceof NewProductsModel) {
            newProductsModel = (NewProductsModel) obj;
        } else if (obj instanceof PopularProductsModel) {
            popularProductsModel = (PopularProductsModel) obj;
        } else if (obj instanceof ShowAllModel) {
            showAllModel = (ShowAllModel) obj;
        }

        // Set data based on the initialized model
        //New Products
        if (newProductsModel != null) {
            Glide.with(getApplicationContext()).load(newProductsModel.getImg_url()).into(detailsImg);
            name.setText(newProductsModel.getName());
            description.setText(newProductsModel.getDescription());
            price.setText(String.valueOf(newProductsModel.getPrice()));
            rating.setText(newProductsModel.getRating());

            ratingValue = Float.parseFloat(newProductsModel.getRating());
            ratingColor = getRatingColor(ratingValue);
            myRating.setRating(ratingValue);
            LayerDrawable layerDrawable = (LayerDrawable) myRating.getProgressDrawable();
            DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(2)), ratingColor);

            totalPrice = newProductsModel.getPrice() * totalQuantity;

            //Popular products
        } else if (popularProductsModel != null) {
            Glide.with(getApplicationContext()).load(popularProductsModel.getImg_url()).into(detailsImg);
            name.setText(popularProductsModel.getName());
            description.setText(popularProductsModel.getDescription());
            price.setText(String.valueOf(popularProductsModel.getPrice()));
            rating.setText(popularProductsModel.getRating());

            ratingValue = Float.parseFloat(popularProductsModel.getRating());
            ratingColor = getRatingColor(ratingValue);
            myRating.setRating(ratingValue);
            LayerDrawable layerDrawable = (LayerDrawable) myRating.getProgressDrawable();
            DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(2)), ratingColor);

            totalPrice = popularProductsModel.getPrice() * totalQuantity;

            //Show All products
        } else if (showAllModel != null) {
            Glide.with(getApplicationContext()).load(showAllModel.getImg_url()).into(detailsImg);
            name.setText(showAllModel.getName());
            description.setText(showAllModel.getDescription());
            price.setText(String.valueOf(showAllModel.getPrice()));
            rating.setText(showAllModel.getRating());

            ratingValue = Float.parseFloat(showAllModel.getRating());
            ratingColor = getRatingColor(ratingValue);
            myRating.setRating(ratingValue);
            LayerDrawable layerDrawable = (LayerDrawable) myRating.getProgressDrawable();
            DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(2)), ratingColor);

            totalPrice = showAllModel.getPrice() * totalQuantity;
        }

        //Quantity
        addItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalQuantity < 10) {
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));

                }
            }
        });
        removeItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalQuantity > 1) {
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));

                }
            }
        });

        //Add to Cart
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });
    }

    //Add product with details to the cart
    private void addToCart() {
        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("productName", name.getText().toString());
        cartMap.put("productPrice", price.getText().toString());
        cartMap.put("totalQuantity", quantity.getText().toString());
        cartMap.put("totalPrice", totalPrice);


        String imageUrl = "";
        if (newProductsModel != null) {
            imageUrl = newProductsModel.getImg_url();
        } else if (popularProductsModel != null) {
            imageUrl = popularProductsModel.getImg_url();
        } else if (showAllModel != null) {
            imageUrl = showAllModel.getImg_url();
        }
        cartMap.put("productImage", imageUrl);

        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("User").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Toast.makeText(DetailsActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
    private int getRatingColor(float rating) {
        int color;
        if (rating == 5.0) {
            color = ContextCompat.getColor(this, R.color.rating_high);
        } else if (rating >= 4.5) {
            color = ContextCompat.getColor(this, R.color.rating_medium);
        } else if (rating >= 3.0 && rating < 4.5) {
            color = ContextCompat.getColor(this, R.color.rating_low);
        } else {
            color = ContextCompat.getColor(this, R.color.rating_extra_low);
        }
        return color;
    }

}