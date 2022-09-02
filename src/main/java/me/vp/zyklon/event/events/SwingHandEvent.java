package me.vp.zyklon.event.events;

import me.vp.zyklon.event.Event;
import net.minecraft.util.Hand;

public class SwingHandEvent extends Event {
    private Hand hand;

	public SwingHandEvent(Hand hand) {
		this.setHand(hand);
	}

	public Hand getHand() {
		return hand;
	}

	public void setHand(Hand hand) {
		this.hand = hand;
	}
}