package com.brainor.myfirstapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ArrayList<data.userInfo> 学生信息 = new ArrayList<>();
    network web = new network();
    //各种按钮
    Switch 地址转换;
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
        final File 文件 = new File(getExternalFilesDir(null), "user.ini");
        try {
            if (文件.isFile()) {
                try(BufferedReader bufferedReader = new BufferedReader(new FileReader(文件))) {
                    String 学号;
                    while ((学号 = bufferedReader.readLine()) != null) {
                        if (Objects.equals(学号, "")) break;
                        学生信息.add(new data.userInfo(学号, bufferedReader.readLine()));
                    }
                }
            } else {
                文件.createNewFile();
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        /*学生信息.add(new data.userInfo("1301110110", "Oudanyi6456"));
        学生信息.add(new data.userInfo("gcuspea", "phyfsc508"));*/
        if (学生信息.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("没有学号信息, 请添加.")
                    .setPositiveButton("添加学号", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            学号信息();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            builder.show();
        }
        // Get our button from the layout resource,
        // and attach an event to it
        地址转换 = (Switch) findViewById(R.id.地址转换);
        textBlock = (TextView) findViewById(R.id.信息文本);
        button[0] = (Button) findViewById(R.id.button1);
        button[1] = (Button) findViewById(R.id.button2);
        button[2] = (Button) findViewById(R.id.button3);
        学号 = (Spinner) findViewById(R.id.学生信息);

        设置列表();

        for (Button item : button)
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    连接(view);
                }
            });
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
                学号信息();
                return true;
            case R.id.提交位置:
                //先检查是否连接Wireless PKU
                final WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                String wifi名称 = wifiManager.getConnectionInfo().getSSID().replace("\"", "");
                if (!Objects.equals(wifi名称, "Wireless PKU")) {
                    builder.setMessage("当前WiFi名称是" + wifi名称 + "\n请将WiFi连接至Wireless PKU后提交当前位置.");
                    List<ScanResult> scanResult = wifiManager.getScanResults();
                    int i;
                    for (i = 0; i < scanResult.size(); i++) {
                        if (Objects.equals(scanResult.get(i).SSID, "Wireless PKU")) {
                            for (WifiConfiguration wifiConfiguration : wifiManager.getConfiguredNetworks()) {
                                if (Objects.equals(wifiConfiguration.SSID, "\"Wireless PKU\"")) {
                                    final WifiConfiguration wifi设置 = wifiConfiguration;
                                    builder.setPositiveButton("连接", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            wifiManager.enableNetwork(wifi设置.networkId, true);
                                        }
                                    });
                                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    builder.create().show();
                    return true;
                }
                LayoutInflater inflater = LayoutInflater.from(this);
                final View 提交位置view = inflater.inflate(R.layout.submit, null);
                builder.setView(提交位置view)
                        .setTitle("提交位置")
                        .setPositiveButton("提交", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                CharSequence[] 邮件信息=new CharSequence[3];
                                邮件信息[0]=((TextView) 提交位置view.findViewById(R.id.IP地址)).getText();
                                邮件信息[1]=((TextView) 提交位置view.findViewById(R.id.子网掩码)).getText();
                                邮件信息[2]=((TextView) 提交位置view.findViewById(R.id.物理地址)).getText();
                                if(邮件信息[2].length()==0)
                                    Toast.makeText(getApplicationContext(), "请输入物理地址", Toast.LENGTH_LONG).show();
                                else {
                                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:brainor@qq.com"));
                                    intent.putExtra(Intent.EXTRA_SUBJECT, "IP地址")
                                            .putExtra(Intent.EXTRA_TEXT, Arrays.toString(邮件信息));
                                    startActivity(intent);
                                    MainActivity.this.finish();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                builder.create().show();
                new AsyncTask<Void, Void, String[]>() {//获取IP地址
                    @Override
                    protected String[] doInBackground(Void... params) {
                        String IP地址 = "", 掩码长度 = "";
                        try {
                            for (InterfaceAddress interfaceAddress : NetworkInterface.getByName("wlan0").getInterfaceAddresses())
                                if (interfaceAddress.getAddress() instanceof Inet4Address) {
                                    IP地址 = interfaceAddress.getAddress().getHostAddress();
                                    掩码长度 = Short.toString(interfaceAddress.getNetworkPrefixLength());
                                    break;
                                }
                        } catch (SocketException e) {
                            e.printStackTrace();
                        }
                        return new String[]{IP地址, 掩码长度};
                    }

                    @Override
                    protected void onPostExecute(String[] IPAddr) {
                        ((TextView) 提交位置view.findViewById(R.id.提交ip文本框)).setText(R.string.提交ip提示);
                        ((TextView) 提交位置view.findViewById(R.id.IP地址)).setText(IPAddr[0]);
                        ((TextView) 提交位置view.findViewById(R.id.子网掩码)).setText(IPAddr[1]);
                    }
                }.execute();
                return true;
            case R.id.关于:
                builder.setMessage("作者:Brainor\n联系方式:Brainor@qq.com")
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
    /**
     * 显示数组中的学号信息
     */
    protected void 学号信息() {
        final ListView 学号view = new ListView(this);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1);
        for (data.userInfo 学生 : 学生信息) arrayAdapter.add(学生.学号);
        registerForContextMenu(学号view);
        学号view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                getMenuInflater().inflate(R.menu.menu_modifyid, contextMenu);
                for(int i=0;i<contextMenu.size();i++){
                    contextMenu.getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
                            int index = info.position;
                            switch (menuItem.getItemId()) {
                                case R.id.修改:
                                    学号操作(index);
                                    break;
                                case R.id.删除:
                                    学生信息.remove(index);
                                    writeFile();
                                    break;
                            }
                            return false;
                        }
                    });
                }
            }

        });
        学号view.setAdapter(arrayAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(学号view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNeutralButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        学号操作(i);
                    }
                })
                .setTitle("学号信息");
        builder.show();
    }
    /**
     * 添加或者修改单条学号
     *
     * @param i 确定是哪一个按钮
     */
    protected void 学号操作(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View 添加用户view = inflater.inflate(R.layout.addid, null);
        final TextView 学号view = (TextView) 添加用户view.findViewById(R.id.学号);
        final TextView 密码view = (TextView) 添加用户view.findViewById(R.id.密码);
        final int index = i;
        if (i >= 0) {//修改已有信息
            学号view.setText(学生信息.get(index).学号);
            密码view.setText(学生信息.get(index).密码);
        }
        final AlertDialog dialog = builder.setView(添加用户view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String 学号 = 学号view.getText().toString();
                        String 密码 = 密码view.getText().toString();
                        if (Objects.equals(学号, "") || Objects.equals(密码, "")) {
                            Toast.makeText(getApplicationContext(), "输入正确学号和密码", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (index >= 0) 学生信息.set(index, new data.userInfo(学号, 密码));
                        else 学生信息.add(new data.userInfo(学号, 密码));
                        writeFile();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        dialog.show();
    }
    static final String[] 连接类型 = new String[]{"ipgwopen", "ipgwopenall", "ipgwclose", "ipgwcloseall"};//连接, 收费链接, 断开连接, 断开所有连接

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
    private void 连接(View view) {
        web.学生 = 学生信息.get(学号.getSelectedItemPosition());
        //确定免费/收费地址
        short Tag;
        Tag = Short.parseShort(view.getTag().toString());
        if (地址转换.isChecked() && Tag == 0) Tag = 1;//收费
        new netInteract().execute(连接类型[Tag]);
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
                显示文本 += Content[i] + "\n";
            }
//            textBlock.setMovementMethod(ScrollingMovementMethod.getInstance());//滚动
            textBlock.setText(显示文本);
        } else//连接失败
            断开指定连接(Content);
    }

    private void 断开指定连接(String[] Content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View 指定连接view = inflater.inflate(R.layout.disconnect, null);
        final AlertDialog dialog = builder.setView(指定连接view).setTitle("断开指定连接").create();
        dialog.show();

        final TableLayout tableLayout = (TableLayout) 指定连接view.findViewById(R.id.表格);
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
                    if (Objects.equals(Content[3 * i + j], "收费")) 文本.setTextColor(Color.RED);
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
//                    设置初始页面();
                    dialog.dismiss();
                    new netInteract().execute(view.getTag().toString(), "断开指定连接");
                }
            });
            tableRow.addView(断开按钮);
            if (tableLayout != null) {
                tableLayout.addView(tableRow);
            }
        }
    }

    /**
     * 保存学号到文件
     */
    protected void writeFile() {
        final File 文件 = new File(getExternalFilesDir(null), "user.ini");
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(文件))) {
            for (data.userInfo 学生 : 学生信息) {
                bufferedWriter.write(学生.学号);
                bufferedWriter.newLine();
                bufferedWriter.write(学生.密码);
                bufferedWriter.newLine();
            }
            设置列表();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    protected void 设置列表() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (data.userInfo item : 学生信息) adapter.add(item.学号);
        学号.setAdapter(adapter);
    }
}
