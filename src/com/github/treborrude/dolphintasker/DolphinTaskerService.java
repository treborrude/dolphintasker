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

public class DolphinTaskerService extends AddonService
{
  private WebViews.PageListener mPageListener =
    new WebViews.PageListener() 
	{
      @Override
	  public void onPageFinished(IWebView webView, String url)
	  {
		Context appContext = DolphinTaskerService.this.getApplicationContext();
		Intent broadcastEvent = new Intent(appContext, QueryReceiver.class);
	    broadcastEvent.setAction(Constants.EVENT_DETECTED);
		broadcastEvent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_ACTIVITY,
		                        com.github.treborrude.dolphintasker.ui.EventEditActivity.class.getCanonicalName());
		broadcastEvent.setData(Uri.parse(url));
		
		appContext.sendBroadcast(broadcastEvent);
	  }

	  @Override
	  public void onPageStarted(IWebView webView, String url)
	  {
	    // TODO: Implement this method
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
	  }
	};
	
  @Override
  protected void onBrowserConnected(Browser browser) 
  {
	try
	{
	  browser.webViews.addPageListener(mPageListener);
	}
	catch (RemoteException re)
	{
	  // TODO: Figure out an appropriate thing to do.
	}
  }

  @Override
  protected void onBrowserDisconnected(Browser browser)
  {
	try
	{
	  browser.webViews.removePageListener(mPageListener);
	}
	catch (RemoteException re)
	{
	  // TODO: Figure out an appropriate thing to do.
	}
  }
}
