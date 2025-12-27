package ua.vdev.vtrap.utils;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.configuration.ConfigurationSection;
import ua.vdev.vtrap.VTrap;
import ua.vdev.vtrap.utils.flags.FlagState;

import java.util.HashMap;
import java.util.Map;

public final class FlagConfigHelper {

    private FlagConfigHelper() {}

    public static Map<StateFlag, StateFlag.State> getFlagsForTrap(String trapId) {
        ConfigurationSection trapSec = VTrap.instance.getConfig()
                .getConfigurationSection("traps." + trapId + ".flags");

        if (trapSec == null) {
            return new HashMap<>();
        }

        Map<StateFlag, StateFlag.State> result = new HashMap<>();

        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

        for (String flagName : trapSec.getKeys(false)) {
            String raw = trapSec.getString(flagName);
            FlagState flagState = FlagState.fromString(raw);
            if (flagState == null) continue;

            Flag<?> flag = registry.get(flagName.toUpperCase());
            if (flag instanceof StateFlag stateFlag) {
                result.put(stateFlag, flagState.getState());
            }
        }
        return result;
    }
}