package com.lunatech.augmentabilities.messaging.adapter.receiver;

import com.lunatech.augmentabilities.messaging.adapter.receiver.event.MessageReceivedEvent;
import com.lunatech.augmentabilities.messaging.message.Message;
import io.github.milkdrinkers.threadutil.Scheduler;

public class BukkitReceiverAdapter extends ReceiverAdapter {
    @Override
    public void accept(Message<?> message) {
        Scheduler.sync(() -> new MessageReceivedEvent(message).callEvent()).execute();
    }
}
