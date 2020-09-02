package com.sty.ne.rxjava.manualimplementation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sty.ne.rxjava.manualimplementation.custom_rxjava.Observable;
import com.sty.ne.rxjava.manualimplementation.custom_rxjava.ObservableOnSubscribe;
import com.sty.ne.rxjava.manualimplementation.custom_rxjava.Observer;

/**
 * 自己实现的RxJava
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button btnCreateOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        addListeners();
    }

    private void initView() {
        btnCreateOperator = findViewById(R.id.btn_create_operator);
    }

    private void addListeners() {
        btnCreateOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnCreateOperatorClicked();
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
}