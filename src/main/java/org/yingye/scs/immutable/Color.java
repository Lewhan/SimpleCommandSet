package org.yingye.scs.immutable;

import net.kyori.adventure.text.format.TextColor;

public interface Color {
    TextColor NORMAL = null;
    TextColor AQUA = TextColor.fromHexString("#55FFFF");
    TextColor WHITE = TextColor.fromHexString("#FFFFFF");
    TextColor LIGHT_RED = TextColor.fromHexString("#FF5555");
    TextColor LIGHT_YELLOW = TextColor.fromHexString("#FFFF55");
    TextColor LIGHT_GREEN = TextColor.fromHexString("#55FF55");
}
