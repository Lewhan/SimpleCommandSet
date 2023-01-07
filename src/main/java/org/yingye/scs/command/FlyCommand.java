package org.yingye.scs.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yingye.scs.core.Core;
import org.yingye.scs.immutable.SenderIdentity;
import org.yingye.scs.util.Auxiliary;
import org.yingye.scs.util.BukkitTool;

import java.util.ArrayList;
import java.util.List;

public class FlyCommand implements CommandExecutor {

    private static final List<Player> FLY_PLAYERS = new ArrayList<>();

    public static List<Player> getFlyPlayers() {
        return List.copyOf(FLY_PLAYERS);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (label.equals("fly") || label.equals(Core.PLUGIN_TAG.toLowerCase() + ":fly")) {
            fly(sender, args);
        } else if (label.equals("flyspeed") || label.equals(Core.PLUGIN_TAG.toLowerCase() + ":flyspeed")) {
            speed(sender, args);
        }
        return true;
    }

    private void fly(CommandSender sender, String[] args) {
        SenderIdentity identity = BukkitTool.getSenderIdentity(sender);
        if (args.length == 0 && identity == SenderIdentity.CONSOLE) {
            Core.printErr("无参数模式只能由玩家使用");
        } else {
            op(sender, args);
        }
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

    private void speed(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                Core.sendErr(player, "请输入-1~1的数值, 正常飞行速度为0.1");
            } else if (args.length == 1) {
                opMode(player, args[0]);
            } else {
                withNormalPlayer(sender, args[0], args[1]);
            }
        } else {
            consoleMode(sender, args);
        }
    }

    private void opMode(Player player, String arg) {
        setSpeed(player, player, arg);
    }

    private void withNormalPlayer(CommandSender sender, String arg, String playerName) {
        Player player = Core.SERVER.getPlayer(playerName);
        if (player == null) {
            Core.sendWarn(sender, "未找到该玩家");
        } else {
            setSpeed(sender, player, arg);
        }
    }

    private void consoleMode(CommandSender sender, String[] args) {
        if (args.length == 0) {
            Core.printErr("请输入玩家名");
        } else if (args.length == 1) {
            if (args[0].equals("")) {
                Core.printErr("请输入玩家名");
            } else {
                Core.printErr("请输入-1~1的数值, 正常飞行速度为0.1");
            }
        } else {
            Player player = Core.SERVER.getPlayer(args[0]);
            if (player == null) {
                Core.sendWarn(sender, "未找到该玩家");
            } else {
                setSpeed(sender, player, args[1]);
            }
        }
    }

    private void setSpeed(CommandSender sender, Player player, String arg) {
        if (Auxiliary.isNumber(arg).isEmpty()) {
            Core.sendErr(player, "参数必须是-1~1的数字, 正常飞行速度为0.1");
        } else {
            float v = Float.parseFloat(arg);
            player.setFlySpeed(Auxiliary.isPositive(arg) ? (v > 1 ? 1 : v) : (v < -1 ? -1 : v));
            if (sender.equals(player)) {
                Core.sendSuccess(player, "设置成功");
            } else {
                Core.sendSuccess(sender, "设置成功");
                Core.sendSuccess(player, "已由管理员将你的飞行速度更改为: " + arg);
            }
        }
    }


}