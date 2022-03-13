package org.yingye.scs.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    @EventHandler
    public void onExecCommand(PlayerCommandPreprocessEvent event) {
        // 打印的信息是：PlayerCommandPreprocessEvent
//    System.out.println(event.getEventName());
        // 打印的信息是：玩家输入的指令
//    System.out.println(event.getMessage());
    }

}
