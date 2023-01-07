package org.yingye.scs.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yingye.scs.core.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeTabCompleter implements TabCompleter {

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> options = new ArrayList<>();
        if (sender instanceof Player player && (alias.equalsIgnoreCase("home") || alias.equalsIgnoreCase("delhome"))) {
            if (args.length == 1) {
                File homeFile = new File(Config.HOME.get("savePath").toString() + player.getName() + ".yml");
                if (homeFile.exists()) {
                    YamlConfiguration config = Config.getHomeConfig(player);
                    if (config != null) {
                        ConfigurationSection root = config.getConfigurationSection("home");
                        if (root != null) {
                            options = new ArrayList<>(root.getKeys(false));
                        }
                    }
                }
            }
        }
        return options;
    }

}
