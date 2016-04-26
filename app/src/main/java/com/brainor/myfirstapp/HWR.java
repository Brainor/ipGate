package com.brainor.myfirstapp;

/**
 * Created by 欧伟科 on 2016/4/3.
 */

import android.annotation.SuppressLint;
import android.os.Handler;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class 网络 extends 数据 {
    public HttpURLConnection Request;

    public 网络(){
        CookieHandler.setDefault(new CookieManager());
    }
    public String[] 建立连接(String URL, String postData) throws MalformedURLException {
        java.net.URL url=new java.net.URL(URL);
        try {
            Request=(HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*if(cookies!=null) {
            for (String cookie : cookies) {
                Request.addRequestProperty("Cookie", cookie.split(":", 2)[0]);
            }
        }*/
        Request.setRequestProperty("Cookie", TextUtils.join(";", ((CookieManager)CookieHandler.getDefault()).getCookieStore().getCookies()));
        Request.setDoInput(true);
        Request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        if (!postData.equals("")){
            Request.setDoOutput(true);
            byte[] byteArray= postData.getBytes(StandardCharsets.UTF_8);
            Request.setRequestProperty("Content-Length",Integer.toString(postData.length()));
            try {
                Request.setRequestMethod("POST");
                java.io.DataOutputStream dataOutputStream = new java.io.DataOutputStream(Request.getOutputStream());
//                dataStreamRequest.writeBytes(PostData);
                dataOutputStream.write(postData.getBytes(StandardCharsets.UTF_8));
                dataOutputStream.flush();
                dataOutputStream.close();
            } catch (Exception ex) {
                return new String[]{"错误", ex.getMessage()};
            }
        }
        return new String[]{"成功"};
    }

    public String[] 获得Cookie() throws MalformedURLException {

        //String[] 错误代码=建立连接("http://www.baidu.com","");
        String[] 错误代码=建立连接("https://its.pku.edu.cn/cas/login","username1=1301110110&password=Oudanyi6456&pwd_t=%E5%AF%86%E7%A0%81&fwrd=free&username=1301110110%7C%3BkiDrqvfi7d%24v0p5Fg72Vwbv2%3B%7COudanyi6456%7C%3BkiDrqvfi7d%24v0p5Fg72Vwbv2%3B%7C12");
//        String[] 错误代码=建立连接("https://its.pku.edu.cn/cas/login", 学生.postData());
        if (Objects.equals(错误代码[0], "错误")) return 错误代码;
        try {
            Request.getInputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String[]{"成功"};
    }
    public String[] 连接(String 连接类型) throws Exception {
        String[] 错误代码=获得Cookie();
        if (Objects.equals(错误代码[0], "错误")) return 错误代码;
        建立连接("https://its.pku.edu.cn/netportal/" + 连接类型,"");
        String 文档=分析网页(Request);
        return 返回信息(文档);
    }
    private String 分析网页(HttpURLConnection request) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String line="";
        String ResponseFromServer="";
        while((line = bufferedReader.readLine()) != null ){
            ResponseFromServer+=line;
        }
        bufferedReader.close();
        return ResponseFromServer;
    }
    public  String[] 返回信息(String 文档) throws Exception {
        int i=文档.indexOf("<!--IPGWCLIENT_START ") + 21;
        if(i==20){
            return new String[] { "错误", "用户名或者密码错误" };
        }
        int j = 文档.indexOf(" IPGWCLIENT_END-->");
        String Content = 文档.substring(i, j);
        if (Content.contains("SUCCESS=NO"))//出现了各种问题
        {
            Matcher matcher=Pattern.compile("(?<=REASON=).*").matcher(Content);
            matcher.find();
            String 原因 = matcher.group();
            switch (原因)
            {
                case "当前连接数超过预定值":
                    return 获得IP(文档);
                default://不是当前的原因
                    return new String[] { "错误", 原因 };
            }
        }
        else
        {
            return 信息翻译(Content);
        }
    }
    public String[] 获得IP(String 文档) throws Exception {
        String postData;
        int i = 文档.indexOf("\"messages\" value") + 18;
        int j = 文档.indexOf("\">", i);
        学生.value = 文档.substring(i, j - 1);

        postData = "messages=" + 学生.value + "&operation=get_disconnectip_err&from=cas&uid=" + 学生.学号 + "&timeout=1&range=2";

        建立连接("https://its.pku.edu.cn/netportal/ipgw.ipgw?" + postData,"");//POST不行GET却可以
        String IP地址=分析网页(Request);

        //断开指定连接
        Pattern[] pattern=new Pattern[2];
        pattern[0]=Pattern.compile("(?<=td2>)\\d+(\\.\\d+){3}?");//IP地址
        pattern[1]=Pattern.compile("(?<=td2>)[\\d\\-]+\\s[\\d:]+\\.0");//时间
        Matcher[] MC=new Matcher[2];
        for (i=0;i<2;i++)             MC[i]=pattern[i].matcher(IP地址);
        String[] IP=new String[2*MC[0].groupCount()];
        for(i=0;i<MC[0].groupCount();i++){
            IP[i*2]=MC[0].group(i);
            IP[i*2+1]=MC[1].group(i);
        }
        Date 时间;
        for(i=0;i<MC[0].groupCount();i++){
            时间=new SimpleDateFormat().parse(IP[i * 2 + 1]);
            IP[2*i+1]=new SimpleDateFormat("M-d H:mm:ss").format(时间);
        }
        return IP;
    }
    public String[] 断开指定连接(String IP) throws Exception {
        //断开第一个连接
        String postData = "messages=" + 学生.value + "&operation=disconnectip_err&from=cas&uid=" + 学生.学号 + "&timeout=1&range=1&disconnectip=" + IP;
        建立连接("http://its.pku.edu.cn/netportal/ipgw.ipgw?" + postData,"");

        String 文档=分析网页(Request);
        return 返回信息(文档);
    }
    private String[] 信息翻译(String Content){
        Content=Content.replace("STATE=","状态\t");
        Content = Content.replace("connected", "已连接");
        Content =Content.replace("USERNAME=", "用户名\t");
        Content = Content.replaceAll("FIXRATE=\\S*\\s", "");
        Content = Content.replace("FR_DESC_CN=", "包月状态\t");
        Content = Content.replaceAll("FR_DESC_EN=\\S*\\s", "");
        Matcher matcher = Pattern.compile("(?<=FR_TIME=)[\\d\\.]*+").matcher(Content);
        matcher.find();
        String 时长=matcher.group();
        if(时长.length()>0){//后期添加吧

        }
        Content = Content.replace("FR_TIME=", "包月剩余时长\t");
        Content = Content.replace("SCOPE=", "访问范围\t");
        if (Content.contains("domestic")) Content = Content.replace("domestic", "免费流量");
        else Content = Content.replace("international", "收费流量");
        Content = Content.replace("CONNECTIONS=", "当前连接数\t");
        Content = Content.replace("BALANCE=", "余额\t");
        Content = Content.replace("IP=", "IP地址\t");
        Content = Content.replace("MESSAGE=", "信息\t");
        return Content.split(" ");//这个地方的空格控制, listview?http://techlovejump.com/android-multicolumn-listview/
    }

}
class 数据{
    public static class 用户信息{
        public String 学号;
        public String 密码;
        private fwrd 类型=fwrd.free;
        public String value;
        private final String code = "|;kiDrqvfi7d$v0p5Fg72Vwbv2;|";
        public String postData(){ return "fwrd=" + 类型 + "&username1=" + 学号 + "&password=" + 密码 + "&username=" + 学号 + code + 密码 + code + 类型.index; }
        public 用户信息(String 学号,String 密码){
            this.学号= 学号;
            this.密码= 密码;
        }

    }
    enum fwrd {
        fee((short)11),        free((short)12),        pkuwireless((short)14);
        private short index;
        fwrd(short index){
            this.index=index;
        }
    }
    public 用户信息 学生;

//    public Handler uiHandler;
}
