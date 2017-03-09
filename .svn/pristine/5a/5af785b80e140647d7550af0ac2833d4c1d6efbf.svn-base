package com.ppfuns.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.ppfuns.vod.R;


public class LoadingDialog extends AlertDialog {
	private Context context;
	private TextView content_text ;
	private String content ;
	private MyOnCancelListerner myOnCancelListerner;
	public LoadingDialog(Context context) {
		super(context, R.style.loadingDialog);
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	public LoadingDialog(Context context, String content) {
		super(context, R.style.loadingDialog);
		this.context = context;
		if(!TextUtils.isEmpty(content)){
			this.content = content ;
		}
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_dialog);
		content_text = (TextView) findViewById(R.id.content_text) ;
		if(!TextUtils.isEmpty(content)){
			content_text.setText(content) ;
		}
	}
	public void setDialogContent(String content){
		if(content_text != null && !TextUtils.isEmpty(content)){
			content_text.setText(content) ;
		}
	}

	public MyOnCancelListerner getMyOnCancelListerner() {
		return myOnCancelListerner;
	}

	public void setMyOnCancelListerner(MyOnCancelListerner myOnCancelListerner) {
		this.myOnCancelListerner = myOnCancelListerner;
	}

	public interface MyOnCancelListerner{
		public void cancel();
	}

}
