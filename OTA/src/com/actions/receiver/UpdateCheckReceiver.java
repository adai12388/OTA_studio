package com.actions.receiver;

import java.util.List;

import com.actions.model.CheckResult;
import com.actions.model.CheckVersionCallBack;
import com.actions.model.CheckVersionMachine;
import com.actions.model.UpdateInfo;
import com.actions.ota.MainActivity;
import com.actions.ota.R;
import com.actions.ota.UpdateApplication;
import com.actions.utils.Debug;
import com.actions.utils.Utilities;
import com.actions.utils.VersionUtils;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.content.IntentFilter;
import android.view.WindowManager;


public class UpdateCheckReceiver extends BroadcastReceiver {
	private static final String TAG = "ota";
	
	private Context mContext;
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Utilities.UpdateCheckReceiver)){
			mContext = context;
			if(intent.getExtra("TYPE").equals("BOOT")){
				Debug.d(TAG,"boot update check");				
				if(!UpdateApplication.instance().isWorkOnCheckingOrDownloading() 
				    && (Utilities.checkConnectivity(context)) 
				    && (Utilities.readAutoCheck(context))){
				    Debug.d("boot check invoked");
					checkNotOnActivity();
				}
			} else if(intent.getExtra("TYPE").equals("DAILY")){
				if(Utilities.mCheckFrequency == Utilities.readCheckFrequency(context)){
				    Debug.d("daily check invoked");
					Utilities.mCheckFrequency = 1;
					if((Utilities.checkConnectivity(context))){
						if(!UpdateApplication.instance().isWorkOnCheckingOrDownloading() 
						    && (Utilities.readAutoCheck(context))){
						    checkNotOnActivity();
						};
					}
				} else{					
					Utilities.mCheckFrequency++;
				}
			}
		}
	}
	
	private void checkNotOnActivity() {
	    Debug.v("on checkNotOnActivity");
	    new CheckVersionMachine(mContext, new CheckVersionCallBack() {
            
            @Override
            public void onCVUpdateNeeded(CheckResult cr) {
                // TODO Auto-generated method stub
//                setUpNotification();
//				updateDialog();
				gotoMainActivity();
            }
            
            @Override
            public void onCVUpToDate(CheckResult cr) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onCVUnknown(CheckResult cr) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onCVStart() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onCVServerNotFound(CheckResult cr) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onCVError(CheckResult cr) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onCVContentNotFound(CheckResult cr) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onCVCheckNotReady(CheckResult cr) {
                // TODO Auto-generated method stub
                
            }
        }).check();
	}
	
	/**
	 * set up notification
	 */	
	public void setUpNotification(){	
		NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		
		Intent mTempIntent = new Intent();
		mTempIntent.setClass(mContext, MainActivity.class);
		mTempIntent.putExtra("LaunchFromNotification", true);
		PendingIntent mTemp = PendingIntent.getActivity(mContext, 0, mTempIntent, 0);		

		Builder mBuilder = new Notification.Builder(mContext);
		mBuilder.setSmallIcon(R.drawable.ic_notification);
		mBuilder.setTicker(mContext.getString(R.string.have_update));
		mBuilder.setContentTitle(mContext.getString(R.string.have_update));
		mBuilder.setContentText(mContext.getString(R.string.notification_update));
		mBuilder.setContentIntent(mTemp);
		Notification mNotification = mBuilder.build();
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;

		mNotificationManager.notify(128 ,mNotification);
		
//		int icon = R.drawable.ic_notification;
//		long when = System.currentTimeMillis();
//		Notification mNotification = new Notification(icon, mContext.getString(R.string.have_update), when);
//		mNotification.flags = Notification.FLAG_ONGOING_EVENT;
//		Intent mTempIntent = new Intent();
//		mTempIntent.setClass(mContext, UpdateOnLineActivity.class);
//		mTempIntent.putExtra("Update", mTargetUpdateInfo);
//		PendingIntent mTemp = PendingIntent.getActivity(mContext, 0, mTempIntent, 0);
//		
//		mNotification.setLatestEventInfo(mContext, mContext.getString(R.string.have_update),
//				mContext.getString(R.string.notification_update), mTemp);		
//		mNotificationManager.notify(R.drawable.ic_notification, mNotification);		
	}

	public void updateDialog(){
		final AlertDialog sDialog = new AlertDialog.Builder(mContext)
				.setTitle(mContext.getString(R.string.have_update))
				.setMessage(mContext.getString(R.string.notification_update))
				.setCancelable(false)
				.setNegativeButton(R.string.sure, null)
				.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dia, int arg1) {
						Intent mTempIntent = new Intent();
						mTempIntent.setClass(mContext, MainActivity.class);
						mTempIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						mTempIntent.putExtra("LaunchFromNotification", true);
						mContext.startActivity(mTempIntent);
					}
				})
				.create();
		sDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
		sDialog.show();
	}
	private void gotoMainActivity(){
		Intent mTempIntent = new Intent();
		mTempIntent.setClass(mContext, MainActivity.class);
		mTempIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mTempIntent.putExtra("LaunchFromNotification", true);
		mContext.startActivity(mTempIntent);
	}
}
