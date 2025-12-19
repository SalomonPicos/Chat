package world.bentobox.chat.commands.island;

import java.util.List;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.chat.Chat;

/**
 * @author tastybento
 */
public class IslandTeamChatCommand extends CompositeCommand {

    public IslandTeamChatCommand(Addon addon, CompositeCommand parent, String label) {
        super(addon, parent, label, "ic");
    }

    @Override
    public void setup() {
        this.setPermission("chat.island-chat");
        this.setDescription("chat.island-chat.description");
        this.setOnlyPlayer(true);
        setConfigurableRankCommand();
    }

    @Override
    public boolean canExecute(User user, String label, List<String> args) {
        Island island = this.getIslands().getIsland(getWorld(), user.getUniqueId());
        if (island == null) {
            user.sendMessage("chat.island-chat.no-island");
            return false;
        }
        return true;
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        Chat addon = this.getAddon();

        if (addon.getListener().togglePlayerIslandChat(user.getUniqueId())) {
            user.sendMessage("chat.island-chat.island-on");
        } else {
            user.sendMessage("chat.island-chat.island-off");
        }
        return true;
    }
}
