package com.github.treborrude.dolphintasker.ui;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import com.github.treborrude.dolphintasker.R;

public class PageFinishedEditActivity extends Activity
{

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
	// TODO: Implement this method
	super.onCreate(savedInstanceState);

	final Intent resultIntent = new Intent();
	final Bundle resultBundle = new Bundle();
	
	resultBundle.putString(com.github.treborrude.dolphintasker.Constants.CONDITION,
	                       getResources().getString(R.string.page_finished));
	resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, resultBundle);
	resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, "");

	setResult(RESULT_OK, resultIntent);
	setResult(Activity.RESULT_OK);
	finish();
  }
  
}
