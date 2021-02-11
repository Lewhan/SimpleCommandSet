package org.yingye.scs.util;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class SimpleUtil {

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
    double x = Double.parseDouble(map.get("x").toString());
    double y = Double.parseDouble(map.get("y").toString());
    double z = Double.parseDouble(map.get("z").toString());
    return new Location(world, x, y, z);
  }

}
