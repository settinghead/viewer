// Copyright © 2010 - May 2014 Rise Vision Incorporated.
// Use of this software is governed by the GPLv3 license
// (reproduced in the LICENSE file).

package com.risevision.viewer.client.cache;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
import com.risevision.common.client.utils.RiseUtils;
import com.risevision.viewer.client.player.RisePlayerController;
import com.risevision.viewer.client.utils.ViewerHtmlUtils;

public class RiseCacheController {
//	private static RiseCacheUtils instance;
	private static boolean isActive = false;
	
//	public RiseCacheUtils() {
//
//	}
	
//	public static RiseCacheUtils getInstance() {
//		if (instance == null) {
//			instance = new RiseCacheUtils();
//		}
//		return instance;
//	}
	
	private static final int PING_TIME = 1 * 60 * 1000;
	private static Timer cachePingTimer = new Timer() {
		public void run() {
			pingCache();
		}
	};
	private static int pingAttempt = 0;
	
	public static String getCacheVideoUrl(String url, String extension) {
		if (!isActive) {
			return url;
		}
		
		if (url.toLowerCase().contains("youtube.com")) {
			return url;
		}
		
		if (RiseUtils.strIsNullOrEmpty(extension) && url.lastIndexOf(".") != -1) {
			extension = url.substring(url.lastIndexOf(".") + 1);
			if (extension.indexOf('?') != -1) {
				extension = extension.substring(0, extension.indexOf("?"));
			}
		}
		
		String response = "http://localhost:9494/video" + (RiseUtils.strIsNullOrEmpty(extension) ? "" : "." + extension);
		response += "?url=" + URL.encodePathSegment(url);
		
		return response;
	}
	
//	public static String getCacheUrl(String url) {
//		if (!isActive) {
//			return url;
//		}
//		
//		String response = "http://localhost:9494/";
//		response += "?url=" + URL.encodeQueryString(url.replace(" ", "+"));
//		
//		return response;
//	}
	
	public static void pingCache() {
		if (!isActive) {
			String url = "http://localhost:9494/ping?callback=?";
			pingCacheNative(url);
			
			if (RisePlayerController.getIsActive()) {
				cachePingTimer.schedule(PING_TIME);
				
				if (pingAttempt > 0) {
					ViewerHtmlUtils.logExternalMessage("cache ping attempt", Integer.toString(pingAttempt));
				}
				
				pingAttempt++;
			}
		}
	}
	
	private static void pingResponseStatic() {
		cachePingTimer.cancel();

		isActive = true;
	}
	
	public static void setActive(boolean active) {
		isActive = active;
	}
	
	public static boolean isActive() {
		return isActive;
	}
	
	private static native void pingCacheNative(String url) /*-{
	    try {
	    	$wnd.writeToLog("Rise Cache ping request - start");
	    	
			$wnd.$.getJSON(url,
				{
					format: 'json'
				},
				function() {
		    	    try { 
//		    	    	debugger;
		    	    	
			        	$wnd.writeToLog("Rise Cache ping response - active");
		    	    	
		    	    	@com.risevision.viewer.client.cache.RiseCacheController::pingResponseStatic()();
		    	    }
		    	    catch (err) {
		    	    	$wnd.writeToLog("Rise Cache ping failed - " + url + " - " + err.message);
		    	    }
				}
			);
	    }
	    catch (err) {
	    	$wnd.writeToLog("Rise Cache ping error - " + url + " - " + err.message);
	    }
   	}-*/;

}
