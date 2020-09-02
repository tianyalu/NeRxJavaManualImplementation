package com.sty.ne.rxjava.manualimplementation.custom_rxjava;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/2 2:37 PM
 */
public interface ObservableOnSubscribe<T> {  //T == String

    //<? super T> 可写模式
    // observableEmitter == 观察者
    //TODO 在真正使用到泛型时/真正方法调用时，就是读写模式（这个接口被在MainActivity中实现了）
    public void subscribe(Observer<? super T> observableEmitter); //Observer<String>
}
