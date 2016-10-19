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
 * 线程池+缓存+Handler加载图片
 */
public class AsyncLoadImage {
	//缓存
	private Map<String,SoftReference<Drawable>> imageCache = new HashMap<String,SoftReference<Drawable>>();
	//线程池
	private ExecutorService executorService = Executors.newFixedThreadPool(6);//总共有3个线程循环使用
	//Hanlder
	private Handler mHandler = new Handler();
	public interface ImageCallback {
		void imageLoad(Drawable image,String imageUrl);
	}
	/**
	 * 
	 * @param imageUrl 图片的地址
	 * @param imageCallback 回调接口
	 * @return 返回内存中缓存的图像 第一次返回null
	 */
	public Drawable loadDrawable(final String imageUrl,final ImageCallback imageCallback){
//		Log.i("AsyncLoadImage", "loadDrawable()"+imageUrl);
		//如果缓存中有则从缓存中取出来
		if(imageCache.containsKey(imageUrl)){
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			if(softReference.get()!=null){//判断是否有drawable
				return softReference.get(); //有则返回
			}
		}
		//使用线程池下载图片
		executorService.submit(new Runnable(){
			@Override
			public void run() {
				try {
//					System.out.println(Thread.currentThread().getName()+"<----------->"+imageUrl);
//					final Drawable drawable = Drawable.createFromStream(new URL(imageUrl).openStream(), "image.jpg");
					final Drawable drawable = getDrawableFormUrl(imageUrl); //调用获取数据的方法
					imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));//将加载的图片放入到内存中
					mHandler.post(new Runnable(){
						@Override
						public void run() {
							imageCallback.imageLoad(drawable,imageUrl);//接口回调
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
	 * 从网络上获取数据
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
