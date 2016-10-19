package com.sfcservice.component;

import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.sfcservice.pda.R;

public class SFCDateDialog implements OnLongClickListener, OnTouchListener {

	private Context context;
	private Dialogcallback dialogcallback;
	private Dialog dialog;
	private Button sure, cancel;
	private Button top1, top2, top3, top4, top5;
	private Button bot1, bot2, bot3, bot4, bot5;
	private TextView tv1, tv2, tv3, tv4, tv5;
	private int a1, a2, a3, a4, a5;
	private View tempView;
	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			dealTime(tempView);
			handler.postDelayed(runnable, 100);
		}
	};

	/**
	 * init the dialog
	 * 
	 * @return
	 */
	public SFCDateDialog(Context con) {
		this.context = con;
		dialog = new Dialog(context, R.style.SFCDialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.sfc_dis_time);

		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialogInterface, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					dialog.dismiss();
					return true;
				}
				if (keyCode == 66 && event.getAction() == KeyEvent.ACTION_DOWN) {
					dealTime(dialog.getCurrentFocus());
				}
				if (keyCode == 66 && event.getAction() == KeyEvent.ACTION_UP) {
					handler.removeCallbacks(runnable);
				}
				return false;
			}
		});

		sure = (Button) dialog.findViewById(R.id.btnConfirm);
		cancel = (Button) dialog.findViewById(R.id.btnCancel);
		top1 = (Button) dialog.findViewById(R.id.top1);
		top2 = (Button) dialog.findViewById(R.id.top2);
		top3 = (Button) dialog.findViewById(R.id.top3);
		top4 = (Button) dialog.findViewById(R.id.top4);
		top5 = (Button) dialog.findViewById(R.id.top5);

		bot1 = (Button) dialog.findViewById(R.id.bot1);
		bot2 = (Button) dialog.findViewById(R.id.bot2);
		bot3 = (Button) dialog.findViewById(R.id.bot3);
		bot4 = (Button) dialog.findViewById(R.id.bot4);
		bot5 = (Button) dialog.findViewById(R.id.bot5);

		tv1 = (TextView) dialog.findViewById(R.id.tv1);
		tv2 = (TextView) dialog.findViewById(R.id.tv2);
		tv3 = (TextView) dialog.findViewById(R.id.tv3);
		tv4 = (TextView) dialog.findViewById(R.id.tv4);
		tv5 = (TextView) dialog.findViewById(R.id.tv5);

		Calendar calendar = Calendar.getInstance();
		a1 = calendar.get(Calendar.YEAR);
		a2 = calendar.get(Calendar.MONTH) + 1;
		a3 = calendar.get(Calendar.DAY_OF_MONTH);
		a4 = calendar.get(Calendar.HOUR_OF_DAY);
		a5 = calendar.get(Calendar.MINUTE);

		showTime(a1, a2, a3, a4, a5);

		top1.setOnLongClickListener(this);
		top2.setOnLongClickListener(this);
		top3.setOnLongClickListener(this);
		top4.setOnLongClickListener(this);
		top5.setOnLongClickListener(this);

		bot1.setOnLongClickListener(this);
		bot2.setOnLongClickListener(this);
		bot3.setOnLongClickListener(this);
		bot4.setOnLongClickListener(this);
		bot5.setOnLongClickListener(this);

		top1.setOnTouchListener(this);
		top2.setOnTouchListener(this);
		top3.setOnTouchListener(this);
		top4.setOnTouchListener(this);
		top5.setOnTouchListener(this);

		bot1.setOnTouchListener(this);
		bot2.setOnTouchListener(this);
		bot3.setOnTouchListener(this);
		bot4.setOnTouchListener(this);
		bot5.setOnTouchListener(this);
		//
		// top1.setOnClickListener(this);
		// top2.setOnClickListener(this);
		// top3.setOnClickListener(this);
		// top4.setOnClickListener(this);
		// top5.setOnClickListener(this);
		//
		// bot1.setOnClickListener(this);
		// bot2.setOnClickListener(this);
		// bot3.setOnClickListener(this);
		// bot4.setOnClickListener(this);
		// bot5.setOnClickListener(this);

		sure.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				dialogcallback.btnConfirm();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
				dialogcallback.btnCancel();
			}
		});
	}

	public interface Dialogcallback {
		public void btnConfirm();

		public void btnCancel();

		public boolean exitActivity();
	}

	public void setDialogCallback(Dialogcallback dialogcallback) {
		this.dialogcallback = dialogcallback;
	}

	public void show() {
		top4.requestFocus();
		top4.setFocusable(true);
		dialog.show();
	}

	public void hide() {
		dialog.hide();
	}

	public void dismiss() {
		dialog.dismiss();
	}

	public boolean isShowing() {
		if (dialog.isShowing()) {
			return true;
		}
		return false;
	}

	public String getTime() {
		String str = tv1.getText().toString() + "-" + tv2.getText().toString()
				+ "-" + tv3.getText().toString() + " "
				+ tv4.getText().toString() + ":" + tv5.getText().toString();
		return str;
	}

	private void showTime(int a1, int a2, int a3, int a4, int a5) {
		String str2 = String.format("%02d", a2);
		String str3 = String.format("%02d", a3);
		String str4 = String.format("%02d", a4);
		String str5 = String.format("%02d", a5);
		tv1.setText(a1 + "");
		tv2.setText(str2);
		tv3.setText(str3);
		tv4.setText(str4);
		tv5.setText(str5);
	}

	private void changedA1(boolean bool) {
		if (bool) {
			a1++;
			if (a1 > 2050) {
				a1 = 1990;
			}
		} else {
			a1--;
			if (a1 < 1990) {
				a1 = 2050;
			}
		}
		if (a2 == 2) {
			// 闰年2月
			if ((a1 % 4 == 0 && a1 % 100 != 0) || (a1 % 400 == 0)) {
				if (a3 == 30 || a3 == 31) {
					a3 = 29;
				}
				showTime(a1, a2, a3, a4, a5);
			}
			// 平年2月
			else {
				if (a3 == 29 || a3 == 30 || a3 == 31) {
					a3 = 28;
				}
				showTime(a1, a2, a3, a4, a5);
			}
			return;
		}
		showTime(a1, a2, a3, a4, a5);
	}

	private void changedA2(boolean bool) {
		if (bool) {
			a2++;
			if (a2 > 12) {
				a2 = 1;
			}
		} else {
			a2--;
			if (a2 < 1) {
				a2 = 12;
			}
		}
		// 小月
		if (a2 == 2 || a2 == 4 || a2 == 6 || a2 == 9 || a2 == 11) {
			// 处理2月份
			if (a2 == 2) {
				if ((a1 % 4 == 0 && a1 % 100 != 0) || (a1 % 400 == 0)) {
					if (a3 == 30 || a3 == 31) {
						a3 = 29;
					}
					showTime(a1, a2, a3, a4, a5);
				} else {
					if (a3 == 29 || a3 == 30 || a3 == 31) {
						a3 = 28;
					}
					showTime(a1, a2, a3, a4, a5);
				}
				return;
			}
			// 处理其他小月份
			if (a3 == 31) {
				a3 = 30;
			}
			showTime(a1, a2, a3, a4, a5);
			return;

		}
		// 大月
		else {
			showTime(a1, a2, a3, a4, a5);
		}
	}

	private void changedA3(boolean bool) {
		// 获取当前月份的最大值
		int bigDay = 0;
		if (a2 == 2) {
			// 闰年2月
			if ((a1 % 4 == 0 && a1 % 100 != 0) || (a1 % 400 == 0)) {
				bigDay = 29;
			}
			// 平年2月
			else {
				bigDay = 28;
			}
		}
		if (a2 == 4 || a2 == 6 || a2 == 9 || a2 == 11) {
			bigDay = 30;
		}
		if (a2 == 1 || a2 == 3 || a2 == 5 || a2 == 7 || a2 == 8 || a2 == 10
				|| a2 == 12) {
			bigDay = 31;
		}
		if (bool) {
			a3++;
			if (a3 > bigDay) {
				a3 = 1;
			}
		} else {
			a3--;
			if (a3 < 1) {
				a3 = bigDay;
			}
		}
		showTime(a1, a2, a3, a4, a5);
	}

	private void changedA4(boolean bool) {
		if (bool) {
			a4++;
			if (a4 > 23) {
				a4 = 0;
			}
		} else {
			a4--;
			if (a4 < 0) {
				a4 = 23;
			}
		}
		showTime(a1, a2, a3, a4, a5);
	}

	private void changedA5(boolean bool) {
		if (bool) {
			a5++;
			if (a5 > 59) {
				a5 = 0;
			}
		} else {
			a5--;
			if (a5 < 0) {
				a5 = 59;
			}
		}
		showTime(a1, a2, a3, a4, a5);
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		tempView = v;
		handler.postDelayed(runnable, 500);
		return true;
	}

	private void dealTime(View v) {
		switch (v.getId()) {
		case R.id.top1:
			changedA1(true);
			break;
		case R.id.top2:
			changedA2(true);
			break;
		case R.id.top3:
			changedA3(true);
			break;
		case R.id.top4:
			changedA4(true);
			break;
		case R.id.top5:
			changedA5(true);
			break;
		case R.id.bot1:
			changedA1(false);
			break;
		case R.id.bot2:
			changedA2(false);
			break;
		case R.id.bot3:
			changedA3(false);
			break;
		case R.id.bot4:
			changedA4(false);
			break;
		case R.id.bot5:
			changedA5(false);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			dealTime(v);
			break;
		case MotionEvent.ACTION_OUTSIDE:
		case MotionEvent.ACTION_UP:
			handler.removeCallbacks(runnable);
			break;
		default:
			break;
		}
		return false;
	}
}