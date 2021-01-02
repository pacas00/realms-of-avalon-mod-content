package net.petercashel.RealmsOfAvalonMod;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = RealmsOfAvalonMod.MODID)
public class RealmsOfAvalonModConfig {
    @Config.Comment("Debug Logging")
    public static boolean debugLogging = false;

    @Config.Comment("Splash Enabled")
    public static boolean splashEnabled = false;

    @Config.Comment("Server List Enabled. Use Custom MainMenu and wrapped button ID 7001 for the menu button.")
    public static boolean serverListEnabled = false;

    @Config.Comment("File name to use when the custom server list is enabled.")
    public static String serverListFileName = "RealmsOfAvalonServers.nbt";


    @Mod.EventBusSubscriber(modid = RealmsOfAvalonMod.MODID)
    private static class EventHandler {

        /**
         * Inject the new values and save to the config file when the config has been changed from the GUI.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(RealmsOfAvalonMod.MODID)) {
                ConfigManager.sync(RealmsOfAvalonMod.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
