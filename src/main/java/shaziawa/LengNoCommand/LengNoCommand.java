package shaziawa.LengNoCommand;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public class LengNoCommand extends JavaPlugin implements Listener {

    private List<String> disabledCommands; // 用于存储禁用命令列表

    @Override
    public void onEnable() {
        // 加载配置文件
        reloadCommandList();

        // 注册事件监听器
        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("§a[权限姬] LengNoCommand 插件已启用");
    }

    @Override
    public void onDisable() {
        getLogger().info("§a[权限姬] LengNoCommand 插件已禁用");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("权限姬")) {
            if (sender.hasPermission("lengnocommand.admin")) {
                if (args.length > 0 && args[0].equalsIgnoreCase("重载")) {
                    // 重新加载配置文件
                    reloadCommandList();
                    sender.sendMessage("§a[权限姬] 配置文件已重载");
                    return true;
                }
            }
            sender.sendMessage("§c[权限姬] 你没有权限使用这个命令");
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase();

        // 检查命令是否在禁用列表中
        synchronized (disabledCommands) {
            if (disabledCommands.contains(command)) {
                if (!event.getPlayer().hasPermission("lengnocommand.use")) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§c[权限姬] " + event.getPlayer().getName() + " 你想干嘛？QWQ..");

                    // 通知在线的 OP
                    String message = "§c[权限姬] " + event.getPlayer().getName() + " 想要使用被禁止的指令: " + command;
                    for (org.bukkit.entity.Player op : Bukkit.getOnlinePlayers()) {
                        if (op.isOp()) {
                            op.sendMessage(message);
                        }
                    }
                }
            }
        }
    }

    private void reloadCommandList() {
        // 加载配置文件
        FileConfiguration config = getConfig();
        saveDefaultConfig();

        // 确保线程安全
        synchronized (this) {
            disabledCommands = Collections.unmodifiableList(config.getStringList("disabled-commands"));
        }

        getLogger().info("§a[权限姬] 禁用命令列表已更新");
    }
}