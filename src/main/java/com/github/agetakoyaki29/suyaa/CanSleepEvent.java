package com.github.agetakoyaki29.suyaa;

import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CanSleepEvent extends Event implements Cancellable {
	private World world;
	private boolean isThunder;

	public CanSleepEvent(World world, boolean isThunder) {
		this.world = world;
		this.isThunder = isThunder;
	}

	public World getWorld() { return world; }
	public boolean isThunder() { return isThunder; }

	// ---------

	private static final HandlerList handlers = new HandlerList();
	private boolean isCancelled;

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		isCancelled = cancelled;

	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
