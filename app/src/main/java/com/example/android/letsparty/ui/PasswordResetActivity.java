package com.example.android.letsparty.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.letsparty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordResetActivity extends AppCompatActivity {
    private EditText currentPwd;
    private EditText newPwd;
    private EditText newPwdConfirm;
    private Button saveBtn;
    private String sCurrentPwd;
    private String sNewPwd;
    private String sNewPwdConfirm;
    private boolean isValidInput;
    private FirebaseAuth mAuth;
    private ActionBar mActionBar;
    final private String TAG = PasswordResetActivity.class.getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        mActionBar = getSupportActionBar();
        mActionBar.setTitle("Change Password");
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        currentPwd = (TextInputEditText) findViewById(R.id.change_un_input);
        newPwd = (TextInputEditText) findViewById(R.id.change_newpw_input);
        newPwdConfirm = (TextInputEditText) findViewById(R.id.change_newpw_input1);

        saveBtn =(Button)findViewById(R.id.bt_pw_save) ;

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInputIsValid();
            }
        });
    }

    private void checkInputIsValid() {
        sCurrentPwd = currentPwd.getText().toString().trim();
        sNewPwd = newPwd.getText().toString().trim();
        sNewPwdConfirm = newPwdConfirm.getText().toString().trim();
        isValidInput = true;
        if (sCurrentPwd.isEmpty() ){
            currentPwd.requestFocus();
            isValidInput = false;
            Toast.makeText(getApplicationContext(), getString(R.string.error_invalid_password), Toast.LENGTH_LONG).show();
        }
        if (!sNewPwd.equals(sNewPwdConfirm)) {
            newPwdConfirm.requestFocus();
            isValidInput = false;
            Toast.makeText(getApplicationContext(), getString(R.string.error_invalid_confirm_password), Toast.LENGTH_LONG).show();
        }
        if (sNewPwd.equals(sNewPwdConfirm)) {
            if(sNewPwd.length() <= 5){
                Toast.makeText(getApplicationContext(), "The password should be at least 6 Characters!", Toast.LENGTH_SHORT).show();
            }else updatePassword();
        }
    }

    private void updatePassword() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email, sCurrentPwd);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updatePassword(sNewPwd).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(),
                                        "Password has been Updated",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "Error in updating Password",
                                        task.getException());
                                Toast.makeText(getApplicationContext(),
                                        "Failed to Update Password.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
