package com.github.agetakoyaki29.suyaa;

import java.util.List;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.agetakoyaki29.suyaa.resource.ConfigManager;

public class EveryNightRunner extends BukkitRunnable {
	private MyPlugin plugin = MyPlugin.getInstance();
	private ConfigManager config = plugin.getConfigManager();

	private List<World> worlds;
	private long night;
	private boolean hasPast = true;

	public EveryNightRunner() {
		reload();
	}

	public void start() {
		hasPast = true;
		runTaskTimer(plugin, 0L, Long.MAX_VALUE);
	}

	public void stop() {
		this.cancel();
	}

	public void reload() {
		worlds = config.getWorlds();
		night = config.getNightTime();
	}

	@Override
	public void run() {
		if(worlds.size() <= 0) return;

		// それぞれのワールドの時間は共通なのでチェックするのは１つ
		long now = worlds.get(0).getTime();
		boolean isPast = night < now;

		// 日をまたいだとき
		if(!isPast && hasPast) {
			hasPast = false;
		}

		// 初めてnightを過ぎたとき
		if(isPast && !hasPast) {
			hasPast = true;

			for(World world : worlds) {
				plugin.sayCanSleep(world, false);
			}
		}
	}

}
