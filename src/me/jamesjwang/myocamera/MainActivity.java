/***
  Copyright (c) 2013 CommonsWare, LLC
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package me.jamesjwang.myocamera;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import com.commonsware.cwac.camera.ZoomTransaction;
import com.commonsware.cwac.camera.demo.R;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.scanner.ScanActivity;



public class MainActivity extends Activity implements
    ActionBar.OnNavigationListener, DemoCameraFragment.Contract {
  private static final String STATE_SELECTED_NAVIGATION_ITEM=
      "selected_navigation_item";
  private static final String STATE_SINGLE_SHOT="single_shot";
  private static final String STATE_LOCK_TO_LANDSCAPE=
      "lock_to_landscape";
  private static final int CONTENT_REQUEST=1337;
  private DemoCameraFragment std=null;
  private DemoCameraFragment ffc=null;
  private DemoCameraFragment current=null;
  private boolean hasTwoCameras=(Camera.getNumberOfCameras() > 1);
  private boolean singleShot=false;
  private boolean isLockedToLandscape=false;
  private static boolean videoMode = false;
  private SeekBar zoom=null;
  
  private DeviceListener mListener = new
          AbstractDeviceListener() {
              @Override
              public void onPose(Myo myo, long timestamp, Pose pose) {
                  //super.onPose(myo, timestamp, pose);
            	  Toast.makeText(getApplicationContext(), "Unlocked", Toast.LENGTH_SHORT);
            	  if(current !=null && current.getView()!=null)
            	  {
            		  zoom = (SeekBar)current.getView().findViewById(R.id.zoom);
            	  }
            	 // Handler seekBarHandler = new Handler();
                  switch (pose) {
                      case FIST:
                          Toast.makeText(getApplicationContext(),
                                  "Fist sensed!", Toast.LENGTH_SHORT)
                                  .show();
                          if(!videoMode)
                          {
                          current.takePicture();
                          Toast.makeText(getApplicationContext(),
                                  "Picture Taken!", Toast.LENGTH_SHORT)
                                  .show();
                          }
						else
							try {
								current.record();
								 Toast.makeText(getApplicationContext(),
		                                  "Starting Recording!", Toast.LENGTH_SHORT)
		                                  .show();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                         
                      case FINGERS_SPREAD:
                          Toast.makeText(getApplicationContext(), "Hand Sensed", Toast.LENGTH_SHORT).show();
                          if(current.isRecording())
							try {
								current.stopRecording();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						else{
                          if (current.equals(ffc)) {
                              std=DemoCameraFragment.newInstance(false);
                            current=std;
                            
                          }
                          else {
                        	ffc=DemoCameraFragment.newInstance(true);
                            current=ffc;
                            
                          }
                          getFragmentManager().beginTransaction()
                          .replace(R.id.container, current).commit();
                          }
                      case WAVE_IN:
                    	  switch (myo.getArm()){
                    	  	case LEFT:
                    	  		
                    	  		Toast.makeText(getApplicationContext(), "Right Wave Sensed", Toast.LENGTH_SHORT).show();
                    	  		if(zoom!=null)
                    	  		{
                    	  		zoom.incrementProgressBy(20);
                    	  		if(current.getView()!=null)
                    	  		updateZoom();
                    	  		}
                    	  		//Log.i("zoom", "progress: " + zoom.getProgress());
                    	  		//ZoomTransaction z3 = current.zoomTo(1);
                    	  		//z3.go();
                    	  	case RIGHT:
                    	  		Toast.makeText(getApplicationContext(), "Left Wave Sensed", Toast.LENGTH_SHORT).show();
                    	  		if(zoom!=null){
                    	  		if(zoom.getProgress() < 20)
                      			  zoom.setProgress(0);
                    	  		else
                    	  		zoom.incrementProgressBy(-20);
                    	  		if(current!=null &&current.getView()!=null)
                    	  		updateZoom();
                    	  		}
                    	  		//Log.i("zoom", "progress: " + zoom.getProgress());
                    	  		
    
                    	  		Toast.makeText(getApplicationContext(), "Progress:", Toast.LENGTH_LONG);
                    	  		//ZoomTransaction z4 = current.zoomTo(1);
                    	  		//z4.go();
                    	  }
                     case WAVE_OUT:
                    	  switch(myo.getArm()){
                    	  case LEFT:
                    		  Toast.makeText(getApplicationContext(), "Left Wave Sensed", Toast.LENGTH_SHORT).show();
                    		  if(zoom!=null){
                    		  if(zoom.getProgress() < 20)
                    			  zoom.setProgress(0);
                    		  else
                  	  			zoom.incrementProgressBy(-20);
                    		  
                    		  if(current.getView()!=null)
                    		  updateZoom();
                    		  }
                    		  
                    		  //ZoomTransaction z1 = current.zoomTo(1);
                    		  //z1.go();
                    	  case RIGHT:
                    		  Toast.makeText(getApplicationContext(), "Right Wave Sensed", Toast.LENGTH_SHORT).show();
                    		  //current.onProgressChanged(zoom, zoom.getProgress()+20, true);
                    		  if(zoom!=null)
                    		  {
                  	  			zoom.incrementProgressBy(20);
                  	  		if(current.getView()!=null)
                  	  			updateZoom();
                    		  }
                  	  		//Log.i("zoom", "progress: " + zoom.getProgress());
                    		  //ZoomTransaction z2 = current.zoomTo(1);
                  	  		  //z2.go();
                    	  }

      findViewById(android.R.id.content).post(new Runnable() {
        @Override
        public void run() {
          current.lockToLandscape(isLockedToLandscape);
        }
      });
                          
                  }
              }
          };
  public void updateZoom()
  {
	  if(current !=null &&zoom!=null)
		  current.onProgressChanged(zoom, 0,true);
	  /*zoom.setEnabled(false);
	   current.zoomTo(zoom.getProgress()).onComplete(new Runnable() {
	          @Override
	          public void run() {
	            zoom.setEnabled(true);
	          }
	        }).go();*/
  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    if (hasTwoCameras) {
      final ActionBar actionBar=getActionBar();

      actionBar.setDisplayShowTitleEnabled(false);
      actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

      ArrayAdapter<CharSequence> adapter=
          ArrayAdapter.createFromResource(actionBar.getThemedContext(),
                                          R.array.nav,
                                          android.R.layout.simple_list_item_1);

      actionBar.setListNavigationCallbacks(adapter, this);
    }
    else {
      current=DemoCameraFragment.newInstance(false);

      getFragmentManager().beginTransaction()
                          .replace(R.id.container, current).commit();
    }
    Hub hub = Hub.getInstance();
    if (!hub.init(this, getPackageName())) {
        // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
        Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
        finish();
        return;
    }
	hub.addListener(mListener);
    
    //register device listener
    Intent intent = new Intent(this, ScanActivity.class);
    this.startActivity(intent);

  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    if (hasTwoCameras) {
      if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
        getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
      }
    }

    setSingleShotMode(savedInstanceState.getBoolean(STATE_SINGLE_SHOT));
    isLockedToLandscape=
        savedInstanceState.getBoolean(STATE_LOCK_TO_LANDSCAPE);

    if (current != null) {
      current.lockToLandscape(isLockedToLandscape);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    if (hasTwoCameras) {
      outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                      getActionBar().getSelectedNavigationIndex());
    }

    outState.putBoolean(STATE_SINGLE_SHOT, isSingleShotMode());
    outState.putBoolean(STATE_LOCK_TO_LANDSCAPE, isLockedToLandscape);
  }

  @Override
  public boolean onNavigationItemSelected(int position, long id) {
    if (position == 0) {
      if (std == null) {
        std=DemoCameraFragment.newInstance(false);
        //zoom = (SeekBar)std.getView().findViewById(R.id.zoom);
      }

      current=std;
      
    }
    else {
      if (ffc == null) {
        ffc=DemoCameraFragment.newInstance(true);
        //zoom = (SeekBar)ffc.getView().findViewById(R.id.zoom);
      }

      current=ffc;
    }

    getFragmentManager().beginTransaction()
                        .replace(R.id.container, current).commit();

    findViewById(android.R.id.content).post(new Runnable() {
      @Override
      public void run() {
        current.lockToLandscape(isLockedToLandscape);
      }
    });

    return(true);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    new MenuInflater(this).inflate(R.menu.main, menu);

    menu.findItem(R.id.landscape).setChecked(isLockedToLandscape);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.content) {
     Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      File dir=
          Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
      File output=new File(dir, "CameraContentDemo.jpeg");

      i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));

      startActivityForResult(i, CONTENT_REQUEST);

    }
    else if (item.getItemId() == R.id.landscape) {
      item.setChecked(!item.isChecked());
      current.lockToLandscape(item.isChecked());
      isLockedToLandscape=item.isChecked();
    }
    else if (item.getItemId() == R.id.fullscreen) {
      startActivity(new Intent(this, FullScreenActivity.class));
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    if (requestCode == CONTENT_REQUEST) {
      if (resultCode == RESULT_OK) {
        // do nothing
      }
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_CAMERA && current != null
        && !current.isSingleShotProcessing()) {
      current.takePicture();

      return(true);
    }

    return(super.onKeyDown(keyCode, event));
  }

  @Override
  public boolean isSingleShotMode() {
    return(singleShot);
  }

  @Override
  public void setSingleShotMode(boolean mode) {
    singleShot=mode;
  }
public static void setVideoMode() {
	// TODO Auto-generated method stub
	if(videoMode == false)
		videoMode = true;
	else
		videoMode=false;
	
}
}
