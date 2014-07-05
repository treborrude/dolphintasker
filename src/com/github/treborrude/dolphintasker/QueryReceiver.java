package com.github.treborrude.dolphintasker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.content.SharedPreferences;
import android.content.res.Resources;

public class QueryReceiver extends BroadcastReceiver
{
  private static String TAG = "QueryReceiver";
  
  @Override
  public void onReceive(Context context, Intent intent)
  {
	Log.d(TAG, "onReceive");
	if (com.twofortyfouram.locale.Intent.ACTION_QUERY_CONDITION.equals(intent.getAction()))
	{
	  Log.d(TAG, "ACTION_QUERY_CONDITION");
	  if (verifyCondition(context, intent, Constants.PF_KEY, R.string.page_finished, "%dtpurl"))
	  {
		return;
	  }
	  if (verifyCondition(context, intent, Constants.PS_KEY, R.string.page_started, "%dtpurl"))
	  {
		return;
	  }
	  verifyCondition(context, intent, Constants.RT_KEY, R.string.receive_title, "%dtptitle");
	}
  }


  private boolean verifyCondition(Context context, Intent intent, String prefKey, int conditionKey, String varName) 
    throws Resources.NotFoundException
  {
	SharedPreferences returnVals = context.getSharedPreferences(Constants.PREFS_NAME, 0);
	String returnVal = returnVals.getString(prefKey, null);
	String conditionString = context.getResources().getString(conditionKey);
	if (conditionString.equals(intent.getStringExtra(Constants.CONDITION)) &&
		returnVal != null)
	{
	  Log.d(TAG, String.format("Querying %s, and have return value.", conditionString));
	  if (TaskerPlugin.Condition.hostSupportsVariableReturn(intent.getExtras())) 
	  {
		Bundle varsBundle = new Bundle();

		varsBundle.putString(varName, returnVal);

		TaskerPlugin.addVariableBundle(getResultExtras(true), varsBundle);
	  }

	  setResultCode(com.twofortyfouram.locale.Intent.RESULT_CONDITION_SATISFIED);
	  SharedPreferences.Editor rvEditor = returnVals.edit();
	  rvEditor.remove(prefKey);
	  if (!rvEditor.commit())
	  {
		Log.e(TAG, String.format("Unable to commit %s removal to SharedPreferences.", prefKey));
	  }
	  
	  return true;
	}
	
	return false;
  }
}
