package com.actions.model;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.actions.download.DownloadHelper;
import com.actions.ota.MainActivity;
import com.actions.ota.R;
import com.actions.ota.UpdateApplication;
import com.actions.utils.Debug;
import com.actions.utils.Utilities;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static android.app.ActivityThread.TAG;

public class CheckVersionMachine {
    Context mContext;
    CheckVersionCallBack mCallBack;
    DownloadHelper mDownloadHelper;
    CheckResult mCheckResult;
    private boolean isDoingCheck = false;
    
    public CheckVersionMachine(Context context, CheckVersionCallBack ccb) {
        mContext = context;
        mCallBack = ccb;
        mDownloadHelper = UpdateApplication.instance().mDownloadHelper;
    }
    
    public void pause() {
        
    }
    
    public void resume() {
        
    }
    
    public void stop() {
        UpdateApplication.instance().workOnChecking(false);
    }
    
    public void check() {
        UpdateApplication.instance().workOnChecking(true);
        if(!isReady()) {
            CheckResult cr = new CheckResult();
            cr.setResult(CheckResult.RESULT_CHECK_NOT_READY);
            mCallBack.onCVCheckNotReady(cr);
            return;
        }

        if(pingOTAServer("edu.ibotn.com")) {
            Log.d(TAG,"yison ping edu.ibotn.com success!");
            mCallBack.onCVStart();
            isDoingCheck = true;
            new CheckUpdateXml().start();
        }else
        {
            Log.e(TAG,"yison ping edu.ibotn.com error!");
            CheckResult cr = new CheckResult();
            cr.setResult(CheckResult.RESULT_CONNECTIVITY_ERROR);
            mCallBack.onCVError(mCheckResult);
        }
    }

    private boolean pingOTAServer(String serverIp) {
        try {
            String str = "ping -c 1 -i 0.2 -W 1 "
                    + serverIp;
            if(0 == executeCommand(str,2000))
                return true;
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }catch (TimeoutException e)
        {
            e.printStackTrace();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    private static class Worker extends Thread {
        private final Process process;
        private Integer exit;

        private Worker(Process process) {
            this.process = process;
        }

        public void run() {
            try {
                exit = process.waitFor();
            } catch (InterruptedException ignore) {
                return;
            }
        }
    }

    /**
     * 运行一个外部命令，返回状态.若超过指定的超时时间，抛出TimeoutException
     * @param command
     * @param timeout
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public static int executeCommand(final String command, final long timeout) throws IOException, InterruptedException, TimeoutException {
        Process process = Runtime.getRuntime().exec(command);
        Worker worker = new Worker(process);
        worker.start();
        try {
            worker.join(timeout);
            if (worker.exit != null){
                return worker.exit;
            } else{
                throw new TimeoutException();
            }
        } catch (InterruptedException ex) {
            worker.interrupt();
            Thread.currentThread().interrupt();
            throw ex;
        } finally {
            process.destroy();
        }
    }


    /**
     * Think about this case:
     * When the users perform a lot of click actions and then invoke a lot of thread to do check version stuff.
     * It should mess up all the state machines. 
     * So we do have to tell whether is it time to do check.
     * @return
     */
    private boolean isReady() {
        // TODO Auto-generated method stub
        return !isDoingCheck;
    }

    
    /**
     * This handler is only used to handle checkversion stuff
     */
    private Handler mCheckHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // dismiss the progress bar
            switch (msg.what) {
                case CheckResult.RESULT_CONTENT_NOT_FOUND:
                    mCallBack.onCVContentNotFound(mCheckResult);
                    break;
                case CheckResult.RESULT_UPDATE_NEEDED:
                    mCallBack.onCVUpdateNeeded(mCheckResult);
                    break;
                case CheckResult.RESULT_UP_TO_DATE:
                    mCallBack.onCVUpToDate(mCheckResult);
                    break;
                case CheckResult.RESULT_CHECK_NOT_READY:
                    mCallBack.onCVCheckNotReady(mCheckResult);
                    break;
                case CheckResult.RESULT_CONNECTIVITY_ERROR:
                    mCallBack.onCVError(mCheckResult);
                    break;
              /*  case CheckResult.RESULT_SERVER_NOT_FOUND:
                    mCallBack.onCVServerNotFound(mCheckResult);
                    break;*/
                case CheckResult.RESULT_SERVER_NOT_FOUND_XML:
                    mCheckResult.setResultMsg(R.string.not_found_xml);
                    mCallBack.onCVServerNotFound(mCheckResult);
                    break;
                case CheckResult.RESULT_SERVER_NOT_FOUND_OTAZIP:
                    mCheckResult.setResultMsg(R.string.not_found_ota_zip);
                    mCallBack.onCVServerNotFound(mCheckResult);
                    break;
                case CheckResult.RESULT_UNKNOW:
                    mCallBack.onCVUnknown(mCheckResult);
                    break;
                default:
                    break;
            }
            
            isDoingCheck = false;
        }
    };
    
    /** 
    * @ClassName: CheckUpdateXml
    * @Description: Parse xml file thread
    * @author  
    * @date 2015-1-30 16:05:02 
    *  
    */
    class CheckUpdateXml extends Thread {
        Message msg = new Message();

        @Override
        public void run() {
            DownloadHelper dlh = UpdateApplication.instance().mDownloadHelper;
            if (dlh != null) {
                mCheckResult = dlh.check();
                UpdateApplication.instance().mUpdateInfo = mCheckResult.mUpdateInfo;
                msg.what = mCheckResult.getResult();
            
                //fix BUG00271034, we don't need to tell UI thread that much hurry
                mCheckHanlder.sendMessageAtTime(msg, SystemClock.uptimeMillis() + 2*1000);
            } else {
                msg.what = CheckResult.RESULT_UP_TO_DATE;
                mCheckHanlder.sendMessage(msg);
            }
        }
        
        
    }// end CheckUpdateXml  
}
