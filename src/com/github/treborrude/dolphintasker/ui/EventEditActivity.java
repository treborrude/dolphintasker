package com.github.treborrude.dolphintasker.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import com.github.treborrude.dolphintasker.R;
import com.github.treborrude.dolphintasker.TaskerPlugin;
import com.github.treborrude.dolphintasker.ui.EventSelectFragment;
import android.app.FragmentTransaction;
import android.util.Log;

// Activity which is called by Tasker to configure DolphinTasker events.
// Tasker expects this activity to return a result. Due to that, and
// a desire to use a Fragment-based design, I felt it was easier to
// use the single-activity-multiple-fragments design, managing everything
// in code.

public class EventEditActivity 
  extends Activity 
  implements EventSelectFragment.EventTypeSelected
{
  private static final String LOG_TAG = "EventEditActivity";
  
  private ConfigurationFragment mConfigurationFragment = null;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
	super.onCreate(savedInstanceState);
    setContentView(R.layout.event_edit_activity);

	// Prevent overlapping fragments.
	if (savedInstanceState != null)
	{
	  // As near as I can tell, if savedInstanceState is not null,
	  // Android is restoring this activity from a previous state,
	  // and it is not necessary to check the Intent that launched
	  // this Activity for Locale information.

	  return;
	}
	
	if (findViewById(R.id.fragment_container) != null)
	{	  
      getFragmentManager().beginTransaction()
	      .add(R.id.fragment_container, new EventSelectFragment(), "event_select").commit();
	}
	
	final Bundle localeBundle = getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
	// TODO: Use localeBundle to restore configuration fragment and selected
	// item state.
  }

  @Override
  public void onEventTypeSelected(String event_type, String event_name)
  {
	mConfigurationFragment = new ConfigurationFragment(event_type, event_name);
	
	int fragmentContainerId = 0;
	
	if (findViewById(R.id.fragment_container) != null)
	{
	  fragmentContainerId = R.id.fragment_container;
	}
	else if (findViewById(R.id.configuration_fragment) != null)
	{
	  fragmentContainerId = R.id.configuration_fragment;
	}
	else
	{
	  Log.e(LOG_TAG, "Unable to find container for configuration fragment!");
	  // TODO: Dialog box telling the user that things are badly broken.
	  finish();
	}
	
	FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
	fragmentTransaction.replace(fragmentContainerId, mConfigurationFragment, "configuration_fragment");
	fragmentTransaction.addToBackStack(null);
	fragmentTransaction.commit();
  }

  @Override
  public void finish()
  {
	Bundle savedConfiguration = 
	    (mConfigurationFragment != null) ? mConfigurationFragment.getSavedConfiguration() : null;
	
	if (savedConfiguration != null)
	{
	  final Intent resultIntent = new Intent();
	  
	  resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, savedConfiguration);
	  resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, mConfigurationFragment.getBlurb());

	  String[] relevantVariables = mConfigurationFragment.getRelevantVariables();
	  
	  if (TaskerPlugin.hostSupportsRelevantVariables(getIntent().getExtras()) &&
	      relevantVariables != null)
	  {
		TaskerPlugin.addRelevantVariableList(resultIntent, relevantVariables);
	  }
	  setResult(RESULT_OK, resultIntent);
	}
	
	super.finish();
  }  
}
