package com.actions.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;
import com.actions.model.UpdateInfo;
import com.actions.ota.R;
import com.actions.parsexml.UpdateInfoContentHandler;
import com.actions.userconfig.UserConfig;

import android.R.anim;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.os.Environment;
import android.os.RecoverySystem;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.widget.TextView;
import android.view.View;
import com.actions.ota.R;

public class Utilities {
	public static final boolean DEBUG = true;
	private static final String TAG = "Update";

	// update info head
	
	public static String Update = "Update";
	public static String UpdateType = "UpdateType";
	public static String UpdateInfo = "UpdateInfo";
	public static String UpdateInfoOldVersion = "oldversion";
	public static String UpdateInfoDownloadUrl = "downloadurl";
	public static String UpdateInfoNewVersion = "newversion";
	public static String UpdateInfoFileSize = "filesize";
	public static String UpdateInfoMd5 = "md5";	
	public static String UpdateInfoMessage = "updatemessage";
	public static String UpdateInfoMsg = "msg";
	
	// help file path
	public static String mHelpFilePath;
	
	// help file language
	public static String mHelpFilePath_CN = "file:///android_asset/help-cn.html";
	public static String mHelpFilePath_TW = "file:///android_asset/help-tw.html";
	public static String mHelpFilePath_EN = "file:///android_asset/help-en.html";
	
	// current system version
	public static String mSystemVersion = "ro.build.display.id";
	
	// recovery update file name
	public static String mRecoveryFileName = "update.zip";
		
	// SharedPreferences file name
	public static String mSharedPreferencesMark = "mark";
	
	// set long type because calculate the size is too long
//	public static long size = 0;
//	public static long old_size = 0;
	
	// sdcard root
	public static String SdCardRoot = "/mnt/sdcard/";
	public static String StorageSdCardRoot = "/storage/sdcard/";
	
	public static String RecoveryPathName = SdCardRoot + mRecoveryFileName;
	public static String RecoveryStoragePathName = StorageSdCardRoot + mRecoveryFileName;
	
	// download result
	public static int mDownloadSuccess = 1;
	public static int mDownloadFail = 0;
	
	//To determine whether the local download
	public static boolean mIslocalDownload = false ;
	
	
	
	// download puase
	public static boolean mIsPaused = false;
	
	// update check Frequency
	public static int mCheckFrequency = 1;
	
	// OTA receiver
	public static String BootReceiver = "android.intent.action.BOOT_COMPLETED";
	public static String ConnectivityReceiver = "android.net.conn.CONNECTIVITY_CHANGE";
	public static String DownloadProgressBarReceiver = "android.CustomDownload.Receiver";
	public static String DownloadCompleteReceiver = "android.CustomDownload.CompleteReceiver";
	public static String UpdateCheckReceiver = "android.alarm.check.UPDATE";
	public static String ShutdownReceiver = "android.intent.action.ACTION_SHUTDOWN";
    public static String DownloadSuccessNotification = "android.DownloadSuccess";
    public static String downloadFailNotification = "android.DownloadFail";
    public static String UpdateFile = "android.intent.UPDATE" ;
    private static String URL_GET_TYPE = "http://edu.ibotn.com/Config-server/terminal/datas";
	
	// get current system version
	public static String mCurrentSystemVersion = android.os.SystemProperties.get(mSystemVersion , "");

	// config build.prop ota check server file path
	public static String mDeviceModel = android.os.SystemProperties.get("ro.device.model" , "");
	public static String mManufacturer = android.os.SystemProperties.get("ro.product.manufacturer", "");
	
    // if mWipeConfig is true ,recovery will wipe data and cache
    public static boolean mWipeConfig = android.os.SystemProperties.getBoolean("ro.recovery.wipe", true);
    
    // auto Recovery wipe data and wipe cache
    public static String mAutoRecoveryPath = "--update_package=/mnt/sdcard/update.zip\n--wipe_data\n--wipe_cache";
    // auto Recovery not wipe data and cache
    public static String mAutoRecoveryNotWipe = "--update_package=/mnt/sdcard/update.zip";
	
	// timer 
	public static Timer timer;
	
