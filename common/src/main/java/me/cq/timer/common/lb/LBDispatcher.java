package me.cq.timer.common.lb;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class LBDispatcher {

    private static LBDispatcher instance = new LBDispatcher();
    private static ConcurrentHashMap<String,List<WeakReference<LBCallBack>>> mCallBackMap = new ConcurrentHashMap();
    private static LocalBroadcastManager mManager;

    private LBDispatcher(){}

    public static LBDispatcher instance() {
        return instance;
    }

    public void init(Context context, LBFilter lbFilter){
        if(context == null){
            throw new IllegalArgumentException("Context 为空");
        }
        mManager = LocalBroadcastManager.getInstance(context);
        mManager.registerReceiver(LBCenter.instance(), lbFilter.buildIntentFilter());
    }

    //注册监听 (会取消为空的引用)
    public synchronized void register(LBCallBack lbCallBack,String action){
        List<WeakReference<LBCallBack>> callBackList = mCallBackMap.get(action);
        if(callBackList ==null){
            callBackList = new ArrayList<>();
            callBackList.add(new WeakReference(lbCallBack));
            mCallBackMap.put(action, callBackList);
        }else{
            Iterator<WeakReference<LBCallBack>> referenceIterator = callBackList.iterator();
            while (referenceIterator.hasNext()){
                WeakReference<LBCallBack> next = referenceIterator.next();
                LBCallBack callBackWeak = next.get();
                if(callBackWeak!=null){
                    if(callBackWeak==lbCallBack){
                        return;
                    }
                }else {
                    //为空就移除
                    referenceIterator.remove();
                }
            }
            callBackList.add(new WeakReference(lbCallBack));
        }
    }

    //注销监听(不主动注销，那么在对象销毁后也会自动取消注册，因为用的是WeakReference)
    public void unregister(LBCallBack lbCallBack){
        Iterator<String> keyIterator = mCallBackMap.keySet().iterator();
        while (keyIterator.hasNext()){
            String key = keyIterator.next();
            List<WeakReference<LBCallBack>> list = mCallBackMap.get(key);

            Iterator<WeakReference<LBCallBack>> referenceIterator = list.iterator();
            while (referenceIterator.hasNext()){
                WeakReference<LBCallBack> next = referenceIterator.next();
                LBCallBack callBackWeak = next.get();
                if(callBackWeak!=null && callBackWeak==lbCallBack){
                    referenceIterator.remove();
                    break;
                }
            }
        }
    }

    //注销所有监听
    public void unregisterAll(){
        mCallBackMap.clear();
    }

    //发送广播
    public void send(String action){
        send(new Intent(action));
    }

    //发送广播
    public void send(Intent intent){
        if(mManager == null){
            throw new IllegalArgumentException("manager未初始化！");
        }
        mManager.sendBroadcast(intent);
    }

    //接受并传递广播
    public void dispatch(Context context, Intent intent) {
        String action = intent.getAction();
        List<WeakReference<LBCallBack>> callBackList = mCallBackMap.get(action);
        if(callBackList != null && callBackList.size() > 0){
            for (WeakReference<LBCallBack> reference : callBackList) {
                LBCallBack lbCallBack = reference.get();
                if(lbCallBack != null){
                    lbCallBack.lbCallback(context, intent);
                }
            }
        }
    }

    //打印所有的广播
    public void printCallBacks(){
        Iterator<String> keyIterator = mCallBackMap.keySet().iterator();
        while (keyIterator.hasNext()){
            String key = keyIterator.next();
            List<WeakReference<LBCallBack>> list = mCallBackMap.get(key);
            String temp = "";
            for (WeakReference<LBCallBack> reference:list){
                LBCallBack lbCallBack = reference.get();
                if(lbCallBack!=null){
                    temp += lbCallBack.getClass().getSimpleName() + ",";
                }
            }
        }
    }

}
