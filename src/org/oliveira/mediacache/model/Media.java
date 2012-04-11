package org.oliveira.mediacache.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class Media {
	
	private int id = 0;
	private String description = "";
	private String file = "";
	private int type = 0;
	private Calendar updated = Calendar.getInstance();
	
	public Media(String file, int type, String updated) {
		
		setFile(file);
		setType(type);
		setUpdated(updated);
		
	}

	public Media(JSONObject json) {
		
		try {
			
			setId(json.getInt("id"));
			setDescription(json.getString("file"));
			setFile(json.getString("file"));
			setType(json.getInt("type"));
			
			if(json.has("updated")){
				setUpdated(json.getString("updated"));
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	public Calendar getUpdated() {
		return updated;
	}
	public void setUpdated(String updated) {
		
		if(updated != null){
			
			SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			try {
				this.updated.setTime(parser.parse(updated));
			} catch (ParseException e) {
				e.printStackTrace();
			}	
		}else{
			this.updated.setTime(new Date());
		}
		
	}	
	
}
