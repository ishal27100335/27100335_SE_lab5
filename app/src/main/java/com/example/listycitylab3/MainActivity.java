package com.example.listycitylab3;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements AddCityFragment.AddCityDialogListener,
        EditCityFragment.EditCityDialogListener {

    FirebaseFirestore db;
    CollectionReference citiesRef;

    private ArrayList<City> dataList;
    private ListView cityList;
    private CityArrayAdapter cityAdapter;

    @Override
    public void editCity(City city) {
        citiesRef.document(city.getName())
                .set(city);
        cityAdapter.notifyDataSetChanged();
    }

    @Override
    public void addCity(City city) {
        citiesRef.document(city.getName()).set(city);
    }

    public void deleteCity(City city) {
        citiesRef.document(city.getName()).delete();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        citiesRef = db.collection("cities");

        dataList = new ArrayList<>();

        cityList = findViewById(R.id.city_list);
        cityAdapter = new CityArrayAdapter(this, dataList);
        cityList.setAdapter(cityAdapter);

        cityList.setOnItemClickListener((parent, view, position, id) -> {
            City selectedCity = dataList.get(position);
            new EditCityFragment(selectedCity)
                    .show(getSupportFragmentManager(), "Edit City");
        });

        FloatingActionButton fab = findViewById(R.id.button_add_city);
        fab.setOnClickListener(v -> {
            new AddCityFragment().show(getSupportFragmentManager(), "Add City");
        });

        citiesRef.addSnapshotListener((snapshots, error) -> {
            if (error != null) return;

            cityAdapter.clear();
            for (QueryDocumentSnapshot doc : snapshots) {
                String name = doc.getString("name");
                String province = doc.getString("province");
                cityAdapter.add(new City(name, province));
            }
            cityAdapter.notifyDataSetChanged();
        });
    }
}