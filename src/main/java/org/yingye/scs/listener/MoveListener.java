package org.yingye.scs.listener;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {

  private static Chunk chunk;

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    Chunk playerChunk = event.getPlayer().getChunk();
    if (playerChunk != chunk) {
      System.out.println(chunk);
      chunk = playerChunk;
    }
  }

}
