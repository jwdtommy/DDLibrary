package com.dd.news.Subscriber;

public interface SubscriberOnNextListener<T> {
    void onNext(T data);
}
