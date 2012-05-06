package com.luugiathuy.apps.remotebluetooth;

import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.StreamConnection;

public class ProcessConnectionThread implements Runnable{

	private StreamConnection mConnection;
	
	
	public ProcessConnectionThread(StreamConnection connection)
	{
		mConnection = connection;
	}
	
	@Override
	public void run() {
		try {
			
			// prepare to receive data
			InputStream inputStream = mConnection.openInputStream();
	        OutputStream openOutputStream = mConnection.openOutputStream();
			System.out.print("waiting for input");
	        
			byte[] data = new byte[1024];
	        while (true) {
	        	
	        	int nr = inputStream.read(data);
	        	
	        	
	        	if(nr != 0)
	        	{
	        		String string = new String(data,"UTF-16LE");
	        		System.err.print("mes: " + string);
	        		openOutputStream.write((string).getBytes("UTF-16LE"));
	        	}
	        	
	        	data = null;
	        	data = new byte[1024];
	        	
        	}
        } catch (Exception e) {
    		e.printStackTrace();
    	}
	}
	
}
