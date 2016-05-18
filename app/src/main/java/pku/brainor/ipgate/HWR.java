package pku.brainor.ipgate;

/**
 * Created by 欧伟科 on 2016/4/3.
 * Edited by 欧伟科 on 2016/5/18.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.text.TextUtils.join;

class network extends data {
    public network() {
        CookieHandler.setDefault(new CookieManager());
        ((CookieManager) CookieHandler.getDefault()).setCookiePolicy(CookiePolicy.ACCEPT_ALL);
    }

    public String[] 连接(String 连接类型) {
        String[] 连接信息 = 建立连接("https://its.pku.edu.cn/cas/login", 学生.postData());//获得cookies
        if (Objects.equals(连接信息[0], "错误")) return 连接信息;
        连接信息 = 建立连接("https://its.pku.edu.cn/netportal/" + 连接类型, "");
        if (Objects.equals(连接信息[0], "错误")) return 连接信息;
        return 返回信息(连接信息[1]);
    }

    public String[] 断开指定连接(String IP) {
        String postData = "messages=" + 学生.value + "&operation=disconnectip_err&from=cas&uid=" + 学生.学号 + "&timeout=1&range=1&disconnectip=" + IP;
        String[] 连接信息 = 建立连接("https://its.pku.edu.cn/netportal/ipgw.ipgw?" + postData, "");
//        String[] 连接信息=建立连接("https://its.pku.edu.cn/netportal/ipgw.ipgw", postData);
        if (Objects.equals(连接信息[0], "错误")) return 连接信息;
        return 返回信息(连接信息[1]);
    }
    /*public String[] 获得Cookie(){
        String[] 连接信息=建立连接("https://its.pku.edu.cn/cas/login", 学生.postData());
        if (Objects.equals(连接信息[0], "错误")) return 连接信息;
        try {
            Request.getInputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[]{"成功"};
    }*/

    /**
     * 连网并返回信息
     *
     * @param URL      要连接的网址
     * @param postData POST方法的数据
     * @return 错误的话, 第一个值为"错误", 第二个值为错误信息; 正确的话, 第一个值为"正确", 第二个值为网页信息.
     */
    public String[] 建立连接(String URL, String postData) {
        java.net.URL url;
        try {
            url = new java.net.URL(URL);
        } catch (MalformedURLException e) {
            return new String[]{"错误", e.getMessage()};
        }
        HttpURLConnection request;
        try {
            request = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            return new String[]{"错误", e.getMessage()};
        }

        request.setRequestProperty("Cookie", join(";", ((CookieManager) CookieHandler.getDefault()).getCookieStore().getCookies()));
        request.setDoInput(true);
        request.setReadTimeout(300000);
        if (!postData.equals("")) {
            request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            request.setDoOutput(true);
            request.setRequestProperty("Content-Length", Integer.toString(postData.length()));
            try {
                request.setRequestMethod("POST");
                try(java.io.DataOutputStream dataOutputStream = new java.io.DataOutputStream(request.getOutputStream())) {
                    dataOutputStream.write(postData.getBytes(StandardCharsets.UTF_8));
                    dataOutputStream.flush();
                }
            } catch (IOException e) {
                return new String[]{"错误", e.getMessage()};
            }
        }
//分析网页部分
        String line, ResponseFromServer = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                ResponseFromServer += line + "\n";
            }
        } catch (IOException e) {
            return new String[]{"错误", e.getMessage()};
        }
        request.disconnect();
        return new String[]{"成功", ResponseFromServer};
    }

    public String[] 返回信息(String 文档) {
        int i = 文档.indexOf("<!--IPGWCLIENT_START ") + 21;
        if (i == 20) {
            return new String[]{"错误", "用户名或者密码错误"};
        }
        int j = 文档.indexOf(" IPGWCLIENT_END-->");
        String Content = 文档.substring(i, j);
        if (Content.contains("SUCCESS=NO"))//出现了各种问题
        {
            Matcher matcher = Pattern.compile("(?<=REASON=).*").matcher(Content);
            matcher.find();
            String 原因 = matcher.group();
            switch (原因) {
                case "当前连接数超过预定值":
                    return 获得IP(文档);
                default://不是当前的原因
                    return new String[]{"错误", 原因};
            }
        } else {
            return 信息翻译(Content);
        }
    }

    /**
     * 超过最大连接数后, 得到IP地址
     *
     * @param 文档 利用文档里的信息获得IP地址
     * @return 字符串数组, 第一项不是"正确"也不是"错误"
     */
    public String[] 获得IP(String 文档) {
        String postData;
        int i = 文档.indexOf("\"messages\" value") + 18;
        int j = 文档.indexOf("\">", i);
        学生.value = 学生.decode(文档.substring(i, j));
        postData = "messages=" + 学生.value + "&operation=get_disconnectip_err&from=cas&uid=" + 学生.学号 + "&timeout=1&range=2";

        String[] 连接信息 = 建立连接("https://its.pku.edu.cn/netportal/ipgw.ipgw?" + postData, "");//POST不行GET却可以
        if (Objects.equals(连接信息[0], "错误")) return 连接信息;

        String IP地址 = 连接信息[1];
        //断开指定连接
        List<String> IPList = new ArrayList<>();
        Matcher matcher = Pattern.compile("(?<=td2>)[^<].*?(?=</td>)").matcher(IP地址);
        while (matcher.find()) {
            IPList.add(matcher.group());
        }
        String[] IP = IPList.toArray(new String[IPList.size()]);//IP,免费,时间

        Date 时间;
        try {
            for (i = 0; i < IP.length / 3; i++) {

                时间 = new SimpleDateFormat("y-MM-dd kk:mm:ss.S").parse(IP[i * 3 + 2]);
                IP[3 * i + 2] = new SimpleDateFormat("M-d H:mm:ss").format(时间);
            }
        } catch (ParseException e) {
            return new String[]{"错误", e.getMessage()};
        }
        return IP;
    }

    private String[] 信息翻译(String Content) {
        Content = Content.replace("STATE=", "状态\t");
        Content = Content.replace("connected", "已连接");
        Content = Content.replace("USERNAME=", "用户名\t");
        Content = Content.replaceAll("FIXRATE=\\S*\\s", "");
        Content = Content.replace("FR_DESC_CN=", "包月状态\t");
        Content = Content.replaceAll("FR_DESC_EN=\\S*\\s", "");
        Matcher matcher = Pattern.compile("(?<=FR_TIME=)[\\d\\.]*+").matcher(Content);
        if (matcher.find()) {
            String 时长 = matcher.group();
            if (时长.length() > 0) {//后期添加吧

            }
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

class data {//数据

    public static class userInfo {//用户信息
        public String 学号;
        public String 密码;
        private fwrd 类型 = fwrd.free;
        public String value;
        private final String code = decode("|;kiDrqvfi7d$v0p5Fg72Vwbv2;|");

        public String postData() {
            return "username1=" + 学号 + "&password=" + 密码 + "&pwd_t=" + decode("密码") + "&fwrd=" + 类型.name() + "&username=" + 学号 + code + 密码 + code + 类型.index;
        }

        public String decode(String data) {
            String 编码data = "";
            try {
                编码data = URLEncoder.encode(data, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return 编码data;
        }

        public userInfo(String 学号, String 密码) {
            this.学号 = 学号;
            this.密码 = 密码;
        }
    }

    enum fwrd {
        fee((short) 11), free((short) 12), pkuwireless((short) 14);
        private short index;

        fwrd(short index) {
            this.index = index;
        }
    }

    public userInfo 学生;

//    public Handler uiHandler;
}
