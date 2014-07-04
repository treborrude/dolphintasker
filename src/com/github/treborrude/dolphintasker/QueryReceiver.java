package com.github.treborrude.dolphintasker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class QueryReceiver extends BroadcastReceiver
{
  private static String TAG = "QueryReceiver";
  
  private String mPageFinishedUrl = null;
  private String mPageStartedUrl = null;
  private String mReceivedTitle = null;
  
  @Override
  public void onReceive(Context context, Intent intent)
  {
	Log.d(TAG, "onReceive");
	if (com.twofortyfouram.locale.Intent.ACTION_QUERY_CONDITION.equals(intent.getAction()))
	{
	  Log.d(TAG, "ACTION_QUERY_CONDITION");
	  if (context.getResources().getString(R.string.page_finished).equals(intent.getStringExtra(Constants.CONDITION)) &&
	      mPageFinishedUrl != null)
	  {
		Log.d(TAG, "Querying page finished, and have page finished url.");
		if (TaskerPlugin.Condition.hostSupportsVariableReturn(intent.getExtras())) 
		{
		  Bundle varsBundle = new Bundle();

		  varsBundle.putString("%dtpurl", mPageFinishedUrl);

		  TaskerPlugin.addVariableBundle(getResultExtras(true), varsBundle);
		}

		setResultCode(com.twofortyfouram.locale.Intent.RESULT_CONDITION_SATISFIED);
		mPageFinishedUrl = null;
	  }
	  else if (mPageFinishedUrl == null)
	  {
		Log.d(TAG, "Do not have page finished url.");
	  }
	  else
	  {
		Log.d(TAG, String.format("Querying %s, not page finished.", intent.getStringExtra(Constants.CONDITION)));
	  }
	}
	else if (Constants.EVENT_DETECTED.equals(intent.getAction()))
	{
	  // TODO: Need to use something else to determine event type.
	  String eventActivity = intent.getStringExtra(com.twofortyfouram.locale.Intent.EXTRA_ACTIVITY);
	  Log.d(TAG, String.format("Received notification of %s event", eventActivity));
	  Intent requestQuery = new Intent(com.twofortyfouram.locale.Intent.ACTION_REQUEST_QUERY);
      requestQuery.putExtra(com.twofortyfouram.locale.Intent.EXTRA_ACTIVITY,
	                        eventActivity);
							
	  if (com.github.treborrude.dolphintasker.ui.EventEditActivity.class.getCanonicalName().equals(eventActivity))
	  {
		Log.d(TAG, "Retrieve page finished URL.");
		mPageFinishedUrl = intent.getData().toString();
		
		if (mPageFinishedUrl != null)
		{
		  Log.d(TAG, String.format("Got page finished URL of %s",mPageFinishedUrl));
		}
		else
		{
		  Log.w(TAG, "No page finished URL present!");
		}
	  }
	  
	  context.sendBroadcast(requestQuery);
	}
  }
}
