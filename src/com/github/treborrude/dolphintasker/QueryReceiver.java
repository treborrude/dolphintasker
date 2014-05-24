package com.github.treborrude.dolphintasker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class QueryReceiver extends BroadcastReceiver
{
  @Override
  public void onReceive(Context context, Intent intent)
  {
	if (com.twofortyfouram.locale.Intent.ACTION_QUERY_CONDITION.equals(intent.getAction()))
	{
	  // TODO: Somehow indicate the recently completed URL.
	  if (TaskerPlugin.Condition.hostSupportsVariableReturn(intent.getExtras())) 
      {
		Bundle varsBundle = new Bundle();

		varsBundle.putString( "%dtpurl", /*url*/ );

		TaskerPlugin.addVariableBundle( getResultExtras( true ), varsBundle );
	  }
      
      // Since this is an event plugin, we had to request Tasker to query us.
      // We only make that request if the condition is satisfied.
      
      setResultCode(com.twofortyfouram.locale.Intent.RESULT_CONDITION_SATISFIED);
	}
  }
}
