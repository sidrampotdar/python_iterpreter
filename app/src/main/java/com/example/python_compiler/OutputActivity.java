package com.example.python_compiler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;



public class OutputActivity extends AppCompatActivity {

    TextView tv_strop;
String outtxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);
        tv_strop=(TextView) findViewById(R.id.tv_strop);
        outtxt=getIntent().getStringExtra("op");
        tv_strop.setText(outtxt);

    }

}