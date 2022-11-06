package dev.vili.zyklon.event.events;

import dev.vili.zyklon.event.Event;

public class KeyReleaseEvent extends Event {
	private final int key;
	private final int scanCode;

	public KeyReleaseEvent(int key, int scanCode) {
		this.key = key;
		this.scanCode = scanCode;
	}

	public int getKey() {
		return key;
	}

	public int getScanCode() {
		return scanCode;
	}
}