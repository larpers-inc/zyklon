package me.vp.zyklon.event.events;

import me.vp.zyklon.event.Event;

public class EntityControlEvent extends Event {
    private Boolean canBeControlled;

    public Boolean canBeControlled() {
        return canBeControlled;
    }

    public void setControllable(Boolean canBeControlled) {
        this.canBeControlled = canBeControlled;
    }

}
