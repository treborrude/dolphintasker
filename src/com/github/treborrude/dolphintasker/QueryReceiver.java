package com.github.treborrude.dolphintasker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.content.SharedPreferences;

public class QueryReceiver extends BroadcastReceiver
{
  private static String TAG = "QueryReceiver";
  
  @Override
  public void onReceive(Context context, Intent intent)
  {
	Log.d(TAG, "onReceive");
	SharedPreferences returnVals = context.getSharedPreferences(Constants.PREFS_NAME, 0);
	if (com.twofortyfouram.locale.Intent.ACTION_QUERY_CONDITION.equals(intent.getAction()))
	{
	  Log.d(TAG, "ACTION_QUERY_CONDITION");
	  String pfURL = returnVals.getString(Constants.PF_KEY, null);
	  if (context.getResources().getString(R.string.page_finished).equals(intent.getStringExtra(Constants.CONDITION)) &&
	      pfURL != null)
	  {
		Log.d(TAG, "Querying page finished, and have page finished url.");
		if (TaskerPlugin.Condition.hostSupportsVariableReturn(intent.getExtras())) 
		{
		  Bundle varsBundle = new Bundle();

		  varsBundle.putString("%dtpurl", pfURL);

		  TaskerPlugin.addVariableBundle(getResultExtras(true), varsBundle);
		}

		setResultCode(com.twofortyfouram.locale.Intent.RESULT_CONDITION_SATISFIED);
		returnVals.edit().remove(Constants.PF_KEY);
		returnVals.edit().commit();
	  }
	  else if (pfURL == null)
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
		returnVals.edit().putString(Constants.PF_KEY, intent.getData().toString());
		returnVals.edit().commit();
		
		if (returnVals.getString(Constants.PF_KEY, null) != null)
		{
		  Log.d(TAG, String.format("Got page finished URL of %s", returnVals.getString(Constants.PF_KEY, null)));
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
