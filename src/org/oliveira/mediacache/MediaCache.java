package org.oliveira.mediacache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;

import org.oliveira.mediacache.listener.OnMediaResponse;
import org.oliveira.mediacache.model.Media;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

public class MediaCache {
	
	private boolean sd = false;
	private String folder;
	private String server;
	
	public MediaCache(String folder, boolean sd, String server) {
		this.folder = folder;
		this.sd = sd;
		this.server = server;
	}
	
	public boolean hasSd() {
		return sd;
	}
	public void setSd(boolean sd) {
		this.sd = sd;
	}
	
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}

	public String getFolder() {
		return "/"+folder+"/";
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}
	
	public void get(Media media, Context context, OnMediaResponse response) {
		
		if(media != null){
			
			File current = getFile(media.getFile(), context);
			if(current.exists()){
				
				if(media.getType() == 0){
					
					Bitmap bitmap = BitmapFactory.decodeFile(current.getAbsolutePath());
					response.onBitmap(bitmap);
				
				}else{
					response.onVideo(current.getAbsolutePath());
				}
				
			}else{
				
				MediaRequest request = new MediaRequest();
				request.setType(media.getType());
				request.setCurrent(current);
				request.setOnReponse(response);
				request.execute(getServer() + "/" + media.getFile());
				
			}
			
		}else{
			throw new InvalidParameterException();
		}
		
	}
	
	private File getFile(String name, Context context){
    	
		File current;
		
        if(hasSd()){
        	current = new File(
    			Environment.getExternalStorageDirectory() + getFolder(), 
    			name
        	);
        }else{
        	current = new File(
    			context.getFilesDir() + getFolder(), 
    			name
        	);
        }
        
        return current;
    }
	
	private class MediaRequest extends AsyncTask<String, Integer, String> {
		
		private OnMediaResponse onReponse;
		private Integer errorCode;
		private String errorMessage;
		private int type;
		private File current;
		private Bitmap bitmap;
		
		public OnMediaResponse getOnReponse() {
			return onReponse;
		}
		public void setOnReponse(OnMediaResponse onReponse) {
			this.onReponse = onReponse;
		}
		
		public Integer getErrorCode() {
			return errorCode;
		}
		public void setErrorCode(Integer errorCode) {
			this.errorCode = errorCode;
		}
		
		public String getErrorMessage() {
			return errorMessage;
		}
		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}
								
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		
		public File getCurrent() {
			return current;
		}
		public void setCurrent(File current) {
			this.current = current;
		}
		
		public Bitmap getBitmap() {
			return bitmap;
		}
		public void setBitmap(Bitmap bitmap) {
			this.bitmap = bitmap;
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			try {
				
				URL url = new URL(params[0]);

	    		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	    		connection.setRequestMethod("GET");
	    		connection.setDoOutput(true);
	    		connection.connect();
	            
	            FileOutputStream out = new FileOutputStream(getCurrent());
	            InputStream is = connection.getInputStream();
	            
	            byte[] buffer = new byte[1024];
	            int bufferLength = 0;

	            while ((bufferLength = is.read(buffer)) > 0) {
	            	out.write(buffer, 0, bufferLength);
	            }
	            out.close();
				
			} catch(Exception e) {
				
				setErrorCode(e.hashCode());
				setErrorMessage(e.getLocalizedMessage());
			
			}
			
			if(getType() == 0){
				setBitmap(BitmapFactory.decodeFile(getCurrent().getAbsolutePath()));
			}
			
            return null;
			
		}
		
		@Override
		protected void onPostExecute(String result) {
			
			if(getOnReponse() != null){
				
				if(getType() == 0){
					
					if(getBitmap() != null){
						getOnReponse().onBitmap(getBitmap());
					}else{
						
						getOnReponse().onError(
							getErrorCode(), 
							getErrorMessage()
						);
						
					}
				}else{
					
					if(getCurrent().exists()){
						getOnReponse().onVideo(getCurrent().getAbsolutePath());
					}else{
						
						getOnReponse().onError(
							getErrorCode(), 
							getErrorMessage()
						);
						
					}
				}
				
			}
		}
		
	}
	
}
