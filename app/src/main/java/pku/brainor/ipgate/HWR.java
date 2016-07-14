package pku.brainor.ipgate;

/**
 * Created by 欧伟科 on 2016/4/3.
 * Edited by 欧伟科 on 2016/5/18.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.text.TextUtils.join;

class network extends netConnectingData {
    public network() {
        CookieHandler.setDefault(new CookieManager());
        ((CookieManager) CookieHandler.getDefault()).setCookiePolicy(CookiePolicy.ACCEPT_ALL);
    }

    public ArrayList<String[]> 连接(netConnectingData 连接信息) {
        String ResponseFromServer = 建立连接("https://its.pku.edu.cn/cas/ITSClient", 连接信息.postData());
        return 信息分割(信息翻译(ResponseFromServer));
    }

    /**
     * 连网并返回信息
     *
     * @param URL      要连接的网址
     * @param postData POST方法的数据
     * @return 错误的话, 第一个值为"错误", 第二个值为错误信息; 正确的话, 第一个值为"正确", 第二个值为网页信息.
     */
    public String 建立连接(String URL, String postData) {
        java.net.URL url;
        try {
            url = new java.net.URL(URL);
        } catch (MalformedURLException e) {
            return "{\"error\":\"" + e.getMessage() + "\"}\n";
        }
        HttpURLConnection request;
        try {
            request = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            return "{\"error\":\"" + e.getMessage() + "\"}\n";
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
                return "{\"error\":\"" + e.getMessage() + "\"}\n";
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
            return "{\"error\":\"" + e.getMessage() + "\"}\n";
        }
        request.disconnect();
        return ResponseFromServer;
    }

    /**
     * 将字符串变成字符串数组的形式, 以及翻译成中文<p>
     * 形式为<p>
     * {"succ":"","ver":"IPGWiOS1.1_IPGWAndroid1.0","CONNECTIONS":"1","IP":"162.105.13.91"}\n
     * @param Content 服务器返回的信息
     * @return 填入UI的字符串数组
     */
    public ArrayList<String[]> 信息分割(String Content) {
        Content = Content.replaceAll("\\{\\\"|\"\\}\\n", "");//去掉首尾{", "}\n
        String[] contentSplit = Content.split("\",\"");//利用","分隔
        ArrayList<String[]> contentDoubleSplit = new ArrayList<>();
        for (String splitPiece : contentSplit) {
            contentDoubleSplit.add(splitPiece.split("\":\"", 2));//每个List用":"分隔
        }
        return contentDoubleSplit;
    }



    private String 信息翻译(String Content) {
        Content = Content.replaceAll("\"ver.*?,", "");
        Content = Content.replaceAll("\"FIXRATE.*?,", "");
        Content = Content.replace("FR_DESC_CN", "包月状态");
        Content = Content.replaceAll("\"FR_DESC_EN.*?,", "");
        Matcher matcher = Pattern.compile("(?<=FR_TIME_CN\":\")\\d++\\.\\d++").matcher(Content);
        if (matcher.find()) {//java.time.Duration duration;不支持JDK8
            double 时长 = Double.parseDouble(matcher.group());
            if (时长 > 0) {//后期添加吧
                double 分钟 = (时长 - Math.floor(时长)) * 60;
                Content = matcher.replaceFirst(String.valueOf((int) Math.floor(时长)) + "时" + String.valueOf((int) Math.floor(分钟)) + "分");
            }
        }
        Content = Content.replace("FR_TIME_CN", "包月剩余时长");
        Content = Content.replaceAll("\"FR_TIME_EN.*?,", "");
        Content = Content.replace("SCOPE", "访问范围");
        if (Content.contains("domestic")) Content = Content.replace("domestic", "免费流量");
        else Content = Content.replace("international", "收费流量");
        Content = Content.replace("CONNECTIONS", "当前连接数");
        Content = Content.replace("BALANCE_CN", "余额");
        Content = Content.replaceAll("\"BALANCE_EN.*?,", "");
        return Content;
    }

}

class netConnectingData {//数据
    public userInfo 学生;
    public String 类型="free";
    public String 命令;
    public String ip地址;

    public String postData() {
        switch (命令) {
            case "getconnections":
            case "closeall":
                return "cmd=" + 命令 + "&username=" + 学生.学号 + "&password=" + 学生.密码;
            case "open":
                return "cmd=" + 命令 + "&username=" + 学生.学号 + "&password=" + 学生.密码 + "&iprange=" + 类型 + "&ip=";
            case "disconnect":
                return "cmd=" + 命令 + "&username=" + 学生.学号 + "&password=" + 学生.密码 + "&ip=" + ip地址;
            case "close":
                return "cmd=" + 命令;
            default:
                return "cmd=" + 命令;
        }
    }
    public static class userInfo {//用户信息
        public String 学号;
        public String 密码;

        public userInfo(String 学号, String 密码) {
            this.学号 = 学号;
            this.密码 = 密码;
        }
    }
}