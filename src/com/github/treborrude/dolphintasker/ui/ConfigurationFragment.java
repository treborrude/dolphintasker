package com.github.treborrude.dolphintasker.ui;

import android.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import com.github.treborrude.dolphintasker.R;
import android.os.Bundle;
import android.view.MenuInflater;

public class ConfigurationFragment extends Fragment
{

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
	setHasOptionsMenu(true);
	super.onCreate(savedInstanceState);
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
	  // TODO: Decide how to communicate state.
	  return true;
	}
	else if (item.getItemId() == R.id.mi_cancel)
	{	  
	  getActivity().finish();
	  return true;
	}
	else
	{
	  // Is this necessary? Would it be better to log
	  // an error and pop up a dialog?
	  return super.onOptionsItemSelected(item);
	}
  }
  
}
