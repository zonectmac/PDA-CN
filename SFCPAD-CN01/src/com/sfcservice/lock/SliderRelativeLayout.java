package com.sfcservice.lock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sfcservice.pda.R;


public class SliderRelativeLayout extends RelativeLayout {

	private TextView tv_slider_icon = null; // ��ʼ�ؼ��������ж��Ƿ�Ϊ�϶���

	private Bitmap dragBitmap = null; //��קͼƬ
	private Context mContext = null; // ��ʼ��ͼƬ��קʱ��Bitmap����

	
	private Handler mainHandler = null; //����Activityͨ�ŵ�Handler����
	
	public SliderRelativeLayout(Context context) {
		super(context);
		mContext = context;
		initDragBitmap();
	}

	public SliderRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		mContext = context;
		initDragBitmap();
	}

	public SliderRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initDragBitmap();
	}
	
	// ��ʼ��ͼƬ��קʱ��Bitmap����
	private void initDragBitmap() {
		if (dragBitmap == null)
			dragBitmap = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.lock_touch);
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		// �ÿؼ���Ҫ�ж��Ƿ��ڻ���������򡣻���ʱ ����INVISIBLE(���ɼ�)״̬������ʱ����VISIBLE(�ɼ�)״̬
		tv_slider_icon = (TextView) findViewById(R.id.slider_icon);
	}
	private int mLastMoveX = 1000;  //��ǰbitmapӦ�û��Ƶĵط� �� ��ʼֵΪ�㹻�󣬿�����Ϊ������	
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastMoveX = (int) event.getX();
			//����Action_Down�¼���  �ж��Ƿ����˻�������
			return handleActionDownEvenet(event);
		case MotionEvent.ACTION_MOVE:
			mLastMoveX = x; //������X�᷽��
            invalidate(); //���»���			    
			return true;
		case MotionEvent.ACTION_UP:
			//����Action_Up�¼���  �ж��Ƿ�����ɹ����ɹ���������ǵ�Activity ������ ���������˸�ͼƬ��
			handleActionUpEvent(event);
			return true;
		}
		return super.onTouchEvent(event);
	}

	// �����϶�ʱ��ͼƬ
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);		
		//Log.(TAG, "onDraw ######" );
		// ͼƬ���������ƶ�
		invalidateDragImg(canvas);
	}

	// ͼƬ���������ƶ�
	private void invalidateDragImg(Canvas canvas) {
		//Log.e(TAG, "handleActionUpEvenet : invalidateDragImg" );
		//�Ժ��ʵ�����ֵ���Ƹ�ͼƬ
		int drawXCor = mLastMoveX - dragBitmap.getWidth();
		int drawYCor = tv_slider_icon.getTop();
	    canvas.drawBitmap(dragBitmap,  drawXCor < 0 ? 5 : drawXCor , drawYCor , null);
	}

	// ���������ǣ��Ƿ������ͼƬ�����Ƿ���Ҫ��ʼ�ƶ�
	private boolean handleActionDownEvenet(MotionEvent event) {
		Rect rect = new Rect();
		tv_slider_icon.getHitRect(rect);
		boolean isHit = rect.contains((int) event.getX(), (int) event.getY());
		
		if(isHit)  //��ʼ��ק �����ظ�ͼƬ
			tv_slider_icon.setVisibility(View.INVISIBLE);
		
		//Log.e(TAG, "handleActionDownEvenet : isHit" + isHit);
		
		return isHit;
	}

	//���˶���ʱ����ֵ 
	private static int BACK_DURATION = 20 ;   // 20ms
    //ˮƽ����ǰ������
	private static float VE_HORIZONTAL = 0.7f ;  //0.1dip/ms
	
    //�ж��ɿ���ָʱ���Ƿ�ﵽĩβ�����Կ����� , �ǣ�����������ͨ��һ�����㷨ʹ����ˡ�
	private void handleActionUpEvent(MotionEvent event){		
		int x = (int) event.getX() ;	
		//������15dip���ڴ�������ɹ���
		boolean isSucess= Math.abs(x - getRight()) <= 30 ;
		
		if(isSucess){
//		   Toast.makeText(mContext, "�����ɹ�", 1000).show();
		   resetViewState();	
		   virbate(); //��һ��
		   //�������ǵ���Activity����
		   mainHandler.obtainMessage(LockActivity.MSG_LOCK_SUCESS).sendToTarget();
		}
		else {//û�гɹ���������һ�����㷨ʹ�����
		    //ÿ��20ms , ����Ϊ0.6dip/ms ,  ʹ��ǰ��ͼƬ�������һ�ξ��룬ֱ�����������	
			mLastMoveX = x ;  //��¼�����ɿ�ʱ����ǰ������λ�á�
			int distance = x - tv_slider_icon.getRight() ;
			//ֻ���ƶ����㹻����Ż���
			if(distance >= 0)
			    mHandler.postDelayed(BackDragImgTask, BACK_DURATION);
			else{  //��ԭ��ʼ����
				resetViewState();
			}
		}
	}
	//���ó�ʼ��״̬����ʾtv_slider_iconͼ��ʹbitmap���ɼ�
	private void resetViewState(){
		mLastMoveX = 1000 ;
		tv_slider_icon.setVisibility(View.VISIBLE);
		invalidate();        //�ػ����һ��
	}
	
	//ͨ����ʱ���Ƶ�ǰ����bitmap��λ������
	private Runnable BackDragImgTask = new Runnable(){
		
		public void run(){
			//һ�´�BitmapӦ�õ��������ֵ
			mLastMoveX = mLastMoveX - (int)(BACK_DURATION * VE_HORIZONTAL);
			
			
			invalidate();//�ػ�		
			//�Ƿ���Ҫ��һ�ζ��� �� �����˳�ʼλ�ã�������Ҫ����
			boolean shouldEnd = Math.abs(mLastMoveX - tv_slider_icon.getRight()) <= 8 ;			
			if(!shouldEnd)
			    mHandler.postDelayed(BackDragImgTask, BACK_DURATION);
			else { //��ԭ��ʼ����
				resetViewState();	
			}				
		}
	};
	
	private Handler mHandler =new Handler (){
		
		public void handleMessage(Message msg){
			
			
		}
	};
	//��һ���¿�
	private void virbate(){
		Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(200);
	}
	public void setMainHandler(Handler handler){
		mainHandler = handler;//activity���ڵ�Handler����
	}
}
