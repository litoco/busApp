package com.example.testapp;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class RouteSearch extends AppCompatActivity implements View.OnClickListener {

    private ImageView searchRoute, backButton;
    private EditText routeNumber;
    private FirebaseFirestore db;
    private CollectionReference routes;
    private ListView routeList;
    private TextView showFailure;
    private ArrayList<String> toAndFroBus, toBus, froBus, name;
    private ArrayList<SingleRowItemHolder> itemHolders;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_search);

        initialiseVariable();

        provideOnClickBehaviour();
    }

    private void provideOnClickBehaviour() {
        searchRoute.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }


    private void initialiseVariable() {
        searchRoute = findViewById(R.id.search_route_button);
        backButton = findViewById(R.id.back_button);
        routeNumber = findViewById(R.id.route_number);
        routeList = findViewById(R.id.routes_list);
        showFailure = findViewById(R.id.show_failure);
        progressBar = findViewById(R.id.progress_bar);
        db = FirebaseFirestore.getInstance();
        routes = db.collection("bus_search_app");
        toAndFroBus = new ArrayList<>();
        toBus = new ArrayList<>();
        froBus = new ArrayList<>();
        name = new ArrayList<>();
        itemHolders = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_route_button:
                implementRouteSearch();
                break;
            case R.id.back_button:
                finish();
                break;
        }
    }

    private void implementRouteSearch() {
        hideKeyboard(this, routeNumber);
        if(routeNumber.getText().toString().equals("")){
            showFailureMethod("Enter route please");
            return;
        }
        getTheData();
    }

    private void getTheData() {
        startProgressBar();
       DocumentReference docRef = routes.document(routeNumber.getText().toString());
       docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               String msg="";
               if(task.isSuccessful()){
                   DocumentSnapshot documentSnapshot = task.getResult();
                   if(documentSnapshot!=null){
                       if(documentSnapshot.exists()){
                           Map<String, Object> hm = documentSnapshot.getData();
                           if(hm!=null) {
                               for (Map.Entry<String, Object> entry : hm.entrySet()) {
                                   String s = entry.getValue().toString();
                                   String[] arr = s.split(",");
                                   toBus = new ArrayList<>();
                                   froBus = new ArrayList<>();
                                   toAndFroBus = new ArrayList<>();
                                   for (String i : arr) {
                                       if (i.contains("name")) {
                                           int index = i.indexOf("=");
                                           name.add(i.substring(index + 1));
                                       } else if (i.contains("to and fro")) {
                                           int index = i.indexOf("=");
                                           toAndFroBus.add(i.substring(0, index));
                                       } else if (i.contains("to")) {
                                           int index = i.indexOf("=");
                                           toBus.add(i.substring(0, index));
                                       } else {
                                           int index = i.indexOf("=");
                                           froBus.add(i.substring(0, index));
                                       }
                                   }
                                   itemHolders.add(new SingleRowItemHolder(-1, name.get(name.size()-1),
                                           " Number Buses from here: "+
                                                   (toBus.size()+toAndFroBus.size()+froBus.size())));
                                   if(hm.size()==name.size()){
                                       populateListView(msg);
                                   }
                               }
                           }else {
                               msg = "No data present";
                               populateListView(msg);
                           }
                       }else{
                           msg ="Not a valid route!";
                           populateListView(msg);
                       }
                   }
               }else{
                   msg = "Some error occurred while executing, please re-try!";
                   populateListView(msg);
               }
           }
       });
    }

    private void populateListView(final String msg) {
        CustomAdapter customAdapter = new CustomAdapter(RouteSearch.this, itemHolders);
        routeList.setAdapter(customAdapter);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                stopProgressBar(msg);
            }
        }, 600);

    }

    private void showFailureMethod(String msg) {
        showFailure.setVisibility(View.VISIBLE);
        showFailure.setText(msg);
    }

    private void stopProgressBar(String msg) {
        if(msg.length()==0) {
            progressBar.setVisibility(View.GONE);
            routeList.setVisibility(View.VISIBLE);
            showFailure.setVisibility(View.GONE);
        }else{
            showFailureMethod(msg);
        }
    }

    private void startProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        routeList.setVisibility(View.GONE);
    }

    public static void hideKeyboard(Activity activity, EditText et) {
        et.setFocusable(false);
        et.setFocusableInTouchMode(true);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
