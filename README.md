# `RxJava`操作符手写实现

[TOC]

## 一、创建型操作符`create`

`create`操作符是使用者**自己去发射**事件的。

`subscribe`方法 --> 上游发射器 `observableEmitter.xxx` --> `Observer` 三个方法中接收事件。

### 1.1 `Observable`

```java
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
```

### 1.2 `ObservableOnSubscribe`

```java
public interface ObservableOnSubscribe<T> {  //T == String
    //<? super T> 可写模式
    // observableEmitter == 观察者
    //TODO 在真正使用到泛型时/真正方法调用时，就是读写模式（这个接口被在MainActivity中实现了）
    public void subscribe(Observer<? super T> observableEmitter); //Observer<String>
}
```

### 1.3 `Observer`

```java
public interface Observer<T> {
    public void onSubscribe();

    public void onNext(T item);

    public void onError(Throwable e);

    public void onComplete();
}
```

### 1.4 调用实现

```java
private void onBtnCreateOperatorClicked() {
  //上游
  Observable.create(new ObservableOnSubscribe<Integer>() {
    @Override
    public void subscribe(Observer<? super Integer> observableEmitter) {
      Log.d(TAG, "subscribe:  上游开始发射...");
      //TODO 第二步
      observableEmitter.onNext(1);  //? super Integer:可写模式 （? extends Integer: 不可写模式-->报错）
      observableEmitter.onComplete();
    }
  }).subscribe(new Observer<Integer>() { //下游
    //接口的实现方法
    @Override
    public void onSubscribe() {
      //todo 第一步
      Log.d(TAG, "onSubscribe: 已经订阅成功，即将开始发射 ");
    }

    //接口的实现方法
    @Override
    public void onNext(Integer item) {
      //TODO 第三步
      Log.d(TAG, "下游接收事件 onNext: " + item);
    }

    //接口的实现方法
    @Override
    public void onError(Throwable e) {

    }

    //接口的实现方法
    @Override
    public void onComplete() {
      //TODO 第四步
      Log.d(TAG, "下游接收事件完成 onComplete: ");
      // D/MainActivity: onSubscribe: 已经订阅成功，即将开始发射
      // D/MainActivity: subscribe:  上游开始发射...
      // D/MainActivity: 下游接收事件 onNext: 1
      // D/MainActivity: 下游接收事件完成 onComplete:
    }
  });
}
```

## 二、创建型操作符`just`

`just`操作符是**内部去发射**的。

`subscribe`方法 --> `just`(内部会根据我们传递的参数来发射事件)  `observer.onNext()` --> 下游接收到事件。

### 2.1 单一参数

`Observable`类中的静态方法：

```java
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
```

### 2.2 可变参数

`Observable`类中的静态方法：

```java
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
```

### 2.3 调用实现

```java
public void onBtnJustOperatorClicked(){
  Observable.just("A", "B", "C", "D")
    .subscribe(new Observer<String>() {
      @Override
      public void onSubscribe() {
        Log.d(TAG, "onSubscribe: 已经订阅成功，即将开始发射 ");
      }

      @Override
      public void onNext(String item) {
        Log.d(TAG, "下游接收事件 onNext: " + item);
      }

      @Override
      public void onError(Throwable e) {
      }

      @Override
      public void onComplete() {
        Log.d(TAG, "下游接收事件完成 onComplete: ");
        // D/MainActivity: onSubscribe: 已经订阅成功，即将开始发射
        // D/MainActivity: 下游接收事件 onNext: A
        // D/MainActivity: 下游接收事件 onNext: B
        // D/MainActivity: 下游接收事件 onNext: C
        // D/MainActivity: 下游接收事件 onNext: D
        // D/MainActivity: 下游接收事件完成 onComplete:
      }
    });
}
```

## 三、变换型操作符`map`

变换型操作符，只管上一层给的类型，把上一层给的类型变换成新的类型传递给下一层。

![image](https://github.com/tianyalu/NeRxJavaManualImplementation/raw/master/show/rxjava_transformation_operator_map.png)

