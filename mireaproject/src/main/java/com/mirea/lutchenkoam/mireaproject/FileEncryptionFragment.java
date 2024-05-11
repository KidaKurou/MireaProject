package com.mirea.lutchenkoam.mireaproject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FileEncryptionFragment extends Fragment {

    private Uri fileUri;
    private FileCryptographyViewModel viewModel;
    private ActivityResultLauncher<Intent> filePickerActivityResultLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FileCryptographyViewModel.class);

        filePickerActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        fileUri = result.getData().getData();
                        Toast.makeText(getContext(), "Выбран файл: " + fileUri.getPath(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_encryption, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        EditText passwordEditText = view.findViewById(R.id.passwordEditText);
        Button encryptButton = view.findViewById(R.id.encryptButton);
        Button decryptButton = view.findViewById(R.id.decryptButton);

        fab.setOnClickListener(v -> chooseFile());

        encryptButton.setOnClickListener(v -> {
            String password = passwordEditText.getText().toString();
            if (!password.isEmpty() && fileUri != null) {
                viewModel.encryptFile(getContext(), fileUri, password);
            } else {
                Toast.makeText(getContext(), "Выберите файл и введите ключ шифрования", Toast.LENGTH_LONG).show();
            }
        });

        decryptButton.setOnClickListener(v -> {
            String password = passwordEditText.getText().toString();
            if (!password.isEmpty() && fileUri != null) {
                viewModel.decryptFile(getContext(), fileUri, password);
            } else {
                Toast.makeText(getContext(), "Выберите файл и введите ключ дешифрования", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        filePickerActivityResultLauncher.launch(intent);
    }
}
