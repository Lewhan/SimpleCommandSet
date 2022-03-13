package org.yingye.scs.listener;

import org.bukkit.Chunk;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.yingye.scs.command.GodCommand;

import java.util.HashSet;

public class GodListener implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player obj) {
            HashSet<Player> players = GodCommand.getPlayers();
            for (Player player : players) {
                if (obj == player) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @SuppressWarnings("all")
    @EventHandler
    public void onMonsterTargetIsPlayer(EntityTargetEvent event) {
        if (event.getEntity() instanceof Mob) {
//      System.out.println("生物: " + event.getEntity() + " 的目标是: " + event.getTarget());
            Entity target = event.getTarget();
            HashSet<Player> players = GodCommand.getPlayers();
            for (Player player : players) {
                if (target == player) {
                    event.setCancelled(true);
                    Mob mob = (Mob) event.getEntity();
                    Chunk chunk = mob.getChunk();
//          System.out.println("ChunK:\tX: " + (chunk.getX() * 16) + " , Z: " + (chunk.getZ() * 16));
                    Entity[] entities = chunk.getEntities();
                    for (Entity entity : entities) {
                        EntityType type = entity.getType();
                        if (type == EntityType.VILLAGER && mob.getType() == EntityType.ZOMBIE) {
                            mob.setTarget((LivingEntity) entity);
                        } else if (type == EntityType.PLAYER && !players.contains(entity)) {
                            mob.setTarget((LivingEntity) entity);
                        }
                    }
                }
            }
        }
    }

}
