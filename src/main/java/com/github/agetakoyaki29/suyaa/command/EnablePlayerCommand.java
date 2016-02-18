package com.github.agetakoyaki29.suyaa.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import com.github.agetakoyaki29.suyaa.MyPlugin;

public class EnablePlayerCommand implements CommandExecutor {
	private MyPlugin plugin = MyPlugin.getInstance();

	public void init() {
		plugin.getCommand("suyaa.enable").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender,
			Command cmd, String alias, String[] args) {

		// check
		if(args.length < 1 || 2 < args.length) return false;

		boolean ison;
		String pname;

		String onoff = args[0];
		if(onoff.equalsIgnoreCase("on")) ison = true;
		else if(onoff.equalsIgnoreCase("off")) ison = false;
		else return false;

		if(args.length == 2) {
			// TODO 権限チェック
			if(! sender.hasPermission("suyaa.enable-other")) {
				sender.sendMessage("パーミッション " + "suyaa.enable-other" + " が無いため、実行できません。");
				return true;
			}
			pname = args[1];
		} else {
			pname = sender.getName();
		}

		plugin.getEnableManager().setEnable(pname, ison);

		String message = "set " + pname + " " + (ison ? "on" : "off");
		Bukkit.getConsoleSender().sendMessage(message);
		if( !(sender instanceof ConsoleCommandSender) ) {
			sender.sendMessage(message);
		}

		return true;
	}

}
