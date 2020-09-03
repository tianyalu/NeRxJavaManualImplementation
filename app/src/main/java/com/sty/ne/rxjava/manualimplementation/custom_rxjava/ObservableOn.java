package com.sty.ne.rxjava.manualimplementation.custom_rxjava;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

/**
 * 给下游切换线程的类
 * @Author: tian
 * @UpdateDate: 2020/9/3 9:41 PM
 */
public class ObservableOn<T> implements ObservableOnSubscribe<T>{
    //需要拿到上一层
    private ObservableOnSubscribe<T> source;
    private int threadMode;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public ObservableOn(ObservableOnSubscribe<T> source, int threadMode) {
        this.source = source;
        this.threadMode = threadMode;
    }

    @Override
    public void subscribe(Observer<? super T> observableEmitter) {
        source.subscribe(new ThreadObserver<T>(observableEmitter));
    }

    //需要包裹，因为需要专门处理回来的事件
    private final class ThreadObserver<T> implements Observer<T> {
        //拿到下一层的 Observer
        Observer<? super T> observableEmitter;

        public ThreadObserver(Observer<? super T> observableEmitter) {
            this.observableEmitter = observableEmitter;
        }

        @Override
        public void onSubscribe() {

        }

        @Override
        public void onNext(final T item) {
            if(Schedulers.MAIN_THREAD == threadMode) {
                new Handler(Looper.getMainLooper(), new Handler.Callback(){
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        observableEmitter.onNext(item);
                        return false;
                    }
                }).sendEmptyMessage(0);
            }else if(Schedulers.Child_THREAD == threadMode){
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        observableEmitter.onNext(item);
                    }
                });
            }else {
                observableEmitter.onNext(item);
            }
        }

        @Override
        public void onError(final Throwable e) {
            if(Schedulers.MAIN_THREAD == threadMode) {
                new Handler(Looper.getMainLooper(), new Handler.Callback(){
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        observableEmitter.onError(e);
                        return false;
                    }
                }).sendEmptyMessage(0);
            }else if(Schedulers.Child_THREAD == threadMode){
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        observableEmitter.onError(e);
                    }
                });
            }else {
                observableEmitter.onError(e);
            }
        }

        @Override
        public void onComplete() {
            if(Schedulers.MAIN_THREAD == threadMode) {
                new Handler(Looper.getMainLooper(), new Handler.Callback(){
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        observableEmitter.onComplete();
                        return false;
                    }
                }).sendEmptyMessage(0);
            }else if(Schedulers.Child_THREAD == threadMode){
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        observableEmitter.onComplete();
                    }
                });
            }else {
                observableEmitter.onComplete();
            }
        }
    }
}
