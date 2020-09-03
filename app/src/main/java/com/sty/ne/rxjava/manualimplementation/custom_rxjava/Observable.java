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

    //static后面的<T>：静态方法声明的<T>泛型 --> 泛型方法
    //ObservableOnSubscribe<? extends T> == 静态方法声明的<T>泛型(上限)
    //ObservableOnSubscribe<? extends T> 和读写模式没有关系，还是上限下限的思想
    //TODO 在方法定义/声明的参数中使用时，一定是上限和下限
    public static <T> Observable<T> create(ObservableOnSubscribe<? extends T> source) { //Integer
        return new Observable<T>(source); //静态方法声明的<T>泛型  Integer
    }

    /**
     * 泛型方法 可变参数
     * @param t
     * @param <T>
     * @return
     */
    public static <T> Observable<T> just(final T... t) { //just内部发射事件
        //想办法让source不为null，而create操作符是使用者自己传进来的
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) {
                for (T t1 : t) {
                    //发射用户传递的参数数据
                    observableEmitter.onNext(t1);
                }
                //调用完毕
                observableEmitter.onComplete(); //发射事件完毕
            }
        });
    }

    /**
     * 泛型方法  单一参数
     * @param t
     * @param <T>
     * @return
     */
    public static <T> Observable<T> just(final T t) { //just内部发射事件
        //想办法让source不为null，而create操作符是使用者自己传进来的
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) {
                //发射用户传递的参数数据
                observableEmitter.onNext(t);
                //调用完毕
                observableEmitter.onComplete(); //发射事件完毕
            }
        });
    }

    //new Observable<T>(source).subscribe(Observer<Integer>)
    //Observer<? extends T> 和读写模式没有关系，还是上限下限的思想
    //TODO 在方法定义/声明的参数中使用时，一定是上限和下限
    public void subscribe(Observer<? extends T> observer) {
        //todo(map) step2
        observer.onSubscribe();

        //todo(map) step4
        source.subscribe(observer);
    }

    /**
     * map变换型操作符
     * T == 上一层传递过来的类型 Integer（变换后的类型）
     * R == 下一层的类型 String （变换后的类型）
     */
    public <R> Observable<R> map(Function<? super T, ? extends R> function) {  //? super T :可写模式   ? extends R :可读模式
        ObservableMap<T, R> observableMap = new ObservableMap<>(source, function); //source 上一层的能力
        return new Observable<R>(observableMap); //observableMap是source的实现类
    }

    /**
     * 所有上游分配线程【指定源Observable工作（发射事件）执行的线程，一直推送延续到Observer】
     * @param threadMode
     * @return
     */
    public Observable<T> subscribeOn(int threadMode) {
        //实例化操作符
        //source == 上一层
        return create(new SubscribeOn<T>(source, threadMode));
    }

    /**
     * 指定下游运算所在线程（可以多次使用无限切换）
     * @param threadMode
     * @return
     */
    public Observable<T> observableOn(int threadMode) {
        //实例化 处理下游的线程操作符
        return create(new ObservableOn<T>(source, threadMode));
    }
}
