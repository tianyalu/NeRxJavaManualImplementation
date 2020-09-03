package com.sty.ne.rxjava.manualimplementation.custom_rxjava;

import android.util.Log;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/2 8:24 PM
 */
//ObservableOnSubscribe 简称 source
public class ObservableMap<T, R> implements ObservableOnSubscribe<R> {
    private ObservableOnSubscribe<T> source; //拥有控制上一层的能力
    private Function<? super T, ? extends R> function;

    private Observer<? super R> observableEmitter; //拥有控制下一层的能力

    public ObservableMap(ObservableOnSubscribe source, Function<? super T, ? extends R> function) {
        this.source = source;
        this.function = function;
    }

    @Override
    public void subscribe(Observer<? super R> observableEmitter) {
        this.observableEmitter = observableEmitter;

        //包裹一层，然后再丢给最顶层的source
        MapObserver<T> mapObserver = new MapObserver<>(observableEmitter, source, function);
        //source.subscribe(observableEmitter); //不应该把下一层的Observer直接交出去给上一层，否则map没有了对下一层的控制权
        //todo(map) step5
        source.subscribe(mapObserver); //把自己的MapObserver交出去了
    }

    //真正拥有控制下一层的能力-->对下一层的控制权   observer,source,function
    class MapObserver<T> implements Observer<T> {
        //为了后续可以使用
        private Observer<? super R> observableEmitter; //变换后的给下一层的类型
        private ObservableOnSubscribe<T> source;
        private Function<? super T, ? extends R> function;

        public MapObserver(Observer<? super R> observableEmitter,
                           ObservableOnSubscribe<T> source,
                           Function<? super T, ? extends R> function) {
            this.observableEmitter = observableEmitter;
            this.source = source;
            this.function = function;
        }

        @Override
        public void onSubscribe() {
            Log.d("MainActivity", "ObservableMap onSubscribe");
            observableEmitter.onSubscribe(); //这里貌似没用
        }

        //todo(map) step7
        @Override
        public void onNext(T item) { //真正做变换的操作
            /**
             * T Integer 变换 R String
             */
            //todo(map) step8
            R nextMapResultSuccessType = function.apply(item);
            //调用下一层onNext方法
            //todo(map) step9
            observableEmitter.onNext(nextMapResultSuccessType);
        }

        @Override
        public void onError(Throwable e) {
            observableEmitter.onError(e);
        }

        @Override
        public void onComplete() {
            //todo(map) step12
            observableEmitter.onComplete();
        }
    }
}
