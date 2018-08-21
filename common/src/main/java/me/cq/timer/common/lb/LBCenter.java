package me.cq.timer.common.lb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class LBCenter extends BroadcastReceiver {
	
	public static LBCenter mLBCenter = new LBCenter();
	private LBCenter(){}
	public static LBCenter instance(){
		return mLBCenter;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		LBDispatcher.instance().dispatch(context, intent);
	}

}
