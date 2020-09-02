package com.sty.ne.rxjava.manualimplementation.custom_rxjava;

/**
 * 被观察者 上游
 * @Author: tian
 * @UpdateDate: 2020/9/2 2:38 PM
 */
public class Observable<T> { //类声明的泛型T
    private ObservableOnSubscribe source;

    private Observable(ObservableOnSubscribe source) {
        this.source = source;
    }

    //static后面的<T>：静态方法声明的<T>泛型
    //ObservableOnSubscribe<? extends T> == 静态方法声明的<T>泛型(上限)
    //ObservableOnSubscribe<? extends T> 和读写模式没有关系，还是上限下限的思想
    //TODO 在方法定义/声明的参数中使用时，一定是上限和下限
    public static <T> Observable<T> create(ObservableOnSubscribe<? extends T> source) { //Integer
        return new Observable<T>(source); //静态方法声明的<T>泛型  Integer
    }

    //new Observable<T>(source).subscribe(Observer<Integer>)
    //Observer<? extends T> 和读写模式没有关系，还是上限下限的思想
    //TODO 在方法定义/声明的参数中使用时，一定是上限和下限
    public void subscribe(Observer<? extends T> observer) {
        observer.onSubscribe();

        source.subscribe(observer);
    }
}
