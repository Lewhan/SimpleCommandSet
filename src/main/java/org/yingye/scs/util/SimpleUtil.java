package org.yingye.scs.util;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SimpleUtil {

  private static final String format = "yyyy-MM-dd HH:mm:ss";

  public static Boolean users(CommandSender sender, String[] args) {
    Boolean flag;
    if (!(sender instanceof Player) && args.length <= 0) {
      flag = null;
    } else {
      flag = sender.isOp() && args.length > 0;
    }
    return flag;
  }

  public static Location createLocation(Server server, Map<String, Object> map) {
    World world = server.getWorld((String) map.get("world"));
    if (world == null) {
      return null;
    }
    double x = Double.parseDouble(map.get("x").toString());
    double y = Double.parseDouble(map.get("y").toString());
    double z = Double.parseDouble(map.get("z").toString());
    float pitch = Float.parseFloat(map.get("pitch").toString());
    float yaw = Float.parseFloat(map.get("yaw").toString());
    return new Location(world, x, y, z, yaw, pitch);
  }

  /**
   * 用于解决back无法自动转换的问题
   *
   * @param server        server
   * @param configuration 配置
   * @return 返回点对应的Location对象
   */
  public static Location createLocation(Server server, ConfigurationSection configuration) {
    Map<String, Object> map = new HashMap<>();
    map.put("world", configuration.getString("world"));
    map.put("x", configuration.getDouble("x"));
    map.put("y", configuration.getDouble("y"));
    map.put("z", configuration.getDouble("z"));
    map.put("pitch", configuration.getString("pitch"));
    map.put("yaw", configuration.getString("yaw"));
    return createLocation(server, map);
  }

  public static String getFormatDate() {
    return getFormatDate(new Date(), format);
  }

  public static String getFormatDate(Date date) {
    return getFormatDate(date, format);
  }

  public static String getFormatDate(Date date, String format) {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    return sdf.format(date);
  }

}
