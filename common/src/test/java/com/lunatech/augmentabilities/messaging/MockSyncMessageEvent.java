package com.lunatech.augmentabilities.messaging;

import com.lunatech.augmentabilities.event.MockEvent;
import com.lunatech.augmentabilities.messaging.message.Message;

public class MockSyncMessageEvent extends MockEvent {
    private final Message<?> message;

    public MockSyncMessageEvent(Message<?> message) {
        this.message = message;
    }

    public Message<?> getMessage() {
        return message;
    }
}