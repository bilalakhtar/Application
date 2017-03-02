package com.project.bilalakhtar.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Button button =(Button) findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void  onClick(View v){
//                gotoSecondActivity();
//            }
//        });
    }
    //abi k liay kafi hai. Go home, study android. Check out available libraries. Teach yourself, otherwise nae samajh aay ga
    //practic, hhit and try etc...
    //tutorial? this apllication
    //Check out bucky roberts channel on youtube!
    //sath sath kaam
    //depends on how much effort you want to put in!
    //I self taught
    //mostly used qmobile
//    1
//    testing
    public void secondOnclick(View v)//lazmi.
    {
        Intent intent= new Intent(this, SecondActivity.class);
        startActivity(intent);
    }
    public void fn(View v)
    {
        Intent intent= new Intent(this, joyActivity.class);
        startActivity(intent);
    }
}



