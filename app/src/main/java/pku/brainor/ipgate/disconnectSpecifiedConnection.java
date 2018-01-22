package pku.brainor.ipgate;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Created by 欧伟科 on 2016/6/11.,<p>
 * 超过三个链接之后, 打开dialog断开指定连接<p>
 * 现在使用了Activity
 */
public class disconnectSpecifiedConnection extends AppCompatActivity {
    String Content;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.disconnect);
        Content = getIntent().getStringExtra("content");//传入变量是String
        // 类似形式: 162.105.13.91;免费地址;物理楼;2016-07-04 22:04:52;162.105.13.133;免费地址;物理楼;2016-07-04 20:07:09;10.128.131.126;免费地址;畅新2号楼;2016-07-04 18:59:34
        String[] contentSplit = Content.split(";");//IP地址, 收费免费, 物理地址, 连接时间
        final TableLayout tableLayout = findViewById(R.id.表格);
        int IP数量 = contentSplit.length / 4;

        TableRow tableRow;
        TextView 文本;
        Button 断开按钮;

        TableRow.LayoutParams 单元格参数;
        for (int i = 0; i < IP数量; i++) {
            tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
            断开按钮 = new Button(this);
            断开按钮.setTag(contentSplit[4 * i]);//在数据更改之前先把IP地址写入
            contentSplit[4 * i] = contentSplit[4 * i] + "\n" + contentSplit[4 * i + 2];
//            contentSplit[4 * i + 1] = contentSplit[4 * i + 1].replace("地址", "");
            try {
                Date 时间 = new SimpleDateFormat("y-MM-dd kk:mm:ss").parse(contentSplit[4 * i + 3]);
                contentSplit[4 * i + 3] = new SimpleDateFormat("M月d日\nH:mm:ss").format(时间);
            } catch (ParseException e) {
                contentSplit[4 * i + 3] = e.getMessage();
            }

            for (int j = 0; j < 4; j++) {

                if (j == 2 || j == 1) {
                    continue;
                }
                单元格参数 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                文本 = new TextView(this);
                if (Objects.equals(contentSplit[4 * i + j], "收费")) 文本.setTextColor(Color.RED);
                //if (j == 3) 文本.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                String 写入内容 = contentSplit[4 * i + j];
                文本.setText(写入内容);
                单元格参数.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
                文本.setLayoutParams(单元格参数);
                文本.setPadding(8, 0, 8, 0);
                tableRow.addView(文本);
            }
            单元格参数 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

            断开按钮.setText("断开");
            单元格参数.gravity = Gravity.CENTER;
            断开按钮.setLayoutParams(单元格参数);
            //断开按钮.setPadding(6, 6, 6, 6);

            断开按钮.setOnClickListener(view -> {
//                    设置初始页面();
                setResult(RESULT_OK, getIntent().putExtra("IP", view.getTag().toString()));
                finish();
            });
            tableRow.addView(断开按钮);
            if (tableLayout != null) tableLayout.addView(tableRow);
        }
        文本 = new TextView(this);
        文本.setText("新建连接可能需要几分钟后才能显示");
        文本.setPadding(8, 64, 8, 0);
        文本.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
        if (tableLayout != null) tableLayout.addView(文本);
    }
}
