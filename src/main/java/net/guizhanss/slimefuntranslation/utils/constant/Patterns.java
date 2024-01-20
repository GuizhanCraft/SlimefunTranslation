package net.guizhanss.slimefuntranslation.utils.constant;

import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Patterns {
    public static final Pattern MINECRAFT_NAMESPACEDKEY = Pattern.compile("minecraft:[a-z0-9/._-]+");
    public static final Pattern SFT_TAG = Pattern.compile("#sft:[a-z_]+");
    public static final Pattern SFT_TAG_CONFIG = Pattern.compile("sft:[a-z_]+");
}
