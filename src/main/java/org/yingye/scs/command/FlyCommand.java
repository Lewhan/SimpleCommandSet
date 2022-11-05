package org.yingye.scs.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.yingye.scs.core.Core;
import org.yingye.scs.enums.SenderIdentity;
import org.yingye.scs.util.Auxiliary;
import org.yingye.scs.util.BukkitTool;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class FlyCommand implements CommandExecutor {

    private static final List<Player> FLY_PLAYERS = new ArrayList<>();

    public static List<Player> getFlyPlayers() {
        return List.copyOf(FLY_PLAYERS);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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

            boolean allow = false;
            if (args.length == 1) {
                allow = !player.getAllowFlight();
            } else {
                if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("off")) {
                    allow = args[1].equalsIgnoreCase("on");
                } else {
                    Core.sendErr(sender, "未知开关操作符");
                    return;
                }
            }

            switchFly(sender, player, allow);
        }
    }

    private void self(Player player) {
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            FLY_PLAYERS.remove(player);
            Core.sendWarn(player, "飞行模式已关闭");
        } else {
            player.setAllowFlight(true);
            FLY_PLAYERS.add(player);
            Core.sendSuccess(player, "飞行模式已开启");
        }
    }

    private void switchFly(CommandSender sender, Player player, boolean allow) {
        player.setAllowFlight(allow);
        if (allow) {
            FLY_PLAYERS.add(player);
            Core.sendSuccess(sender, "已开启玩家(" + player.getName() + ")的飞行模式");
            Core.sendSuccess(player, "已由管理员开启你的飞行模式");
            Core.printWarn(Auxiliary.getFormatDate() + " --- 管理员: " + sender.getName() + ",开启了玩家: " + player.getName() + "的飞行模式");
        } else {
            FLY_PLAYERS.remove(player);
            Core.sendSuccess(sender, "已关闭玩家(" + player.getName() + ")的飞行模式");
            Core.sendWarn(player, "已由管理员关闭你的飞行模式");
            Core.printWarn(Auxiliary.getFormatDate() + " --- 管理员: " + sender.getName() + ",关闭了玩家: " + player.getName() + "的飞行模式");
        }
    }

}