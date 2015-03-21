package com.example.facialprocessing;

import android.content.Context;
import android.graphics.AvoidXfermode.Mode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.SurfaceView;

import com.qualcomm.snapdragon.sdk.face.FaceData;

public class DrawView extends SurfaceView{
	
	public FaceData[] mFaceArray;
	boolean _inFrame;
	private Paint LeftEyeBrush= new Paint();
	private Paint RightEyeBrush= new Paint();
	private Paint mouthBrush= new Paint();
	private Paint rectBrush= new Paint();
	public Point leftEye, rightEye,mouth;
	Rect mFaceRect;

	public DrawView(Context context, FaceData[] faceArray, boolean inFrame) {
		super(context);
		setWillNotDraw(false);
		mFaceArray=faceArray;
		_inFrame=inFrame;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(_inFrame)
		{
			for(int i=0;i<mFaceArray.length;i++)
			{
				if(mFaceArray[i].getLeftEyeBlink()>50)
				{
					LeftEyeBrush.setColor(Color.WHITE);
					canvas.drawCircle(mFaceArray[i].leftEye.x, mFaceArray[i].leftEye.y, 10f, LeftEyeBrush);
				}
				else
				{
				LeftEyeBrush.setColor(Color.RED);
				canvas.drawCircle(mFaceArray[i].leftEye.x, mFaceArray[i].leftEye.y, 5f, LeftEyeBrush);
				}
				
				if(mFaceArray[i].getRightEyeBlink()>50 )
				{
					RightEyeBrush.setColor(Color.WHITE);
					canvas.drawCircle(mFaceArray[i].rightEye.x, mFaceArray[i].rightEye.y, 10f, RightEyeBrush);
				}
				else
				{
				RightEyeBrush.setColor(Color.GREEN);
				canvas.drawCircle(mFaceArray[i].rightEye.x, mFaceArray[i].rightEye.y, 5f, RightEyeBrush);
				}
				
				
				setRectColor(mFaceArray[i],rectBrush);
				rectBrush.setStrokeWidth(2);
				rectBrush.setStyle(Paint.Style.STROKE);
				canvas.drawRect(mFaceArray[i].rect.left,mFaceArray[i].rect.top,mFaceArray[i].rect.right
						,mFaceArray[i].rect.bottom,rectBrush);
				
				
			}
		}
		else
		{
			canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
		}
	}
	
	private void setRectColor(FaceData faceData,Paint rectBrush)
	{
		if(faceData.getSmileValue()<40)
		{
			rectBrush.setColor(Color.RED);
		}
		else if(faceData.getSmileValue()<70)
		{
			rectBrush.setColor(Color.BLUE);
		}
		else
		{
			rectBrush.setColor(Color.BLACK);
		}
	}

}
