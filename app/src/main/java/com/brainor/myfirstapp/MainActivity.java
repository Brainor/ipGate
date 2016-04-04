package com.brainor.myfirstapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.AndroidCharacter;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    数据.用户信息[] 学生信息=new 数据.用户信息[2];
    网络 web=new 网络();
    //各种按钮
    RadioButton[] radioButton=new RadioButton[2];
    TextView textBlock;
    Button[] button=new Button[3];
    Spinner 学号;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //增加用户信息列表
        学生信息[0] = new 数据.用户信息("1301110110","Oudanyi6456");
        学生信息[1] = new 数据.用户信息("gcuspea", "phyfsc508");

        // Get our button from the layout resource,
        // and attach an event to it
        radioButton[0] = (RadioButton)findViewById(R.id.radioButton1);
        radioButton[1] =(RadioButton)findViewById(R.id.radioButton2);
        textBlock =(TextView) findViewById(R.id.信息文本);
        button[0] = (Button)findViewById(R.id.button1);
        button[1] = (Button)findViewById(R.id.button2);
        button[2] = (Button)findViewById(R.id.button3);
        学号 = (Spinner)findViewById(R.id.学生信息);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
