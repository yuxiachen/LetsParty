package com.example.android.letsparty.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.letsparty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private EditText et_email;
    private EditText et_username;
    private EditText et_password;
    private EditText et_confirm_password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.signup);
        et_email = findViewById(R.id.et_email);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_confirm_password = findViewById(R.id.et_confirm_password);


        Button btn_signup = findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInputIsValid()) {
                    signUpNewUser();
                }
            }
        });
    }

    private boolean checkInputIsValid(){
        String password = et_password.getText().toString().trim();
        String passwordConfirm = et_confirm_password.getText().toString().trim();
        String username = et_username.getText().toString().trim();
        String email = et_email.getText().toString().trim();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.requestFocus();
            showErrorMessage(getString(R.string.error_invalid_email));
            return false;
        }

        if (username.isEmpty()) {
            et_username.requestFocus();
            showErrorMessage(getString(R.string.error_invalid_username));
            return false;
        }


        if (password.isEmpty() || password.length() < 8) {
            et_password.requestFocus();
            showErrorMessage(getString(R.string.error_invalid_password));
            return false;
        }

        if (!password.equals(passwordConfirm)) {
            et_confirm_password.requestFocus();
            showErrorMessage(getString(R.string.error_invalid_confirm_password));
            return false;
        }

        return true;
    }

    private void showErrorMessage(String error) {
        TextView tv_error = findViewById(R.id.error_message);
        tv_error.setText(error);
        tv_error.setVisibility(View.VISIBLE);
    }

    private void signUpNewUser() {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final String password = et_password.getText().toString().trim();
        final String username = et_username.getText().toString().trim();
        final String email = et_email.getText().toString().trim();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            auth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Sign Up Successfully, please check your email for verification!", Toast.LENGTH_LONG).show();
                                                openLoginActivity();
                                            }
                                        }
                                    });

                        } else {
                            // If sign in fails, display a message to the user.
                            showErrorMessage("Error: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }
}
