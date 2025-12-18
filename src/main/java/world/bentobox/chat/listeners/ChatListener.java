package world.bentobox.chat.listeners;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import world.bentobox.bentobox.api.events.team.TeamKickEvent;
import world.bentobox.bentobox.api.events.team.TeamLeaveEvent;
import world.bentobox.bentobox.api.localization.TextVariables;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.chat.Chat;

/**
 * Implements island chat handling.
 * @author tastybento
 *
 */
public class ChatListener implements Listener, EventExecutor {

    private static final String MESSAGE = "[message]";
    private final Chat addon;
    private final Set<UUID> islandChatUsers;
    // List of which users are spying or not on island chat
    private final Set<UUID> spies;

    public ChatListener(Chat addon) {
        this.islandChatUsers = new HashSet<>();
        this.addon = addon;
        // Initialize spies
        spies = new HashSet<>();
    }

    @Override
    public void execute(Listener listener, Event e) {

        // Needs to be checked, as we manually registered the listener
        // It's a replacement for ignoreCannceled = true
        if (((AsyncChatEvent) e).isCancelled())
            return;

        // Call the event method
        onChat((AsyncChatEvent) e);
    }

    public void onChat(final AsyncChatEvent e) {

        Player p = e.getPlayer();
        World ww = e.getPlayer().getWorld();
        // Check world
        if (!addon.isRegisteredGameWorld(ww)) {
            // Check to see if there is a default game mode for chat
            if (addon.getChatWorld().isPresent()) {
                ww = addon.getChatWorld().get();
            } else {
                return;
            }
        }
        World w = ww;
        Island island = addon.getIslands().getIsland(w, p.getUniqueId());
        if (island == null) {
            return;
        }

        boolean toggled = islandChatUsers.contains(p.getUniqueId());
        if (!toggled) {
            // Not an island chat message, let default pipeline continue
            return;
        }

        Set<UUID> targetUUIDs = island.getMemberSet().stream()
                .map(Bukkit::getPlayer)
                .filter(player -> player != null && player.isOnline())
                .map(Player::getUniqueId)
                .collect(Collectors.toSet());

        // Apply viewers
        Set<org.bukkit.entity.Player> recipients = targetUUIDs.stream()
                .map(Bukkit::getPlayer)
                .filter(player -> player != null && player.isOnline())
                .collect(Collectors.toSet());

        e.viewers().clear();
        e.viewers().addAll(recipients);

        // Renderer to match island chat format (simplified, uses colors similar to old format)
        e.renderer((source, sourceDisplayName, message, viewer) -> Component.text()
                .append(Component.text("[island] ").color(NamedTextColor.GOLD))
                .append(Component.text(source.getName()).color(NamedTextColor.GREEN))
                .append(Component.text(": ").color(NamedTextColor.DARK_GRAY))
                .append(message.colorIfAbsent(NamedTextColor.WHITE))
                .build());

        // Log if required
        if (addon.getSettings().isLogIslandChats()) {
            String msg = PlainTextComponentSerializer.plainText().serialize(e.originalMessage());
            addon.log("[Island Chat Log] " + p.getName() + ": " + msg);
        }
        // Spy if required
        Bukkit.getOnlinePlayers().stream()
        .filter(player -> spies.contains(player.getUniqueId()) && !recipients.contains(player))
        .map(User::getInstance)
        .forEach(u -> {
            String msg = PlainTextComponentSerializer.plainText().serialize(e.originalMessage());
            u.sendMessage("chat.island-chat.spy.syntax", TextVariables.NAME, p.getName(), MESSAGE, msg);
        });
    }

    // Removes player from island chat set if he left the island
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLeave(TeamLeaveEvent e) {
        islandChatUsers.remove(e.getPlayerUUID());
    }

    // Removes player from island chat set if he was kicked from the island
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onKick(TeamKickEvent e) {
        islandChatUsers.remove(e.getPlayerUUID());
    }

    public void islandChat(World w, final Player player, String message) {
        // Get island members of member or above
        addon.getIslands().getIsland(w, player.getUniqueId()).getMemberSet().stream()
        // Map to users
        .map(User::getInstance)
        // Filter for online only
        .filter(User::isOnline)
        // Send the message to them
        .forEach(target -> target.sendMessage("chat.island-chat.syntax", TextVariables.NAME, player.getName(), MESSAGE, message));
        // Log if required
        if (addon.getSettings().isLogIslandChats()) {
            addon.log("[Island Chat Log] " + player.getName() + ": " + message);
        }
        // Spy if required
        Bukkit.getOnlinePlayers().stream()
        .filter(p -> spies.contains(p.getUniqueId()))
        .map(User::getInstance)
        .forEach(u -> u.sendMessage("chat.island-chat.spy.syntax", TextVariables.NAME, player.getName(), MESSAGE, message));
    }

    /**
     * Whether the player has island chat on or not
     * @param playerUUID - the player's UUID
     * @return true if island chat is on
     */
    public boolean isIslandChat(UUID playerUUID) {
        return this.islandChatUsers.contains(playerUUID);
    }

    /**
     * Toggles island chat spy. Spy must also have the spy permission to see chats
     * @param playerUUID - the player's UUID
     * @return true if toggled on, false if toggled off
     */
    public boolean toggleSpy(UUID playerUUID) {
        if (spies.contains(playerUUID)) {
            spies.remove(playerUUID);
            return false;
        } else {
            spies.add(playerUUID);
            return true;
        }
    }

    /**
    /**
     * Toggle player's island chat state
     * @param playerUUID - player's uuid
     * @return true if island chat is now on, otherwise false
     */
    public boolean togglePlayerIslandChat(UUID playerUUID) {
        if (islandChatUsers.contains(playerUUID)) {
            islandChatUsers.remove(playerUUID);
            return false;
        } else {
            islandChatUsers.add(playerUUID);
            return true;
        }

    }

}
