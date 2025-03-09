package me.daniel1385.ultrabarrels.events;

import me.daniel1385.ultrabarrels.objects.LagerData;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LagerUpdateEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Location loc;
	private LagerData data;

	public LagerUpdateEvent(Location loc, LagerData data) {
		this.loc = loc;
		this.data = data;
	}

	public LagerData getData() {
		return data;
	}

	public Location getLocation() {
		return this.loc;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
