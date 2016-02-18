package com.github.agetakoyaki29.suyaa;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.agetakoyaki29.suyaa.command.EnablePlayerCommand;
import com.github.agetakoyaki29.suyaa.command.ReloadCommand;
import com.github.agetakoyaki29.suyaa.resource.ConfigManager;
import com.github.agetakoyaki29.suyaa.resource.EnableManager;
import com.github.agetakoyaki29.suyaa.resource.MessageManager;

public class MyPlugin extends JavaPlugin {
	private MyPlugin plugin;
	private static MyPlugin instance;
	public static MyPlugin getInstance() { return instance; }

	private ConfigManager config;
	private MessageManager message;
	private EnableManager enable;

	private EveryNightRunner task;

	@Override
	public void onEnable() {
		init();

		start();
	}

	private void init() {
		if(instance == null) instance = this;
		plugin = this;

		// yaml resource
		config = new ConfigManager();
		message = new MessageManager();
		enable = new EnableManager();

		// command
		new ReloadCommand().init();
		new EnablePlayerCommand().init();

		task = new EveryNightRunner();
	}

	public void start() {
		// check
//		if(getConfigManager().getNightTime() >= 24000) {
//			return;
//		}
//		if(getConfigManager().getWorlds().size() <= 0) {
//			return;
//		}

		// start
		task.start();
		getServer().getPluginManager().registerEvents(new ThunderSleepListener(), this);
	}

	public void stop() {
		task.stop();
		ThunderChangeEvent.getHandlerList().unregister((Plugin) plugin);
	}

	@Override
	public void onDisable() {
		if(task != null) {
			try {
				task.cancel();
			} catch(IllegalStateException e) {
				// 無視
			}
		}
	}

	public void sayCanSleep(World world, boolean isThunder) {
		Bukkit.getPluginManager().callEvent(new CanSleepEvent(world, isThunder));

		MessageManager mm = plugin.getMessageManager();
		String mess = isThunder ? mm.getThunder() : mm.getNight();

		String sendMessage = ChatColor.BLUE + "[Suyaa]" + mess;

		// to console
		ConsoleCommandSender ccs = Bukkit.getConsoleSender();
		if(plugin.getEnableManager().getEnable(ccs.getName()))
			ccs.sendMessage(message + "(world: \"" + world.getName() + "\")");

		// to players
		worldBroadcast(world, sendMessage);
	}

	public void reloadAllResource() {
		config.reload();
		message.reload();
		enable.reload();

		task.reload();
	}

	// ---- accessor --------

	public ConfigManager getConfigManager() { return config; }
	public MessageManager getMessageManager() { return message; }
	public EnableManager getEnableManager() { return enable; }

	// ---- static func -----------

	public static void worldBroadcast(World world, String message) {
		MyPlugin plugin = getInstance();

		for(Player player : world.getPlayers()) {
			// player enable?
			if( ! plugin.getEnableManager().getEnable(player.getName()) ) continue;

			// depth check
			double locY = player.getLocation().getY();
			int depth = plugin.getConfigManager().getDepth();
			if(locY < depth) continue;

			player.sendMessage(message);
		}
	}

}
