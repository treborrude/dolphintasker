package com.github.treborrude.dolphintasker;

import com.dolphin.browser.addons.AddonService;
import com.dolphin.browser.addons.Browser;
import com.dolphin.browser.addons.WebViews;
import com.dolphin.browser.addons.IWebView;
import com.dolphin.browser.addons.IHttpAuthHandler;
import android.os.RemoteException;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import com.github.treborrude.dolphintasker.ui.EventEditActivity;
import android.util.Log;
import android.content.SharedPreferences;

public class DolphinTaskerService extends AddonService
{
  private static final String TAG = "DolphinTaskerService";
  
  private WebViews.PageListener mPageListener =
    new WebViews.PageListener() 
	{
	  private static final String TAG = "PageListener";
	  
      @Override
	  public void onPageFinished(IWebView webView, String url)
	  {
		Log.d(TAG, "onPageFinished");
		Context appContext = DolphinTaskerService.this.getApplicationContext();
		Intent broadcastEvent = new Intent(appContext, QueryReceiver.class);
	    broadcastEvent.setAction(Constants.EVENT_DETECTED);
		broadcastEvent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_ACTIVITY,
		                        com.github.treborrude.dolphintasker.ui.EventEditActivity.class.getCanonicalName());
		// TODO: Need something more to distinguish event type.
		broadcastEvent.setData(Uri.parse(url));
		
		appContext.sendBroadcast(broadcastEvent);
		Log.d(TAG, "onPageFinished broadcast sent.");
	  }

	  @Override
	  public void onPageStarted(IWebView webView, String url)
	  {
	    // TODO: Implement this method
		Log.d(TAG, "onPageStarted");
	  }

	  @Override
	  public boolean onReceivedHttpAuthRequest(IWebView webView, 
	                                           IHttpAuthHandler httpAuthHandler, 
											   String host, 
											   String realm)
	  {
	    // TODO: Implement this method
	    return false;
	  }

	  @Override
	  public void onReceiveTitle(IWebView webView, String title)
	  {
	    // TODO: Implement this method
		Log.d(TAG, "onReceiveTitle");
	  }
	};
	
  @Override
  protected void onBrowserConnected(Browser browser) 
  {
	Log.d(TAG, "onBrowserConnected");
	try
	{
	  browser.webViews.addPageListener(mPageListener);
	  Log.d(TAG, "Successfully added page listener.");
	}
	catch (RemoteException re)
	{
	  // TODO: Add an error dialog?
	  Log.e(TAG, "Unable to add page listener.", re);
	}
  }

  @Override
  protected void onBrowserDisconnected(Browser browser)
  {
	Log.d(TAG, "onBrowserDisconnected");
	try
	{
	  browser.webViews.removePageListener(mPageListener);
	  Log.d(TAG, "Successfully removed page listener.");
	}
	catch (RemoteException re)
	{
	  // No need for anything other than a log message in this case.
	  Log.e(TAG, "Unable to remove page listener.", re);
	}
	
	SharedPreferences returnVals = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, 0);
	returnVals.edit().clear();
	returnVals.edit().commit();
  }
}
