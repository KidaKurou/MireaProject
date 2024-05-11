package com.mirea.lutchenkoam.mireaproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private EditText etUsername, etEmail, etFIO;
    private TextView twUsername, twEmail, twFIO;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        twFIO = view.findViewById(R.id.twFIO);
        twUsername = view.findViewById(R.id.twUsername);
        twEmail = view.findViewById(R.id.twEmail);

        etFIO = view.findViewById(R.id.etFIO);
        etUsername = view.findViewById(R.id.etUsername);
        etEmail = view.findViewById(R.id.etEmail);

        Button btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveProfile());
        
        showProfileInfo();

        return view;
    }

    private void saveProfile() {
        sharedPreferences =
                requireActivity().getSharedPreferences(
                        "Profile",
                        Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("FIO", etFIO.getText().toString());
        editor.putString("Username", etUsername.getText().toString());
        editor.putString("Email", etEmail.getText().toString());
        editor.apply();
        
        showProfileInfo();
    }

    private void showProfileInfo(){
        Context context = getActivity();
        sharedPreferences = context.getSharedPreferences("Profile", Context.MODE_PRIVATE);

        String sFIO = sharedPreferences.getString("FIO", "undefined");
        String sUsername = sharedPreferences.getString("Username", "undefined");
        String sEmail = sharedPreferences.getString("Email", "undefined");

        if(!sFIO.equals("undefined"))
        {
            twFIO.setText(sFIO);
        }
        if(!sUsername.equals("undefined"))
        {
            twUsername.setText(sUsername);
        }
        if(!sEmail.equals("undefined"))
        {
            twEmail.setText(sEmail);
        }
    }
}