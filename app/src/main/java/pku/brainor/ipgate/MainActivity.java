package pku.brainor.ipgate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ArrayList<netConnectingData.userInfo> 学号s_spinner = new ArrayList<>();
    public netConnectingData 连接信息 = new netConnectingData();
    network web = new network();
    //各种按钮
    Switch 地址转换_switch;
    TextView UI显示_textview;
    Button[] button = new Button[3];
    Spinner 学号_spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        设置初始页面();
    }

    void 设置初始页面(){
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //增加用户信息列表
        final File 文件 = new File(getExternalFilesDir(null), "user.ini");
        try {
            if (文件.isFile()) {
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(文件))) {
                    String 学号;
                    while ((学号 = bufferedReader.readLine()) != null) {
                        if (Objects.equals(学号, "")) break;
                        学号s_spinner.add(new netConnectingData.userInfo(学号, bufferedReader.readLine()));
                    }
                }
            } else {
                文件.createNewFile();
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        /*学号s_spinner.add(new netConnectingData.userInfo("1301110110", "Oudanyi6456"));
        学号s_spinner.add(new netConnectingData.userInfo("gcuspea", "phyfsc508"));*/
        if (学号s_spinner.size() == 0) {
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
        地址转换_switch = (Switch) findViewById(R.id.地址转换);
        UI显示_textview = (TextView) findViewById(R.id.信息文本);
        button[0] = (Button) findViewById(R.id.button1);
        button[1] = (Button) findViewById(R.id.button2);
        button[2] = (Button) findViewById(R.id.button3);
        学号_spinner = (Spinner) findViewById(R.id.学生信息);

        设置列表();

        for (Button item : button)
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    连接(view);
                }
            });
        button[1].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                view.setTag("3");
                连接(view);
                Toast.makeText(getApplicationContext(), "断开指定连接", Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

 /*   *//**
     * 暂时有问题
     *
     *//*
    @Override
    protected void onResume() {
        super.onResume();
        //注册广播
        IntentFilter intentFilter=new IntentFilter("android.net.wifi.STATE_CHANGE");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                WifiInfo wifiInfo=intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                if(intent.hasExtra(WifiManager.EXTRA_BSSID) && wifiInfo!=null && Objects.equals(wifiInfo.getSSID(), "\"Wireless PKU\"") ){
                    Toast.makeText(getApplicationContext(), "尝试自动联网", Toast.LENGTH_LONG).show();
                    连接(button[0]);
                }
            }
        }, intentFilter);
    }*/

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
                if(!wifiManager.isWifiEnabled()){//再检查是否打开WiFi
                    builder.setMessage("WiFi没有打开.")
                            .setPositiveButton("打开WiFi", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    wifiManager.setWifiEnabled(true);
                                }
                            });
                    builder.create().show();
                    return true;
                }
                String wifi名称 = wifiManager.getConnectionInfo().getSSID().replace("\"", "");
                if (!Objects.equals(wifi名称, "Wireless PKU")) {
                    builder.setMessage("当前连接名称是" + wifi名称 + "\n请将WiFi连接至Wireless PKU后提交当前位置.");
                    List<ScanResult> scanResult = wifiManager.getScanResults();

                    for (int i = 0; i < scanResult.size(); i++) {
                        if (Objects.equals(scanResult.get(i).SSID, "Wireless PKU")) {
                            for (WifiConfiguration wifiConfiguration : wifiManager.getConfiguredNetworks()) {//如果没有打开WiFi开关, 会返回null
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
                                CharSequence[] 邮件信息 = new CharSequence[3];
                                邮件信息[0] = ((TextView) 提交位置view.findViewById(R.id.IP地址)).getText();
                                邮件信息[1] = ((TextView) 提交位置view.findViewById(R.id.子网掩码)).getText();
                                邮件信息[2] = ((TextView) 提交位置view.findViewById(R.id.物理地址)).getText();
                                if (邮件信息[2].length() == 0)
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
                builder.setMessage("连接北大网关.\n" +
                        "版本更新:\n" +
                        "v1.4\n" +
                        "  使用学校API\n" +
                        "  长按断开连接可以断开指定连接" +
                        "\n\n" +
                        "作者:Brainor\n" +
                        "联系方式:Brainor@qq.com")
                        .setTitle("关于");
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
        for (netConnectingData.userInfo 学生 : 学号s_spinner) arrayAdapter.add(学生.学号);
        registerForContextMenu(学号view);
        学号view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                getMenuInflater().inflate(R.menu.menu_modifyid, contextMenu);
                for (int i = 0; i < contextMenu.size(); i++) {
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
                                    学号s_spinner.remove(index);
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
            学号view.setText(学号s_spinner.get(index).学号);
            密码view.setText(学号s_spinner.get(index).密码);
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
                        if (index >= 0) 学号s_spinner.set(index, new netConnectingData.userInfo(学号, 密码));
                        else 学号s_spinner.add(new netConnectingData.userInfo(学号, 密码));
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

    /* web.建立连接("https://its.pku.edu.cn/cas/ITSClient", "cmd=getconnections&username=1301110110&password=Oudanyi6456");//closeall 显示连接情况, 断开所有链接
    * "cmd=open&username=" + username + "&password=" + password + "&iprange=" + fee或者free + "&ip=", //打开指定连接, 打开这个连接就ip=就可以
    * "cmd=disconnect&username=" + username + "&password=" + password + "&ip="//断开指定连接
    * "cmd=close"//断开本地连接
    */
    static final String[] 连接类型 = new String[]{"open", "close", "closeall", "getconnections", "disconnect"};//连接, 断开连接, 断开所有连接, 获取连接状态, 断开指定连接

    public class netInteract extends AsyncTask<netConnectingData, Void, ArrayList<String[]>> {//网络交互

        @Override
        protected ArrayList<String[]> doInBackground(netConnectingData... 连接信息s) {
            try {
                ArrayList<String[]> contentSplit = web.连接(连接信息s[0]);
                switch (contentSplit.get(0)[0]) {//错误处理
                    case "error":
                        String 错误信息 = contentSplit.get(0)[1];
                        switch (错误信息) {
                            case "您打开的网络连接已经达到设定的连接数，网络连接失败。请断开全部连接，重新登录。":
                                连接信息.命令 = 连接类型[3];
                                contentSplit.addAll(0, web.连接(连接信息));//在最后加上错误信息, 与直接点击按钮区分开
                        }
                }
                return contentSplit;
            } catch (Exception e) {
                ArrayList<String[]> 错误信息 = new ArrayList<>();
                错误信息.add(new String[]{"error", e.getMessage()});
                return 错误信息;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String[]> Content) {
            判断(Content);
        }
    }

    private void 连接(View view) {
        连接信息.学生 = 学号s_spinner.get(学号_spinner.getSelectedItemPosition());
        //确定免费/收费地址
        连接信息.类型 = 地址转换_switch.isChecked() ? "fee" : "free";
        连接信息.ip地址 = "";
        连接信息.命令 = 连接类型[Short.parseShort(view.getTag().toString())];
        new netInteract().execute(连接信息);
        if (Objects.equals(view.getTag().toString(), "3")) view.setTag("1");
    }

    private void 判断(ArrayList<String[]> contentSplit) {
        switch (contentSplit.get(0)[0]) {
            case "error":
                String 错误信息 = contentSplit.get(0)[1];
                UI显示_textview.setText(错误信息);//直接显示错误信息, 只有一条list
                /*switch (错误信息) {
                    case "您打开的网络连接已经达到设定的连接数，网络连接失败。请断开全部连接，重新登录。":
                        if (contentSplit.size() == 2) {//获得了连接数据

                        }
                }*/


                break;
            case "succ":
                if (Objects.equals(连接信息.命令, 连接类型[3])) {
                    if (contentSplit.size() == 2)
                        UI显示_textview.setText(contentSplit.get(1)[1]);//如果是点连接, 超过连接数, 需要注明
                    断开指定连接(contentSplit.get(0)[1]);
                    break;//不需后续处理, 因为直接弹出对话框
                }
                if (Objects.equals(contentSplit.get(0)[1], "")) contentSplit.remove(0);//删去成功信息
            default:
                String 显示文本 = "";
                for (String[] content : contentSplit) 显示文本 += content[0] + ":" + content[1] + "\n";
                UI显示_textview.setText(显示文本);
                break;
        }
    }

    private void 断开指定连接(String message){
        Intent 断开指定连接界面 = new Intent(this, disconnectSpecifiedConnection.class);
        断开指定连接界面.putExtra("content", message);
        startActivityForResult(断开指定连接界面, 0);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 0) {
            连接信息.ip地址 = data.getStringExtra("IP");
            连接信息.命令 = 连接类型[4];
            new netInteract().execute(连接信息);
        }
    }

    /**
     * 保存学号到文件
     */
    protected void writeFile() {
        final File 文件 = new File(getExternalFilesDir(null), "user.ini");
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(文件))) {
            for (netConnectingData.userInfo 学生 : 学号s_spinner) {
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
        for (netConnectingData.userInfo item : 学号s_spinner) adapter.add(item.学号);
        学号_spinner.setAdapter(adapter);
    }
}
