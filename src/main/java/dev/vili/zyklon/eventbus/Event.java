package dev.vili.zyklon.eventbus;

public abstract class Event {

    private boolean cancelled;

    private Era era;

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void cancel() {
        setCancelled(true);
    }

    public void setEra(Era era) {
        this.era = era;
    }

    public Era getEra() {
        return era;
    }

}