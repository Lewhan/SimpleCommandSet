package org.yingye.scs.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.yingye.scs.core.Config;
import org.yingye.scs.util.SimpleUtil;

@SuppressWarnings("all")
public class BackCommand implements CommandExecutor {

  public BackCommand() {
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    // 判断是不是从控制台输入的
    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatColor.RED + " 该命令只能由玩家使用");
    } else {
      Player player = (Player) sender;
      YamlConfiguration config = Config.getHomeConfig(player);
      if (config != null) {
        ConfigurationSection root = config.getConfigurationSection("back");
        if (root != null) {
          if (root.contains("back")) {
            Location location = SimpleUtil.createLocation(sender.getServer(), root.getConfigurationSection("back"));
            if (location != null) {
              player.teleport(location);
            } else {
              player.sendMessage(ChatColor.RED + "暂无返回点");
            }
          } else {
            player.sendMessage(ChatColor.RED + "暂无返回点");
          }
        } else {
          player.sendMessage(ChatColor.RED + "暂无返回点");
        }
      }
    }
    return true;
  }

}
