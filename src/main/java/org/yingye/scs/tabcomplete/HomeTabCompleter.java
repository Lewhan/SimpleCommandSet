package org.yingye.scs.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.yingye.scs.core.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class HomeTabCompleter implements TabCompleter {

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    ArrayList<String> options = new ArrayList<>();
    if (sender instanceof Player && (alias.equalsIgnoreCase("home") || alias.equalsIgnoreCase("delhome"))) {
      if (args.length == 1) {
        Player player = (Player) sender;
        File homeFile = new File(Config.homeSavePath + player.getDisplayName() + ".yml");
        if (!homeFile.exists()) {
          return options;
        } else {
          YamlConfiguration config = YamlConfiguration.loadConfiguration(homeFile);
          ConfigurationSection root = config.getConfigurationSection("home");
          if (root != null) {
            options = new ArrayList<>(root.getKeys(true));
          }
        }
      }
    }
    return options;
  }

}