package net.guizhanss.slimefuntranslation;

import java.io.File;
import java.lang.reflect.Method;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import org.bukkit.plugin.Plugin;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.updater.BlobBuildUpdater;

import net.guizhanss.guizhanlib.slimefun.addon.AbstractAddon;
import net.guizhanss.guizhanlib.updater.GuizhanBuildsUpdater;
import net.guizhanss.slimefuntranslation.core.Registry;
import net.guizhanss.slimefuntranslation.core.services.CommandService;
import net.guizhanss.slimefuntranslation.core.services.ConfigurationService;
import net.guizhanss.slimefuntranslation.core.services.IntegrationService;
import net.guizhanss.slimefuntranslation.core.services.ListenerService;
import net.guizhanss.slimefuntranslation.core.services.TranslationService;
import net.guizhanss.slimefuntranslation.core.services.UserService;
import net.guizhanss.slimefuntranslation.utils.tags.SlimefunTranslationTag;

import org.bstats.bukkit.Metrics;

public final class SlimefunTranslation extends AbstractAddon {

    private ConfigurationService configService;
    private Registry registry;
    private UserService userService;
    private TranslationService translationService;
    private IntegrationService integrationService;
    private boolean debugEnabled = false;

    public SlimefunTranslation() {
        super("ybw0014", "SlimefunTranslation", "master", "auto-update");
    }

    private static SlimefunTranslation inst() {
        return getInstance();
    }

    @Nonnull
    public static ConfigurationService getConfigService() {
        return inst().configService;
    }

    @Nonnull
    public static Registry getRegistry() {
        return inst().registry;
    }

    @Nonnull
    public static UserService getUserService() {
        return inst().userService;
    }

    @Nonnull
    public static TranslationService getTranslationService() {
        return inst().translationService;
    }

    @Nonnull
    public static IntegrationService getIntegrationService() {
        return inst().integrationService;
    }

    public static void debug(@Nonnull String message, @Nonnull Object... args) {
        Preconditions.checkNotNull(message, "message cannot be null");

        if (inst().debugEnabled) {
            inst().getLogger().log(Level.INFO, "[DEBUG] " + message, args);
        }
    }

    @Override
    public void enable() {
        log(Level.INFO, "====================");
        log(Level.INFO, "Slimefun Translation");
        log(Level.INFO, "     by ybw0014     ");
        log(Level.INFO, "====================");

        // check if slimefun has set a server default language
        if (Slimefun.getLocalization().getDefaultLanguage() == null) {
            log(Level.SEVERE, "Slimefun has no default language, this will make SlimefunTranslation not working properly, disabling...");
            getPluginLoader().disablePlugin(this);
            return;
        }

        // tags
        SlimefunTranslationTag.reloadAll();

        // config
        configService = new ConfigurationService(this);

        // registry
        registry = new Registry();

        // debug
        debugEnabled = configService.isDebug();

        // other services
        userService = new UserService();
        translationService = new TranslationService(this, getFile());
        new CommandService(this);
        new ListenerService(this);
        integrationService = new IntegrationService(this);

        // metrics
        setupMetrics();
    }

    @Override
    public void disable() {
        // does nothing
    }

    private void setupMetrics() {
        new Metrics(this, 20496);
    }

    @Override
    protected void autoUpdate() {
        if (getPluginVersion().startsWith("Dev")) {
            new BlobBuildUpdater(this, getFile(), getGithubRepo()).start();
        } else if (getPluginVersion().startsWith("Build")) {
            try {
                // use updater in lib plugin
                Class<?> clazz = Class.forName("net.guizhanss.guizhanlibplugin.updater.GuizhanUpdater");
                Method updaterStart = clazz.getDeclaredMethod("start", Plugin.class, File.class, String.class, String.class, String.class);
                updaterStart.invoke(null, this, getFile(), getGithubUser(), getGithubRepo(), getGithubBranch());
            } catch (Exception ignored) {
                // use updater in lib
                new GuizhanBuildsUpdater(this, getFile(), getGithubUser(), getGithubRepo(), getGithubBranch()).start();
            }
        }
    }
}
