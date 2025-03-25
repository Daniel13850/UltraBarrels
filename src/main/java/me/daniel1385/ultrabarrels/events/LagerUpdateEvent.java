package me.daniel1385.ultrabarrels.events;

import me.daniel1385.ultrabarrels.objects.LagerData;
import org.bukkit.Location;
import org.bukkit.block.Barrel;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LagerUpdateEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Barrel barrel;
	private LagerData data;

	public LagerUpdateEvent(Barrel barrel, LagerData data) {
		this.barrel = barrel;
		this.data = data;
	}

	public LagerData getData() {
		return data;
	}

	public Barrel getBarrel() {
		return barrel;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
