package com.sfcservice.pda.home;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sfcservice.bean.DisDisBean;
import com.sfcservice.component.SFCDateDialog;
import com.sfcservice.component.SFCDateDialog.Dialogcallback;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class SFCDisDis extends Activity implements OnClickListener {
	private Button btnBack, btnChoose;
	private ExpandableListView exListView;
	private ArrayList<String> listStr;
	private ArrayList<ArrayList<DisDisBean>> listBeans;
	private MyAdapter adapter;
	private PopupWindow p;
	private TextView tvShow;
	private TextView tvTitle;
	private LinearLayout linePro;
	private View pRootView;
	private SFCDateDialog dialog;
	private String time;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCDisDis.this, "连接服务器失败");
				exListView.setVisibility(View.INVISIBLE);
				break;
			case MyConfig.RESULTS:
				MyTool.playSuccessSound();
				linePro.setVisibility(View.INVISIBLE);
				exListView.setVisibility(View.VISIBLE);
				listBeans=null;
				listStr=null;
				listBeans = new ArrayList<ArrayList<DisDisBean>>();
				listStr = MyConnection.getMyConnection().getDisInfo(listBeans);
				adapter.notifyDataSetChanged();
				break;
			case MyConfig.RESULTF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCDisDis.this, strMsg);
				exListView.setVisibility(View.INVISIBLE);
				break;
			default:
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sfc_dis_dis);
		init();
	}

	public void init() {
		dialog=new SFCDateDialog(this);
		listBeans = new ArrayList<ArrayList<DisDisBean>>();
		listStr = MyConnection.getMyConnection().getDisInfo(listBeans);
		btnBack = (Button) findViewById(R.id.btn_back);
		exListView = (ExpandableListView) findViewById(R.id.exListView);
		btnChoose = (Button) findViewById(R.id.btn_choose);
		tvShow=(TextView)findViewById(R.id.tv_show);
		linePro=(LinearLayout)findViewById(R.id.line_pro);
		tvTitle=(TextView)findViewById(R.id.tv_title);
		btnChoose.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		adapter = new MyAdapter();
		exListView.setAdapter(adapter);
		exListView.setGroupIndicator(null);
		
		dialog.setDialogCallback(new Dialogcallback() {
			
			@Override
			public boolean exitActivity() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void btnConfirm() {
				// TODO Auto-generated method stub
				time=dialog.getTime();
				getData();
			}
			
			@Override
			public void btnCancel() {
				// TODO Auto-generated method stub
				
			}
		});
		String TYPE=getIntent().getStringExtra(MyConfig.TAG);
		if(TYPE.equals("0")){
			tvTitle.setText("一票一件分布");
		}else if(TYPE.equals("1")){
			tvTitle.setText("一票多件单SKU分布");
		}else if(TYPE.equals("2")){
			tvTitle.setText("一票多件多SKU分布");
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_choose:
			popShow();
			break;
		case R.id.btn1:
			p.dismiss();
			dialog.show();
			break;
		default:
			break;
		}
	}

	private class MyAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return listStr.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return listBeans.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return listStr.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return listBeans.get(groupPosition).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v = LayoutInflater.from(SFCDisDis.this).inflate(
					R.layout.sfc_dis_dis_item, null);
			ImageView img = (ImageView) v.findViewById(R.id.img);
			if (!isExpanded) {
				img.setImageResource(R.drawable.img_up);
			} else {
				img.setImageResource(R.drawable.img_down);
			}
			TextView tvArea = (TextView) v.findViewById(R.id.tvArea);
			tvArea.setText(listStr.get(groupPosition));
			return v;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v = LayoutInflater.from(SFCDisDis.this).inflate(
					R.layout.sfc_dis_dis_item2, null);
			TextView tv = (TextView) v.findViewById(R.id.tvItem);
			DisDisBean bean = listBeans.get(groupPosition).get(childPosition);
			tv.setText("分区 : " + bean.getArea() + " ; 货架数量 : "
					+ bean.getWsCodeCount() + " ; 订单数量 : "
					+ bean.getOrderCount() + " ; 产品数量 : "
					+ bean.getProductCount()

			);
			return v;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}

	}

	private void popShow() {
		if (p != null) {
			pRootView.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.top_in));
			p.showAsDropDown(btnChoose);
			return;
		}

		pRootView = LayoutInflater.from(this).inflate(
				R.layout.sfc_dis_dis_choose, null);
		pRootView.startAnimation(AnimationUtils.loadAnimation(this,
				R.anim.top_in));

		Button btn1 = (Button) pRootView.findViewById(R.id.btn1);
		btn1.setOnClickListener(this);

		p = new PopupWindow(pRootView, 100, LayoutParams.WRAP_CONTENT);
		p.setFocusable(true);
		p.setBackgroundDrawable(new BitmapDrawable());
		p.setOutsideTouchable(true);
		p.showAsDropDown(btnChoose);

	}
	private void getData(){
		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);

		MyConnection.getMyConnection().acceptServer(
				MyConfig.URL_COMMON,
				MyConnection.getMyConnection().writeJsonWithUserInfo(
						new String[] { "end_time" },
						new String[] { time },
						"countOrdersDetail"), handler);
	}
}
