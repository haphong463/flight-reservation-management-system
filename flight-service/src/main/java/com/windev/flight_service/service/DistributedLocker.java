package com.windev.flight_service.service;

import java.util.concurrent.TimeUnit;

public interface DistributedLocker {
    boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit) throws InterruptedException;
    void unlock(String key);
}
