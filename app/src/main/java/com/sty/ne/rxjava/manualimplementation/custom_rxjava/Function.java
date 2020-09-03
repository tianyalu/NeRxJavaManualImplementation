package com.sty.ne.rxjava.manualimplementation.custom_rxjava;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/2 8:18 PM
 */
public interface Function<T, R> {

    public R apply(T t); //变换的行为标准
}
