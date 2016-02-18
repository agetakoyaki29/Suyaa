package com.github.agetakoyaki29.suyaa.resource;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.agetakoyaki29.suyaa.MyPlugin;

public class MessageManager extends AbstractYamlManager {
	private String night;
	private String thunder;

	public MessageManager() {
		super(MyPlugin.getInstance(), "message.yml");
		init();
	}

	@Override
	protected void setSample(YamlConfiguration yaml) {
	}

	@Override
	public void reset(YamlConfiguration yaml) {
		night = yaml.getString("night");
		thunder = yaml.getString("thunder");
	}

	@Override
	protected boolean updateVersion(String now) {
		return true;
	}

	// ---- accessor -------------

	public String getNight() { return night; }
	public String getThunder() { return thunder; }

}
