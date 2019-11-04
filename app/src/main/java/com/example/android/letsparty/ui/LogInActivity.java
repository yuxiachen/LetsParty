package com.example.android.letsparty.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.letsparty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LogInActivity extends AppCompatActivity {
    private EditText et_email;
    private EditText et_password;
    private FirebaseAuth auth;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);

        TextView tv_forget_password = findViewById(R.id.tv_forget_password);
        tv_forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPasswordResetActivity();
            }
        });
        Button login = findViewById(R.id.btn_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInputIsValid()) {
                    signInWithEmail();
                }
            }
        });
        Button signUp = findViewById(R.id.btn_signup);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignUpActivity();
            }
        });
    }

    private boolean checkInputIsValid() {
        String password = et_password.getText().toString().trim();
        String email = et_email.getText().toString().trim();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.requestFocus();
            showErrorMessage(getString(R.string.error_invalid_email));
            return false;
        }

        if (password.isEmpty()) {
            et_password.requestFocus();
            showErrorMessage(getString(R.string.error_empty_password));
            return false;
        }
        return true;
    }

    private void signInWithEmail() {
        auth.signInWithEmailAndPassword(et_email.getText().toString(), et_password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (auth.getCurrentUser().isEmailVerified()) {
                                openHomeActivity();
                            } else {
                                showErrorMessage(getString(R.string.error_email_not_verified));
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            showErrorMessage("Error: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void showErrorMessage(String error) {
        TextView errorMessage = findViewById(R.id.error_message);
        errorMessage.setText(error);
        errorMessage.setVisibility(View.VISIBLE);
    }

    private void openSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void openPasswordResetActivity() {
        Intent intent = new Intent(this, PasswordResetActivity.class);
        startActivity(intent);
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
