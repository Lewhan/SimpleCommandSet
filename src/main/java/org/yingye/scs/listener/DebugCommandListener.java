package org.yingye.scs.listener;

import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.yingye.scs.command.DebugCommand;

import java.util.HashMap;

public record DebugCommandListener(Plugin plugin) implements Listener {

  private static final HashMap<Player, Location> locations = new HashMap<>();

  @EventHandler
  public void oneHitKillListener(EntityDamageByEntityEvent event) {
    Entity source = event.getDamager();
    if (source instanceof Player) {
      if (DebugCommand.HERCLUES.contains(source)) {
        // 让伤害计算完了再清空血量，不然不会掉落经验值
        new BukkitRunnable() {
          @Override
          public void run() {
            ((Damageable) event.getEntity()).setHealth(0);
          }
        }.runTaskLater(plugin, 0);
      }
    }
  }

  @EventHandler
  public void testerMoveListener(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    if (DebugCommand.CURRENT_LOCATION.contains(player)) {
      if (locations.containsKey(player)) {
        Location saveLocation = locations.get(player);
        Location location = player.getLocation();
        if (saveLocation.getX() != location.getX() || saveLocation.getY() != location.getY() || saveLocation.getZ() != location.getZ()) {
          player.sendMessage("x:" + location.getX() + ", y:" + location.getY() + ", z:" + location.getZ());
          locations.put(player, location);
        }
      } else {
        locations.put(player, player.getLocation());
      }

    }
  }

}
