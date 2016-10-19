package com.sfcservice.img;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;

public class Img extends Activity implements OnClickListener, OnTouchListener {
	/** Called when the activity is first created. */
	private ImageView img;
	private TextView tvFShow;
	private Bitmap bitmap;
	private Button btnBig, btnSmall, btnClose;
	private Matrix m, saveMatrix;
	private float i = 1;
	private int STATUS = -1;
	private float sx, sy = 0;
	private int w, h;
	private int iw, ih;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sfc_img);
		init();
	}

	public void init() {
		bitmap = MyConfig.getMyConfig().getBitmap();
		if (bitmap == null) {
			tvFShow = (TextView) findViewById(R.id.tvFShow);
			tvFShow.setVisibility(View.VISIBLE);
			btnClose = (Button) findViewById(R.id.btnClose);
			btnClose.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			return;
		}
		img = (ImageView) findViewById(R.id.img);
		img.setImageBitmap(bitmap);
		btnBig = (Button) findViewById(R.id.btnBig);
		btnSmall = (Button) findViewById(R.id.btnSmall);
		btnClose = (Button) findViewById(R.id.btnClose);
		btnBig.setOnClickListener(this);
		btnSmall.setOnClickListener(this);
		btnClose.setOnClickListener(this);

		img.setOnTouchListener(this);
		m = new Matrix();
		saveMatrix = new Matrix();

		w = getWindowManager().getDefaultDisplay().getWidth();
		h = getWindowManager().getDefaultDisplay().getHeight();
		m.postTranslate((w - bitmap.getWidth()) / 2,
				(h - bitmap.getHeight()) / 2);
		img.setImageMatrix(m);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
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
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyConfig.getMyConfig().setBitmap(null);
	}
}