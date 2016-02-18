package com.github.agetakoyaki29.suyaa.resource;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.agetakoyaki29.suyaa.MyPlugin;

/**
 * メモリ上のコンフィグ情報を保持する。
 */
public class ConfigManager extends AbstractYamlManager {
	private MyPlugin plugin = MyPlugin.getInstance();
	private long nightTime;
	private List<World> worlds;
	private boolean underground;
	private int depth;

	public ConfigManager() {
		super(MyPlugin.getInstance(), "config.yml");
		init();
	}

	@Override
	protected void setSample(YamlConfiguration yaml) {
		yaml.set("worlds", Arrays.asList("world"));
	}

	@Override
	public void reset(YamlConfiguration yaml) {
		nightTime = yaml.getLong("nightTime", 13000L);

		worlds = yaml.getStringList("worlds").stream()
				.map(name -> Bukkit.getWorld(name))
				.filter(w -> w != null)
				.collect(Collectors.toList());

		underground = yaml.getBoolean("underground.enabled", true);
		depth = yaml.getInt("underground.depth", -1);

		check();
	}

	@Override
	protected boolean updateVersion(String now) {
		return true;
	}

	private void check() {
		Logger logger = plugin.getLogger();

		if(nightTime >= 24000L) {
			logger.warning(getFileName() + "の" + "nightTime" + "が1日を超えてます。");
		}

		if(worlds.size() <= 0) {
			logger.warning(getFileName() + "の" + "worlds" + "で存在するworldがありません。");
		} else {
			String names = worlds.stream()
					.map(w -> w.getName())
					.collect(Collectors.toList())
					.toString()
					;
			logger.info("enable world: " + names);
		}
	}

	// ---- accessor ---------

	public long getNightTime() { return nightTime; }
	public List<World> getWorlds() { return worlds; }
	public int getDepth() {
		if(underground) return depth;
		else return -1;
	}

}
