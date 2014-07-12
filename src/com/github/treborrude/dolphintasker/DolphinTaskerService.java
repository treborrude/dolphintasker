package com.github.treborrude.dolphintasker;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.RemoteException;
import android.util.Log;
import com.dolphin.browser.addons.AddonService;
import com.dolphin.browser.addons.Browser;
import com.dolphin.browser.addons.IHttpAuthHandler;
import com.dolphin.browser.addons.IWebView;
import com.dolphin.browser.addons.OnClickListener;
import com.dolphin.browser.addons.WebViews;
import com.dolphin.browser.addons.Downloads;
import com.dolphin.browser.addons.DownloadInfo;
import com.dolphin.browser.addons.AlertDialogBuilder;

public class DolphinTaskerService extends AddonService
{
  private static final String TAG = "DolphinTaskerService";
  
  private WebViews.PageListener mPageListener =
    new WebViews.PageListener() 
	{
	  private static final String TAG = "PageListener";
	
	  private void eventDetected(String eventType, String eventData)
      {	
	    Context context = DolphinTaskerService.this.getApplicationContext();
	    
		SharedPreferences.Editor rvEditor = 
		  context.getSharedPreferences(Constants.PREFS_NAME, 0).edit();
		rvEditor.putString(eventType, eventData);
	    
		if (!rvEditor.commit())
		{
		  Log.e(TAG, String.format("Unable to commit %s data to SharedPreferences!", eventType));
		}
		
		Intent requestQuery = new Intent(com.twofortyfouram.locale.Intent.ACTION_REQUEST_QUERY);
	    requestQuery.putExtra(com.twofortyfouram.locale.Intent.EXTRA_ACTIVITY,
                              com.github.treborrude.dolphintasker.ui.EventEditActivity.class.getCanonicalName());
	    context.sendBroadcast(requestQuery);
	  }
	
      @Override
	  public void onPageFinished(IWebView webView, String url)
	  {
		Log.d(TAG, "onPageFinished");
		eventDetected(Constants.PF_KEY, url);
		Log.d(TAG, "onPageFinished broadcast sent.");
	  }
	
	  @Override
	  public void onPageStarted(IWebView webView, String url)
	  {
		Log.d(TAG, "onPageStarted");
		eventDetected(Constants.PS_KEY, url);
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
		Log.d(TAG, "onReceiveTitle");
		eventDetected(Constants.RT_KEY, title);
	  }
	};
	
  private Downloads.DownloadClient mDownloadClient = 
    new Downloads.DownloadClient()
  {
	@Override
    public void onDownloadEnded(DownloadInfo downloadInfo)
	{
	  
	}
	
	@Override
	public boolean onDownloadStart(String url,
                                   String userAgent,
								   String contentDisposition,
								   String mimetype,
							       long contentLength)
	{
	  return false;
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
	  // I have no idea how to test whether or not this works,
	  // since this is a very unlikely case.
	  Log.e(TAG, "Unable to add page listener.", re);
	  showErrorDialog(browser, R.string.ed_pl_title, R.string.ed_pl_message);
	}
	try
	{
	  browser.downloads.registerDownloadClient(mDownloadClient);
	}
	catch (RemoteException re)
	{
	  Log.e(TAG, "Unable to add download client.");
	  showErrorDialog(browser, R.string.ed_dlc_title, R.string.ed_dlc_message);
	}
	try
	{
	  browser.addonBarAction.setTitle(getString(R.string.app_name));
	  Log.d(TAG, "Successfully set addon bar title.");
	  browser.addonBarAction.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.dolphintasker));
	  Log.d(TAG, "Successfully set addon bar icon.");
	  browser.addonBarAction.setOnClickListener(new OnClickListener()
	  {
		@Override
		public void onClick(Browser browser)
		{
		  // I considered using a dialog here, but I couldn't find
		  // a way to make the links in the InfoActivity work under
		  // RemoteViews. So I just launch the activity instead.
		  
		  Log.d(TAG, "OnClickListener.onClick()");
		  Intent launch_info = new Intent();
		  launch_info.setComponent(new ComponentName("com.github.treborrude.dolphintasker",
		                                             "com.github.treborrude.dolphintasker.ui.InfoActivity"));
		  launch_info.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		  DolphinTaskerService.this.startActivity(launch_info);
		}
	  });
	  browser.addonBarAction.show();
	}
	catch (RemoteException re)
	{
	  // This isn't critical, the plugin will still function.
	  Log.e(TAG, "Problem setting up AddonBar.");
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
	
	SharedPreferences.Editor rvEditor = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, 0).edit();
	rvEditor.clear();
	if (!rvEditor.commit())
	{
	  Log.e(TAG, "Unable to commit clear to SharedPreferences.");
	}
  }

  private void showErrorDialog(Browser browser,
                               int titleResId,
							   int messageResId)
  {
	AlertDialogBuilder errorDialog = new AlertDialogBuilder();
	errorDialog.setTitle(getString(titleResId));
	errorDialog.setIcon(BitmapFactory.decodeResource(getResources(),
													 R.drawable.dolphintasker));
	errorDialog.setMessage(getString(messageResId));
	try
	{
	  browser.window.showDialog(errorDialog);
	}
	catch (RemoteException re)
	{
	  Log.e(TAG, "Error showing error dialog.", re);
	}
  }
}
