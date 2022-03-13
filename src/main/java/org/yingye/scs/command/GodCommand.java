package org.yingye.scs.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.yingye.scs.core.Core;
import org.yingye.scs.util.SimpleUtil;

import java.util.HashSet;
import java.util.List;

@SuppressWarnings("all")
public class GodCommand implements CommandExecutor {

    private static final HashSet<Player> players = new HashSet<>();
    private Logger log = Core.log;

    public static HashSet<Player> getPlayers() {
        return players;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Boolean flag = null;
        flag = SimpleUtil.users(sender, args);
        if (flag == null) {
            log.error("该命令只能由玩家使用");
            return true;
        } else if (flag) {
            op(sender, args);
        } else {
            player(sender);
        }
        return true;
    }

    private void op(CommandSender sender, String[] args) {
        Player player = sender.getServer().getPlayerExact(args[0]);
        if (player != null) {
            Boolean flag = null;
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("on")) {
                    flag = true;
                } else if (args[0].equalsIgnoreCase("off")) {
                    flag = false;
                }
            }
            if (flag == null) {
                change(sender, player);
            } else if (flag) {
                add(sender, player);
            } else {
                remove(sender, player);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "未找到名为(" + args[0] + ")的玩家");
        }
    }

    private void player(CommandSender sender) {
        Player player = (Player) sender;
        if (players.contains(player)) {
            players.remove(player);
            player.sendMessage(ChatColor.RED + "无敌模式已关闭");
        } else {
            players.add(player);
            player.sendMessage(ChatColor.GREEN + "已开启无敌模式");
            now(player);
        }
    }

    private void remove(CommandSender sender, Player player) {
        players.remove(player);
        sender.sendMessage(ChatColor.GREEN + "已关闭玩家（" + player.getName() + ")的无敌模式");
        player.sendMessage(ChatColor.GREEN + "已由管理员关闭你的无敌模式");
    }

    private void add(CommandSender sender, Player player) {
        players.add(player);
        sender.sendMessage(ChatColor.GREEN + "已开启玩家（" + player.getName() + ")的无敌模式");
        player.sendMessage(ChatColor.GREEN + "已由管理员开启你的无敌模式");
        log.warn(SimpleUtil.getFormatDate() + " --- 管理员: " + sender.getName() + ",开启了玩家: " + player.getName() + "的无敌模式");
    }

    private void change(CommandSender sender, Player player) {
        if (players.contains(player)) {
            remove(sender, player);
        } else {
            add(sender, player);
            now(player);
        }
    }

    private void now(Player player) {
        List<Entity> entities = Bukkit.getServer().getWorld(player.getLocation().getWorld().getName()).getEntities();
        for (Entity entity : entities) {
            if (entity instanceof Mob) {
                Mob mob = (Mob) entity;
                if (mob.getTarget() == player) {
                    mob.setTarget(null);
                }
            }
        }
    }

}
