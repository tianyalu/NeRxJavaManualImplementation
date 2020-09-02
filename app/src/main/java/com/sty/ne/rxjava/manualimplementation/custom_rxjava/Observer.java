package com.sty.ne.rxjava.manualimplementation.custom_rxjava;

/**
 * 观察者 下游
 * @Author: tian
 * @UpdateDate: 2020/9/2 2:31 PM
 */
public interface Observer<T> {
    public void onSubscribe();

    public void onNext(T item);

    public void onError(Throwable e);

    public void onComplete();
}
