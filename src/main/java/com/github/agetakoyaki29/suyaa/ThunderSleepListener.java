package com.github.agetakoyaki29.suyaa;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;

public class ThunderSleepListener implements Listener {
	MyPlugin plugin = MyPlugin.getInstance();


	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onThunderChangeEvent(ThunderChangeEvent event) {
		if(!event.toThunderState()) return;

		World world = event.getWorld();
		if( !plugin.getConfigManager().getWorlds().contains(world) ) return;

		plugin.sayCanSleep(world, true);
    }

}
