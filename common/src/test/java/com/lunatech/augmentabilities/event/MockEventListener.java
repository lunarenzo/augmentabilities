package com.lunatech.augmentabilities.event;

@FunctionalInterface
public interface MockEventListener {
    void onEvent(MockEvent event);
}