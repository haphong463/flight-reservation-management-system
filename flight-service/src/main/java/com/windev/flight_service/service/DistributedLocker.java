package com.windev.flight_service.service;

import java.util.concurrent.TimeUnit;

public interface RedisDistributedLocker {

    // Here, only write the interface for distributed Lock
    // And please explain each function, in public.. read
    boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException;

    void lock(long leaseTime, TimeUnit unit);

    void unlock();

    boolean isLocked();

    boolean isHeldByThread(long threadId);

    boolean isHeldByCurrentThread();
}
