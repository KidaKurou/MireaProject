package com.mirea.lutchenkoam.mireaproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import com.mirea.lutchenkoam.mireaproject.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    String userState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        binding.createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailField.getText().toString();
                String password = binding.passwordField.getText().toString();
                createAccount(email, password);
            }
        });

        binding.signedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailField.getText().toString();
                String password = binding.passwordField.getText().toString();
                signIn(email, password);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        userState = getIntent().getStringExtra("userState");
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if(userState != null && userState.equals("signedOut"))
        {
            user = null;
            userState = null;
        }
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            binding.emailField.setVisibility(View.VISIBLE);
            binding.passwordField.setVisibility(View.VISIBLE);
            binding.signedIn.setVisibility(View.VISIBLE);
            binding.createAcc.setVisibility(View.VISIBLE);
        }

    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure",
                                    task.getException());
                            if (task.getException() instanceof FirebaseAuthException) {
                                handleAuthenticationException((FirebaseAuthException) task.getException());
                            }
                            updateUI(null);
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthException) {
                                handleAuthenticationException((FirebaseAuthException) task.getException());
                            }
                            updateUI(null);
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = binding.emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            binding.emailField.setError("Требуется электронная почта.");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailField.setError("Неверный формат электронной почты.");
            valid = false;
        } else {
            binding.emailField.setError(null);
        }

        String password = binding.passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            binding.passwordField.setError("Требуется пароль.");
            valid = false;
        } else if (password.length() < 6) {
            binding.passwordField.setError("Пароль должен содержать не менее 6 символов.");
            valid = false;
        } else {
            binding.passwordField.setError(null);
        }

        return valid;
    }

    private void handleAuthenticationException(FirebaseAuthException e) {
        String errorCode = e.getErrorCode();

        switch (errorCode) {
            case "ERROR_INVALID_EMAIL":
                binding.emailField.setError("Неверный формат электронной почты.");
                break;
            case "ERROR_WRONG_PASSWORD":
                binding.passwordField.setError("Неверный пароль.");
                break;
            // Добавьте здесь другие коды ошибок по мере необходимости
            default:
                Toast.makeText(LoginActivity.this, "Ошибка аутентификации.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
