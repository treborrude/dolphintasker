package com.github.treborrude.dolphintasker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.RemoteException;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;
import com.dolphin.browser.addons.AddonService;
import com.dolphin.browser.addons.AlertDialogBuilder;
import com.dolphin.browser.addons.Browser;
import com.dolphin.browser.addons.IHttpAuthHandler;
import com.dolphin.browser.addons.IWebView;
import com.dolphin.browser.addons.OnClickListener;
import com.dolphin.browser.addons.WebViews;

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
	  // TODO: Probably best to have an error dialog in this case, since
	  // the plugin won't function correctly without the page listener.
	  // Need to use browser.window.showDialog(). I have no idea how to
	  // test whether or not it works, though, since this is a very unlikely
	  // case.
	  Log.e(TAG, "Unable to add page listener.", re);
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
		  Log.d(TAG, "OnClickListener.onClick()");
		  AlertDialogBuilder info_dialog = new AlertDialogBuilder();
		  info_dialog.setTitle("About DolphinTasker");
		  info_dialog.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.dolphintasker));
		  RemoteViews info_view = new RemoteViews("com.github.treborrude.dolphintasker",
		                                          R.layout.info);
		  info_view.setTextViewText(R.id.description, Html.fromHtml(getString(R.string.long_description)));
		  info_view.setInt(R.id.description, "setBackgroundColor", android.graphics.Color.BLACK);
		  info_dialog.setView(info_view);
		  try
		  {
		    browser.window.showDialog(info_dialog);
		  }
		  catch (RemoteException re)
		  {
			Log.e(TAG, "Unable to show info dialog.", re);
		  }
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
}
