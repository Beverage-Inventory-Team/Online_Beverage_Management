package com.example.database;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText Name;
    private EditText Password;
    private TextView Info;
    private Button Login;
    private TextView ForgotPassword;
    private EditText ResetEmail;
    private ProgressBar progressBar;
    private int counter = 3;
    private TextView Registration;
    private FirebaseAuth Fauth;
    private ProgressDialog processDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Name = (EditText) findViewById(R.id.et_Name);
        ResetEmail = findViewById(R.id.et_Name);
        Password = (EditText) findViewById(R.id.et_Password);
        Login = (Button) findViewById(R.id.btn_Login);
        Info = (TextView) findViewById(R.id.tv_info);
        Registration = (TextView) findViewById(R.id.tv_Register);
        ForgotPassword = findViewById(R.id.forgotpassword);
        Info.setText("No of attempts remaining: 3");
        progressBar = (ProgressBar) findViewById(R.id.progressbars);
        progressBar.setVisibility(View.GONE);

        processDialog = new ProgressDialog(this);

        Fauth = FirebaseAuth.getInstance();

        //Pro= new progress(MainActivity.this);

        FirebaseUser user = Fauth.getCurrentUser();

        if (user != null) {
            finish();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Name.getText().toString().isEmpty() || Password.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                } else {
                    validate(Name.getText().toString(), Password.getText().toString());
                }

            }

        });

        Registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });
    }

    private void forgotPassword() {
        final String Resetemail = ResetEmail.getText().toString();

        if (Resetemail.isEmpty()) {
            ResetEmail.setError("It's empty");
            ResetEmail.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        Fauth.sendPasswordResetEmail(Resetemail)

                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }

                        progressBar.setVisibility(View.GONE);
                    }
                });


    }

    private void validate(String UserName, String userPassword) {
        processDialog.setMessage("..............Please Wait..........");
        processDialog.show();

        Fauth.signInWithEmailAndPassword(UserName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    processDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));

                } else {
                    processDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();

                    counter--;

                    Info.setText("No.of attempts remaining: " + String.valueOf(counter));

                    if (counter == 0) {
                        Login.setEnabled(false);
                    }
                }
            }
        });
    }
}