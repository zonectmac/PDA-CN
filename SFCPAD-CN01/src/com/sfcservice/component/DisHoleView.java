package com.sfcservice.component;

import java.util.ArrayList;

import com.sfcservice.bean.DisMoreItemBean;
import com.sfcservice.pda.config.MyConfig;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.View;

public class DisHoleView extends View {
	private int width;
	private int height;
	private int SIZE = 0;
	private ArrayList<DisMoreItemBean> listBean;

	public DisHoleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public DisHoleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public DisHoleView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void setData(ArrayList<DisMoreItemBean> listBean) {
		this.listBean = listBean;
		SIZE = listBean.size();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		width = 200;
		height = 200;
		setMeasuredDimension(widthMeasureSpec, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		// a=msg_account.getWidth();
		// b=msg_account.getHeight();
		// paint.setStrokeWidth(1);
		// paint.setColor(Color.BLACK);
		// canvas.drawLine(a/2, 0, a/2, b, paint);
		// canvas.drawLine(0, b/3, a, b/3, paint);
		// canvas.drawLine(0, 2*b/3, a, 2*b/3, paint);
		// 画矩形框（带颜色）
		Paint paintRect = new Paint();
		Paint paintRect1 = new Paint();
		paintRect.setColor(0xffe6e6e6);
		paintRect1.setColor(0xffffffff);
		if (SIZE == 0) {
			this.setVisibility(View.INVISIBLE);
			return;
		} else {
			this.setVisibility(View.VISIBLE);
		}

		int h = SIZE % 2 == 0 ? SIZE/2 : (SIZE/2+1);
		int l = 4;

		for (int i = 0; i < SIZE; i++) {
			
		}

		canvas.drawRect(0, 0, width * 1 / 10, height, paintRect);
		canvas.drawRect(width * 1 / 10, 0, width * 2 / 10, height, paintRect1);
		canvas.drawRect(width * 2 / 10, 0, width * 3 / 10, height, paintRect);
		canvas.drawRect(width * 3 / 10, 0, width * 4 / 10, height, paintRect1);
		canvas.drawRect(width * 4 / 10, 0, width * 5 / 10, height, paintRect);
		canvas.drawRect(width * 5 / 10, 0, width * 6 / 10, height, paintRect1);
		canvas.drawRect(width * 6 / 10, 0, width * 7 / 10, height, paintRect);
		canvas.drawRect(width * 7 / 10, 0, width * 8 / 10, height, paintRect1);
		canvas.drawRect(width * 8 / 10, 0, width * 9 / 10, height, paintRect);
		canvas.drawRect(width * 9 / 10, 0, width * 10 / 10, height, paintRect1);

		// //画文字
		Paint textPaint = new Paint();
		textPaint.setColor(0xff000000);
		textPaint.setAntiAlias(true);
		textPaint.setStrokeWidth(1);
		textPaint.setTextSize(height / 20 - 3);
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setTextAlign(Paint.Align.CENTER);
		FontMetrics fm = textPaint.getFontMetrics();

		for (int i = 0; i < 50; i++) {
			if (i >= 0 && i <= 9) {
				canvas.drawText("#" + (i + 1), width / 20,
						(height / 20 + (height / 10) * i)
								- (fm.ascent + fm.descent) / 2, textPaint);
			}
			if (i >= 10 && i <= 19) {
				canvas.drawText("#" + (i + 1), width / 20 + width / 5,
						(height / 20 + (height / 10) * (i - 10))
								- (fm.ascent + fm.descent) / 2, textPaint);
			}
			if (i >= 20 && i <= 29) {
				canvas.drawText("#" + (i + 1), width / 20 + width * 2 / 5,
						(height / 20 + (height / 10) * (i - 20))
								- (fm.ascent + fm.descent) / 2, textPaint);
			}
			if (i >= 30 && i <= 39) {
				canvas.drawText("#" + (i + 1), width / 20 + width * 3 / 5,
						(height / 20 + (height / 10) * (i - 30))
								- (fm.ascent + fm.descent) / 2, textPaint);
			}
			if (i >= 40 && i <= 49) {
				canvas.drawText("#" + (i + 1), width / 20 + width * 4 / 5,
						(height / 20 + (height / 10) * (i - 40))
								- (fm.ascent + fm.descent) / 2, textPaint);
			}
		}
		// 画数据
		textPaint.setColor(0xffffffff);
		Paint paintData = new Paint();
		paintData.setColor(0xffff8a00);
		if (listBean == null) {
			return;
		}
		for (int i = 0; i < listBean.size(); i++) {
			int location = Integer.parseInt(listBean.get(i).getLocation());

			int x = location % 10 == 0 ? (location / 10) : (location / 10 + 1);
			int y = location % 10 == 0 ? 10 : location % 10;
			canvas.drawRect((2 * x - 1) * width / 10, (y - 1) * height / 10,
					(2 * x - 1) * width / 10 + width / 10, (y - 1) * height
							/ 10 + height / 10, paintData);
			canvas.drawText(listBean.get(i).getCount(), (2 * x - 1) * width
					/ 10 + width / 20, (y - 1) * height / 10 + height / 20
					- (fm.ascent + fm.descent) / 2, textPaint);

		}
		// 画横线
		Paint paintLine = new Paint();
		paintLine.setColor(0xffffcccc);
		canvas.drawLine(0, height / 10, width, height / 10, paintLine);
		canvas.drawLine(0, height * 2 / 10, width, height * 2 / 10, paintLine);
		canvas.drawLine(0, height * 3 / 10, width, height * 3 / 10, paintLine);
		canvas.drawLine(0, height * 4 / 10, width, height * 4 / 10, paintLine);
		canvas.drawLine(0, height * 5 / 10, width, height * 5 / 10, paintLine);
		canvas.drawLine(0, height * 6 / 10, width, height * 6 / 10, paintLine);
		canvas.drawLine(0, height * 7 / 10, width, height * 7 / 10, paintLine);
		canvas.drawLine(0, height * 8 / 10, width, height * 8 / 10, paintLine);
		canvas.drawLine(0, height * 9 / 10, width, height * 9 / 10, paintLine);
		canvas.drawLine(0, height - 1, width, height - 1, paintLine);
	}

}
