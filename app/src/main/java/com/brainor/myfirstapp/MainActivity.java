package com.brainor.myfirstapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Console;
import java.util.Objects;

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
        web.uiHandler = new Handler();//绑定UI线程
        设置初始页面();

    }

    void 设置初始页面() {
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (数据.用户信息 item : 学生信息) adapter.add(item.学号);
        学号.setAdapter(adapter);

        for (Button item : button) {
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        连接(view);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (item.getItemId()) {
            case R.id.学号信息://明文存储你的用户名和密码
                return true;
            case R.id.提交位置:
                LayoutInflater inflater = LayoutInflater.from(this);
                builder.setView(inflater.inflate(R.layout.submit, null))
                        .setTitle("提交位置")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                builder.create().show();
                return true;
            case R.id.关于:
                builder.setMessage("作者:欧伟科\n联系方式:Brainor@qq.com")
                        .setTitle("关于")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                builder.create().show();
                return true;
            default:
                return true;
        }
    }

    static final String[] 连接类型 = new String[]{"ipgwopen", "ipgwopenall", "ipgwclose", "ipgwcloseall"};//连接, 收费链接, 断开连接, 断开所有连接
    Button[] IP按钮 = new Button[2];


    private void 连接(View view) throws Exception {
        ////清除文本信息
        //textBlock.Inlines.Clear();

        //grid.Children.Remove(IP按钮[0]);
        //grid.Children.Remove(IP按钮[1]);
        //确定学号和密码
        web.学生 = 学生信息[学号.getSelectedItemPosition()];
        //确定免费/收费地址
        short Tag;
        //Button button = (Button)view;
        Tag = Short.parseShort(view.getTag().toString());
        //Convert.ToInt16(((Button)sender).Tag);//在主界面上点击按钮
        if (radioButton[1].isChecked() && Tag == 0) Tag = 1;//收费
        new 网络交互().execute(连接类型[Tag]);
        /*String[] Content = web.连接(连接类型[Tag]);
        判断(Content);*/

    }

    private void 判断(String[] Content) {
        if (Objects.equals(Content[0], "错误")) {
            textBlock.setText(Content[1]);
            return;
        }
        if (Content[0].contains("YES"))//(断开)连接成功, 显示信息
        {
            String 显示文本 = "";
            for (int i = 1; i < Content.length; i++) {
                显示文本 = 显示文本 + Content[i] + "\n";
                //if (连接Tag == 3) 图标.Icon = new System.Drawing.Icon(@"E:\Code\Visual Studio\网关\PC\Resources\2.ico");//收费连接更改图标
                //else 图标.Icon = new System.Drawing.Icon(@"E:\Code\Visual Studio\网关\PC\Resources\1.ico");
            }
            textBlock.setText(显示文本);
        } else//连接失败
        {
            断开指定连接(Content);
        }
    }

    private void 断开指定连接(String[] Content) {
        setContentView(R.layout.disconnect);
        TextView textBlock;
        Button[] IP按钮 = new Button[2];

        textBlock = (TextView) findViewById(R.id.信息文本);
        IP按钮[0] = (Button) findViewById(R.id.button1);
        IP按钮[1] = (Button) findViewById(R.id.button2);

        int IP数量 = Content.length / 2;
        String 显示文本 = "断开指定连接\n";
        for (int i = 0; i < IP数量; i++) {
            IP按钮[i].setVisibility(View.VISIBLE);//有IP地址就设为可见
            IP按钮[i].setTag(Content[2 * i]);//增加Tag
            IP按钮[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    设置初始页面();
                    String[] Content_断开连接 = new String[0];
                    new 网络交互().execute(view.getTag().toString(),"断开指定连接");
                    /*try {
                        Content_断开连接 = web.断开指定连接(view.getTag().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    判断(Content_断开连接);*/
                }
            });

            显示文本 += "IP" + (i + 1) + ":" + Content[2 * i] + "\n连接时间:" + Content[2 * i + 1] + "\n";
            textBlock.setText(显示文本);
        }

    }
    private class 网络交互 extends AsyncTask<String,Void,String[]>{
        @Override
        protected String[] doInBackground(String... URLs) {
            if (URLs.length==1) {//是一开始的连接
                try {
                    return web.连接(URLs[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{//断开指定连接
                try {
                    return web.断开指定连接(URLs[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return new String[]{"错误"};
        };
        @Override
        protected void onPostExecute(String[] Content) {
            判断(Content);
        }
    }
}
