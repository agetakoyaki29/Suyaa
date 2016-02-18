package com.github.agetakoyaki29.suyaa.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import com.github.agetakoyaki29.suyaa.MyPlugin;

public class ReloadCommand implements CommandExecutor {
	MyPlugin plugin = MyPlugin.getInstance();

	public void init() {
		plugin.getCommand("suyaa.reload").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender,
			Command cmd, String alias, String[] args) {

		plugin.reloadAllResource();

		String message = ChatColor.GRAY + "Suyaa reloaded.";
		if(! (sender instanceof ConsoleCommandSender)) {
			plugin.getServer().getConsoleSender().sendMessage(message);;
		}
		sender.sendMessage(message);

		return true;
	}

}
