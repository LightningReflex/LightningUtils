package me.lightningreflex.lightningutils.managers;

import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.UUID;

@UtilityClass
public class PlayerCache {

    @Getter
    private final HashSet<UUID> toggledStaffChat = new HashSet<>();
}
