package org.yingye.scs.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.yingye.scs.core.Core;
import org.yingye.scs.util.SimpleUtil;

@SuppressWarnings("all")
public class FlyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Boolean flag = SimpleUtil.users(sender, args);
        if (flag == null) {
            sender.sendMessage(ChatColor.RED + "该命令只能由玩家使用");
        } else if (flag != null && flag) {
            op(sender, args);
        } else if (flag != null && flag == false) {
            player(sender);
        }
        return true;
    }

    private void op(CommandSender sender, String[] args) {
        if (args.length <= 0) {
            player(sender);
        } else if (args.length == 1) {
            Player player = sender.getServer().getPlayerExact(args[0]);
            if (player != null) {
                if (player.getAllowFlight()) {
                    player.setAllowFlight(false);
                    sender.sendMessage(ChatColor.GREEN + "已关闭玩家(" + args[0] + ")的飞行模式");
                    player.sendMessage(ChatColor.RED + "飞行模式已关闭");
                } else {
                    player.setAllowFlight(true);
                    sender.sendMessage(ChatColor.GREEN + "已开启玩家(" + args[0] + ")的飞行模式");
                    player.sendMessage(ChatColor.GREEN + "飞行模式已开启");
                }
            } else {
                sender.sendMessage(ChatColor.GREEN + "未找到名为(" + args[0] + ")的玩家");
            }
        } else if (args.length == 2) {
            Player player = sender.getServer().getPlayerExact(args[0]);
            if (player != null) {
                if (args[1].equalsIgnoreCase("on")) {
                    player.setAllowFlight(true);
                    sender.sendMessage(ChatColor.GREEN + "已开启玩家(" + args[0] + ")的飞行模式");
                    player.sendMessage(ChatColor.GREEN + "飞行模式已开启");
                    Core.printWarn(SimpleUtil.getFormatDate() + " --- 管理员: " + sender.getName() + ",开启了玩家: " + player.getName() + "的飞行模式");
                } else if (args[1].equalsIgnoreCase("off")) {
                    player.setAllowFlight(false);
                    sender.sendMessage(ChatColor.GREEN + "已关闭玩家(" + args[0] + ")的飞行模式");
                    player.sendMessage(ChatColor.RED + "飞行模式已关闭");
                }
            } else {
                sender.sendMessage(ChatColor.GREEN + "未找到名为(" + args[0] + ")的玩家");
            }
        }
    }

    private void player(CommandSender sender) {
        Player player = (Player) sender;
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.sendMessage(ChatColor.RED + "飞行模式已关闭");
        } else {
            player.setAllowFlight(true);
            player.sendMessage(ChatColor.GREEN + "飞行模式已开启");
        }
    }
}