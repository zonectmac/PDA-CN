package com.sfcservice.img;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class ImgLoad extends Activity implements OnClickListener,
		OnTouchListener {
	/** Called when the activity is first created. */
	private ImageView img;
	private Bitmap bitmap = null;
	private Button btnBig, btnSmall, btnClose;
	private Matrix m, saveMatrix;
	private LinearLayout lineLoadingImg;
	private TextView tvLoadingImg;
	private float i = 1;
	private int STATUS = -1;
	private float sx, sy = 0;
	private int w, h;
	private int iw, ih;
	private String urlImg = "";
	private long length, readCount;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				int a = (int) (((double) readCount / length) * 100);
				tvLoadingImg.setText(a + "");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				lineLoadingImg.setVisibility(View.INVISIBLE);
				bitmap = null;
				MyTool.toastShow(ImgLoad.this, "连接服务器失败");
				break;
			case MyConfig.RESULTS:
				lineLoadingImg.setVisibility(View.INVISIBLE);
				img.setImageBitmap(bitmap);
				m.postTranslate((w - bitmap.getWidth()) / 2,
						(h - bitmap.getHeight()) / 2);
				img.setImageMatrix(m);
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sfc_img_load);
		init();
	}

	public void init() {
		if (getIntent().getStringExtra(MyConfig.TAG).contains("http")) {
			urlImg = getIntent().getStringExtra(MyConfig.TAG);
		} else {
			urlImg = MyConfig.URL_PRE
					+ getIntent().getStringExtra(MyConfig.TAG);
		}
		img = (ImageView) findViewById(R.id.img);
		btnBig = (Button) findViewById(R.id.btnBig);
		btnSmall = (Button) findViewById(R.id.btnSmall);
		btnClose = (Button) findViewById(R.id.btnClose);
		lineLoadingImg = (LinearLayout) findViewById(R.id.line_loading_img);
		tvLoadingImg = (TextView) findViewById(R.id.tv_loading_img);
		btnBig.setOnClickListener(this);
		btnSmall.setOnClickListener(this);
		btnClose.setOnClickListener(this);
		img.setOnTouchListener(this);
		m = new Matrix();
		saveMatrix = new Matrix();
		w = getWindowManager().getDefaultDisplay().getWidth();
		h = getWindowManager().getDefaultDisplay().getHeight();
		loadImag();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (bitmap == null) {
			if (v.getId() == R.id.btnClose) {
				finish();
			}
			return;
		}
		iw = img.getWidth() / 2;
		ih = img.getHeight() / 2;
		switch (v.getId()) {
		case R.id.btnBig:
			if (STATUS == 1) {
				i = 1;
			}
			big();
			break;
		case R.id.btnSmall:
			if (STATUS == 0) {
				i = 1;
			}
			small();
			break;
		case R.id.btnClose:
			finish();
			break;
		default:
			break;
		}
	}

	public void big() {
		i += 0.3;
		m.postScale(i, i, iw, ih);
		img.setImageMatrix(m);
		STATUS = 0;
	}

	public void small() {
		if (iw <= 10 || ih <= 10) {
			return;
		}
		if (i <= 0) {
			i = 0;
			return;
		}
		i -= 0.3;
		m.postScale(i, i, iw, ih);
		img.setImageMatrix(m);
		STATUS = 1;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (bitmap == null) {
			return true;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			saveMatrix.set(m);
			sx = event.getX();
			sy = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			m.set(saveMatrix);
			m.postTranslate(event.getX() - sx, event.getY() - sy);
			img.setImageMatrix(m);
			break;
		default:
			break;
		}
		return true;
	}

	// private long getFileLength(HttpURLConnection hc) {
	// long nFileLength = -1;
	// for (int i = 1;; i++) {
	// String sHeader = hc.getHeaderFieldKey(i);
	// if (sHeader != null) {
	// if (sHeader.equals("Content-Length")) {
	// nFileLength = Long.parseLong(hc.getHeaderField(sHeader));
	// break;
	// }
	// } else {
	// break;
	// }
	// }
	//
	// return nFileLength;
	// }
	private void loadImag() {
		new Thread() {
			public void run() {
				try {
					URL url = new URL(urlImg);
					HttpURLConnection httpUrlConnection = (HttpURLConnection) url
							.openConnection();
					httpUrlConnection.setConnectTimeout(MyConfig.TIME_OUT);
					httpUrlConnection.setReadTimeout(MyConfig.TIME_OUT);
					httpUrlConnection.setDoOutput(true);
					httpUrlConnection.setDoInput(true);
					httpUrlConnection.setRequestMethod("POST");
					httpUrlConnection.setUseCaches(false);
					httpUrlConnection.setInstanceFollowRedirects(true);
					HttpURLConnection.setFollowRedirects(true);
					httpUrlConnection.setRequestProperty("Accept-Encoding",
							"identity");
					length = httpUrlConnection.getContentLength();
					InputStream is = httpUrlConnection.getInputStream();
					if (is != null) {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						byte buf[] = new byte[1024];
						while (true) {
							int numread = is.read(buf);
							if (numread <= 0) {
								// 完成
								baos.close();
								byte[] bytes = baos.toByteArray();
								bitmap = BitmapFactory.decodeByteArray(bytes,
										0, bytes.length);
								handler.sendEmptyMessage(MyConfig.RESULTS);
								break;
							}
							readCount += numread;
							baos.write(buf, 0, numread);
							handler.sendEmptyMessage(MyConfig.ACCESSS);
						}
					}
					is.close();
					httpUrlConnection.disconnect();
				} catch (Exception e) {
					handler.sendEmptyMessage(MyConfig.ACCESSF);
				}
			}

		}.start();
	}

}