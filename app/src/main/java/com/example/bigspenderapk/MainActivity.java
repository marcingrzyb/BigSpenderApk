package com.example.bigspenderapk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onClickKantor(View view){
        startActivity(new Intent(this,KantorActivity.class));
    }
    public void onClickTrans(View view){
        startActivity(new Intent(this,TransActivity.class));
    }
    public void onClickInfo(View view){
        Toast.makeText(getBaseContext(),getString(R.string.Info),Toast.LENGTH_SHORT).show();
    }
}
