//
//  LFXSDKLightRandomColorActivity.java
//  LIFX
//
//  Created by Jarrod Boyes on 24/03/14.
//  Copyright (c) 2014 LIFX Labs. All rights reserved.
//

package com.example.lifx_sdk_samples;

import java.util.ArrayList;

import lifx.java.android.client.LFXClient;
import lifx.java.android.entities.LFXHSBKColor;
import lifx.java.android.entities.LFXTypes.LFXFuzzyPowerState;
import lifx.java.android.entities.LFXTypes.LFXPowerState;
import lifx.java.android.light.LFXLight;
import lifx.java.android.light.LFXLight.LFXLightListener;
import lifx.java.android.light.LFXLightCollection;
import lifx.java.android.light.LFXLightCollection.LFXLightCollectionListener;
import lifx.java.android.light.LFXTaggedLightCollection;
import lifx.java.android.network_context.LFXNetworkContext;
import lifx.java.android.network_context.LFXNetworkContext.LFXNetworkContextListener;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class LFXSDKLightRandomColorActivity extends Activity implements LFXLightListener, LFXLightCollectionListener, LFXNetworkContextListener
{
	private LFXNetworkContext networkContext;
	private LFXSDKLightListAdapter lightListAdapter;
	private LFXSDKTaggedLightCollectionListAdapter groupListAdapter;
	
	@Override
	protected void onCreate( Bundle savedInstanceState)
	{
		super.onCreate( savedInstanceState);
		setContentView( R.layout.lifx_list_sample_layout);
		
		networkContext = LFXClient.getSharedInstance( getApplicationContext()).getLocalNetworkContext();
		networkContext.addNetworkContextListener( this);
		networkContext.getAllLightsCollection().addLightCollectionListener( this);
		
		ListView lightListView = (ListView) findViewById( R.id.light_list_view);
		lightListAdapter = new LFXSDKLightListAdapter( this);
		lightListView.setAdapter( lightListAdapter);
		
		ListView groupListView = (ListView) findViewById( R.id.group_list_view);
		groupListAdapter = new LFXSDKTaggedLightCollectionListAdapter( this);
		groupListView.setAdapter( groupListAdapter);
		
		updateStateFromLIFX();
		
		lightListView.setOnItemClickListener( new OnItemClickListener()
		{
			@Override
			public void onItemClick( AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				LFXLight light = (LFXLight) lightListAdapter.getItem( position);
				
				LFXHSBKColor color = LFXHSBKColor.getColor( (float) (Math.random() * 360), 1.0f, 1.0f, 3500);
				light.setColor( color);
			}
		});
		
		groupListView.setOnItemClickListener( new OnItemClickListener()
		{
			@Override
			public void onItemClick( AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				LFXTaggedLightCollection lightCollection = (LFXTaggedLightCollection) groupListAdapter.getItem( position);
				
				LFXHSBKColor color = LFXHSBKColor.getColor( (float) (Math.random() * 360), 1.0f, 1.0f, 3500);
				lightCollection.setColor( color);
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu( Menu menu)
	{
		getMenuInflater().inflate( R.menu.sample, menu);
		return true;
	}
	
	@Override
	protected void onPause()
	{
		networkContext.removeNetworkContextListener( this);
		super.onPause();
	}
	
	private void updateStateFromLIFX()
	{	
		ArrayList<LFXLight> allLights = networkContext.getAllLightsCollection().getLights();
		lightListAdapter.updateWithLights( allLights);
		
		ArrayList<LFXTaggedLightCollection> lightCollections = networkContext.getTaggedLightCollections();
		groupListAdapter.updateWithLightCollections( lightCollections);
	}

	@Override
	public void networkContextDidConnect( LFXNetworkContext networkContext)
	{
		Toast toast = Toast.makeText( this, "CONNECTED", Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void networkContextDidDisconnect( LFXNetworkContext networkContext)
	{
		Toast toast = Toast.makeText( this, "DISCONNECTED", Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void networkContextDidAddTaggedLightCollection( LFXNetworkContext networkContext, LFXTaggedLightCollection collection) 
	{
		updateStateFromLIFX();
	}

	@Override
	public void networkContextDidRemoveTaggedLightCollection( LFXNetworkContext networkContext, LFXTaggedLightCollection collection) 
	{
		updateStateFromLIFX();
	}

	@Override
	public void lightCollectionDidAddLight( LFXLightCollection lightCollection, LFXLight light)
	{
		updateStateFromLIFX();
		light.addLightListener( this);
	}

	@Override
	public void lightCollectionDidRemoveLight( LFXLightCollection lightCollection, LFXLight light)
	{
		updateStateFromLIFX();
		light.removeLightListener( this);
	}

	@Override
	public void lightCollectionDidChangeLabel( LFXLightCollection lightCollection, String label) {}

	@Override
	public void lightCollectionDidChangeColor( LFXLightCollection lightCollection, LFXHSBKColor color) {}

	@Override
	public void lightCollectionDidChangeFuzzyPowerState( LFXLightCollection lightCollection, LFXFuzzyPowerState fuzzyPowerState) {}

	@Override
	public void lightDidChangeLabel( LFXLight light, String label)
	{
		updateStateFromLIFX();
	}

	@Override
	public void lightDidChangeColor( LFXLight light, LFXHSBKColor color)
	{
		updateStateFromLIFX();
	}

	@Override
	public void lightDidChangePowerState( LFXLight light, LFXPowerState powerState)
	{
		updateStateFromLIFX();
	}
}
