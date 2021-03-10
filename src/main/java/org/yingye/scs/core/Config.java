package org.yingye.scs.core;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public class Config {

  public static final Map<String, Integer> teleport = Map.of("waitTime", 3, "cdTime", 5, "timeout", 60);
  public static final Map<String, Object> home = Map.of("savePath", "./plugins/SimpleCommandSet/data/");
  public static final Map<String, Integer> weather = Map.of("switchSecond", 600);
  public static final String ConsoleName = "SimpleCommandSet";

  public static void loadConfig() {
    BufferedWriter bw;
    try {
      bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./plugins/SimpleCommandSet/info.log")));
    } catch (Exception e) {
      System.out.println("[SimpleCommandSet] " + ChatColor.RED + "输出流创建失败，停止加载配置");
      return;
    }

    File file;
    try {
      file = new File("./plugins/SimpleCommandSet/config.yml");
      if (file == null) {
        throw new Exception();
      }
    } catch (Exception e) {
      System.out.println("[SimpleCommandSet] " + ChatColor.RED + "没有找到配置文件, 使用默认配置");
      return;
    }
    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
    try {
      loadTeleportConfig(configuration, bw);
      loadHomeConfig(configuration, bw);
      loadWeatherConfig(configuration, bw);
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void loadTeleportConfig(YamlConfiguration configuration, BufferedWriter bw) throws IOException {
    try {
      ConfigurationSection configurationSection = configuration.getConfigurationSection("teleport");
      Set<String> keys = teleport.keySet();
      for (String key : keys) {
        try {
          if (!configurationSection.contains(key)) {
            throw new Exception();
          }
          teleport.put(key, configurationSection.getInt(key));
          write("teleport 的 " + key + " 设置为: " + teleport.get(key), bw);
        } catch (Exception e) {
          write("teleport 的 " + key + " 配置获取失败, 该项使用默认配置: " + teleport.get(key), bw);
        }
      }
      bw.newLine();
    } catch (Exception e) {
      write("teleport配置获取失败", bw);
    }
  }

  private static void loadHomeConfig(YamlConfiguration configuration, BufferedWriter bw) throws IOException {
    try {
      ConfigurationSection configurationSection = configuration.getConfigurationSection("home");
      Set<String> keys = home.keySet();
      for (String key : keys) {
        try {
          if (!configurationSection.contains(key)) {
            throw new Exception();
          }
          home.put(key, configurationSection.get(key));
          write("home 的 " + key + " 设置为: " + home.get(key), bw);
        } catch (Exception e) {
          write("home 的 " + key + " 配置获取失败, 该项使用默认配置: " + home.get(key), bw);
        }
      }
      bw.newLine();
    } catch (Exception e) {
      write("home配置获取失败", bw);
    }
  }

  private static void loadWeatherConfig(YamlConfiguration configuration, BufferedWriter bw) throws IOException {
    try {
      ConfigurationSection configurationSection = configuration.getConfigurationSection("weather");
      Set<String> keys = weather.keySet();
      for (String key : keys) {
        try {
          if (!configurationSection.contains(key)) {
            throw new Exception();
          }
          weather.put(key, configurationSection.getInt(key));
          write("weather 的 " + key + " 设置为: " + weather.get(key), bw);
        } catch (Exception e) {
          write("weather 的 " + key + " 配置获取失败, 该项使用默认配置: " + weather.get(key), bw);
        }
      }
      bw.newLine();
    } catch (Exception e) {
      write("weather配置获取失败", bw);
    }
  }

  private static void write(String str, BufferedWriter bw) throws IOException {
    bw.write(str);
    bw.flush();
    bw.newLine();
  }

  public static YamlConfiguration getHomeConfig(String playerName) {
    File dir = new File(home.get("savePath").toString());
    if (!dir.exists()) {
      dir.mkdirs();
    }
    File homeFile = new File(home.get("savePath").toString() + playerName + ".yml");
    return YamlConfiguration.loadConfiguration(homeFile);
  }

  public static void saveHomeConfig(YamlConfiguration config, Player player) {
    try {
      config.save(new File(home.get("savePath").toString() + player.getDisplayName() + ".yml"));
    } catch (Exception e) {
      player.sendMessage(ChatColor.RED + "返回点设置失败");
      e.printStackTrace();
    }
  }

}
