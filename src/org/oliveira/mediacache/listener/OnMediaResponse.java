package org.oliveira.mediacache.listener;

import android.graphics.Bitmap;

public interface OnMediaResponse {
	public abstract void onBitmap(Bitmap bitmap, int index);
	public abstract void onBitmap(Bitmap bitmap);
	public abstract void onVideo(String video);
	public abstract void onError(StackTraceElement[] stackTrace);
}
