package ru.petrutik.smartbuy.event.scheduler;

public class SchedulerUserNotifyEvent {
    private Long key;

    public SchedulerUserNotifyEvent() {
    }

    public SchedulerUserNotifyEvent(Long key) {
        this.key = key;
    }

    public Long getKey() {
        return key;
    }
}
