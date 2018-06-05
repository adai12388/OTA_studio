package com.ibotn.testageingvideo;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

/**
 * Created by yison on 2018/6/1.
 */

public class TestService extends Service {

    private static final String TAG = TestService.class.getSimpleName();
    private String activityCmpName = "com.tencent.research.drop.player.activity.PlayerActivity";
    private TestThread testThread = null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
        if(testThread == null) {
            testThread = new TestThread();
            testThread.start();
        }else
        {
            testThread.stopThread();
            testThread = null;
            testThread = new TestThread();
            testThread.start();
        }
    }

    class TestThread extends Thread {
        boolean flag = true;

        public void stopThread()
        {
            flag = false;
        }

        @Override
        public void run() {
            super.run();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String type = "video/mp4";
            Uri name = Uri.parse("file:///storage/sd-ext/movies/test.mp4");
            intent.setDataAndType(name, type);
//            intent.setAction("android.intent.action.VIEW");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClassName("com.tencent.research.drop", "com.tencent.research.drop.player.activity.PlayerActivity");
            startActivity(intent);
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isTopActivy(activityCmpName);

           /* while (flag) {
                if (isTopActivy(activityCmpName)) {

                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String type = "video/mp4";
                    Uri name = Uri.parse("file:///storage/sd-ext/movies/test.mp4");
                    intent.setDataAndType(name, type);
                    intent.setAction("android.intent.action.VIEW");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setClassName("com.cooliris.media", "com.cooliris.media.MovieView");
                    startActivity(intent);
                }
            }*/
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        if(testThread != null)
        {
            testThread.stopThread();
            testThread = null;
        }
    }

    public boolean isTopActivy(String cmpName) {
        /*ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        Log.d(TAG, "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        return activityCmpName.contains(componentInfo.getPackageName());*/


        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfos = am.getRunningTasks(10);
        for(ActivityManager.RunningTaskInfo taskInfo :taskInfos )
        {
            Log.d(TAG, "CURRENT Activity ::" + taskInfo.topActivity.getClassName());

        }

        return false;
    }
}
