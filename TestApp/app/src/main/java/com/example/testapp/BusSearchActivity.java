package com.example.testapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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

public class BusSearchActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView busSearchButton;
    private ImageView backButton;
    private ListView busSearchList;
    private EditText fromRoute, toRoute;
    private TextView errorText;
    private ProgressBar progressBar;
    private ArrayList<SingleRowItemHolder> busDetails;
    private ArrayList<String> fromHere, toHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_search);

        initializeVariables();

        setOnClickBehavior();
    }

    private void setOnClickBehavior() {
        busSearchButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    private void initializeVariables() {
        busSearchButton = findViewById(R.id.search_bus_button);
        backButton = findViewById(R.id.back_button);
        busSearchList = findViewById(R.id.find_buses_list);
        fromRoute = findViewById(R.id.from_route);
        toRoute = findViewById(R.id.to_route);
        errorText = findViewById(R.id.error_text);
        progressBar = findViewById(R.id.progress_bar);
        busDetails = new ArrayList<>();
        fromHere = new ArrayList<>();
        toHere = new ArrayList<>();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_bus_button:
                implementBusSearch();
                break;
            case R.id.back_button:
                finish();
                break;
        }
    }

    private void implementBusSearch() {
        hideKeyboard(this, toRoute, fromRoute);
        if(toRoute.getText().toString().length()==0 || fromRoute.getText().toString().length()==0){
            showFailure("Fill all the fields!");
            return;
        }
        getData();
    }

    private void showFailure(String s) {
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(s);
    }

    private void getData() {
        showProgressBar();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference busSearch = db.collection("bus_search_app");
        DocumentReference fromRouteSearch = busSearch.document(fromRoute.getText().toString());
        fromRouteSearch.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult()!=null){
                        Map<String, Object> hm = task.getResult().getData();
                        if (hm != null) {
                            int i=0;
                            for(Map.Entry<String, Object> e: hm.entrySet()){
                                String s = e.getValue().toString().trim();
                                s = s.substring(1);
                                String[] arr = s.split(", ");
                                for(String data: arr){
                                    if(data.contains("to") && data.contains("bus")){
                                        int equalsTo = data.indexOf("=");
                                        if(!fromHere.contains(data.substring(0, equalsTo))) {
                                            fromHere.add(data.substring(0, equalsTo));
                                        }
                                    }
                                }
                                i++;
                                if(i==hm.size()){
                                    getFroData(busSearch);
                                }
                            }
                        }

                    }
                }
            }
        });


    }

    private void getFroData(CollectionReference busSearch) {
        DocumentReference toRouteSearch = busSearch.document(toRoute.getText().toString());
        toRouteSearch.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult()!=null){
                        Map<String, Object> hm = task.getResult().getData();
                        if (hm != null) {
                            int i=0;
                            for(Map.Entry<String, Object> e: hm.entrySet()){
                                String s = e.getValue().toString().trim();
                                s = s.substring(1);
                                String[] arr = s.split(", ");
                                for(String data: arr){
                                    if(data.contains("fro") && !toHere.contains(data)){
                                        int equalsTo = data.indexOf("=");
                                        toHere.add(data.substring(0,equalsTo));
                                    }
                                }
                                i++;
                                if(i==hm.size()){
                                    addDataToBusDetails();
                                }
                            }
                        }else{
                            stopProgressBar(false);
                            showFailure("No data available");
                        }
                    }else{
                        stopProgressBar(false);
                        showFailure("No data available");
                    }
                }else{
                    stopProgressBar(false);
                    showFailure("Unable to process request, try again later");
                }
            }
        });
    }

    private void stopProgressBar(boolean removeErrorText) {
        progressBar.setVisibility(View.GONE);
        busSearchList.setVisibility(View.VISIBLE);
        if(removeErrorText)
            errorText.setVisibility(View.GONE);
    }

    private void addDataToBusDetails() {
        for(String s:fromHere){
            if(toHere.contains(s)){
                busDetails.add(new SingleRowItemHolder(-1, s, "Up and Down"));
            }
        }
        showList();
    }

    private void showList() {
        if(busDetails.size()>0) {
            CustomAdapter adapter = new CustomAdapter(BusSearchActivity.this, busDetails);
            busSearchList.setAdapter(adapter);
            stopProgressBar(true);
        }else{
            stopProgressBar(false);
            showFailure("No bus available");
        }
    }

    private void showProgressBar() {
        busDetails = new ArrayList<>();
        toHere = new ArrayList<>();
        fromHere = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        busSearchList.setVisibility(View.GONE);
        errorText.setVisibility(View.GONE);
    }

    public static void hideKeyboard(Activity activity, EditText et, EditText et2) {
        et.setFocusable(false);
        et2.setFocusable(false);
        et2.setFocusableInTouchMode(true);
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
