package com.paxw.fly.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.view.SurfaceHolder;

/**
 * Created by lichuang on 2016/5/25.
 */
public class GameViewThread extends Thread{
    private SurfaceHolder mSurfaceHolder ;
    private Context mContext;
    private Handler mHandler;

    /**
     * 运行的标记
     */
    private boolean isRun = true;

    /**
     * 构造方法
     * @param surfaceHolder
     * @param context
     * @param handler
     */
    public GameViewThread( SurfaceHolder surfaceHolder, Context context, Handler handler){
        this.mSurfaceHolder = surfaceHolder;
        this.mHandler = handler;
        this.mContext = context;
    }

    public void run(){
        while (isRun){
            //获取Surfaceview的画布
            Canvas mCanvas = null;

            mCanvas = mSurfaceHolder.lockCanvas();
            //开始操作这个画布
            synchronized (mSurfaceHolder){
                doDraw(mCanvas);
            }






        }
    }


    private void doDraw(Canvas canvas){

    }


}
