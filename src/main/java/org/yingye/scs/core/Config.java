package org.yingye.scs.core;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

@SuppressWarnings("all")
public class Config {

  public static int teleportWaitTime;
  public static int teleportCdTime;
  public static int teleportOutTime;
  public static String homeSavePath;
  public static int weatherSecond;

  public static void loadConfig() {
    File file = new File("./plugins/SimpleCommandSet/config.yml");
    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

    // 获取tp相关的设置
    ConfigurationSection tpa = configuration.getConfigurationSection("tpa");
    teleportWaitTime = tpa.getInt("teleportWaitTime");
    teleportCdTime = tpa.getInt("teleportCdTime");
    teleportOutTime = tpa.getInt("teleportOutTime");

    // 获取home相关的设置
    ConfigurationSection home = configuration.getConfigurationSection("home");
    homeSavePath = home.getString("savepath");

    // 获取weather相关的设置
    ConfigurationSection weather = configuration.getConfigurationSection("weather");
    weatherSecond = weather.getInt("second");
  }

  public static YamlConfiguration getHomeConfig(String playerName) {
    File dir = new File(homeSavePath);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    File homeFile = new File(homeSavePath + playerName + ".yml");
    return YamlConfiguration.loadConfiguration(homeFile);
  }

  public static void saveHomeConfig(YamlConfiguration config, Player player) {
    try {
      config.save(new File(homeSavePath + player.getDisplayName() + ".yml"));
    } catch (Exception e) {
      player.sendMessage(ChatColor.RED + "返回点设置失败");
      e.printStackTrace();
    }
  }

}
