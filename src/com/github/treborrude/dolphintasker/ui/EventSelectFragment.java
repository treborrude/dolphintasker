package com.github.treborrude.dolphintasker.ui;

import android.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.app.Activity;
import com.github.treborrude.dolphintasker.R;

public class EventSelectFragment extends ListFragment
{
  public interface EventTypeSelected
  {
	void onEventTypeSelected(String event_type, String event_name);
  }
  
  private EventTypeSelected mEventTypeSelectedCallback = null;
  
  @Override
  public void onListItemClick(ListView l, View v, int position, long id)
  {
	if (mEventTypeSelectedCallback != null)
	{
	  mEventTypeSelectedCallback.onEventTypeSelected(getResources().getStringArray(R.array.event_types)[position],
	                                                 getResources().getStringArray(R.array.tasker_events)[position]);
	}
	super.onListItemClick(l, v, position, id);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
	setListAdapter(ArrayAdapter.createFromResource(inflater.getContext(),
	                                               R.array.tasker_events,
												   android.R.layout.simple_list_item_1));
	return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override
  public void onAttach(Activity activity)
  {
	// I got this from the Android developer's web site.
	// Obviously, this only works well as long as each 
	// instance of this class can only be attached to a
	// single Activity. I'll assume that's true for now.
	try
	{
	  mEventTypeSelectedCallback = (EventTypeSelected)activity;
	}
	catch (ClassCastException cce)
	{
	  throw new ClassCastException(activity.toString() + "must implement EventTypeSelected interfae.");
	}
	super.onAttach(activity);
  }
  
}
