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

import java.io.File;

@SuppressWarnings("all")
public class HomeCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player) {
      if (label.equalsIgnoreCase("sethome")) {
        setHome((Player) sender, args);
      } else {
        home((Player) sender, args, label);
      }
    } else {
      sender.sendMessage(ChatColor.RED + "该命令只能由玩家使用");
    }
    return true;
  }

  private void setHome(Player player, String[] args) {
    if (args.length != 1) {
      player.sendMessage(ChatColor.RED + "请输入该 home 的名字");
      return;
    } else {
      try {
        File dir = new File(Config.homeSavePath);
        if (!dir.exists()) {
          dir.mkdirs();
        }
        File homeFile = new File(Config.homeSavePath + player.getDisplayName() + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        if (homeFile.exists()) {
          config = YamlConfiguration.loadConfiguration(homeFile);
        }
        if (!config.contains("home")) {
          config.createSection("home");
        }
        ConfigurationSection root = config.getConfigurationSection("home");
        root.set(args[0], player.getLocation());
        config.save(homeFile);
        player.sendMessage(ChatColor.GREEN + "家设置成功");
      } catch (Exception e) {
        player.sendMessage(ChatColor.RED + "家设置失败");
        e.printStackTrace();
      }
    }
  }

  private void home(Player player, String[] args, String label) {
    if (args.length != 1) {
      player.sendMessage(ChatColor.RED + "未输入家的名字");
      return;
    } else {
      File homeFile = new File(Config.homeSavePath + player.getDisplayName() + ".yml");
      // 判断文件存不存在
      if (!homeFile.exists()) {
        player.sendMessage(ChatColor.RED + "没有这个家");
        return;
      }
      YamlConfiguration config = YamlConfiguration.loadConfiguration(homeFile);
      ConfigurationSection root = config.getConfigurationSection("home");
      if (root == null) {
        player.sendMessage(ChatColor.RED + "没有这个家");
        return;
      }
      // 判断键值对存不存在
      if (!root.contains(args[0])) {
        player.sendMessage(ChatColor.RED + "没有这个家");
        return;
      }
      if (label.equalsIgnoreCase("home")) {
        Location serializable = root.getSerializable(args[0], Location.class);
        if (serializable == null) {
          player.sendMessage(ChatColor.RED + "无法回到这个家");
        } else {
          player.teleport(serializable);
          player.sendMessage(ChatColor.GREEN + "回家成功");
        }
      } else {
        try {
          root.set(args[0], null);
          config.save(homeFile);
          player.sendMessage(ChatColor.GREEN + "家删除成功");
        } catch (Exception e) {
          player.sendMessage(ChatColor.GREEN + "家删除失败");
          e.printStackTrace();
        }
      }
    }
  }

}