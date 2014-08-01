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
  
  public void endConfiguration(View view)
  {
	RadioGroup eventtype = (RadioGroup) findViewById(R.id.eventtype);
	int selected_event = eventtype.getCheckedRadioButtonId();
	Resources resources = getResources();
	String selected_event_name = resources.getResourceEntryName(selected_event);
	String event_name = null;
	String[] relevantVariables = resources.getStringArray(resources.getIdentifier("rv_" + selected_event_name,
	                                                                              "array",
																				  getPackageName()));
	
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
	
	try
	{
	  relevantVariables = resources.getStringArray(resources.getIdentifier("rv_" + selected_event_name,
	                                               "array",
	                                               getPackageName()));
	}
	catch (Resources.NotFoundException rnfe)
	{
	  // There should always be at least an empty array,
	  // so this exception is an error.
	  // TODO: Dialog box explaining that "Variable Value" state contexts will not function,
	  // and asking for a bug report?
	  Log.e(LOG_TAG, String.format("Unable to find relevant variables resource for %s", selected_event_name), rnfe);
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
