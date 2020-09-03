package com.sty.ne.rxjava.manualimplementation.custom_rxjava;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

/**
 * 上游处理线程切换的类【给所有上游切换线程】
 * @Author: tian
 * @UpdateDate: 2020/9/3 8:16 PM
 */
public class SubscribeOn<T> implements ObservableOnSubscribe<T> {
    private final ObservableOnSubscribe<T> source; //source == 上一层
    private int threadMode;
    //给上面所有的操作符都分配异步线程
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public SubscribeOn(ObservableOnSubscribe<T> source, int threadMode) {
        this.source = source;
        this.threadMode = threadMode;
    }

    @Override
    public void subscribe(final Observer<? super T> observableEmitter) {
        if(Schedulers.Child_THREAD == threadMode) {
            //用异步线程执行
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    source.subscribe(observableEmitter);
                }
            });
        }else if(Schedulers.MAIN_THREAD == threadMode){
            new Handler(Looper.getMainLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(@NonNull Message msg) {
                    source.subscribe(observableEmitter);
                    return false;
                }
            }).sendEmptyMessage(0);
        }else {
            source.subscribe(observableEmitter);
        }

    }

    /**
     * 由于不需要处理回来后的操作，所以不需要包裹
     */
//    private static final class MyObserverOn implements Observer {
//
//    }
}
