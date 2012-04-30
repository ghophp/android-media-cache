package org.oliveira.mediacache.listener;

import android.graphics.Bitmap;
import android.widget.ImageView;

public interface OnMediaResponse {
	public abstract void onBitmap(Bitmap bitmap, ImageView place);
	public abstract void onVideo(String video);
	public abstract void onError(Integer errorCode, String errorMessage);
}
