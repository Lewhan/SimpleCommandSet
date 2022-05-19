package org.yingye.scs.command;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.yingye.scs.core.Config;
import org.yingye.scs.core.Core;

import java.util.*;

public class TeleportCommand implements CommandExecutor {

    /**
     * 存放等待使用的Runnable<br>
     * ArrayList<String> 0:发起者的玩家名,1:目标玩家名,2:命令名
     */
    private static final HashMap<ArrayList<String>, BukkitRunnable> DATA = new HashMap<>();

    /**
     * 存放销毁TP请求的Runnable
     * ArrayList<String> 0:发起者的玩家名,1:目标玩家名,2:命令名
     */
    private static final HashMap<ArrayList<String>, BukkitRunnable> DESTROY = new HashMap<>();

    /**
     * 进入命令冷却状态的玩家
     */
    private static final HashSet<Player> WAIT = new HashSet<>();

    /**
     * 命令触发器 & 方法调配中心，调用命令对应的方法
     *
     * @param sender  触发者
     * @param command 命令实体
     * @param label   命令名
     * @param args    参数,args不会为空,args索引从0开始,且其中不包括输入的命令
     * @return 是否回显
     */
    @SuppressWarnings("all")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 判断是不是从控制台输入的
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + " 该命令只能由玩家使用");
            return true;
        }

        // 将命令名小写，以防后面的使用出问题
        label = label.toLowerCase();

        // 判断是哪个命令
        if (label.equalsIgnoreCase("tpa") || label.equalsIgnoreCase("simplecommandset:tpa")
                || label.equalsIgnoreCase("tpahere") || label.equalsIgnoreCase("simplecommandset:tpahere")) {
            teleport((Player) sender, label, args);
        } else if (label.equalsIgnoreCase("accept") || label.equalsIgnoreCase("simplecommandset:accept")) {
            accept((Player) sender, args);
        } else if (label.equalsIgnoreCase("deaccept") || label.equalsIgnoreCase("simplecommandset:deaccept")) {
            deAccept((Player) sender, args);
        } else if (label.equalsIgnoreCase("tpclear") || label.equalsIgnoreCase("simplecommandset:tpclear")) {
            clearTeleportRequest((Player) sender, args);
        }
        return true;
    }

    /**
     * 发起tpa和tpahere请求
     *
     * @param player 触发命令的玩家
     * @param label  触发的命令
     * @param args   附加的参数
     */
    private void teleport(Player player, String label, String[] args) {
        if ((label.equalsIgnoreCase("tpa") || label.equalsIgnoreCase("simplecommandset:tpa")) && WAIT.contains(player)) {
            player.sendMessage("tpa命令的使用间隔为: " + Config.TELEPORT.get("cdTime") + " 秒");
            return;
        }

        Server server = player.getServer();
        // 判断参数长度是否有传入玩家名
        if (args.length <= 0) {
            player.sendMessage(ChatColor.RED + "请输入玩家名");
            return;
        }

        // 获取目标玩家实体
        Player targetPlayer = server.getPlayerExact(args[0]);
        if (targetPlayer == null) {
            player.sendMessage(ChatColor.GREEN + "未找到名为(" + ChatColor.AQUA + args[0] + ChatColor.GREEN + ")的玩家");
            return;
        }

        // 判断是否为自己
        if (player == targetPlayer) {
            player.sendMessage(ChatColor.RED + "不能对自己发起请求");
            return;
        }

        // 判断是否存在请求
        ArrayList<String> param = new ArrayList<>();
        param.add(player.getName());
        param.add(targetPlayer.getName());
        param.add(label);
        if (DATA.get(param) != null) {
            player.sendMessage(ChatColor.GREEN + "您已存在一个发送给该玩家的 " + label + " 请求,请等待对方接受或拒绝请求,或请求超时后再次发送请求");
            return;
        }

        // 创建TP用的Runnable
        BukkitRunnable runnable = createRunnable(player, targetPlayer, label);
        DATA.put(param, runnable);

        // 给予提示
        player.sendMessage(ChatColor.GREEN + "请求已发送");
        if (label.equalsIgnoreCase("tpa")) {
            targetPlayer.sendMessage(ChatColor.GREEN + "玩家(" + ChatColor.AQUA + player.getName() + ChatColor.GREEN + ")请求传送到你这");
            // 进入命令冷却期
            WAIT.add(player);
            // 指定秒数之后恢复
            new BukkitRunnable() {
                @Override
                public void run() {
                    WAIT.remove(player);
                }
            }.runTaskLater(Core.getPlugin(), Config.TELEPORT.get("cdTime") * 20);
        } else {
            targetPlayer.sendMessage(ChatColor.GREEN + "玩家(" + ChatColor.AQUA + player.getName() + ChatColor.GREEN + ")邀请你过去");
        }
        targetPlayer.sendMessage(ChatColor.GREEN + "输入 " + ChatColor.RED + "/accept " + ChatColor.GREEN + "接受请求");
        targetPlayer.sendMessage(ChatColor.GREEN + "输入 " + ChatColor.RED + "/deaccept " + ChatColor.GREEN + "拒绝请求");

        // 创建销毁TP Runnable的Runnable
        BukkitRunnable destroy = new BukkitRunnable() {
            @Override
            public void run() {
                ArrayList<String> array = new ArrayList<>();
                array.add(player.getName());
                array.add(targetPlayer.getName());
                array.add(label);
                new Thread(DATA.get(array)).interrupt();
                DATA.remove(array);
                DESTROY.remove(array);
                player.sendMessage(ChatColor.YELLOW + "发送给(" + ChatColor.AQUA + targetPlayer.getName() + ChatColor.YELLOW + ")的 " + label + " 请求已超时");
                targetPlayer.sendMessage(ChatColor.YELLOW + "来自玩家(" + ChatColor.AQUA + player.getName() + ChatColor.YELLOW + ")的 " + label + " 请求已超时");
            }
        };

        // 设置多少秒后自动执行销毁TP用的Runnable
        destroy.runTaskLater(Core.getPlugin(), Config.TELEPORT.get("timeout") * 20L);

        // 将销毁用的Runnable放入DESTROY中，当接收或拒绝请求的时候，取消并删除该Runnable
        DESTROY.put(param, destroy);
    }

    /**
     * accept的调配中心，决定使用哪一种accept方法
     *
     * @param player 触发命令的玩家
     * @param args   附加的参数
     */
    private void accept(Player player, String[] args) {
        Server server = player.getServer();
        if (args.length <= 0) {
            accept(server, player);
        } else if (args.length == 1) {
            accept(server, player, args[0]);
        } else {
            accept(server, player, args[0], args[1]);
        }
    }

    /**
     * 接收指定玩家的指定请求
     *
     * @param server     服务器对象
     * @param player     触发命令的玩家
     * @param sourceName 发起请求的玩家名
     * @param method     接收的请求方式
     */
    private void accept(Server server, Player player, String sourceName, String method) {
        Player source = server.getPlayerExact(sourceName);
        if (source == null) {
            player.sendMessage(ChatColor.GREEN + "未找到该玩家，该玩家可能已离线");
            return;
        }
        ArrayList<String> key = (ArrayList<String>) Arrays.asList(new String[]{sourceName, player.getName(), method});
        BukkitRunnable runnable = DATA.remove(key);
        BukkitRunnable destroy = DESTROY.remove(key);
        sendAcceptMessage(runnable, destroy, player, Objects.requireNonNull(server.getPlayerExact(sourceName)), method);
    }

    /**
     * 接收指定玩家的请求
     *
     * @param server     服务器对象
     * @param player     触发命令的玩家
     * @param sourceName 发起请求的玩家名
     */
    private void accept(Server server, Player player, String sourceName) {
        Player source = server.getPlayerExact(sourceName);
        if (source == null) {
            player.sendMessage(ChatColor.GREEN + "未找到该玩家，该玩家可能已离线");
            return;
        }

        List<ArrayList<String>> keys = DATA.keySet()
                .stream()
                .filter(al -> al.get(0).equals(sourceName) && al.get(1).equals(player.getName())).toList();
        if (keys.size() <= 0) {
            player.sendMessage(ChatColor.GREEN + "未找到玩家(" + ChatColor.AQUA + sourceName + ChatColor.GREEN + ")对您发送的请求");
            return;
        }

        BukkitRunnable runnable = DATA.remove(keys.get(0));
        BukkitRunnable destroy = DESTROY.remove(keys.get(0));
        sendAcceptMessage(runnable, destroy, player, source, keys.get(0).get(2));
    }

    /**
     * 接收请求
     *
     * @param server 服务器对象
     * @param player 触发命令的玩家
     */
    private void accept(Server server, Player player) {
        List<ArrayList<String>> keys = DATA.keySet()
                .stream()
                .filter(al -> al.get(1).equals(player.getName()))
                .toList();
        if (keys.size() <= 0) {
            player.sendMessage(ChatColor.GREEN + "没有待处理的请求");
            return;
        }

        ArrayList<String> key = keys.get(0);
        BukkitRunnable runnable = DATA.remove(key);
        BukkitRunnable destroy = DESTROY.remove(key);
        sendAcceptMessage(runnable, destroy, player, Objects.requireNonNull(server.getPlayerExact(key.get(0))), key.get(2));
    }

    /**
     * 接收请求并发送提示信息
     *
     * @param runnable 传送用的BukkitRunnable
     * @param destroy  定时清除请求用的BukkitRunnable
     * @param player   触发命令的玩家
     * @param source   发起请求的玩家
     * @param method   请求的方式
     */
    private void sendAcceptMessage(BukkitRunnable runnable, BukkitRunnable destroy, Player player, Player source, String method) {
        player.sendMessage(ChatColor.GREEN + "你接受了玩家(" + ChatColor.AQUA + source.getName() + ChatColor.GREEN + ")的 " + method + " 请求");
        source.sendMessage(ChatColor.GREEN + "玩家(" + ChatColor.AQUA + player.getName() + ChatColor.GREEN + ")接受了你的 " + method + " 请求,将在" + Config.TELEPORT.get("waitTime") + "秒后进行传送");
        destroy.cancel();
        runnable.runTaskLater(Core.getPlugin(), Config.TELEPORT.get("waitTime") * 20L);
    }

    /**
     * 创建一个待使用的BukkitRunnable
     *
     * @param initiator 发起者
     * @param target    目标
     * @param label     命令名
     * @return 等待使用的BukkitRunnable
     */
    private BukkitRunnable createRunnable(Player initiator, Player target, String label) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (label.equalsIgnoreCase("tpa")) {
                    initiator.teleport(target.getLocation());
                } else {
                    target.teleport(initiator.getLocation());
                }
            }
        };
    }

    /**
     * 拒绝请求的调配中心，调用对应的方法
     *
     * @param player 触发命令的玩家
     * @param args   附加的参数
     */
    private void deAccept(Player player, String[] args) {
        Server server = player.getServer();
        if (args.length <= 0) {
            deAccept(player, server);
        } else if (args.length == 1) {
            deAccept(player, server, args[0]);
        } else {
            deAccept(player, server, args[0], args[1]);
        }
    }

    /**
     * 拒绝请求
     *
     * @param player     触发命令的玩家
     * @param server     服务器对象
     * @param sourceName 要拒绝的玩家名
     * @param method     何种请求方式
     */
    private void deAccept(Player player, Server server, String sourceName, String method) {
        Player source = server.getPlayerExact(sourceName);
        if (source == null) {
            player.sendMessage(ChatColor.GREEN + "未找到该玩家，该玩家可能已离线");
            return;
        }

        ArrayList<String> key = (ArrayList<String>) Arrays.asList(new String[]{sourceName, player.getName(), method});
        BukkitRunnable bukkitRunnable = DATA.remove(key);
        if (bukkitRunnable == null) {
            player.sendMessage(ChatColor.GREEN + "未找到玩家(" + ChatColor.AQUA + sourceName + ChatColor.GREEN + ")对您发送的" + method + "请求");
            return;
        }

        sendDeAcceptMessage(bukkitRunnable, player, source, sourceName, method);
    }

    /**
     * 拒绝请求
     *
     * @param player     触发命令的玩家
     * @param server     服务器对象
     * @param sourceName 要拒绝的玩家名
     */
    private void deAccept(Player player, Server server, String sourceName) {
        Player source = server.getPlayerExact(sourceName);
        if (source == null) {
            player.sendMessage(ChatColor.GREEN + "未找到该玩家，该玩家可能已离线");
            return;
        }

        List<ArrayList<String>> collect = DATA.keySet()
                .stream()
                .filter(al -> al.get(0).equals(sourceName) && al.get(1).equals(player.getName())).toList();
        if (collect.size() <= 0) {
            player.sendMessage(ChatColor.GREEN + "未找到玩家(" + ChatColor.AQUA + sourceName + ChatColor.GREEN + ")对您发送的请求");
            return;
        }

        BukkitRunnable bukkitRunnable = DATA.remove(collect.get(0));
        sendDeAcceptMessage(bukkitRunnable, player, source, sourceName, collect.get(0).get(2));
    }

    /**
     * 从 DATA 中获取符合条件的第一个请求并拒绝
     *
     * @param player 触发命令的玩家
     * @param server 服务器对象
     */
    private void deAccept(Player player, Server server) {
        List<ArrayList<String>> keys = DATA.keySet().stream().filter(al -> al.get(1).equals(player.getName())).toList();
        if (keys.size() <= 0) {
            player.sendMessage(ChatColor.GREEN + "还没有玩家向你发起请求");
            return;
        }

        ArrayList<String> key = keys.get(0);
        BukkitRunnable bukkitRunnable = DATA.remove(key);
        sendDeAcceptMessage(bukkitRunnable, player, server.getPlayerExact(key.get(0)), key.get(0), key.get(2));
    }

    /**
     * 清除请求的调配中心
     *
     * @param player 触发命令的对象
     * @param args   附加的参数
     */
    private void clearTeleportRequest(Player player, String[] args) {
        Server server = player.getServer();
        if (args.length <= 0) {
            clearTeleportRequest(player, server);
        } else {
            clearTeleportRequest(player, server, args[0]);
        }
    }

    /**
     * 清除来自指定玩家的请求
     *
     * @param player     触发命令的玩家
     * @param server     服务器对象
     * @param sourceName 要拒绝的玩家名
     */
    private void clearTeleportRequest(Player player, Server server, String sourceName) {
        List<ArrayList<String>> keys = DATA.keySet()
                .stream()
                .filter(al -> al.get(1).equals(player.getName()) && al.get(0).equals(sourceName)).toList();
        keys.forEach(strings -> {
            BukkitRunnable bukkitRunnable = DATA.remove(strings);
            sendDeAcceptMessage(bukkitRunnable, player, server.getPlayerExact(sourceName), sourceName, strings.get(2));
        });
    }

    /**
     * 清除所有的请求
     *
     * @param player 触发命令的玩家
     * @param server 服务器对象
     */
    private void clearTeleportRequest(Player player, Server server) {
        List<ArrayList<String>> keys = DATA.keySet()
                .stream()
                .filter(al -> al.get(1).equals(player.getName())).toList();
        keys.forEach(strings -> {
            BukkitRunnable bukkitRunnable = DATA.remove(strings);
            sendDeAcceptMessage(bukkitRunnable, player, server.getPlayerExact(strings.get(0)), strings.get(0), strings.get(2));
        });
    }

    /**
     * 拒绝请求后的提示信息
     *
     * @param runnable   请求对应的BukkitRunnable对象
     * @param player     触发命令的玩家
     * @param source     要拒绝的玩家
     * @param sourceName 要拒绝的玩家名；玩家名可以从玩家对象中获取，但不想在前面判断是否为null
     * @param method     拒绝何种方式的请求
     */
    private void sendDeAcceptMessage(BukkitRunnable runnable, Player player, Player source, String sourceName, String method) {
        new Thread((runnable)).interrupt();
        player.sendMessage(ChatColor.GREEN + "您拒绝了玩家(" + ChatColor.AQUA + sourceName + ChatColor.GREEN + ")的" + method + "请求");
        if (source != null) {
            source.sendMessage(ChatColor.GREEN + "玩家(" + ChatColor.AQUA + player.getName() + ChatColor.GREEN + ")拒绝了您的" + method + "请求");
        }
    }

}