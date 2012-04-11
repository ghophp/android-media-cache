# Media Cache client for Android

This library use android based resources to download and cache images from a server.

## Install

	1 - Clone the project;
	2 - In Eclipse > File > New > Java Project;
	3 - Unmark use default location and select the folder cloned;
	4 - Add to build path a android.jar compatible with your project version (sdk folder > platform > android-14 > android.jar);
	5 - Add to your Android project a the dependence to this project. 

## Instance
	
	String folder = "teste";
	boolean hasSD = false;
	String remote = "http://localhost:8888/public/images";
	
	MediaCache cache = new MediaCache(
		folder, 
		hasSD, 
		remote
    );
    

**folder**: A local folder, that should exist, and will receive cached files.<br>
**hasSD**: The library should save on external storage or internal.<br>
**remote**: Remote folder with the files.

## Usage

	String path = 'teste.jpg';
	int type = 0; // 0 - image | 1 - video
	String updated = "1970-01-01 00:00:00";

	Media media = new Media(
		path,
		type,
		updated
	);

	cache.get(media, context, new OnMediaResponse() {
				
		public void onVideo(String video) {
			
		}
		
		public void onError(Integer errorCode, String errorMessage) { 
			
		}
		
		public void onBitmap(Bitmap bitmap) {
			
		}
		
	});

## TODO

* update file based on update attribute on Media model.
* teste suite

## License

Copyright (C) 2012 Guilherme Henrique de Oliveira

Distributed under the MIT License.