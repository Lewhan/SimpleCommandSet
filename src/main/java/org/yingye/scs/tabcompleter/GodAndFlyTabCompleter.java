package org.yingye.scs.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yingye.scs.core.Core;

import java.util.ArrayList;
import java.util.List;

public class GodAndFlyTabCompleter implements TabCompleter {

    /**
     * 参数提示
     *
     * @param sender  触发者
     * @param command 命令实体
     * @param label   命令名
     * @param args    命令参数, 1:要提示的参数一,2:要提示的参数二
     * @return 用于提示的参数
     */
    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (label.endsWith("god") || label.endsWith("fly")) {
            return godAndFly(sender, args);
        } else if (label.endsWith("flyspeed")) {
            return flySpeed(sender, args);
        } else {
            return List.of();
        }
    }

    private List<String> godAndFly(CommandSender sender, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        if (args.length == 1) {
            sender.getServer().getOnlinePlayers().forEach(p -> list.add(p.getName()));
        } else if (args.length == 2) {
            list.add("on");
            list.add("off");
        }
        return list;
    }

    private List<String> flySpeed(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            return op(args);
        } else {
            return console(args);
        }
    }

    private List<String> op(String[] args) {
        return switch (args.length) {
            case 1 -> args[0].equals("") ? List.of("-1", "0.1", "1") : List.of();
            case 2 -> {
                if (args[1].equals("")) {
                    yield Core.SERVER.getOnlinePlayers().stream().map(Player::getName).toList();
                } else {
                    yield Core.SERVER.getOnlinePlayers().stream().map(Player::getName).filter(name -> name.toLowerCase().contains(args[1].toLowerCase())).toList();
                }
            }
            default -> List.of();
        };
    }

    private List<String> console(String[] args) {
        return switch (args.length) {
            case 1 -> {
                if (args[0].equals("")) {
                    yield Core.SERVER.getOnlinePlayers().stream().map(Player::getName).toList();
                } else {
                    yield Core.SERVER.getOnlinePlayers().stream().map(Player::getName).filter(name -> name.toLowerCase().contains(args[0].toLowerCase())).toList();
                }
            }
            case 2 -> args[1].equals("") ? List.of("-1", "0.1", "1") : List.of();
            default -> List.of();
        };
    }

}
