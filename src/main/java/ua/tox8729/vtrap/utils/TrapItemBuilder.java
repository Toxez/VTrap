package ua.tox8729.vtrap.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.inventory.ItemFlag;
import ua.tox8729.vtrap.VTrap;

public class TrapItemBuilder {
    public static ItemStack createTrapItem(String trapId, int amount) {
        ItemStack trapItem = new ItemStack(Material.valueOf(ConfigUtil.getString("traps." + trapId + ".material")), amount);
        ItemMeta trapMeta = trapItem.getItemMeta();
        trapMeta.setDisplayName(ConfigUtil.getString("traps." + trapId + ".name"));
        trapMeta.setLore(VTrap.instance.getConfig().getStringList("traps." + trapId + ".lore").stream()
                .map(HexUtil::translate)
                .toList());

        if (VTrap.instance.getConfig().getBoolean("traps." + trapId + ".enchanted", false)) {
            trapMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
            if (VTrap.instance.getConfig().getBoolean("traps." + trapId + ".hide_enchants", false)) {
                trapMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }

        if (VTrap.instance.getConfig().getBoolean("traps." + trapId + ".hide_attributes", false)) {
            trapMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }

        trapMeta.getPersistentDataContainer().set(new NamespacedKey(VTrap.instance, "trap"), PersistentDataType.STRING, trapId);
        trapItem.setItemMeta(trapMeta);
        return trapItem;
    }
}