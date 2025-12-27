package ua.vdev.vtrap.utils.flags;

import com.sk89q.worldguard.protection.flags.StateFlag;

import java.util.Arrays;

public enum FlagState {
    ALLOW("ALLOW", StateFlag.State.ALLOW),
    DENY("DENY", StateFlag.State.DENY);

    private final String name;
    private final StateFlag.State state;

    FlagState(String name, StateFlag.State state) {
        this.name = name;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public StateFlag.State getState() {
        return state;
    }

    public static FlagState fromString(String value) {
        if (value == null) return null;
        return Arrays.stream(values())
                .filter(type -> type.name.equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElse(null);
    }
}