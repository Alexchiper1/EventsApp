package com.example.eventsapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void loginClick(View view) {
        String email = ((EditText)findViewById(R.id.editEmail)).getText().toString();
        String password = ((EditText)findViewById(R.id.editPassword)).getText().toString();

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task ->{
            if(task.isSuccessful()){
                Toast.makeText(this, "Loged In Succesfulyl", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Failed To Login", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void regClick(View view) {
        String email = ((EditText)findViewById(R.id.editEmail)).getText().toString();
        String password = ((EditText)findViewById(R.id.editPassword)).getText().toString();

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task ->{
            if(task.isSuccessful()){
                Toast.makeText(this, "Registration Is Successful", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}