	// get server xml url
	public static String getServerXmlUrl(){
		String mServerXmlUrl = "";
		String type = getType(URL_GET_TYPE);
		String ServerIP = UserConfig.mServerIP;
		Log.i(TAG,"yison getType type = "+type);
		//如果是空则使用原始的配置
		if(!TextUtils.isEmpty(type))
		{
			String defaultType = ServerIP.substring(ServerIP.indexOf("/", 7) + 1, ServerIP.length() - 1);
			Log.e(TAG,"yison defaultType = "+defaultType);
			ServerIP = ServerIP.replace(defaultType, type);
			Log.e(TAG,"yison mServerIP = "+ServerIP);
		}

		if((!mDeviceModel.equals("")) && (!(mManufacturer.equals("")))){
		    if (ServerIP.charAt(ServerIP.length() -1) == '/') {
			    mServerXmlUrl = ServerIP + "UpdateInfo_" + mDeviceModel + "_" + mManufacturer + ".xml";
			} else {
			    mServerXmlUrl = ServerIP + "/UpdateInfo_" + mDeviceModel + "_" + mManufacturer + ".xml";
			}
			Log.e(TAG,"yison getServerXmlUrl "+mServerXmlUrl);
			return mServerXmlUrl;
		} else{
			System.out.println("OTA Server xml url is not Complete");
		}
		Log.e(TAG,"yison getServerXmlUrl "+mServerXmlUrl);
		return mServerXmlUrl;
	}

