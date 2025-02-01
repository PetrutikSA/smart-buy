package ru.petrutik.smartbuy.event.scheduler;

public class SchedulerRequestUpdateEvent {
    private Long key;

    public SchedulerRequestUpdateEvent() {
    }

    public SchedulerRequestUpdateEvent(Long key) {
        this.key = key;
    }

    public Long getKey() {
        return key;
    }
}
