package com.lunatech.augmentabilities.messaging;

import com.lunatech.augmentabilities.event.MockEventSystem;
import com.lunatech.augmentabilities.messaging.adapter.receiver.ReceiverAdapter;
import com.lunatech.augmentabilities.messaging.message.Message;

public class MockReceiverAdapter extends ReceiverAdapter {
    @Override
    public void accept(Message<?> message) {
        MockEventSystem.fireEvent(new MockSyncMessageEvent(message));
    }
}
