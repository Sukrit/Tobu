package com.journal.Tobu;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class SaveMedia {
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	/** Create a file Uri for saving an image or video */
	public static Uri getOutputMediaFileUri(int type, Context context){
	      return Uri.fromFile(getOutputMediaFile(type, context));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type, Context context){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.
		
	    File mediaStorageDir = new File(context.getExternalFilesDir(
	            Environment.DIRECTORY_PICTURES), "ShareMe");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.
	    
	    System.out.println("mediaStorageDir is " + mediaStorageDir.getPath());
	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("ShareMe", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}

}
