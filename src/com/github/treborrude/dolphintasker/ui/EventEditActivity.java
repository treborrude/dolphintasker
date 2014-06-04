package com.github.treborrude.dolphintasker.ui;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import com.github.treborrude.dolphintasker.R;
import android.view.View;
import android.widget.RadioGroup;

public class EventEditActivity extends Activity
{

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
	// TODO: Implement this method
	super.onCreate(savedInstanceState);
    setContentView(R.layout.event_edit);
  }
  
  public void endConfiguration(View view)
  {
	RadioGroup eventtype = (RadioGroup) findViewById(R.id.eventtype);
	int selected_event = eventtype.getCheckedRadioButtonId();
	String event = null;
	
	if (selected_event == R.id.page_finished)
	{
	  event = getResources().getString(R.string.page_finished);
	}
	else if (selected_event == R.id.page_started)
	{
	  event = getResources().getString(R.string.page_started);
	}
	else if (selected_event == R.id.receive_title)
	{
	  event = getResources().getString(R.string.receive_title);
	}
	
	if (event != null)
	{
	  final Intent resultIntent = new Intent();
	  final Bundle resultBundle = new Bundle();
	  
	  resultBundle.putString(com.github.treborrude.dolphintasker.Constants.CONDITION,
							 event);
	  resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, resultBundle);
	  resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, event);

	  setResult(RESULT_OK, resultIntent);
	  setResult(Activity.RESULT_OK);
	}
	
	finish();
  }
  
}
