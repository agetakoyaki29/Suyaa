package com.github.agetakoyaki29.suyaa.resource;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.agetakoyaki29.suyaa.MyPlugin;

public class EnableManager extends AbstractYamlManager {
	private ConfigurationSection players;

	public EnableManager() {
		super(MyPlugin.getInstance(), "enable.yml");
		init();
	}

	@Override
	protected void setSample(YamlConfiguration yaml) {
		yaml.set("players.CONSOLE", false);
	}

	@Override
	public void reset(YamlConfiguration yaml) {
		players = yaml.getConfigurationSection("players");
	}

	@Override
	protected boolean updateVersion(String now) {
		return true;
	}

	// ---- accessor ------------

	public boolean getEnable(String player) {
		return players.getBoolean(player, true);
	}
	public void setEnable(String player, boolean isEnable) {
		players.set(player, isEnable);
		save();
	}

}
