package com.brainor.myfirstapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    data.userInfo[] 学生信息 = new data.userInfo[2];
    network web = new network();
    //各种按钮
    RadioButton[] radioButton = new RadioButton[2];
    TextView textBlock;
    Button[] button = new Button[3];
    Spinner 学号;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        设置初始页面();
    }

    void 设置初始页面() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //增加用户信息列表
        学生信息[0] = new data.userInfo("1301110110", "Oudanyi6456");
        学生信息[1] = new data.userInfo("gcuspea", "phyfsc508");

        // Get our button from the layout resource,
        // and attach an event to it
        radioButton[0] = (RadioButton) findViewById(R.id.radioButton1);
        radioButton[1] = (RadioButton) findViewById(R.id.radioButton2);
        textBlock = (TextView) findViewById(R.id.信息文本);
        button[0] = (Button) findViewById(R.id.button1);
        button[1] = (Button) findViewById(R.id.button2);
        button[2] = (Button) findViewById(R.id.button3);
        学号 = (Spinner) findViewById(R.id.学生信息);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (data.userInfo item : 学生信息) adapter.add(item.学号);
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
                new AsyncTask<Void, Void, String>() {//获取IP地址
                    @Override
                    protected String doInBackground(Void... params) {
                        String IP地址="";
                        try {
                            IP地址=Inet4Address.getLocalHost().getHostAddress();//NetworkInterface.getNetworkInterfaces()
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                        return IP地址;
                    }
                    @Override
                    protected void onPostExecute(String IPAddr) {
                        TextView IP地址=(TextView)findViewById(R.id.IP地址);
                        IP地址.setText(IPAddr);
                    }
                }.execute();

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
        Tag = Short.parseShort(view.getTag().toString());
        if (radioButton[1].isChecked() && Tag == 0) Tag = 1;//收费
        new netInteract().execute(连接类型[Tag]);
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
            for (int i = 1; i < Content.length; i++) 显示文本 += Content[i] + "\n";
//            textBlock.setMovementMethod(ScrollingMovementMethod.getInstance());//滚动
            textBlock.setText(显示文本);
        } else//连接失败
            断开指定连接(Content);
    }

    private void 断开指定连接(String[] Content) {
        setContentView(R.layout.disconnect);
        final TableLayout tableLayout = (TableLayout) findViewById(R.id.表格);
        int IP数量 = Content.length / 3;

        TableRow tableRow;
        TextView 文本;
        Button 断开按钮;
        TableRow.LayoutParams 单元格参数;
        for (int i = 0; i < IP数量; i++) {
            tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
            for (int j = 0; j < 3; j++) {
                单元格参数 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                文本 = new TextView(this);
                if (j == 1) {
                    Content[3 * i + j] = Content[3 * i + j].replace("地址", "");
                    if(Objects.equals(Content[3 * i + j], "收费"))文本.setTextColor(Color.RED);
                }
                文本.setText(Content[3 * i + j]);
                单元格参数.gravity = (j != 1) ? Gravity.LEFT | Gravity.CENTER_VERTICAL : Gravity.CENTER;
                文本.setLayoutParams(单元格参数);
                文本.setPadding(8, 0, 8, 0);
                tableRow.addView(文本);
            }
            单元格参数 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            断开按钮 = new Button(this);
            断开按钮.setText("断开\n连接");
            单元格参数.gravity = Gravity.CENTER;
            断开按钮.setLayoutParams(单元格参数);
            断开按钮.setPadding(6, 0, 6, 0);
            断开按钮.setTag(Content[3 * i]);
            断开按钮.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    设置初始页面();
                    String[] Content_断开连接 = new String[0];
                    new netInteract().execute(view.getTag().toString(), "断开指定连接");
                }
            });
            tableRow.addView(断开按钮);
            if (tableLayout != null) {
                tableLayout.addView(tableRow);
            }
        }
    }

    private class netInteract extends AsyncTask<String, Void, String[]> {//网络交互
        @Override
        protected String[] doInBackground(String... URLs) {
            if (URLs.length == 1) {//是一开始的连接
                try {
                    return web.连接(URLs[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {//断开指定连接
                try {
                    return web.断开指定连接(URLs[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return new String[]{"错误"};
        }

        @Override
        protected void onPostExecute(String[] Content) {
            判断(Content);
        }
    }
}
