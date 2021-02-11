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

@SuppressWarnings("all")
public class BackCommand implements CommandExecutor {

  public BackCommand() {
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    // 判断是不是从控制台输入的
    if (!(sender instanceof Player)) {
      sender.sendMessage("[SimpleCommandSet]" + ChatColor.RED + " 该命令只能由玩家使用");
    } else {
      Player player = (Player) sender;
      YamlConfiguration config = Config.getHomeConfig(player.getDisplayName());
      ConfigurationSection root = config.getConfigurationSection("back");
      if (root != null) {
        if (root.contains("back")) {
          Location location = root.getSerializable("back", Location.class);
          if (location != null) {
            player.teleport(location);
          } else {
            player.sendMessage(ChatColor.RED + "无法返回到上一个位置");
          }
        } else {
          player.sendMessage(ChatColor.RED + "无法返回到上一个位置");
        }
      } else {
        player.sendMessage(ChatColor.RED + "无法返回到上一个位置");
      }
    }
    return true;
  }

}
