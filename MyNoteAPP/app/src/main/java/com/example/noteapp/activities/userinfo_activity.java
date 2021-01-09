package com.example.noteapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.noteapp.R;

public class userinfo_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        Button next = findViewById(R.id.buttonNext);
        EditText editText = findViewById(R.id.inputName);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Enter Your name",Toast.LENGTH_SHORT).show();
                }else
                {

                Intent intent = new Intent(userinfo_activity.this, MainActivity2.class);
                String userName = editText.getText().toString();
                intent.putExtra("userName",userName);
                startActivity(intent);}
            }
        });
    }
}