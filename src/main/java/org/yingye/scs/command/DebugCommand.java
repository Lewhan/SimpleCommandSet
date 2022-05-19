package org.yingye.scs.command;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.yingye.scs.core.Core;
import org.yingye.scs.util.SimpleUtil;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class DebugCommand implements CommandExecutor {

    public static final List<Player> HERCLUES = new ArrayList<>();
    public static final List<Player> CURRENT_LOCATION = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        if (s.equals("ohk") || s.equals("simplecommandset:ohk")) {
            oneHitKill((Player) sender);
        } else if (s.equals("hb") || s.equals("simplecommandset:hb")) {
            healthBoost(sender, args);
        } else if (s.equals("cl") || s.equals("simplecommandset:cl")) {
            currentLocation((Player) sender);
        }
        return true;
    }

    private void oneHitKill(Player player) {
        if (HERCLUES.contains(player)) {
            HERCLUES.remove(player);
            player.sendMessage(ChatColor.GREEN + "已为你关闭一击必杀模式");
        } else {
            HERCLUES.add(player);
            player.sendMessage(ChatColor.GREEN + "已为你开启一击必杀模式");
        }
    }

    private void healthBoost(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return;
        }
        Player player = sender.getServer().getPlayerExact(args[0]);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "未找到名为(" + ChatColor.AQUA + args[0] + ChatColor.RED + ")的玩家");
            return;
        }

        Double health = Double.valueOf(args[1]);
        if (health.isNaN()) {
            sender.sendMessage(ChatColor.RED + "输入的血量有误");
            return;
        }
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        attribute.setBaseValue(health);
        player.setHealth(health);
        sender.sendMessage(ChatColor.GREEN + "成功将玩家(" + ChatColor.AQUA + args[0] + ChatColor.GREEN + ")的血量上限设置为: " + health);
        Core.printWarn(SimpleUtil.getFormatDate() + ChatColor.GREEN + " --- 管理员: " + sender.getName() + ",将玩家: " + player.getName() + "的血量上限更改为: " + health);
    }

    private void currentLocation(Player player) {
        if (CURRENT_LOCATION.contains(player)) {
            CURRENT_LOCATION.remove(player);
            player.sendMessage(ChatColor.GREEN + "已为你关闭坐标提示");
        } else {
            CURRENT_LOCATION.add(player);
            player.sendMessage(ChatColor.GREEN + "已为你开启坐标提示");
        }
    }

    private void spawnDummy() {

    }

}
