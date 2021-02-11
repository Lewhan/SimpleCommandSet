package org.yingye.scs.listener;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.yingye.scs.command.WorldCommand;

public class WeatherListener implements Listener {

  @EventHandler
  public void alwaysClear(WeatherChangeEvent event) {
    World world = event.getWorld();
    if (WorldCommand.CLEAR.contains(world)) {
      if (world.isClearWeather()) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void alwaysRain(WeatherChangeEvent event) {
    World world = event.getWorld();
    if (WorldCommand.RAIN.contains(world)) {
      if (!world.isClearWeather()) {
        world.setThundering(false);
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void alwaysThunder(WeatherChangeEvent event) {
    World world = event.getWorld();
    if (WorldCommand.THUNDER.contains(world)) {
      if (!world.isClearWeather()) {
        if (world.isThundering()) {
          event.setCancelled(true);
        }
      }
    }
  }

}
