package cashbacklogin.lt.belekas.ehomeshoplt.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cashbacklogin.lt.belekas.ehomeshoplt.R;
import cashbacklogin.lt.belekas.ehomeshoplt.fragments.ChatListFragment;
import cashbacklogin.lt.belekas.ehomeshoplt.fragments.ChatUsersFragment;

public class ChatDashboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dashboard);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Chat With Seller");
        firebaseAuth = FirebaseAuth.getInstance();
        //bottom nav
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        //pagrindinis page kuriame atsidaro kaip paspuadi per seller home page i chato iconele per Dashboarda
        //users fragment transaction
        getAccType();
        //actionBar.setTitle("Sellers"); //change actionbar title
        ChatUsersFragment fragment = new ChatUsersFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, fragment, "");
        ft.commit();
    }

    private void getAccType(){

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String accountType = "" + ds.child("accountType").getValue();
                            if (accountType.equals("Seller")) {

                                actionBar.setTitle("Users");

                                BottomNavigationView nav = findViewById(R.id.navigation);
                                Menu menu = nav.getMenu();
                                MenuItem nav_users = menu.findItem(R.id.nav_users);
                                nav_users.setTitle("Users");

                            }
                            else {

                                actionBar.setTitle("Sellers");

                                BottomNavigationView nav = findViewById(R.id.navigation);
                                Menu menu = nav.getMenu();
                                MenuItem nav_users = menu.findItem(R.id.nav_users);
                                nav_users.setTitle("Sellers");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    //handle item clicks
                    switch (item.getItemId()){
                        case R.id.nav_users:
                            //users fragment transaction
                            getAccType();
                            //actionBar.setTitle("Sellers"); //change actionbar title
                            ChatUsersFragment fragment = new ChatUsersFragment();
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.content, fragment, "");
                            ft.commit();
                            return true;

                        case R.id.nav_chat:
                            actionBar.setTitle("Chats"); //change actionbar title
                            ChatListFragment fragment1 = new ChatListFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content, fragment1, "");
                            ft1.commit();
                            return true;
                    }
                    return false;
                }
            };
}