package com.github.treborrude.dolphintasker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class QueryReceiver extends BroadcastReceiver
{
  private String mPageFinishedUrl = null;
  private String mPageStartedUrl = null;
  private String mReceivedTitle = null;
  
  @Override
  public void onReceive(Context context, Intent intent)
  {
	if (com.twofortyfouram.locale.Intent.ACTION_QUERY_CONDITION.equals(intent.getAction()))
	{
	  if (context.getResources().getString(R.string.page_finished).equals(intent.getStringExtra(Constants.CONDITION)) &&
	      mPageFinishedUrl != null)
	  {
		if (TaskerPlugin.Condition.hostSupportsVariableReturn(intent.getExtras())) 
		{
		  Bundle varsBundle = new Bundle();

		  varsBundle.putString("%dtpurl", mPageFinishedUrl);

		  TaskerPlugin.addVariableBundle(getResultExtras(true), varsBundle);
		}

		setResultCode(com.twofortyfouram.locale.Intent.RESULT_CONDITION_SATISFIED);
		mPageFinishedUrl = null;
	  }
	}
	else if (Constants.EVENT_DETECTED.equals(intent.getAction()))
	{
	  String eventActivity = intent.getStringExtra(com.twofortyfouram.locale.Intent.EXTRA_ACTIVITY);
	  Intent requestQuery = new Intent(com.twofortyfouram.locale.Intent.ACTION_REQUEST_QUERY);
      requestQuery.putExtra(com.twofortyfouram.locale.Intent.EXTRA_ACTIVITY,
	                        eventActivity);
							
	  if (com.github.treborrude.dolphintasker.ui.PageFinishedEditActivity.class.getCanonicalName().equals(eventActivity))
	  {
		mPageFinishedUrl = intent.getData().toString();		                      
	  }
	  
	  context.sendBroadcast(requestQuery);
	}
  }
}
