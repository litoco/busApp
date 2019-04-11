package com.example.testapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView supportActionBar;
    private CardView busSearchCard, routeSearchCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseVariables();

        handleNavigationDrawerClick();

        supportActionBar.setOnClickListener(this);
        busSearchCard.setOnClickListener(this);
        routeSearchCard.setOnClickListener(this);
    }

    private void initialiseVariables() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        supportActionBar = findViewById(R.id.support_action_bar);
        busSearchCard = findViewById(R.id.bus_search_card);
        routeSearchCard = findViewById(R.id.search_route_card);
    }

    private void handleNavigationDrawerClick() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.bus_search:
                        Intent busSearch = new Intent(MainActivity.this, BusSearchActivity.class);
                        startActivity(busSearch);
                        break;
                    case R.id.search_route:
                        Intent routeSearch = new Intent(MainActivity.this, RouteSearch.class);
                        startActivity(routeSearch);
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.support_action_bar:
                drawerLayout.openDrawer(Gravity.START);
                break;
            case R.id.bus_search_card:
                Intent busActivity = new Intent(MainActivity.this, BusSearchActivity.class);
                startActivity(busActivity);
                break;
            case R.id.search_route_card:
                Intent routeActivity = new Intent(MainActivity.this, RouteSearch.class);
                startActivity(routeActivity);
                break;
        }
    }
}
