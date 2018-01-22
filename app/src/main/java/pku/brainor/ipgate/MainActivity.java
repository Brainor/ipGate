package pku.brainor.ipgate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

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

    void 设置初始页面() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //增加用户信息列表
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        Set<String> 学生信息 = preferences.getStringSet("student", new HashSet<>());
        for (String 学号密码 : 学生信息) {
            学号s_spinner.add(new netConnectingData.userInfo(学号密码.split("\\|\\|")));
        }

       /* final File 文件 = new File(getExternalFilesDir(null), "user.ini");
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
        }*/
        /*学号s_spinner.add(new netConnectingData.userInfo("1301110110", "Oudanyi6456"));
        学号s_spinner.add(new netConnectingData.userInfo("gcuspea", "phyfsc508"));*/
        if (学号s_spinner.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("没有学号信息, 请添加.")
                    .setPositiveButton("添加学号", (dialogInterface, i) -> 学号信息())
                    .setNegativeButton("取消", (dialogInterface, i) -> {

                    });
            builder.show();
        }
        // Get our button from the layout resource,
        // and attach an event to it
        UI显示_textview = findViewById(R.id.信息文本);
        button[0] = findViewById(R.id.button1);
        button[1] = findViewById(R.id.button2);
        button[2] = findViewById(R.id.button3);
        学号_spinner = findViewById(R.id.学生信息);

        //设置token
        netConnectingData.userInfo.token = preferences.getString("token", "");
        if (netConnectingData.userInfo.token.isEmpty()) {
            String token = "IPGWAndroid1.4_" +
                    Build.BRAND.replace(" ", "_") + Build.VERSION.RELEASE + "_" +
                    UUID.randomUUID().toString();
            preferences.edit().putString("token", token).apply();
            netConnectingData.userInfo.token = token;
        }
        设置列表();

        for (Button item : button)
            item.setOnClickListener(this::连接);
        button[1].setOnLongClickListener(view -> {
            view.setTag("3");
            连接(view);
            Toast.makeText(getApplicationContext(), "断开指定连接", Toast.LENGTH_LONG).show();
            return true;
        });
    }

 /*   */

    /**
     * 暂时有问题
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
            /*case R.id.提交位置:
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
                return true;*/
            case R.id.关于:
                builder.setMessage("连接北大网关.\n" +
                        "版本更新:\n" +
                        "v1.5\n" +
                        "   添加user-agent\n" +
                        "v1.4\n" +
                        "   使用学校API\n" +
                        "   长按断开连接可以断开指定连接" +
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
        学号view.setOnCreateContextMenuListener((contextMenu, view, contextMenuInfo) -> {
            getMenuInflater().inflate(R.menu.menu_modifyid, contextMenu);
            for (int i = 0; i < contextMenu.size(); i++) {
                contextMenu.getItem(i).setOnMenuItemClickListener(menuItem -> {
                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
                    int index = info.position;
                    switch (menuItem.getItemId()) {
                        case R.id.修改:
                            学号操作(index);
                            break;
                        case R.id.删除:
                            学号s_spinner.remove(index);
                            writePreferences();
                            break;
                    }
                    return false;
                });
            }
        });
        学号view.setAdapter(arrayAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(学号view)
                .setPositiveButton("确定", (dialogInterface, i) -> {

                })
                .setNeutralButton("添加", (dialogInterface, i) -> 学号操作(i))
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
        final TextView 学号view = 添加用户view.findViewById(R.id.学号);
        final TextView 密码view = 添加用户view.findViewById(R.id.密码);
        final int index = i;
        if (i >= 0) {//修改已有信息
            学号view.setText(学号s_spinner.get(index).学号);
            密码view.setText(学号s_spinner.get(index).密码);
        }
        final AlertDialog dialog = builder.setView(添加用户view)
                .setPositiveButton("确定", (dialogInterface, i1) -> {
                    String 学号 = 学号view.getText().toString();
                    String 密码 = 密码view.getText().toString();
                    if (Objects.equals(学号, "") || Objects.equals(密码, "")) {
                        Toast.makeText(getApplicationContext(), "输入正确学号和密码", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (index >= 0)
                        学号s_spinner.set(index, new netConnectingData.userInfo(学号, 密码));
                    else 学号s_spinner.add(new netConnectingData.userInfo(学号, 密码));
                    writePreferences();
                })
                .setNegativeButton("取消", (dialogInterface, i12) -> {

                })
                .create();
        dialog.show();
    }

    /* web.建立连接("https://its.pku.edu.cn/cas/ITSClient", "cmd=getconnections&username=1301110110&password=Oudanyi6456&lang=");//closeall 显示连接情况, 断开所有链接
    * 返回: {"succ":"162..105.13.191;收费;物理楼;2017-02-20 12:24:27;10.128.130.230;收费;畅新2号楼;2017-02-25 02:25:24;10.128.131.126;收费;畅新2号楼;2017-02-25 03:22:01"}
    * "cmd=open&username=" + username + "&password=" + password + "&iprange=fee&ip="+ip+ "&lang=&app=IPGWAndrroid1.4_Xiaomi7.0_fc53be1c-57f6-4d3b-9e59-75cd3280f416"//打开指定连接, 打开这个连接就ip=就可以
    * {"succ":"",ver":"1.1","FIXRATE":"YES","FR_TYPE":"","FR_DESC_CN":"不限时间","FR_DESC_EN":"Unlimited","SCOPE":"international","DEFICIT":"","FR_TIME_CN":"","FR_TIME_EN":"Unlimited","CONNECTIONS":"3","BALANCE_CN":"151.468","BALANCE_EN":"151.468","IP":"10.128.131.126"}
    * "cmd=disconnect&username=" + username + "&password=" + password + "&ip="+ip+"&lang="//断开指定连接
    * {"succ":"断开连接成功"}
    * "cmd=close&lang="//断开本地连接//{"succ":"close_OK"}
    *
    * content-type: application/x-www-form-urlencoded; charset=utf-8
    * User-Agent: IPGWAndrroid1.4_Xiaomi7.0_fc53be1c-57f6-4d3b-9e59-75cd3280f416
    * Connection: Keep-Alive
    * Accept-Encoding: gzip
    *
    * Xiaomi: Build.BRAND or na, 空格用_代替
    * 7.0: Build.VERSION.RELEASE or na
    * 很长一串是UUID.randomUUID().toString()
    * SharedPreferences localSharedPreferences = paramContext.getSharedPreferences("preference_file_key", 0);
    * Object localObject = localSharedPreferences.getString("store_install_id", ""); ip的值储存在: store_latest_ip;
    */
    static final String[] 连接类型 = new String[]{"open", "close", "closeall", "getconnections", "disconnect"};//连接, 断开连接, 断开所有连接, 获取连接状态, 断开指定连接

    private class netInteract extends AsyncTask<netConnectingData, Void, ArrayList<String[]>> {//网络交互

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
        netConnectingData.学生 = 学号s_spinner.get(学号_spinner.getSelectedItemPosition());
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
                StringBuilder 显示文本 = new StringBuilder();
                for (String[] content : contentSplit) 显示文本.append(content[0]).append(":").append(content[1]).append("\n");
                try {
                    for (InterfaceAddress interfaceAddress : NetworkInterface.getByName("wlan0").getInterfaceAddresses())
                        if (interfaceAddress.getAddress() instanceof Inet4Address) {
                            显示文本.append("局域网地址:").append(interfaceAddress.getAddress().getHostAddress());
                            break;
                        }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

                UI显示_textview.setText(显示文本.toString());
                break;
        }
    }

    private void 断开指定连接(String message) {
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
     *//*
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
    }*/
    protected void writePreferences() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        HashSet<String> 学生信息 = new HashSet<>();
        for (netConnectingData.userInfo 学生 : 学号s_spinner) 学生信息.add(学生.学号 + "||" + 学生.密码);
        editor.putStringSet("student", 学生信息).apply();
        设置列表();

    }

    protected void 设置列表() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (netConnectingData.userInfo item : 学号s_spinner) adapter.add(item.学号);
        学号_spinner.setAdapter(adapter);
    }
}
