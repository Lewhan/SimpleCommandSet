package org.yingye.scs.core;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;
import org.yingye.scs.util.BukkitTool;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Config {

    public static final HashMap<String, Integer> TELEPORT = new HashMap<>(Map.of("waitTime", 3, "cdTime", 5, "timeout", 60));
    public static final HashMap<String, Object> HOME = new HashMap<>(Map.of("savePath", "./plugins/SimpleCommandSet/data/"));
    public static final HashMap<String, Integer> WEATHER = new HashMap<>(Map.of("switchSecond", 600));
    public static final int TAB_SIZE = 4;

    public static void loadConfig() {
        BufferedWriter bw;
        File outLog = new File("plugins/SimpleCommandSet/info.log");
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outLog)));
        } catch (Exception e) {
            Core.printErr("输出流创建失败，停止加载并使用默认配置");
            return;
        }

        File file;
        try {
            file = new File("plugins/SimpleCommandSet/config.yml");
        } catch (Exception e) {
            Core.printInfo("没有找到配置文件, 使用默认配置");
            return;
        }
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        try {
            loadTeleportConfig(configuration, bw);
            loadHomeConfig(configuration, bw);
            loadWeatherConfig(configuration, bw);
            bw.close();
            Core.printSuccess("配置文件加载完毕，日志路径: " + outLog.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Core.printErr("配置文件加载失败，日志路径: " + outLog.getAbsolutePath());
            try {
                bw.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private static void loadTeleportConfig(YamlConfiguration configuration, BufferedWriter bw) throws IOException {
        try {
            ConfigurationSection configurationSection = configuration.getConfigurationSection("teleport");
            assert configurationSection != null;
            Set<String> keys = TELEPORT.keySet();
            for (String key : keys) {
                if (!configurationSection.contains(key)) {
                    write("teleport 的 " + key + " 配置获取失败, 该项使用默认配置: " + TELEPORT.get(key), bw);
                    continue;
                }
                TELEPORT.put(key, configurationSection.getInt(key));
                write("teleport 的 " + key + " 设置为: " + TELEPORT.get(key), bw);
            }
            bw.newLine();
        } catch (Exception e) {
            write("teleport配置获取失败，使用默认配置", bw);
        }
    }

    private static void loadHomeConfig(YamlConfiguration configuration, BufferedWriter bw) throws IOException {
        try {
            ConfigurationSection configurationSection = configuration.getConfigurationSection("home");
            assert configurationSection != null;
            Set<String> keys = HOME.keySet();
            for (String key : keys) {
                if (!configurationSection.contains(key)) {
                    write("home 的 " + key + " 配置获取失败, 该项使用默认配置: " + HOME.get(key), bw);
                    continue;
                }
                HOME.put(key, configurationSection.get(key));
                write("home 的 " + key + " 设置为: " + HOME.get(key), bw);
            }
            bw.newLine();
        } catch (Exception e) {
            write("home配置获取失败，使用默认配置", bw);
        }
    }

    private static void loadWeatherConfig(YamlConfiguration configuration, BufferedWriter bw) throws IOException {
        try {
            ConfigurationSection configurationSection = configuration.getConfigurationSection("weather");
            assert configurationSection != null;
            Set<String> keys = WEATHER.keySet();
            for (String key : keys) {
                if (!configurationSection.contains(key)) {
                    write("weather 的 " + key + " 配置获取失败, 该项使用默认配置: " + WEATHER.get(key), bw);
                    continue;
                }
                WEATHER.put(key, configurationSection.getInt(key));
                write("weather 的 " + key + " 设置为: " + WEATHER.get(key), bw);
            }
            bw.newLine();
        } catch (Exception e) {
            write("weather配置获取失败，使用默认配置", bw);
        }
    }

    private static void write(String str, BufferedWriter bw) throws IOException {
        bw.write(str);
        bw.flush();
        bw.newLine();
    }

    public static File getPlayerConfigFile(String name) {
        return new File(HOME.get("savePath").toString() + name + ".yml");
    }

    public static YamlConfiguration getHomeConfig(Player player) {
        File dir = new File(HOME.get("savePath").toString());
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Core.printErr("玩家数据存放文件夹创建失败! 文件夹路径: " + dir.getAbsolutePath());
            }
        }
        // 文件可能不存在
        File homeFile = getPlayerConfigFile(player.getName());
        return checkConfigAvailability(homeFile, player);
    }

    public static void saveHomeConfig(YamlConfiguration config, Player player) {
        try {
            config.save(getPlayerConfigFile(player.getName()));
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "返回点设置失败");
            e.printStackTrace();
        }
    }

    private static YamlConfiguration map2YamlConfiguration(Map<String, Map<String, Map<String, Object>>> source) {
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

    @SuppressWarnings("unchecked")
    private static YamlConfiguration checkConfigAvailability(File file, Player player) {
        // 如果配置文件不存在则给出一个空的map
        if (!file.exists()) {
            return map2YamlConfiguration(new HashMap<>());
        }
        Yaml yaml = new Yaml();
        try {
            // 因为读取出的Map的泛型不确定，所以会报unchecked的警告
            Map<String, Map<String, Map<String, Object>>> map = yaml.loadAs(new FileInputStream(file), Map.class);

            if (map == null) {
                // 如果配置文件是个空文件，也给出一个空的map
                return map2YamlConfiguration(new HashMap<>());
            } else {
                Server server = player.getServer();
                StringBuilder sb = new StringBuilder();

                if (map.containsKey("home")) {
                    Map<String, Map<String, Object>> homeMap = map.get("home");
                    if (homeMap != null && !homeMap.isEmpty()) {
                        Set<String> homeKeys = homeMap.keySet();
                        for (String key : homeKeys) {
                            if (BukkitTool.createLocation(server, homeMap.get(key)) == null) {
                                homeMap.remove(key);
                                sb.append(ChatColor.RED).append("家").append(ChatColor.AQUA).append(key).append(ChatColor.RED).append("所在的世界已不存在，从home中移除\n");
                            }
                        }
                    }
                }

                if (map.containsKey("back")) {
                    Map<String, Map<String, Object>> backMap = map.get("back");
                    if (backMap != null && !backMap.isEmpty()) {
                        if (BukkitTool.createLocation(server, backMap.get("back")) == null) {
                            backMap.remove("back");
                            sb.append(ChatColor.RED).append("返回点所在的世界已不存在，已移除");
                        }
                    }
                }

                if (!sb.toString().equals("")) {
                    Core.sendErr(player, sb.toString());
                }

                YamlConfiguration parse = map2YamlConfiguration(map);
                saveHomeConfig(parse, player);
                return parse;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
