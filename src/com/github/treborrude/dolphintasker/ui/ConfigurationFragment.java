package com.github.treborrude.dolphintasker.ui;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.github.treborrude.dolphintasker.R;

public class ConfigurationFragment extends Fragment
{
  private static final String LOG_TAG = "ConfigurationFragment";
  
  private Bundle mSavedConfiguration = null;
  private String mEventType = null;
  private String mEventName = null;
  
  public ConfigurationFragment(String event_type, String event_name)
  {
	mEventType = event_type;
	mEventName = event_name;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
	setHasOptionsMenu(true);
	super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
	// TODO: Inflate view based on getEventType().
	return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
  {
	inflater.inflate(R.menu.configuration_fragment_options, menu);
	super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
	if (item.getItemId() == R.id.mi_save)
	{
	  mSavedConfiguration = new Bundle();
	  onSaveInstanceState(mSavedConfiguration);
	  getActivity().finish();
	  return true;
	}
	else if (item.getItemId() == R.id.mi_cancel)
	{
	  // This is safe because it won't affect the state
  	  // already saved by Android or Tasker.
	  mSavedConfiguration = null;	  
	  getActivity().finish();
	  return true;
	}

	// Is this necessary? Would it be better to log
	// an error and pop up a dialog?
	return super.onOptionsItemSelected(item);
  }

  // Since the two functioons are very similar, this is intended
  // to save state for both the Android system and for Tasker.

  @Override
  public void onSaveInstanceState(Bundle outState)
  {
	// TODO: Implement this method
	super.onSaveInstanceState(outState);
  }  
  
  // Hook for dervied classes to do something different, if necessary.
  String getEventType()
  {
	return mEventType;
  }
  
  // Return the localized event name.
  String getEventName()
  {
	return mEventName;
  }
  
  Bundle getSavedConfiguration()
  {
	// Since this function is not public, I'll trust
	// the callers not to modify this configuraton.
	return mSavedConfiguration;
  }
  
  // These two items could be returned in the saved configuration
  // bundle, but there's no reason to persist their state, so
  // I elected to return them separately.
  
  // Return the "blurb" that provides a short description of the event
  // configuration. Default only returns event name. It is recommended
  // that derived classes provide a more specific implementation.
  String getBlurb()
  {
	return getEventName();
  }
  
  // Returns the relevant variables for the current event type.
  // Returns null if there are no relevant variables defined.
  String[] getRelevantVariables()
  {
	try
	{
	  return getResources().getStringArray(getResources().getIdentifier(getEventType() + "_relevant_variables",
	                                                                    "array",
                                                                        getActivity().getPackageName()));
	}
	catch (Resources.NotFoundException rnfe)
	{
	  Log.e(LOG_TAG, String.format("Unable to find relevant variables for %s", getEventType()), rnfe);
	}
	
	return null;
  }
}
