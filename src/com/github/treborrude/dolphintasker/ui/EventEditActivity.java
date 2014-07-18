package com.github.treborrude.dolphintasker.ui;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import com.github.treborrude.dolphintasker.R;
import android.view.View;
import android.widget.RadioGroup;
import com.github.treborrude.dolphintasker.TaskerPlugin;
import android.view.Menu;
import android.view.MenuItem;
import com.github.treborrude.dolphintasker.ui.EventSelectFragment;
import android.app.FragmentManager;

// Activity which is called by Tasker to configure DolphinTasker events.
// Tasker expects this activity to return a result. Due to that, and
// a desire to use a Fragment-based design, I felt it was easier to
// use the single-activity-multiple-fragments design, managing everything
// in code.

public class EventEditActivity 
  extends Activity 
  implements EventSelectFragment.EventTypeSelected
{

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
	super.onCreate(savedInstanceState);

    FragmentManager fragmentManager = getFragmentManager();

    fragmentManager.beginTransaction().add(new EventSelectFragment(), "event_select").commit();	
	final Bundle localeBundle = getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
	if (localeBundle == null)
	{
	  return;
	}
  }

  @Override
  public void onEventTypeSelected(String event_type)
  {
	// TODO: Launch details activity for selected event.
	String[] event_types = getResources().getStringArray(R.array.tasker_events);
  }
  
  public void endConfiguration(View view)
  {
	String event = null;
	String[] relevantVariables = null;
	
	if (event != null)
	{
	  final Intent resultIntent = new Intent();
	  final Bundle resultBundle = new Bundle();
	  
	  resultBundle.putString(com.github.treborrude.dolphintasker.Constants.CONDITION,
							 event);
	  resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, resultBundle);
	  resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, event);

	  if (TaskerPlugin.hostSupportsRelevantVariables(getIntent().getExtras()) &&
	      relevantVariables != null)
	  {
		TaskerPlugin.addRelevantVariableList(resultIntent, relevantVariables);
	  }
	  setResult(RESULT_OK, resultIntent);
	}
	
	finish();
  }

  
}
