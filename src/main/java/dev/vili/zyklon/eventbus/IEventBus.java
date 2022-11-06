package dev.vili.zyklon.eventbus;

public interface IEventBus {

    void register(Object registerClass);

    void unregister(Object registerClass);

    void post(Event event);

}