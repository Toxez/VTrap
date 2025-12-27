package ua.vdev.vtrap.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.inventory.ItemFlag;
import ua.vdev.vtrap.VTrap;
import ua.vdev.vtrap.models.Trap;
import ua.vdev.vtrap.models.factory.TrapFactory;

public class TrapItemBuilder {
    public static ItemStack createTrapItem(String trapId, int amount) {
        Trap trap = TrapFactory.create(trapId);
        if (trap == null) return null;

        var info = trap.itemInfo();
        ItemStack item = new ItemStack(Material.valueOf(info.material()), amount);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(info.displayName());
        meta.setLore(info.lore());

        String base = "traps." + trapId + ".";
        if (VTrap.instance.getConfig().getBoolean(base + "enchanted", false)) {
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
            if (VTrap.instance.getConfig().getBoolean(base + "hide_enchants", false)) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }

        if (VTrap.instance.getConfig().getBoolean(base + "hide_attributes", false)) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }

        meta.getPersistentDataContainer().set(new NamespacedKey(VTrap.instance, "trap"), PersistentDataType.STRING, trapId);
        item.setItemMeta(meta);
        return item;
    }
}