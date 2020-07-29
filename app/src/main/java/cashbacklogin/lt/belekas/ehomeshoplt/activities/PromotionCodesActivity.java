package cashbacklogin.lt.belekas.ehomeshoplt.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cashbacklogin.lt.belekas.ehomeshoplt.R;
import cashbacklogin.lt.belekas.ehomeshoplt.adapters.AdapterPromotionShop;
import cashbacklogin.lt.belekas.ehomeshoplt.models.ModelPromotion;

public class PromotionCodesActivity extends AppCompatActivity {

    private ImageButton backBtn, addPromoBtn, filterBtn;
    private TextView filteredTv;
    private RecyclerView promoRv;

    private ArrayList<ModelPromotion> promotionArrayList;
    private AdapterPromotionShop adapterPromotionShop;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_codes);

        backBtn = findViewById(R.id.backBtn);
        addPromoBtn = findViewById(R.id.addPromoBtn);
        filterBtn = findViewById(R.id.filterBtn);
        filteredTv = findViewById(R.id.filteredTv);
        promoRv = findViewById(R.id.promoRv);

        firebaseAuth = FirebaseAuth.getInstance();
        loadAllPromoCodes();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addPromoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PromotionCodesActivity.this, AddPromotionCodeActivity.class));
            }
        });

        // handel filter button click, show filter dialog
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog();
            }
        });
    }

    private void filterDialog() {
        // options to display in dialog
        String[] options = {"All", "Expired", "Not Expired"};

        // dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter Promotions COdes")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // handle item click
                        if (which == 0){
                            // all clicked
                            loadAllPromoCodes();
                            filteredTv.setText("All Promotion Codes");
                        }
                        else if (which == 1){
                            // expired clicked
                            loadExpiredPromoCodes();
                            filteredTv.setText("Expired Promotion Codes");
                        }
                        else if (which == 2){
                            // not expired clicked
                            loadNotExpiredPromoCodes();
                            filteredTv.setText("Not Expired Promotion Codes");
                        }
                    }
                })
                .show();
    }

    private void loadAllPromoCodes(){
        // init list
            promotionArrayList = new ArrayList<>();

            // db reference User > current user > Promotions > codes data
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Promotions")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // clear list before adding new data
                            promotionArrayList.clear();
                            for (DataSnapshot ds: snapshot.getChildren()){
                                ModelPromotion modelPromotion = ds.getValue(ModelPromotion.class);

                                // add to list
                                promotionArrayList.add(modelPromotion);
                            }
                            // setup adapter. add list to adapter
                            adapterPromotionShop = new AdapterPromotionShop(PromotionCodesActivity.this, promotionArrayList);

                            // set adapter to recyclerview
                            promoRv
                                    .setAdapter(adapterPromotionShop);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

        private void loadExpiredPromoCodes(){

            // get current date
            DecimalFormat mFormat = new DecimalFormat("00");
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            final String todayDate = day + "/" + month + "/" + year;

            // init list
            promotionArrayList = new ArrayList<>();

            // db reference User > current user > Promotions > codes data
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Promotions")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // clear list before adding new data
                            promotionArrayList.clear();
                            for (DataSnapshot ds: snapshot.getChildren()){
                                ModelPromotion modelPromotion = ds.getValue(ModelPromotion.class);

                                String expDate = modelPromotion.getExpireDate();

                                /*------- checj for expired ------*/
                                try {
                                    SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
                                    Date currentDate = sdformat.parse(todayDate);
                                    Date expireDate = sdformat.parse(expDate);

                                    if (expireDate.compareTo(currentDate) > 0 ){
                                        // date 1 occurs after date2
                                    }
                                    else if(expireDate.compareTo(currentDate) < 0 ){
                                        // date 1 occurs before date2 (i. e. Expired)
                                        // add to list
                                        promotionArrayList.add(modelPromotion);
                                    }
                                    else if(expireDate.compareTo(currentDate) == 0 ){
                                        // both date equals
                                    }
                                }
                                catch (Exception e){

                                }
                            }
                            // setup adapter. add list to adapter
                            adapterPromotionShop = new AdapterPromotionShop(PromotionCodesActivity.this, promotionArrayList);

                            // set adapter to recyclerview
                            promoRv
                                    .setAdapter(adapterPromotionShop);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

        private void loadNotExpiredPromoCodes(){
            // get current date
            DecimalFormat mFormat = new DecimalFormat("00");
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            final String todayDate = day + "/" + month + "/" + year;

            // init list
            promotionArrayList = new ArrayList<>();

            // db reference User > current user > Promotions > codes data
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Promotions")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // clear list before adding new data
                            promotionArrayList.clear();
                            for (DataSnapshot ds: snapshot.getChildren()){
                                ModelPromotion modelPromotion = ds.getValue(ModelPromotion.class);

                                String expDate = modelPromotion.getExpireDate();

                                /*------- checj for expired ------*/
                                try {
                                    SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
                                    Date currentDate = sdformat.parse(todayDate);
                                    Date expireDate = sdformat.parse(expDate);

                                    if (expireDate.compareTo(currentDate) > 0 ){
                                        // date 1 occurs after date2
                                        // add to list
                                        promotionArrayList.add(modelPromotion);
                                    }
                                    else if(expireDate.compareTo(currentDate) < 0 ){
                                        // date 1 occurs before date2 (i. e. Expired)
                                    }
                                    else if(expireDate.compareTo(currentDate) == 0 ){
                                        // both date equals
                                        // add to list
                                        promotionArrayList.add(modelPromotion);
                                    }
                                }
                                catch (Exception e){

                                }
                            }
                            // setup adapter. add list to adapter
                            adapterPromotionShop = new AdapterPromotionShop(PromotionCodesActivity.this, promotionArrayList);

                            // set adapter to recyclerview
                            promoRv
                                    .setAdapter(adapterPromotionShop);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
}