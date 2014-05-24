package com.github.treborrude.dolphintasker;

import com.dolphin.browser.addons.AddonService;
import com.dolphin.browser.addons.Browser;
import com.dolphin.browser.addons.WebViews;
import com.dolphin.browser.addons.IWebView;
import com.dolphin.browser.addons.IHttpAuthHandler;
import android.os.RemoteException;

public class DolphinTaskerService extends AddonService
{
  private WebViews.PageListener mPageListener =
    new WebViews.PageListener() 
	{
      @Override
	  public void onPageFinished(IWebView webView, String url)
	  {
	    // TODO: Save url in such a way that it can be retrieved
		//       by the BroadcastReceiver. And perhaps a token
		//       that indicates this was a "page finished" event.
		
		// TODO: Request that Tasker query the BroadcastReceiver.
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