	public static int type = 0; // 0 Test 1 Release
	private static String TYPE_RELEASE = "Release";
	private static String TYPE_TEST = "Test";
	//obtain ota update tpe like nk
	public static String getType(String urlStr) {
		try {
			String id = Build.SERIAL.substring(0,13);
			Log.e(TAG,"yison request = "+"termNum="+id + " urlStr = "+urlStr);
			String iRecv = HttpUnity.utPostData("termNum="+id, urlStr);
			Log.e(TAG,"yison response = "+iRecv);

			JSONObject jsonObject = new JSONObject(iRecv);
			if(jsonObject.getInt("state") == 200) {
				JSONArray array = jsonObject.getJSONArray("list");

				//有两个配置，则通过判断type type = 0，则使用Release ，type = 1 则使用Test
				if(array.length() > 1)
				{
					for(int i = 0;i<array.length();i++)
					{
						JSONObject config = array.getJSONObject(i);
						String typeValue = config.getString("typeValue");
						String configValue = config.getString("configValue");
						if(type == 0 && configValue.contains(TYPE_TEST))
								continue;
						iRecv = typeValue + "/" + configValue;
						Log.e(TAG,"yison typeValue = "+typeValue + " configValue = "+configValue );
						return iRecv;
					}
				}else
				{
					//只有一个的话就用这个
					JSONObject config = array.getJSONObject(0);
					String typeValue = config.getString("typeValue");
					String configValue = config.getString("configValue");
					iRecv = typeValue + "/" + configValue;
					Log.e(TAG,"yison typeValue = "+typeValue + " configValue = "+configValue );
					return iRecv;
				}

			}else
			{
				String reason = jsonObject.getString("msg");
				int errorCode = jsonObject.getInt("state");
				Log.e(TAG,"yison getType error reason = "+reason + " errorCode = "+errorCode);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			Log.e(TAG,"yison getType error exception " + e.getMessage());
		}
			return "";
	}
	
	// reboot to recovery update model
	public static void rebootToRecovery(Context mContext){
    	Process localProcess;
    	try {
			RecoverySystem.bootCommand(mContext, "/");
 		} catch (Exception e) {
			e.printStackTrace();
		} 
    }
	
	// reboot to auto recovery update model
	public static void rebootToAutoRecovery(Context mContext){
		try {
            if(mWipeConfig){
            	if(android.os.SystemProperties.get("ro.build_mode", "CTS").equals("CTS")){
            		// cts firware
            		if(Environment.isExternalStorageEmulated()){
            			mAutoRecoveryPath = "--update_package=" + Environment.getMediaStorageDirectory() + "/update.zip\n--wipe_data\n--wipe_cache";
            		}
            	}
				Log.e(TAG,"recovery wipe true AutoRecoveryPath = "+mAutoRecoveryPath);
				RecoverySystem.bootCommand(mContext, mAutoRecoveryPath);
            } else{
            	if(android.os.SystemProperties.get("ro.build_mode", "CTS").equals("CTS")){
            		// cts firware
            		if(Environment.isExternalStorageEmulated()){
            			mAutoRecoveryNotWipe = "--update_package=" + Environment.getMediaStorageDirectory() + "/update.zip";
            		}
            	}
				Log.e(TAG,"recovery wipe false AutoRecoveryNotWipe = "+mAutoRecoveryNotWipe);
                RecoverySystem.bootCommand(mContext, mAutoRecoveryNotWipe);
            }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// check whether connect to the internet
	public static boolean checkConnectivity(Context mContext){
		ConnectivityManager mManager=(ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mManager.getActiveNetworkInfo();
		if((mNetworkInfo != null) && (mNetworkInfo.isConnected())){
			return true;
		}
		return false;
	}
	
	// check wifi connect state
	public static boolean checkWifiState(Context mContext){
		ConnectivityManager mManager=(ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = mManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if(wifi == State.CONNECTED){
			return true;
		}
		return false;
	}
	
	// parse the xml data
    public static UpdateInfoContentHandler parse(String xmlStr) {
    	List<UpdateInfo> mTemp = new ArrayList<UpdateInfo>();
    	UpdateInfoContentHandler mUpdateInfoContentHandler = null;
        try {
        	mUpdateInfoContentHandler = new UpdateInfoContentHandler();
            android.util.Xml.parse(xmlStr, mUpdateInfoContentHandler);           
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mUpdateInfoContentHandler;
    }
	
	// if the server does not support Chinese path need to convert the url encoding 
    public static String encodeGB(String string){
        // convert Chinese encoding
        String split[] = string.split("/"); 
        for (int i = 1; i < split.length; i++) {
            try {
                split[i] = URLEncoder.encode(split[i], "GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            split[0] = split[0]+"/"+split[i];
        }
        // handling spaces
        split[0] = split[0].replaceAll("\\+", "%20");
        return split[0];
    }
    
    // get the mode of Recovery
    // 0 : manual recovery
    // 1 : auto recovery
    public static int getRecoveryMode(){
    	String result;
    	result = android.os.SystemProperties.get("ro.ota.autorecovery", "disable");
    	Log.e(TAG,"update tpe result = "+result + " (disable : manual recovery enable : auto recovery)");
    	if(result.equals("enable")){
    		return 1;
    	} else{
    		return 0;
    	}
    }
    
    // save the server xml file to SharedPreferences
    public static void saveServerMD5Msg(Context mContext, UpdateInfo mUpdateInfo){
    	SharedPreferences prefs = mContext.getSharedPreferences(mSharedPreferencesMark, 
				Context.MODE_PRIVATE);
    	Editor editor = prefs.edit();
    	editor.putString(mContext.getString(R.string.md5), mUpdateInfo.getMd5());
		editor.commit();
    }
    
    // get md5 msg from SharedPreferences
    public static String getMD5Msg(Context mContext){
    	SharedPreferences prefs = mContext.getSharedPreferences(mSharedPreferencesMark, 
				Context.MODE_PRIVATE);
    	String mMd5 = prefs.getString(mContext.getString(R.string.md5), "abcd");
    	return mMd5;
    }
    
    public static void writeAutoCheck(Context mContext, boolean value){
    	SharedPreferences prefs = mContext.getSharedPreferences(Utilities.mSharedPreferencesMark, 
				Context.MODE_PRIVATE);
    	Editor editor = prefs.edit();
    	editor.putBoolean(mContext.getResources().getString(R.string.autocheck), value);
		editor.commit();
    }
    
    public static void writeCheckFrequency(Context mContext, int value){
    	SharedPreferences prefs = mContext.getSharedPreferences(Utilities.mSharedPreferencesMark, 
				Context.MODE_PRIVATE);
    	Editor editor = prefs.edit();
    	editor.putInt(mContext.getResources().getString(R.string.checkfrequency), value);
		editor.commit();
    }
    
    public static boolean readAutoCheck(Context mContext){
    	SharedPreferences prefs = mContext.getSharedPreferences(Utilities.mSharedPreferencesMark, 
				Context.MODE_PRIVATE);
    	boolean status = prefs.getBoolean(mContext.getResources().getString(R.string.autocheck), true);
    	return status;
    }
    
    public static int readCheckFrequency(Context mContext){
    	SharedPreferences prefs = mContext.getSharedPreferences(Utilities.mSharedPreferencesMark, 
				Context.MODE_PRIVATE);
    	int value = prefs.getInt(mContext.getResources().getString(R.string.checkfrequency), 1);
    	return value;
    }
    
    // get current system language
    public static String getCurrentSystemLanguage(){
    	String language = Locale.getDefault().toString();
    	return language;
    }
    
    // get sdk version
    public static int getSdkVersion(){
    	int version = android.os.SystemProperties.getInt("ro.build.version.sdk", 16);
    	return version;
    }
    
    /** 
     * @Title: setViewDisText 
     * @Description: View display format
     * @param @param textview -- View Object
     * @param @param text -- Display info
     * @return void
     * @throws 
     */  
    public static void setViewDisText(TextView textview, String text){
    	textview.setText("");
    	textview.append("\n\t");
    	textview.append(text);
    }
    
}
