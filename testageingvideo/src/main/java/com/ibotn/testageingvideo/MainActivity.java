package com.ibotn.testageingvideo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * 老化视频apk
 */
public class MainActivity extends Activity {

private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onClick(View view)
    {
        Intent intent = new Intent(MainActivity.this,TestService.class);
        switch (view.getId())
        {
            case R.id.btn_start:
                Log.d(TAG,"yison onClick btn_start");
                startService(intent);
                break;
            case R.id.btn_stop:
                Log.d(TAG,"yison onClick btn_stop");
                stopService(intent);
                break;
                default:
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        String type = "video/mp4";
        Uri name = Uri.parse("file:///storage/sd-ext/movies/test.mp4");
        intent.setDataAndType(name, type);
            intent.setAction("android.intent.action.VIEW");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setClassName("com.tencent.research.drop", "com.tencent.research.drop.player.activity.PlayerActivity");
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
