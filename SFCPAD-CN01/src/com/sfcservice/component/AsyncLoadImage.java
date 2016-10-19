package com.sfcservice.component;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.drawable.Drawable;
import android.os.Handler;

/**
 * �̳߳�+����+Handler����ͼƬ
 */
public class AsyncLoadImage {
	//����
	private Map<String,SoftReference<Drawable>> imageCache = new HashMap<String,SoftReference<Drawable>>();
	//�̳߳�
	private ExecutorService executorService = Executors.newFixedThreadPool(6);//�ܹ���3���߳�ѭ��ʹ��
	//Hanlder
	private Handler mHandler = new Handler();
	public interface ImageCallback {
		void imageLoad(Drawable image,String imageUrl);
	}
	/**
	 * 
	 * @param imageUrl ͼƬ�ĵ�ַ
	 * @param imageCallback �ص��ӿ�
	 * @return �����ڴ��л����ͼ�� ��һ�η���null
	 */
	public Drawable loadDrawable(final String imageUrl,final ImageCallback imageCallback){
//		Log.i("AsyncLoadImage", "loadDrawable()"+imageUrl);
		//�������������ӻ�����ȡ����
		if(imageCache.containsKey(imageUrl)){
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			if(softReference.get()!=null){//�ж��Ƿ���drawable
				return softReference.get(); //���򷵻�
			}
		}
		//ʹ���̳߳�����ͼƬ
		executorService.submit(new Runnable(){
			@Override
			public void run() {
				try {
//					System.out.println(Thread.currentThread().getName()+"<----------->"+imageUrl);
//					final Drawable drawable = Drawable.createFromStream(new URL(imageUrl).openStream(), "image.jpg");
					final Drawable drawable = getDrawableFormUrl(imageUrl); //���û�ȡ���ݵķ���
					imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));//�����ص�ͼƬ���뵽�ڴ���
					mHandler.post(new Runnable(){
						@Override
						public void run() {
							imageCallback.imageLoad(drawable,imageUrl);//�ӿڻص�
						}
					});
				} catch (Exception e) {
					throw new RuntimeException();
				}
			}
			
		});
		return null ;
	}
	/**
	 * �������ϻ�ȡ����
	 */
	public Drawable getDrawableFormUrl(String imageUrl){
		Drawable drawable = null ;
		try {
			URL url=new URL(imageUrl);
			InputStream is=url.openStream();
			drawable = Drawable.createFromStream(is, "image.jpg");
			is.close();
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return drawable ;
	}

}
