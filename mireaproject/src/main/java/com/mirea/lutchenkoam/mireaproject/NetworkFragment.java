package com.mirea.lutchenkoam.mireaproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkFragment extends Fragment {

    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_network, container, false);
        textView = view.findViewById(R.id.text);

        // Разрешить выполнение сетевых операций в основном потоке
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL url = new URL("https://ipinfo.io/json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder jsonResult = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                jsonResult.append(line);
            }
            String locationResult = jsonResult.toString();
            JSONObject locationObject = new JSONObject(locationResult);
            String city = locationObject.getString("city");
            String region = locationObject.getString("region");

            String loc = locationObject.getString("loc");
            String[] latLong = loc.split(",");
            String latitude = latLong[0];
            String longitude = latLong[1];

            URL weatherUrl = new URL(String.format("https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&current_weather=true", latitude, longitude));
            HttpURLConnection weatherConnection = (HttpURLConnection) weatherUrl.openConnection();
            BufferedReader weatherReader = new BufferedReader(new InputStreamReader(weatherConnection.getInputStream()));
            StringBuilder weatherResult = new StringBuilder();
            while ((line = weatherReader.readLine()) != null) {
                weatherResult.append(line);
            }
            String weatherData = weatherResult.toString();

            textView.setText("City: " + city + "\nRegion: " + region + "\nWeather Data: " + weatherData);

        } catch (Exception e) {
            textView.setText("Error: " + e.getMessage());
        }

        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}