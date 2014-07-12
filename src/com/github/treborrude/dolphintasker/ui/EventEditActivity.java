package com.github.treborrude.dolphintasker.ui;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import com.github.treborrude.dolphintasker.R;
import android.view.View;
import android.widget.RadioGroup;
import com.github.treborrude.dolphintasker.TaskerPlugin;

public class EventEditActivity extends Activity
{

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
	// TODO: Implement this method
	super.onCreate(savedInstanceState);
	setContentView(R.layout.event_edit);
	
	final Bundle localeBundle = getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
	if (localeBundle == null)
	{
	  return;
	}
	String event = localeBundle.getString(com.github.treborrude.dolphintasker.Constants.CONDITION);
	RadioGroup eventtype = (RadioGroup) findViewById(R.id.eventtype);
	
	if (getResources().getString(R.string.page_finished).equals(event))
	{
	  eventtype.check(R.id.page_finished);
	}
	else if (getResources().getString(R.string.page_started).equals(event))
	{
	  eventtype.check(R.id.page_started);
	}
	else if (getResources().getString(R.string.receive_title).equals(event))
	{
	  eventtype.check(R.id.receive_title);
	}
  }
  
  public void endConfiguration(View view)
  {
	RadioGroup eventtype = (RadioGroup) findViewById(R.id.eventtype);
	int selected_event = eventtype.getCheckedRadioButtonId();
	String event = null;
	String[] relevantVariables = null;
	
	if (selected_event == R.id.page_finished)
	{
	  event = getResources().getString(R.string.page_finished);
	  relevantVariables = new String[1];
	  relevantVariables[0] = getString(R.string.rv_pf_dtpurl);
	}
	else if (selected_event == R.id.page_started)
	{
	  event = getResources().getString(R.string.page_started);
	  relevantVariables = new String[1];
	  relevantVariables[0] = getString(R.string.rv_ps_dtpurl);
	}
	else if (selected_event == R.id.receive_title)
	{
	  event = getResources().getString(R.string.receive_title);
	  relevantVariables = new String[1];
	  relevantVariables[0] = getString(R.string.rv_rt_dtptitle);
	}
	
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
