import com.github.puregero.multilib.MultiLib
import org.bukkit.plugin.java.JavaPlugin

class Plugin : JavaPlugin() {
    
    companion object {
        lateinit var instance: Plugin
            private set
        var PLUGIN_VERSION: String = "unknown"
        var PLUGIN_DESCRIPTION: String = "unknown"
    }
    
    override fun onEnable() {
        instance = this
        PLUGIN_VERSION = pluginMeta.version ?: "unknown"
        PLUGIN_DESCRIPTION = pluginMeta.description ?: "unknown"
        getCommand("civbroadcast")?.let { command ->
            command.setExecutor(Commands(this))
            command.setTabCompleter(Commands(this))
        }
        saveDefaultConfig()  // Initializes the config file if non-existent
        if (MultiLib.isMultiPaper()) {
            logger.info("MultiPaper detected! Plugin initialized successfully. Server name: ${MultiLib.getLocalServerName()}")
            MultiLib.onString(this, "civbroadcast:broadcast", ::handleBroadcastMessage)
        } else {
            logger.warning("This plugin is designed for MultiPaper servers! Some features may not work correctly on single-server setups.")
        }
        logger.info("${name} v${PLUGIN_VERSION} has been enabled!")
    }
    
    override fun onDisable() {
        logger.info("${name} has been disabled!")
    }
    
    private fun handleBroadcastMessage(data: String) {
        logger.info(data)
    }
} 