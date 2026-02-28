import com.github.puregero.multilib.MultiLib
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.CommandExecutor
import org.bukkit.command.TabCompleter
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import java.time.Duration

class Commands(private val plugin: Plugin) : CommandExecutor, TabCompleter {
    private val logger = plugin.logger
    private val miniMessage = MiniMessage.miniMessage()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return when (command.name.lowercase()) {
            "civbroadcast" -> if (sender.hasPermission("civbroadcast.admin")) handleCommand(sender, args) else {
                sender.sendMessage(miniMessage.deserialize("<red>You don't have permission to use this command!"))
                false
            }
            else -> false
        }
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return when (command.name.lowercase()) {
            "civbroadcast" -> if (sender.hasPermission("civbroadcast.admin")) handleTabComplete(args) else emptyList()
            else -> emptyList()
        }
    }

    fun handleCommand(sender: CommandSender, args: Array<out String>): Boolean {
        when {
            args.isEmpty() -> handleHelpInfo(sender)
            args[0].lowercase() == "help" -> handleHelpInfo(sender)
            args[0].lowercase() == "debug" -> handleDebugInfo(sender)
            args[0].lowercase() == "broadcast" -> if (args.size < 2) handleBroadcastInfo(sender) else handleBroadcast(args)
            else -> handleUnknownCommand(sender, args)
        }
        return true
    }

    fun handleTabComplete(args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> listOf("help", "debug", "broadcast").filter { it.startsWith(args[0].lowercase(), ignoreCase = true) }
            2 -> when (args[0].lowercase()) {
                "broadcast" -> listOf("<message>")
                else -> emptyList()
            }
            else -> emptyList()
        }
    }

    private fun handleBroadcast(args: Array<out String>) {
        val message = args.drop(1).joinToString(" ")
        val playerMessage = miniMessage.deserialize("<newline>  <red>[ <bold><gradient:#ffa347:#fcd490>BROADCAST<reset> <red>] <white>${message}<newline>")
        val titleMessage = miniMessage.deserialize("")
        val subtitleMessage = miniMessage.deserialize("<gradient:#ffa347:#fcd490:#ffa347>${message}")
        val consoleMessage = "[BROADCAST] ${message}"
        logger.info(consoleMessage)
        val title = Title.title(titleMessage, subtitleMessage, Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(5), Duration.ofMillis(500)))
        if (MultiLib.isMultiPaper()) {
            MultiLib.notify("civbroadcast:broadcast", consoleMessage)
            MultiLib.getAllOnlinePlayers().forEach { player -> 
                player.sendMessage(playerMessage)
                player.showTitle(title)
            }
        } else {
            Bukkit.getOnlinePlayers().forEach { player -> 
                player.sendMessage(playerMessage)
                player.showTitle(title)
            }
        }
    }

    private fun handleHelpInfo(sender: CommandSender) {
        sender.sendMessage(miniMessage.deserialize("<gold>=== <b>Civilization Broadcast</b> <gray>v<white>${Plugin.PLUGIN_VERSION} <gold>===="))
        sender.sendMessage(miniMessage.deserialize("<gray>/civbroadcast <green>help <gray>- <white>Show this help menu"))
        sender.sendMessage(miniMessage.deserialize("<gray>/civbroadcast <green>debug <gray>- <white>Show plugin debug information"))
        sender.sendMessage(miniMessage.deserialize("<gray>/civbroadcast <green>broadcast <message> <gray>- <white>Broadcast message to server(s)"))
    }

    private fun handleDebugInfo(sender: CommandSender) {
        sender.sendMessage(miniMessage.deserialize("<gold>=== <b>Civilization Broadcast Debug</b> ==="))
        sender.sendMessage(miniMessage.deserialize("<green>MultiPaper: <white>${MultiLib.isMultiPaper()}"))
    }

    private fun handleBroadcastInfo(sender: CommandSender) {
        sender.sendMessage(miniMessage.deserialize("<red>Usage: /civbroadcast <broadcast> <message>"))
    }

    private fun handleUnknownCommand(sender: CommandSender, args: Array<out String>) {
        sender.sendMessage(miniMessage.deserialize("<red>Unknown command: /civbroadcast ${args[0]}"))
        sender.sendMessage(miniMessage.deserialize("<white>Type <gold>/civbroadcast</gold> or <gold>/civbroadcast help</gold> for available commands"))
    }
} 