package pku.brainor.ipgate;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.Button;
import android.widget.RemoteViews;

/**
 * Created by 欧伟科 on 2016/7/15.
 */
public class appWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;//只有一个应该就不用了
        int appWidgetId=appWidgetIds[0];

        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.appwidget);
//需要        views.setOnClickPendingIntent(R.id.免费_widgetbutton,pendingIntent);
//        views.setOnClickFillInIntent();

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
