package com.github.treborrude.dolphintasker.ui;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import com.github.treborrude.dolphintasker.R;
import android.view.View;
import android.widget.RadioGroup;
import com.github.treborrude.dolphintasker.TaskerPlugin;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class EventEditActivity extends Activity
{
  private static final String LOG_TAG = "EventEditActivity";
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.event_edit);
	
	final Bundle localeBundle = getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
	if (localeBundle == null)
	{
	  return;
	}
	int event = localeBundle.getInt(com.github.treborrude.dolphintasker.Constants.EVENT_TYPE);
	RadioGroup eventtype = (RadioGroup) findViewById(R.id.eventtype);
	eventtype.check(event);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
	getMenuInflater().inflate(R.menu.event_edit_options, menu);
	return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
	if (item.getItemId() == R.id.mi_save)
	{
	  endConfiguration();
	  return true;
	}
	else if (item.getItemId() == R.id.mi_cancel)
	{
	  finish();
	  return true;
	}

	// Is this necessary? Would it be better to log
	// an error and pop up a dialog?
	return super.onOptionsItemSelected(item);
  }
  
  public void endConfiguration()
  {
	RadioGroup eventtype = (RadioGroup) findViewById(R.id.eventtype);
	int selected_event = eventtype.getCheckedRadioButtonId();
	
	if (selected_event == -1)
	{
	  Toast.makeText(this, R.string.em_select_option, Toast.LENGTH_SHORT).show();
	  return;
	}
	
	Resources resources = getResources();
	String selected_event_name = resources.getResourceEntryName(selected_event);
	String event_name = null;
	int rv_id = resources.getIdentifier("rv_" + selected_event_name,
										"array",
										getPackageName());
	String[] relevantVariables = null;
	
	if (rv_id != 0)
	{
	  relevantVariables = resources.getStringArray(rv_id);
	}
	else
	{
	  // There are some events that don't have any relevant variables
	  // (because Dolphin doesn't expose any information), so this is
	  // not necessarily an error. But I'll still log it just in case
	  // I ever forget relevant variables that should be there.
	  Log.d(LOG_TAG, String.format("Unable to find relevant variables for event type %s", selected_event_name));
	}
	
	try
	{
	  event_name = resources.getString(resources.getIdentifier("en_" + selected_event_name,
	                                                           "string",
	                                                           getPackageName()));
	}
	catch (Resources.NotFoundException rnfe)
	{
	  // TODO: Dialog box explaining that things are in a bad way?
	  Log.e(LOG_TAG, String.format("Unable to find event name resource for %s", selected_event_name), rnfe);
	}
		
	if (event_name != null)
	{
	  final Intent resultIntent = new Intent();
	  final Bundle resultBundle = new Bundle();
	  
	  resultBundle.putInt(com.github.treborrude.dolphintasker.Constants.EVENT_TYPE,
					      selected_event);
	  resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, resultBundle);
	  resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, event_name);

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
