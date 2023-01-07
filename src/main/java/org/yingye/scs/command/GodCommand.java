package org.yingye.scs.command;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yingye.scs.core.Core;
import org.yingye.scs.immutable.SenderIdentity;
import org.yingye.scs.util.Auxiliary;
import org.yingye.scs.util.BukkitTool;

import java.util.HashSet;
import java.util.List;

public class GodCommand implements CommandExecutor {

    private static final HashSet<Player> GOD_PLAYERS = new HashSet<>();

    public static HashSet<Player> getGodPlayers() {
        return GOD_PLAYERS;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        SenderIdentity identity = BukkitTool.getSenderIdentity(sender);
        if (args.length == 0 && identity == SenderIdentity.CONSOLE) {
            Core.printErr("无参数模式只能由玩家使用");
        } else {
            op(sender, args);
        }
        return true;
    }

    private void op(CommandSender sender, String[] args) {
        if (args.length == 0) {
            self((Player) sender);
        } else {
            Player player = sender.getServer().getPlayerExact(args[0]);
            if (player == null) {
                Core.sendErr(sender, "未找到名为(" + args[0] + ")的玩家");
                return;
            }

            boolean isEnable = false;
            if (args.length == 1) {
                isEnable = !GOD_PLAYERS.contains(player);
            } else {
                if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("off")) {
                    isEnable = args[1].equalsIgnoreCase("on");
                } else {
                    Core.sendErr(sender, "未知开关操作符");
                    return;
                }
            }

            if (isEnable) {
                add(sender, player);
                now(player);
            } else {
                remove(sender, player);
            }
        }
    }

    private void self(Player player) {
        if (GOD_PLAYERS.contains(player)) {
            GOD_PLAYERS.remove(player);
            Core.sendWarn(player, "无敌模式已关闭");
        } else {
            GOD_PLAYERS.add(player);
            now(player);
            Core.sendSuccess(player, "已开启无敌模式");
        }
    }

    private void remove(CommandSender sender, Player player) {
        GOD_PLAYERS.remove(player);
        Core.sendSuccess(sender, "已关闭玩家(" + player.getName() + ")的无敌模式");
        Core.sendWarn(player, "已由管理员关闭你的无敌模式");
        Core.printWarn(Auxiliary.getFormatDate() + " --- 管理员: " + sender.getName() + ",关闭了玩家: " + player.getName() + "的无敌模式");
    }

    private void add(CommandSender sender, Player player) {
        GOD_PLAYERS.add(player);
        Core.sendSuccess(sender, "已开启玩家(" + player.getName() + ")的无敌模式");
        Core.sendSuccess(player, "已由管理员开启你的无敌模式");
        Core.printWarn(Auxiliary.getFormatDate() + " --- 管理员: " + sender.getName() + ",开启了玩家: " + player.getName() + "的无敌模式");
    }

    private void now(Player player) {
        World world = Bukkit.getServer().getWorld(player.getLocation().getWorld().getName());
        if(world == null) {
            Core.sendErr(player, "所在世界获取失败, 未能清除生物注视");
        } else {
            List<Entity> entities = world.getEntities();
            for (Entity entity : entities) {
                if (entity instanceof Mob mob) {
                    if (mob.getTarget() == player) {
                        mob.setTarget(null);
                    }
                }
            }
        }
    }

}
