package org.yingye.scs.core;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;
import org.yingye.scs.util.SimpleUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public class Config {

  public static final Map<String, Integer> TELEPORT = new HashMap<>();
  public static final Map<String, Object> HOME = new HashMap<>();
  public static final Map<String, Integer> WEATHER = new HashMap<>();
  public static final String ConsoleName = "SimpleCommandSet";

  static {
    TELEPORT.put("waitTime", 3);
    TELEPORT.put("cdTime", 5);
    TELEPORT.put("timeout", 60);

    HOME.put("savePath", "./plugins/SimpleCommandSet/data/");

    WEATHER.put("switchSecond", 600);
  }

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
      Set<String> keys = TELEPORT.keySet();
      for (String key : keys) {
        try {
          if (!configurationSection.contains(key)) {
            throw new Exception();
          }
          TELEPORT.put(key, configurationSection.getInt(key));
          write("teleport 的 " + key + " 设置为: " + TELEPORT.get(key), bw);
        } catch (Exception e) {
          write("teleport 的 " + key + " 配置获取失败, 该项使用默认配置: " + TELEPORT.get(key), bw);
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
      Set<String> keys = HOME.keySet();
      for (String key : keys) {
        try {
          if (!configurationSection.contains(key)) {
            throw new Exception();
          }
          HOME.put(key, configurationSection.get(key));
          write("home 的 " + key + " 设置为: " + HOME.get(key), bw);
        } catch (Exception e) {
          write("home 的 " + key + " 配置获取失败, 该项使用默认配置: " + HOME.get(key), bw);
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
      Set<String> keys = WEATHER.keySet();
      for (String key : keys) {
        try {
          if (!configurationSection.contains(key)) {
            throw new Exception();
          }
          WEATHER.put(key, configurationSection.getInt(key));
          write("weather 的 " + key + " 设置为: " + WEATHER.get(key), bw);
        } catch (Exception e) {
          write("weather 的 " + key + " 配置获取失败, 该项使用默认配置: " + WEATHER.get(key), bw);
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

  public static YamlConfiguration getHomeConfig(Player player) {
    File dir = new File(HOME.get("savePath").toString());
    if (!dir.exists()) {
      dir.mkdirs();
    }
    File homeFile = new File(HOME.get("savePath").toString() + player.getDisplayName() + ".yml");
    return checkConfigAvailability(homeFile, player);
  }

  public static void saveHomeConfig(YamlConfiguration config, Player player) {
    try {
      config.save(new File(HOME.get("savePath").toString() + player.getDisplayName() + ".yml"));
    } catch (Exception e) {
      player.sendMessage(ChatColor.RED + "返回点设置失败");
      e.printStackTrace();
    }
  }

  private static YamlConfiguration map2YamlConfiguration(Map<String, Map<String, Map>> source) {
    YamlConfiguration configuration = new YamlConfiguration();
    if (source.get("home") == null) {
      configuration.createSection("home");
    } else {
      configuration.createSection("home", source.get("home"));
    }

    if (source.get("back") == null) {
      configuration.createSection("back");
    } else {
      configuration.createSection("back", source.get("back"));
    }

    return configuration;
  }

  private static YamlConfiguration checkConfigAvailability(File file, Player player) {
    Yaml yaml = new Yaml();
    try {
      Map<String, Map<String, Map>> map = yaml.loadAs(new FileInputStream(file), Map.class);
      if (map != null) {
        Server server = player.getServer();
        StringBuffer sb = new StringBuffer();

        if (map.containsKey("home")) {
          Map<String, Map> homeMap = map.get("home");
          if (homeMap != null && !homeMap.isEmpty()) {
            Set<String> homeKeys = homeMap.keySet();
            for (String key : homeKeys) {
              if (SimpleUtil.createLocation(server, homeMap.get(key)) == null) {
                homeMap.remove(key);
                sb.append(ChatColor.RED + "家:( " + ChatColor.AQUA + key + ChatColor.RED + " )" + "所在的世界已不存在，从home中移除\n");
              }
            }
          }
        }

        if (map.containsKey("back")) {
          Map<String, Map> backMap = map.get("back");
          if (backMap != null && !backMap.isEmpty()) {
            if (SimpleUtil.createLocation(server, backMap.get("back")) == null) {
              backMap.remove("back");
              sb.append(ChatColor.RED + "返回点所在的世界已不存在，已移除");
            }
          }
        }

        if (!sb.toString().equals("")) {
          player.sendMessage(ChatColor.RED + sb.toString());
        }

        YamlConfiguration parse = map2YamlConfiguration(map);
        saveHomeConfig(parse, player);
        return parse;
      } else {
        return null;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

}
