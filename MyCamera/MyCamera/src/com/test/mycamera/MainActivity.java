package com.test.mycamera;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint3;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextWatcher;
import android.R.integer;
import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements
		CvCameraViewListener2 {

	private CameraBridgeViewBase mCameraView;

	private double d = 0, l = 0, scale = 0, refr =30.0,refl = 50.0;
	private double dSum = 0, lSum = 0, dsSum = 0,lsSum = 0;
	private boolean isCalculate = false;
	private boolean usingScale1 = true;
	private boolean mIsDetectStartC = false;
	private boolean mIsDetectStartS = false;
	private boolean isWorking = false;
	private int counter = 0;
	private long exitTime = 0;
	
	
	Mat mBgr;
	TextView showMeasureC;
	TextView showMeasureS;
	TextView catchScale;
	TextView calcuYuan, calcuFang, jieguo,showMeasureCS,showMeasureFS,jieguoS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		this.setTitle(R.string.biaoti);
		//��ֹ��Ļ����
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mCameraView = (CameraBridgeViewBase) findViewById(R.id.cameraView);
		// mCameraView.setMaxFrameSize(860,480);
		mCameraView.setVisibility(SurfaceView.VISIBLE);
		mCameraView.setCvCameraViewListener(this);
		// mCameraView.enableView();
		showMeasureC = (TextView) findViewById(R.id.showMeasureC);
		showMeasureS = (TextView) findViewById(R.id.showMeasureS);
		catchScale = (TextView) findViewById(R.id.catchScale);
		calcuYuan = (TextView) findViewById(R.id.catchYuan);
		calcuFang = (TextView) findViewById(R.id.catchFang);
		jieguo = (TextView) findViewById(R.id.jieguo);
		showMeasureCS = (TextView)findViewById(R.id.showMeasureCS);
		showMeasureFS  = (TextView)findViewById(R.id.showMeasureFS);
		jieguoS = (TextView)findViewById(R.id.jieguoS);
		//Ϊ��ñ����߰�ť�趨������ 
		catchScale.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (usingScale1)
				{ double radnow = DetectCircles.getRad();
					scale = refr / radnow; //���������
				}
				else{
					double lnow =  DetectSquares.getLength();
					scale = refl / lnow;
					}
					
					if(scale > 100)   scale = 0;
					catchScale.setText((double) Math.round(scale * 100) / 100
								+ "mmÿ����");
}
//					detectFirst = false;
//				} else
//					//��ʼ�����ټ���  ��ֹ��0 scale = NaN;
//					Toast.makeText(MainActivity.this, "�뿪ʼ�����ٵ��", Toast.LENGTH_SHORT).show();

		});

		final Handler myHandler = new Handler() {@Override
			public void handleMessage(Message msg) {

			if (msg.what == 0x1234) {
			d = DetectCircles.getRad() * 2;
			l = DetectSquares.getLength();
			if (isCalculate) {//�������ƽ��ֵ���㹦��
				calcuFang.setVisibility(View.VISIBLE);
				calcuYuan.setVisibility(View.VISIBLE);
				jieguo.setVisibility(View.VISIBLE);
				jieguoS.setVisibility(View.VISIBLE);
				if (l != 0 & d != 0) {//l d Ϊ0ʱ������ƽ��ֵ
					if (counter % 11 == 0) {//��11 �� ������
						calcuYuan.setText("ֱ��ƽ��ֵ"
								+ (double) Math.round(dSum / 10 * 100)
								/ 100);
						calcuFang.setText("�߳�ƽ��ֵ"
								+ (double) Math.round(lSum / 10 * 100)
								/ 100);
						jieguo.setText("ֱ��ƽ��ֵ"
								+ (double) Math.round(dSum / 10 * 100)
								/ 100 + "mm\n" + "�߳�ƽ��ֵ"
								+ (double) Math.round(lSum / 10 * 100)
								/ 100+"mm");
						jieguoS.setText("Բ���ƽ��ֵ"
								+ (double) Math.round(dsSum / 10 * 100)
								/ 100 + "mm2\n" + "���������ƽ��ֵ"
								+ (double) Math.round(lsSum / 10 * 100)
								/ 100+"mm2");
						counter = 0;
						dSum = 0;
						lSum = 0;
						dsSum = 0;
						lsSum = 0;
					} else {//��ʾ���ڼӵڼ�λ����
						calcuYuan
								.setText("����ֱ��ƽ��ֵ(" + (counter % 11) + ")");
						calcuFang
								.setText("����߳�ƽ��ֵ(" + (counter % 11) + ")");

						dSum += d * scale;
						lSum += l * scale;
						dsSum += 3.1415 *d*d/4.0*scale* scale ;
						lsSum += l*l*scale*scale;
								
					}
					counter++;
				}
			} else {//�رռ���ƽ��ֵ����
				calcuFang.setVisibility(View.INVISIBLE);
				calcuYuan.setVisibility(View.INVISIBLE);
				jieguo.setVisibility(View.INVISIBLE);
				jieguoS.setVisibility(View.INVISIBLE);
			}
			
			//ʵʱ��ʾ�������
				showMeasureC.setText("Բֱ��Ϊ��"
						+ (double) Math.round(d * scale * 100) / 100+"mm");
				//DetectCircles.changeRad2zero();
				showMeasureS.setText("�����α߳�Ϊ��"
						+ (double) Math.round(l * scale * 100) / 100+"mm");
				//DetectSquares.changeLength2zero();
				showMeasureCS.setText("Բ�����Ϊ��"
						+ (double) Math.round(3.1415 *d*d/4.0*scale* scale * 100) / 100+"mm2");
				showMeasureFS.setText("���������Ϊ��"
						+ (double) Math.round( l*l*scale*scale*100) / 100+"mm2");
			}
		}
	};				
		new Timer().schedule(new TimerTask() {
			//�趨��ʱ��  ÿ0.3�� ����
			@Override
			public void run() {
				// TODO Auto-generated method stub
				myHandler.sendEmptyMessage(0x1234);

			}
		}, 0, 300);
	}

	@SuppressLint("NewApi")
	@Override
	public void onPause() {
		if (mCameraView != null) {
			mCameraView.disableView();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this,
				mLoaderCallback);

	}

	@Override
	public void onDestroy() {
		if (mCameraView != null) {
			mCameraView.disableView();
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.detect) {
			if (isWorking) {//������ڼ��״̬ ֹͣ
				isWorking = false;
				mIsDetectStartC = false;
				mIsDetectStartS = false;
				item.setTitle("��ʼ���");
				l = 0;
				d = 0;
				Toast.makeText(this, "Stop Successfully", Toast.LENGTH_SHORT).show();
			} else {// ��֮ �������
				mIsDetectStartC = true;
				mIsDetectStartS = true;
				isWorking = true;

				item.setTitle("ȡ�����");
			}
		}
		if (id == R.id.detectCircles) {
			if (isWorking) {
				isWorking = false;
				mIsDetectStartC = false;
				mIsDetectStartS = false;
				item.setTitle("����Բ�μ��");
				Toast.makeText(this, "Stoped", Toast.LENGTH_SHORT).show();
			} else {
				mIsDetectStartC = true;
				isWorking = true;
				item.setTitle("ȡ��Բ�μ��");
			}

			return true;
		}
		if (id == R.id.detectSquares) {
			if (isWorking) {
				isWorking = false;
				mIsDetectStartC = false;
				mIsDetectStartS = false;
				item.setTitle("���·��μ��");
				Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();
			} else {
				mIsDetectStartS = true;
				isWorking = true;
				item.setTitle("ȡ�����μ��");
			}
			return true;
		}
		if (id == R.id.changeScale) {
			if (usingScale1) {
				usingScale1 = false;
				item.setTitle("ʹ��40mm������");
			} else {
				usingScale1 = true;
				item.setTitle("ʹ��60mmԲ");
			}
			return true;

		}
		if (id == R.id.calculate) {
			if (isCalculate) {
				isCalculate = false;
				item.setTitle("����ƽ��ֵ����");
			} else {
				isCalculate = true;
				item.setTitle("�ر�ƽ��ֵ����");
			}
			return true;

		}

		return super.onOptionsItemSelected(item);
	}

	//��opencvManager ��������
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
				mCameraView.enableView();
				mBgr = new Mat();
				break;
			default:
				super.onManagerConnected(status);
				break;
			}

		}
	};

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub

	}

	//��ÿ֡����ͼ����
	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// TODO Auto-generated method stub
		Mat image = inputFrame.rgba();
		if (isWorking) {
			//ͼ��Ԥ���� ת��Ϊ�Ҷ�ͼ �˲�
			Mat dstImage = image.clone();

			Imgproc.cvtColor(image, dstImage, Imgproc.COLOR_BGR2GRAY);
			// Imgproc.bilateralFilter(dstImage, dstImage, 25, 25*2, 25/2);
			// Imgproc.medianBlur(dstImage, dstImage,5);
			Imgproc.blur(dstImage, dstImage, new org.opencv.core.Size(5, 5));
			// Imgproc.GaussianBlur(dstImage, dstImage, new
			// org.opencv.core.Size(9, 9), 2, 2);
			// Imgproc.Canny(dstImage, dstImage, 50, 200);

			if (mIsDetectStartC) {
				//���Բ��
				Mat circles = new Mat();
				 DetectCircles.findCircles(dstImage, circles);
				 DetectCircles.drawCircles(image, circles);

			}

			if (mIsDetectStartS) {
				//���������
				
				List<MatOfPoint> squares = new ArrayList<MatOfPoint>();
				DetectSquares.findSquares(dstImage, squares);
				DetectSquares.drawSquares(image, squares);

			}

		}

		return image;
	}

	//ʵ���ٰ�һ���˳�
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
