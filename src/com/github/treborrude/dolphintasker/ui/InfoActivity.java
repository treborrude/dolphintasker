package com.github.treborrude.dolphintasker.ui;

import android.app.Activity;
import android.os.Bundle;
import com.github.treborrude.dolphintasker.R;
import android.widget.TextView;
import android.text.Html;
import android.text.method.LinkMovementMethod;

public class InfoActivity extends Activity
{
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.info);
	TextView description = (TextView)findViewById(R.id.description);
	description.setText(Html.fromHtml(getString(R.string.long_description)));
	description.setMovementMethod(LinkMovementMethod.getInstance());
  }
  
}
