package com.example.facialprocessing;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.qualcomm.snapdragon.sdk.face.FaceData;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing.PREVIEW_ROTATION_ANGLE;

public class MainActivity extends ActionBarActivity implements Camera.PreviewCallback {
	Camera cameraObj;
	FrameLayout preview;
	private CameraSurfaceView mPreview;
	private int Front_camera=1;
	private int Back_camera=0;
	private boolean _qcSDKenabled;
	FacialProcessing faceProc;
	Display display;
	private int displayangle;
	private int numFaces;
	FaceData[] facearray=null;
	DrawView drawview;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		display=((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		setContentView(R.layout.activity_main);
		preview=(FrameLayout) findViewById(R.id.cameraPreview);
		start_camera();
	}
	
	private void start_camera(){
		_qcSDKenabled=FacialProcessing.isFeatureSupported(FacialProcessing.FEATURE_LIST.FEATURE_FACIAL_PROCESSING);
		if(_qcSDKenabled && faceProc==null)
		{
			faceProc=FacialProcessing.getInstance();
		}
		else if(!_qcSDKenabled)
		{
			Toast.makeText(this, "Facial processing is not supported", Toast.LENGTH_LONG).show();
		}
		cameraObj=Camera.open(Front_camera);
		
		mPreview=new CameraSurfaceView(MainActivity.this, cameraObj);
		preview=(FrameLayout) findViewById(R.id.cameraPreview);
		preview.addView(mPreview);
		cameraObj.setPreviewCallback(MainActivity.this);
	}
	
	private void stop_camera(){
		if(cameraObj!=null)
		{
			cameraObj.stopPreview();
			cameraObj.setPreviewCallback(null);
			preview.removeView(mPreview);
			cameraObj.release();
			if(_qcSDKenabled)
			{
				faceProc.release();
				faceProc=null;
			}
		}
	}
	
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		stop_camera();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(cameraObj!=null)
		{
			stop_camera();
		}
		start_camera();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		int surfaceWidth=mPreview.getWidth();
		int surfaceHeight=mPreview.getHeight();
		int dRotation=display.getRotation();
		faceProc.normalizeCoordinates(surfaceWidth, surfaceHeight);
		PREVIEW_ROTATION_ANGLE angleEnum=PREVIEW_ROTATION_ANGLE.ROT_0;
		switch(dRotation){
		case 0:
			displayangle=90;
			angleEnum=PREVIEW_ROTATION_ANGLE.ROT_90;
			break;
		case 1:
			displayangle=0;
			angleEnum=PREVIEW_ROTATION_ANGLE.ROT_0;
			break;
		case 2:
			displayangle=270;
			angleEnum=PREVIEW_ROTATION_ANGLE.ROT_270;
			break;
		case 3:
			displayangle=180;
			angleEnum=PREVIEW_ROTATION_ANGLE.ROT_180;
			break;
		
		}
		cameraObj.setDisplayOrientation(displayangle);
		if(_qcSDKenabled)
		{
			if(faceProc==null)
			{
				faceProc=FacialProcessing.getInstance();
				
			}
			Parameters params=cameraObj.getParameters();
			Size preview_size=params.getPreviewSize();
			if(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE)
			{
				faceProc.setFrame(data, preview_size.width, preview_size.height, true, angleEnum);
			}
			else if(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT)
			{
				faceProc.setFrame(data, preview_size.width, preview_size.height, true, angleEnum);
			}
			
			numFaces=faceProc.getNumFaces();
			if(numFaces>0)
			{
				Toast.makeText(MainActivity.this, numFaces+" faces detected", Toast.LENGTH_SHORT).show();
				facearray=faceProc.getFaceData();
				preview.removeView(drawview);
				drawview=new DrawView(MainActivity.this, facearray, true);
				preview.addView(drawview);
			}
			else
			{
				preview.removeView(drawview);
				drawview=new DrawView(MainActivity.this, null, false);
				preview.addView(drawview);
			}
		}
		
	}

}
