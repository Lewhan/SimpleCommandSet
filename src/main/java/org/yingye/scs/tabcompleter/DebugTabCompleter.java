package org.yingye.scs.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yingye.scs.command.DebugCommand;
import org.yingye.scs.core.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DebugTabCompleter implements TabCompleter {

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (alias.equalsIgnoreCase("showdata")) {
            return showDataCompleter(sender, args);
        } else if (alias.equalsIgnoreCase("hb")) {
            return healthBoostCompleter(sender, args);
        } else if (alias.equalsIgnoreCase("ohk")) {
            return oneHitKillComplete(sender, args);
        } else if (alias.equalsIgnoreCase("cl")) {
            return currentLocation(sender, args);
        } else {
            return new ArrayList<>();
        }
    }

    private List<String> showDataCompleter(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            return playerDataCompleter(sender, args);
        } else {
            return consoleDataCompleter(sender, args);
        }
    }

    private List<String> consoleDataCompleter(CommandSender sender, String[] args) {
        List<String> complete = new ArrayList<>();
        if (args.length == 1) {
            complete = List.of("all", "cllist", "flylist", "godlist", "limit", "ohklist");
        } else if (args.length > 1) {
            String mode = args[0];
            if (DebugCommand.getOpCommand().contains(mode)) {
                return complete;
            } else if (args.length == 2 && (mode.equalsIgnoreCase("all") || mode.equalsIgnoreCase("limit"))) {
                complete = getAllPlayerConfig();
            }
        }
        return complete;
    }

    private List<String> playerDataCompleter(CommandSender sender, String[] args) {
        List<String> complete = new ArrayList<>();
        if (args.length == 1) {
            if (sender.isOp()) {
                complete = List.of("all", "cllist", "flylist", "godlist", "limit", "ohklist");
            } else {
                complete = List.of("all", "limit");
            }
        } else if (args.length > 1) {
            if ((args.length == 2 && args[0].equalsIgnoreCase("all") && sender.isOp())
                    || (args.length == 3 && args[0].equalsIgnoreCase("limit") && sender.isOp())) {
                complete = getAllPlayerConfig();
            }
        }
        return complete;
    }

    private List<String> getAllPlayerConfig() {
        List<String> complete = new ArrayList<>();
        File dir = new File(Config.HOME.get("savePath").toString());
        if (dir.exists()) {
            File[] files = dir.listFiles();
            assert files != null;
            for (File file : files) {
                complete.add(file.getName().substring(0, file.getName().lastIndexOf(".")));
            }
        }
        return complete;
    }

    private List<String> healthBoostCompleter(CommandSender sender, String[] args) {
        List<String> complete = new ArrayList<>();
        if (args.length == 1) {
            complete.add("20");
            sender.getServer().getOnlinePlayers().forEach(p -> complete.add(p.getName()));
        } else if (args.length == 2) {
            complete.add("20");
        }
        return complete;
    }

    private List<String> oneHitKillComplete(CommandSender sender, String[] args) {
        List<String> complete = new ArrayList<>();
        if (args.length == 1) {
            sender.getServer().getOnlinePlayers().forEach(p -> complete.add(p.getName()));
        }
        return complete;
    }

    private List<String> currentLocation(CommandSender sender, String[] args) {
        List<String> complete = new ArrayList<>();
        if (args.length == 1) {
            sender.getServer().getOnlinePlayers().forEach(p -> complete.add(p.getName()));
        }
        return complete;
    }

}
