package cashbacklogin.lt.belekas.ehomeshoplt.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cashbacklogin.lt.belekas.ehomeshoplt.R;
import cashbacklogin.lt.belekas.ehomeshoplt.adapters.AdapterReview;
import cashbacklogin.lt.belekas.ehomeshoplt.models.ModelReview;

public class ShopReviewsActivity extends AppCompatActivity {


    private ImageButton backBtn;
    private ImageView profileIv;
    private TextView shopNameTv, ratingsTV;
    private RatingBar ratingBar;
    private RecyclerView reviewsRv;

    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelReview> reviewArrayList; // will contain list of all reviews
    private AdapterReview adapterReview;

    private String shopUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_reviews);

        backBtn = findViewById(R.id.backBtn);
        profileIv = findViewById(R.id.profileIv);
        shopNameTv = findViewById(R.id.shopNameTv);
        ratingBar = findViewById(R.id.ratingBar);
        ratingsTV = findViewById(R.id.ratingsTV);
        reviewsRv = findViewById(R.id.reviewsRv);

        // get shop uid from intent
        shopUid = getIntent().getStringExtra("shopUid");

        firebaseAuth = FirebaseAuth.getInstance();
        loadShopDetails(); // for shop name, image
        loadReviews(); // for reviews list

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private float ratingSum = 0;
    private void loadReviews() {

        // init ist
        reviewArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //clear list before adding data into it
                        reviewArrayList.clear();
                        ratingSum = 0;
                        for (DataSnapshot ds: snapshot.getChildren()){
                            float rating = Float.parseFloat("" + ds.child("ratings").getValue()); // e.g. 1.3
                            ratingSum = ratingSum + rating; // for avg rating, add (addition of) all ratings, later will divide it by number of reviews

                            ModelReview modelReview = ds.getValue(ModelReview.class);
                            reviewArrayList.add(modelReview);
                        }

                        // setup adapter
                        adapterReview = new AdapterReview(ShopReviewsActivity.this, reviewArrayList);

                        //set to recyclerview
                        reviewsRv.setAdapter(adapterReview);

                        long numberOfReviews = snapshot.getChildrenCount();
                        float avgRating = ratingSum / numberOfReviews;

                        ratingsTV.setText(String.format("%.2f", avgRating) + "[" + numberOfReviews + "]"); //e.g. 1.3[1000.22]
                        ratingBar.setRating(avgRating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadShopDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String shopName = "" + snapshot.child("shopName").getValue();
                        String profileImage = "" + snapshot.child("profileImage").getValue();

                        shopNameTv.setText(shopName);
                        try {
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_store_gray).into(profileIv);
                        }
                        catch (Exception e){
                            // if anything goes wrong setting image (exception occurs), set default image
                            profileIv.setImageResource(R.drawable.ic_store_gray);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}