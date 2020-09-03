package com.sty.ne.rxjava.manualimplementation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sty.ne.rxjava.manualimplementation.custom_rxjava.Function;
import com.sty.ne.rxjava.manualimplementation.custom_rxjava.Observable;
import com.sty.ne.rxjava.manualimplementation.custom_rxjava.ObservableOnSubscribe;
import com.sty.ne.rxjava.manualimplementation.custom_rxjava.Observer;
import com.sty.ne.rxjava.manualimplementation.custom_rxjava.Schedulers;

/**
 * 自己实现的RxJava
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button btnCreateOperator;
    private Button btnJustOperator;
    private Button btnMapOperator;
    private Button btnThreadSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        addListeners();
    }

    private void initView() {
        btnCreateOperator = findViewById(R.id.btn_create_operator);
        btnJustOperator = findViewById(R.id.btn_just_operator);
        btnMapOperator = findViewById(R.id.btn_map_operator);
        btnThreadSwitch = findViewById(R.id.btn_thread_switch);
    }

    private void addListeners() {
        btnCreateOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnCreateOperatorClicked();
            }
        });
        btnJustOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnJustOperatorClicked();
            }
        });
        btnMapOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnMapOperatorClicked();
            }
        });
        btnThreadSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnThreadSwitchClicked();
            }
        });
    }

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

    private void onBtnThreadSwitchClicked() {
        //上游
        Observable
                .create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(Observer<? super Integer> observableEmitter) {
                        Log.d(TAG, "subscribe:  上游开始发射..."+ Thread.currentThread().getName());
                        observableEmitter.onNext(1);  //? super Integer:可写模式 （? extends Integer: 不可写模式-->报错）
                        observableEmitter.onComplete();
                    }
                })
                .observableOn(Schedulers.Child_THREAD)
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) {
                        Log.d(TAG, "第一个变换 apply: " + integer + "  " + Thread.currentThread().getName());
                        return "[ " + integer + " ]";
                    }
                })
                .observableOn(Schedulers.MAIN_THREAD)
                .map(new Function<String, StringBuffer>() {
                    @Override
                    public StringBuffer apply(String s) {
                        Log.d(TAG, "第二个变换 apply: " + s + "  " + Thread.currentThread().getName());
                        return new StringBuffer().append(s).append("----- ");
                    }
                })
                .observableOn(Schedulers.Child_THREAD)
                .map(new Function<StringBuffer, StringBuffer>() {
                    @Override
                    public StringBuffer apply(StringBuffer stringBuffer) {
                        Log.d(TAG, "第三个变换 apply: " + stringBuffer + "  " + Thread.currentThread().getName());
                        return stringBuffer.append("第三次变换");
                    }
                })
                .subscribeOn(Schedulers.Child_THREAD)
                .observableOn(Schedulers.MAIN_THREAD)
                .subscribe(new Observer<StringBuffer>() { //下游
                    //接口的实现方法
                    @Override
                    public void onSubscribe() {
                        Log.d(TAG, "onSubscribe: 已经订阅成功，即将开始发射 " + Thread.currentThread().getName());
                    }

                    //接口的实现方法
                    @Override
                    public void onNext(StringBuffer item) {
                        Log.d(TAG, "下游接收事件 onNext: " + item + "  " + Thread.currentThread().getName());
                    }

                    //接口的实现方法
                    @Override
                    public void onError(Throwable e) {

                    }

                    //接口的实现方法
                    @Override
                    public void onComplete() {
                        Log.d(TAG, "下游接收事件完成 onComplete: " + Thread.currentThread().getName());
                        // TODO 只有subscribeOn(Schedulers.Child_THREAD)操作符时
                        // D/MainActivity: onSubscribe: 已经订阅成功，即将开始发射 main
                        // D/MainActivity: subscribe:  上游开始发射...pool-1-thread-1
                        // D/MainActivity: 第一个变换 apply: 1  pool-1-thread-1
                        // D/MainActivity: 第二个变换 apply: [ 1 ]  pool-1-thread-1
                        // D/MainActivity: 第三个变换 apply: [ 1 ]-----   pool-1-thread-1
                        // D/MainActivity: 下游接收事件 onNext: [ 1 ]----- 第三次变换  pool-1-thread-1
                        // D/MainActivity: 下游接收事件完成 onComplete: pool-1-thread-1

                        // TODO 加上最下面的observableOn(Schedulers.MAIN_THREAD)操作符时
                        // D/MainActivity: onSubscribe: 已经订阅成功，即将开始发射 main
                        // D/MainActivity: subscribe:  上游开始发射...pool-1-thread-1
                        // D/MainActivity: 第一个变换 apply: 1  pool-1-thread-1
                        // D/MainActivity: 第二个变换 apply: [ 1 ]  pool-1-thread-1
                        // D/MainActivity: 第三个变换 apply: [ 1 ]-----   pool-1-thread-1
                        // D/MainActivity: 下游接收事件 onNext: [ 1 ]----- 第三次变换  main
                        // D/MainActivity: 下游接收事件完成 onComplete: main

                        // TODO TODO 加上所有的线程切换操作符时
                        // D/MainActivity: onSubscribe: 已经订阅成功，即将开始发射 main
                        // D/MainActivity: subscribe:  上游开始发射...pool-4-thread-1
                        // D/MainActivity: 第一个变换 apply: 1  pool-1-thread-1
                        // D/MainActivity: 第二个变换 apply: [ 1 ]  main
                        // D/MainActivity: 第三个变换 apply: [ 1 ]-----   pool-3-thread-1
                        // D/MainActivity: 下游接收事件 onNext: [ 1 ]----- 第三次变换  main
                        // D/MainActivity: 下游接收事件完成 onComplete: main
                    }
                });
    }
}