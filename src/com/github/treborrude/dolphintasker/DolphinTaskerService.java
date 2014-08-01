package com.github.treborrude.dolphintasker;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import com.dolphin.browser.addons.AddonService;
import com.dolphin.browser.addons.AlertDialogBuilder;
import com.dolphin.browser.addons.Browser;
import com.dolphin.browser.addons.DownloadInfo;
import com.dolphin.browser.addons.Downloads;
import com.dolphin.browser.addons.IHttpAuthHandler;
import com.dolphin.browser.addons.IWebView;
import com.dolphin.browser.addons.OnClickListener;
import com.dolphin.browser.addons.WebViews;

public class DolphinTaskerService extends AddonService
{
  private static final String LOG_TAG = "DolphinTaskerService";
  
  private void eventDetected(int eventType, Bundle eventData)
  {	
	Context context = DolphinTaskerService.this.getApplicationContext();

	Intent eventDetectedIntent = new Intent(Constants.EVENT_DETECTED,
											null,
											context,
											QueryReceiver.class);

	eventDetectedIntent.putExtra(Constants.EVENT_TYPE, eventType);
	eventDetectedIntent.putExtra(Constants.EVENT_DATA, eventData);

	context.sendBroadcast(eventDetectedIntent);
	Log.d(LOG_TAG, String.format("Sent broadcast for event type %s", getResources().getResourceEntryName(eventType)));
	Intent requestQuery = new Intent(com.twofortyfouram.locale.Intent.ACTION_REQUEST_QUERY);
	requestQuery.putExtra(com.twofortyfouram.locale.Intent.EXTRA_ACTIVITY,
						  com.github.treborrude.dolphintasker.ui.EventEditActivity.class.getCanonicalName());
	context.sendBroadcast(requestQuery);
  }

  private WebViews.PageListener mPageListener =
    new WebViews.PageListener() 
	{
	  private static final String LOG_TAG = "PageListener";
	
      @Override
	  public void onPageFinished(IWebView webView, String url)
	  {
		Log.d(LOG_TAG, "onPageFinished");
		Bundle eventData = new Bundle();
		eventData.putString("%dtpurl", url);
		eventDetected(R.id.page_finished, eventData);
	  }
	
	  @Override
	  public void onPageStarted(IWebView webView, String url)
	  {
		Log.d(LOG_TAG, "onPageStarted");
		Bundle eventData = new Bundle();
		eventData.putString("%dtpurl", url);
		eventDetected(R.id.page_started, eventData);
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
		Log.d(LOG_TAG, "onReceiveTitle");
		Bundle eventData = new Bundle();
		eventData.putString("%dtptitle", title);
		eventDetected(R.id.page_title_received, eventData);
	  }
	};
	
  private Downloads.DownloadClient mDownloadClient = 
    new Downloads.DownloadClient()
  {
	private static final String LOG_TAG = "DownloadClient";
	
	@Override
    public void onDownloadEnded(DownloadInfo downloadInfo)
	{
	  Log.d(LOG_TAG, "onDownloadEnded");
	  Bundle eventData = new Bundle();
	  eventData.putString("%dtpurl", downloadInfo.uri);
	  eventData.putLong("%dtpid", downloadInfo.id);
	  eventData.putString("%dtphint", downloadInfo.hint);
	  eventData.putString("%dtpfilename", downloadInfo.fileName);
	  eventData.putString("%dtpmt", downloadInfo.mimeType);
	  eventData.putInt("%dtpvisibility", downloadInfo.visibility);
	  eventData.putInt("%dtpstatus", downloadInfo.status);
	  eventData.putString("%dtpcookies", downloadInfo.cookies);
	  eventData.putString("%dtpua", downloadInfo.userAgent);
	  eventData.putString("%dtpreferer", downloadInfo.referer);
	  eventData.putInt("%dtptotalbytes", downloadInfo.totalBytes);
	  eventData.putString("%dtptitle", downloadInfo.title);
	  eventData.putString("%dtpdescription", downloadInfo.description);
	  eventDetected(R.id.download_finished, eventData);
	}
	
	@Override
	public boolean onDownloadStart(String url,
                                   String userAgent,
								   String contentDisposition,
								   String mimetype,
							       long contentLength)
	{
	  Log.d(LOG_TAG, "onDownloadStart");
	  Bundle eventData = new Bundle();
	  eventData.putString("%dtpurl", url);
	  eventData.putString("%dtpua", userAgent);
	  eventData.putString("%dtpcd", contentDisposition);
	  eventData.putString("%dtpmt", mimetype);
	  eventData.putLong("%dtplength", contentLength);
      eventDetected(R.id.download_started, eventData);
	  return false;
	}
  };

  @Override
  protected void onBrowserConnected(Browser browser) 
  {
	Log.d(LOG_TAG, "onBrowserConnected");
	try
	{
	  browser.webViews.addPageListener(mPageListener);
	  Log.d(LOG_TAG, "Successfully added page listener.");
	}
	catch (RemoteException re)
	{
	  // I have no idea how to test whether or not this works,
	  // since this is a very unlikely case.
	  Log.e(LOG_TAG, "Unable to add page listener.", re);
	  showErrorDialog(browser, R.string.ed_pl_title, R.string.ed_pl_message);
	}
	try
	{
	  browser.downloads.registerDownloadClient(mDownloadClient);
	  Log.d(LOG_TAG, "Successfully added DownloadClient.");
	}
	catch (RemoteException re)
	{
	  Log.e(LOG_TAG, "Unable to add download client.");
	  showErrorDialog(browser, R.string.ed_dlc_title, R.string.ed_dlc_message);
	}
	try
	{
	  browser.addonBarAction.setTitle(getString(R.string.app_name));
	  Log.d(LOG_TAG, "Successfully set addon bar title.");
	  browser.addonBarAction.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.dolphintasker));
	  Log.d(LOG_TAG, "Successfully set addon bar icon.");
	  browser.addonBarAction.setOnClickListener(new OnClickListener()
	  {
		@Override
		public void onClick(Browser browser)
		{
		  // I considered using a dialog here, but I couldn't find
		  // a way to make the links in the InfoActivity work under
		  // RemoteViews. So I just launch the activity instead.
		  
		  Log.d(LOG_TAG, "OnClickListener.onClick()");
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
	  Log.e(LOG_TAG, "Problem setting up AddonBar.");
	}
  }

  @Override
  protected void onBrowserDisconnected(Browser browser)
  {
	Log.d(LOG_TAG, "onBrowserDisconnected");
	try
	{
	  browser.webViews.removePageListener(mPageListener);
	  Log.d(LOG_TAG, "Successfully removed page listener.");
	}
	catch (RemoteException re)
	{
	  // No need for anything other than a log message in this case.
	  Log.e(LOG_TAG, "Unable to remove page listener.", re);
	}
	
	try
	{
	  browser.downloads.unregisterDownloadClient(mDownloadClient);
	}
	catch (RemoteException re)
	{
	  Log.e(LOG_TAG, "Unable to remove download client.", re);
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
	  Log.e(LOG_TAG, "Error showing error dialog.", re);
	}
  }
}
