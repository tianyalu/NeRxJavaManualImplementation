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

`map`要拥有控制上一层的能力（把自己交付给上一层<拦截>），还要拥有控制下一层的能力（通过`observer`做事情）。

![image](https://github.com/tianyalu/NeRxJavaManualImplementation/raw/master/show/rxjava_transformation_operator_map.png)

### 3.1 `map`操作符的实现思路

`map`实现的代码流向如下(主要分析map变换的程序执行流程)：

> 1. 从右往左走：
>
>      订阅 --> `observer.onSubscribe()` --> `source.subscribe(observer)` -->   `ObservableMap.subscribe`(会包裹一层，给上一层) --> 最顶层的上游（使用者自己去发射）
>     
> 2. 从左往右走：
>
>     使用者自己发射的`onNext()` --> `MapObserver.onNext()` --> 最右边的观察者的`onNext()`    

以下流程图演示了当只有一个`map`变换时程序执行的流程：

![image](https://github.com/tianyalu/NeRxJavaManualImplementation/raw/master/show/rxjava_transformation_operator_map.png)

### 3.2 `map`操作符的源码实现

#### 3.2.1 `map`操作符

`Observable`类中的静态方法：

```java
/**
 * map变换型操作符
 * T == 上一层传递过来的类型 Integer（变换后的类型）
 * R == 下一层的类型 String （变换后的类型）
 */
//? super T :可写模式   ? extends R :可读模式
public <R> Observable<R> map(Function<? super T, ? extends R> function) {  
  //source 上一层的能力
  ObservableMap<T, R> observableMap = new ObservableMap<>(source, function); 
  return new Observable<R>(observableMap); //observableMap是source的实现类
}
```

#### 3.2.2 `Function`

```java
public interface Function<T, R> {
    public R apply(T t); //变换的行为标准
}
```

#### 3.2.3 `ObservableMap`

```java
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
```

#### 3.2.4 调用实现

```java
/**
 * 以下的步骤分析以一个map变换为例，主要分析map变换的程序执行流程
 */
private void onBtnMapOperatorClicked() {
  //上游
  Observable
    .create(new ObservableOnSubscribe<Integer>() {
      @Override
      public void subscribe(Observer<? super Integer> observableEmitter) {
        Log.d(TAG, "subscribe:  上游开始发射...");
        //todo(map) step6
        observableEmitter.onNext(1);  //? super Integer:可写模式 （? extends Integer: 不可写模式-->报错）
        //todo(map) step11
        observableEmitter.onComplete();
      }
    })
    .map(new Function<Integer, String>() {
      //todo(map) step10
      @Override
      public String apply(Integer integer) {
        Log.d(TAG, "第一个变换 apply: " + integer);
        return "[ " + integer + " ]";
      }
    })
    .map(new Function<String, StringBuffer>() {
      @Override
      public StringBuffer apply(String s) {
        Log.d(TAG, "第二个变换 apply: " + s);
        return new StringBuffer().append(s).append("-----------");
      }
    })
    //todo(map) step1
    .subscribe(new Observer<StringBuffer>() { //下游
      //接口的实现方法
      @Override
      public void onSubscribe() {
        //todo(map) step3
        Log.d(TAG, "onSubscribe: 已经订阅成功，即将开始发射 ");
      }

      //接口的实现方法
      @Override
      public void onNext(StringBuffer item) {
        //todo(map) step10
        Log.d(TAG, "下游接收事件 onNext: " + item);
      }

      //接口的实现方法
      @Override
      public void onError(Throwable e) {

      }

      //接口的实现方法
      @Override
      public void onComplete() {
        //todo(map) step13
        Log.d(TAG, "下游接收事件完成 onComplete: ");
        // D/MainActivity: onSubscribe: 已经订阅成功，即将开始发射
        // D/MainActivity: subscribe:  上游开始发射...
        // D/MainActivity: 第一个变换 apply: 1
        // D/MainActivity: 第二个变换 apply: [ 1 ]
        // D/MainActivity: 下游接收事件 onNext: [ 1 ]-----------
        // D/MainActivity: 下游接收事件完成 onComplete:
      }
    });
}
```

