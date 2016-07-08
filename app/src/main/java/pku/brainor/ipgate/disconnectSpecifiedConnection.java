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

import java.util.Objects;

/**
 * Created by 欧伟科 on 2016/6/11.
 * 超过三个链接之后, 打开dialog断开指定连接
 * 现在使用了Activity
 */
public class disconnectSpecifiedConnection extends AppCompatActivity {
    String[] Content;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.disconnect);
        Content = getIntent().getStringArrayExtra("content");//传入变量是String
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
                    if (Objects.equals(Content[3 * i + j], "收费")) 文本.setTextColor(Color.RED);
                }
                文本.setText(Content[3 * i + j]);
                单元格参数.gravity = (j != 1) ? Gravity.START | Gravity.CENTER_VERTICAL : Gravity.CENTER;
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
                    setResult(RESULT_OK, getIntent().putExtra("IP", view.getTag().toString()));
                    finish();
                }
            });
            tableRow.addView(断开按钮);
            if (tableLayout != null) {
                tableLayout.addView(tableRow);
            }
        }
    }
}
