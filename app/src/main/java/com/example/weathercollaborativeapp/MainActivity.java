package com.example.weathercollaborativeapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.weathercollaborativeapp.model.WeatherType;
import com.example.weathercollaborativeapp.network.WeatherService;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        textViewResult = findViewById(R.id.textViewResult);

        setupRetrofit();

    }

    private void setupRetrofit() {

        Log.d("JADEN COUCHOT", "Setting up Retrofit...");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Log.d("JADEN COUCHOT", "Log1");


        WeatherService service = retrofit.create(WeatherService.class);

        Call<List<WeatherType>> call = service.listWeatherTypes();
        Log.d("toto", "Log2");

        call.enqueue(new Callback<List<WeatherType>>() {
            @Override
            public void onResponse(Call<List<WeatherType>> call, Response<List<WeatherType>> response) {
                Log.d("toto", "onResponse: " + response.toString());
                if(response.isSuccessful()){
                    List<WeatherType> weatherTypes = response.body();
                    textViewResult.setText("Loaded " + weatherTypes.size() + " weather types.");
                }
                else{
                    Log.d("toto", "Error: " + response.code() + " - " + response.errorBody());
                    Toast.makeText(MainActivity.this, "Error loading weather types.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<WeatherType>> call, Throwable throwable) {
                Log.e("toto", "onFailure: " + throwable.getMessage(), throwable);
                Toast.makeText(MainActivity.this, "Failure: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}