package ua.vdev.vtrap.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import ua.vdev.vtrap.VTrap;
import ua.vdev.vtrap.utils.ConfigUtil;
import ua.vdev.vtrap.utils.TrapItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class VTrapCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("vtrap.use")) return true;

        if (args.length != 4 || !args[0].equalsIgnoreCase("give")) {
            sender.sendMessage(ConfigUtil.getString("messages.usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ConfigUtil.getString("messages.player-offline"));
            return true;
        }

        String trapId = args[2];
        if (!VTrap.instance.getConfig().contains("traps." + trapId)) {
            sender.sendMessage(ConfigUtil.getString("messages.invalid-trap").replace("{trap_id}", trapId));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[3]);
            if (amount < 1) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(ConfigUtil.getString("messages.invalid-amount"));
            return true;
        }

        target.getInventory().addItem(TrapItemBuilder.createTrapItem(trapId, amount));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("vtrap.use")) return List.of();

        List<String> suggestions = new ArrayList<>();

        switch (args.length) {
            case 1 -> {
                suggestions.add("give");
            }
            case 2 -> {
                String prefix = args[1].toLowerCase();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(prefix)) {
                        suggestions.add(player.getName());
                    }
                }
            }
            case 3 -> {
                if (VTrap.instance.getConfig().isConfigurationSection("traps")) {
                    String prefix = args[2].toLowerCase();
                    for (String key : VTrap.instance.getConfig().getConfigurationSection("traps").getKeys(false)) {
                        if (key.toLowerCase().startsWith(prefix)) {
                            suggestions.add(key);
                        }
                    }
                }
            }
            case 4 -> {
                List<String> amounts = List.of("1", "5", "10", "16", "32", "64");
                String prefix = args[3];
                for (String amount : amounts) {
                    if (amount.startsWith(prefix)) {
                        suggestions.add(amount);
                    }
                }
            }
        }

        return suggestions;
    }
}