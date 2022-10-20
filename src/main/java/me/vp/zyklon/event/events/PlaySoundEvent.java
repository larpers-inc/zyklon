package me.vp.zyklon.event.events;

import me.vp.zyklon.event.Event;
import net.minecraft.client.sound.SoundInstance;

public class PlaySoundEvent extends Event {
    private final SoundInstance soundInstance;

    public PlaySoundEvent(SoundInstance soundInstance) {
        this.soundInstance = soundInstance;
    }

    public SoundInstance getSoundInstance() {
        return this.soundInstance;
    }
}
