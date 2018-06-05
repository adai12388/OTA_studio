package com.actions.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUnity {
	
	private static final String _Get = "GET";
	private static final String _Post = "POST";
	
	public static void utSleep(long iArgSecond){
		try {
			Thread.sleep(iArgSecond);
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//请求Http数据(GET)
	public static String utGetData(String iArgUrl){
		return rvGetHttpData("", iArgUrl, _Get);
	}
	
	//请求Http数据(POST)
	public static String utPostData(String iArgData, String iArgUrl){
		return rvGetHttpData(iArgData, iArgUrl, _Post);
	}
	
	//请求Http数据
	public static String utGetHttpData(String iArgData, String iArgUrl, String iArgMethod){
		
		if(iArgMethod == null){
			return new String();
		}
		
		if(iArgMethod.toUpperCase().equals(_Get)){
			return rvGetHttpData(iArgData, iArgUrl, _Get);
		}
		else if(iArgMethod.toUpperCase().equals(_Post)){
			return rvGetHttpData(iArgData, iArgUrl, _Post);
		}
		
		return new String();
	}
	
	//建立Http连接
	public static HttpURLConnection utOpenHttpConnect(String iArgUrl, String iArgMethod) {
		try {
			URL iUrl = new URL(iArgUrl);
			
			String iHost = iUrl.getHost();
    		HttpURLConnection iConnect = (HttpURLConnection) iUrl.openConnection();
    		iConnect.setRequestProperty("Host", iHost);
    		iConnect.setDoOutput(true);
    		iConnect.setRequestMethod(iArgMethod);
    		iConnect.setConnectTimeout(10 * 1000);
    		iConnect.setReadTimeout(10 * 1000);
    		iConnect.connect();

    		return iConnect;
		} catch (Exception e) {
		}
    	
    	return null;
	}
	
	//发送数据
	public static void utSendData(String iArgData, OutputStream iArgOutput) {
		
		try {
			rvSendData(iArgData, iArgOutput);
		} 
		catch (Exception e) {
		}
		
	}
	
	//接收数据
	public static String utRecvData(InputStream iArgInput) {
		try {
			return rvRecvData(iArgInput);
		} 
		catch (IOException e) {
			// TODO: handle exception
		}
		
		return new String();
	}
	
	private static String rvGetHttpData(String iArgData, String iArgUrl, String iArgMethod){
		
		/*建立和服务器的连接*/
		HttpURLConnection iConnect = utOpenHttpConnect(iArgUrl, iArgMethod);
		
		if(iConnect == null){
			return new String();
		}
		
		/*请求数据*/
		try {
			if(_Post.equals(iArgMethod)){
				rvSendData(iArgData, iConnect.getOutputStream());
			}
			
			return rvRecvData(iConnect.getInputStream());
		} 
		catch (IOException e) {
			// TODO: handle exception
		}
		
		return new String();
	}
	
	/**
	 * 发送数据
	 * @param iArgData
	 * @param iArgOutput
	 * @throws IOException
	 */
	private static void rvSendData(String iArgData, OutputStream iArgOutput) throws IOException{
		
		if(iArgOutput == null){
			return;
		}
		
		iArgOutput.write(iArgData.getBytes("UTF-8"));
		
		try {
			iArgOutput.close();
		} 
		catch (IOException e) {
			// TODO: handle exception
		}

	}
	
	/**
	 * 接收数据
	 * @param iArgInput
	 * @return
	 * @throws IOException
	 */
	private static String rvRecvData(InputStream iArgInput) throws IOException {
		
		if(iArgInput == null){
			return new String();
		}
		
		BufferedReader iReader = new BufferedReader(new InputStreamReader(iArgInput, "UTF-8"));
		
		StringBuffer iBuffer = new StringBuffer();
		String iLine = null;
		
		while ((iLine = iReader.readLine()) != null) {
			iBuffer.append(iLine);
		}
		
		try {
			iReader.close();
			iArgInput.close();
		} catch (IOException e) {
			// TODO: handle exception
		}
		
		return iBuffer.toString();
		
	}
}
