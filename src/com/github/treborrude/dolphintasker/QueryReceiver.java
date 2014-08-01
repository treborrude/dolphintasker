package com.github.treborrude.dolphintasker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

public class QueryReceiver extends BroadcastReceiver
{
  private static String LOG_TAG = "QueryReceiver";
  private static Map<Integer, Bundle> mDetectedEvents = new HashMap<Integer, Bundle>();
  
  @Override
  public void onReceive(Context context, Intent intent)
  {
	Log.d(LOG_TAG, "onReceive");
	if (com.twofortyfouram.locale.Intent.ACTION_QUERY_CONDITION.equals(intent.getAction()))
	{
	  int event_type = intent.getIntExtra(Constants.EVENT_TYPE, 0);
	  Log.d(LOG_TAG, String.format("ACTION_QUERY_CONDITION for event type %s", context.getResources().getResourceEntryName(event_type)));
	  Bundle return_vars = mDetectedEvents.get(event_type);

	  if (return_vars != null)
	  {
		Log.d(LOG_TAG, "Found return variables for event");
		if (TaskerPlugin.Condition.hostSupportsVariableReturn(intent.getExtras())) 
		{
		  TaskerPlugin.addVariableBundle(getResultExtras(true), return_vars);
		}

		setResultCode(com.twofortyfouram.locale.Intent.RESULT_CONDITION_SATISFIED);
		
		mDetectedEvents.remove(event_type);
	  }
	}
	else if (Constants.EVENT_DETECTED.equals(intent.getAction()))
	{
	  int event_type = intent.getIntExtra(Constants.EVENT_TYPE, 0);
	  if (event_type != 0)
	  {
		Log.d(LOG_TAG, String.format("Received EVENT_DETECTED broadcast for event type %s", context.getResources().getResourceEntryName(event_type)));
		Bundle return_vars = intent.getBundleExtra(Constants.EVENT_DATA);
		if (return_vars != null)
		{
		  Log.d(LOG_TAG, "Install return vars in mDetectedEvents");
	      mDetectedEvents.put(event_type, return_vars);
		}
		else
		{
		  Log.d(LOG_TAG, "No return vars found for event!");
		}
	  }
	  else
	  {
		Log.e(LOG_TAG, "Received EVENT_DETECTED broadcast without valid EVENT_TYPE.");
	  }
	}
  }
}
