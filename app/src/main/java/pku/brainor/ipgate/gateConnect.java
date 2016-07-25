package pku.brainor.ipgate;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by 欧伟科 on 2016/7/16.
 * 把连接部分做成一个Activity
 */
/*public class gateConnect extends AppCompatActivity {
    network web = new network();

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
                                MainActivity.连接信息.命令 = "2";
                                contentSplit.addAll(0, web.连接(MainActivity.连接信息));//在最后加上错误信息, 与直接点击按钮区分开
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
                *//*switch (错误信息) {
                    case "您打开的网络连接已经达到设定的连接数，网络连接失败。请断开全部连接，重新登录。":
                        if (contentSplit.size() == 2) {//获得了连接数据

                        }
                }*//*


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

}*/
