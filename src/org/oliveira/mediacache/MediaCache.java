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
	private Context context;
	
	public MediaCache(Context context, String folder, boolean sd, String server) {
		this.folder = folder;
		this.sd = sd;
		this.server = server;
		this.context = context;
	}
	
	public MediaCache(Context context, String folder, boolean sd) {
		this.folder = folder;
		this.sd = sd;
		this.context = context;
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
	
	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	
	public void get(Media media, OnMediaResponse response) {
		
		if(media != null){
			
			File current = getFile(media.getFile(), context);
			if(current.exists() && current.length() > 0 && 
					current.lastModified() > media.getUpdated().getTimeInMillis()){
				
				if(media.getType() == Media.IMAGE){
					
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
				request.execute(getServer() + media.getFile());
				
			}
			
		}else{
			throw new InvalidParameterException();
		}
		
	}
	
	public void get(Media media, OnMediaResponse response, final int index) {
		
		if(media != null){
			
			File current = getFile(media.getFile(), context);
			if(current.exists() && current.length() > 0 && 
					current.lastModified() > media.getUpdated().getTimeInMillis()){
				
				if(media.getType() == Media.IMAGE){
					
					Bitmap bitmap = BitmapFactory.decodeFile(current.getAbsolutePath());
					response.onBitmap(bitmap, index);
				
				}else{
					response.onVideo(current.getAbsolutePath());
				}
				
			}else{
				
				MediaRequest request = new MediaRequest();
				request.setType(media.getType());
				request.setCurrent(current);
				request.setOnReponse(response);
				request.setIndex(index);
				request.execute(getServer() + media.getFile());
				
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
    			context.getFilesDir(), 
    			name
        	);
        }
        
        return current;
    }
	
	private class MediaRequest extends AsyncTask<String, Integer, String> {
		
		private OnMediaResponse onReponse;
		private int type;
		private File current;
		private Bitmap bitmap;
		private int index = -1;
		
		public OnMediaResponse getOnReponse() {
			return onReponse;
		}
		public void setOnReponse(OnMediaResponse onReponse) {
			this.onReponse = onReponse;
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
		
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			try {
				
				URL url = new URL(params[0]);

	    		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	    		connection.setRequestMethod("GET");
	    		connection.setDoOutput(true);
	    		connection.connect();
	            
	    		FileOutputStream out = null;
	    		if(hasSd()){
	    			out = new FileOutputStream(getCurrent());
	    		}else{
	    			out = getContext().openFileOutput(
                		getCurrent().getName(), 
                		Context.MODE_WORLD_READABLE
                	);
	    		}
	    		
	            InputStream is = connection.getInputStream();
	            
	            byte[] buffer = new byte[1024];
	            int bufferLength = 0;

	            while ((bufferLength = is.read(buffer)) > 0) {
	            	out.write(buffer, 0, bufferLength);
	            }
	            out.close();
				
			} catch(Exception e) {
				
				if(getOnReponse() != null){
					getOnReponse().onError(e.getStackTrace());
				}
			
			}
			
			if(getType() == Media.IMAGE){
				setBitmap(BitmapFactory.decodeFile(getCurrent().getAbsolutePath()));
			}
			
            return null;
			
		}
		
		@Override
		protected void onPostExecute(String result) {
			
			if(getOnReponse() != null){
				
				if(getType() == Media.IMAGE){
					
					if(getIndex() >= 0){
						getOnReponse().onBitmap(getBitmap(), getIndex());
					}else{
						getOnReponse().onBitmap(getBitmap());
					}
				}else{
					getOnReponse().onVideo(getCurrent().getAbsolutePath());
				}				
			}
			
			setBitmap(null);
			setCurrent(null);
		}
		
	}
	
}
