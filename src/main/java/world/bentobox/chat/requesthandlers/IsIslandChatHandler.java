package world.bentobox.chat.requesthandlers;

import java.util.Map;
import java.util.UUID;

import world.bentobox.bentobox.api.addons.request.AddonRequestHandler;
import world.bentobox.chat.Chat;

/**
 * Handles API requests from plugins.
 * Returns whether a player has island chat enabled.
 */
public class IsIslandChatHandler extends AddonRequestHandler {

    private final Chat addon;

    /**
     * @param addon - chat addon
     */
    public IsIslandChatHandler(Chat addon) {
        super("isIslandChat");
        this.addon = addon;
    }

    @Override
    public Object handle(Map<String, Object> map) {
        /*
        What we need in the map:
        1. "uuid" -> UUID of player to check
        What we will return:
        boolean
        - true if player is in island chat
        - false if not in island chat
         */
        // Error checking
        if (map == null || map.isEmpty()
                || map.get("uuid") == null || !(map.get("uuid") instanceof UUID)) {
            return false;
        }

        return addon.getListener().isIslandChat((UUID) map.get("uuid"));
    }

}
