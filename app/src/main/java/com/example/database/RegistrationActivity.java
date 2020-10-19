package com.example.database;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText UsName;
    private EditText UsPassword;
    private EditText Confirm;
    private EditText Email;
    private TextView UserLogin;
    private Button Save;
    private FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupUIViews();

        Auth = FirebaseAuth.getInstance();

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate_data())
                {
                    //upload data to database
                    String us_email= Email.getText().toString().trim();
                    String us_password= UsPassword.getText().toString().trim();

                    Auth.createUserWithEmailAndPassword(us_email,us_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                            }
                            else
                            {
                                Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        UserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });
    }

    private void setupUIViews()
    {
        UsName=(EditText)findViewById(R.id.et_User);
        UsPassword=(EditText)findViewById(R.id.et_UserPassword);
        Email=(EditText)findViewById(R.id.et_Email);
        UserLogin=(TextView)findViewById(R.id.tv_back);
        Save=(Button)findViewById(R.id.btn_Register);
        Confirm = (EditText) findViewById(R.id.et_confirm);
    }

    private Boolean validate_data()
    {
        Boolean result=false;
        String usName=UsName.getText().toString();
        String UsPass=UsPassword.getText().toString();
        String UEmail=Email.getText().toString();
        String UsConfirm = Confirm.getText().toString();

        if (UEmail.isEmpty()) {
            Email.setError("Please enter your email address");
            Email.requestFocus();
            return result;
        }

        if (usName.isEmpty()) {
            UsName.setError("Please enter your name");
            UsName.requestFocus();
            return result;
        }

        if (UsPass.isEmpty()) {
            UsPassword.setError("Please enter a password");
            UsPassword.requestFocus();
            return result;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(UEmail).matches()) {
            Email.setError("Not a valid email address");
            Email.requestFocus();
            return result;
        }


        if (UsPass.length() < 6) {
            UsPassword.setError("Password should be at least 6 character long");
            UsPassword.requestFocus();
            return result;
        }
        if(!UsPass.equals(UsConfirm)){
            Confirm.setError("Passwords Do not Match");
            Confirm.requestFocus();
            return result;
        }

        return true;

    }
}