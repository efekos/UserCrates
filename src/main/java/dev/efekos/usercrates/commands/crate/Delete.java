package dev.efekos.usercrates.commands.crate;

import dev.efekos.usercrates.Main;
import dev.efekos.usercrates.Utilities;
import dev.efekos.usercrates.data.Crate;
import me.efekos.simpler.annotations.Command;
import me.efekos.simpler.commands.CoreCommand;
import me.efekos.simpler.commands.SubCommand;
import me.efekos.simpler.commands.syntax.Syntax;
import me.efekos.simpler.translation.TranslateManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Command(name = "delete",description = "Delete one of your crates",playerOnly = true,permission = "usercrates.delete")
public class Delete extends SubCommand {
    public Delete(@NotNull String name) {
        super(name);
    }

    public Delete(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public Class<? extends CoreCommand> getParent() {
        return dev.efekos.usercrates.commands.Crate.class;
    }

    @Override
    public @NotNull Syntax getSyntax() {
        return new Syntax();
    }

    @Override
    public void onPlayerUse(Player player, String[] strings) {
        Block targetBlock = Utilities.getTargetBlock(player,5);

        if(targetBlock.getType()!= Material.CHEST){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("delete.not-chest","&cYou need to look at the crate that you want to delete.")));
            return;
        }

        Chest chest = (Chest) targetBlock.getState();
        if(!chest.getPersistentDataContainer().has(Main.CRATE_UUID, PersistentDataType.STRING)){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("delete.not-crate","&cYou are not looking at a crate.")));
            return;
        }

        UUID id = UUID.fromString(chest.getPersistentDataContainer().get(Main.CRATE_UUID,PersistentDataType.STRING));
        Crate crate = Main.CRATES.get(id);
        if(!crate.getOwner().equals(player.getUniqueId())&&!player.hasPermission("usercrates.admin")){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("delete.not-owner","&cThat crate is not yours.")));
            return;
        }

        player.getWorld().getEntities().stream().filter(entity -> crate.getHolograms().contains(entity.getUniqueId())).forEach(Entity::remove);

        chest.getPersistentDataContainer().remove(Main.CRATE_UUID);

        Main.CRATES.delete(id);


        chest.update();
        player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("delete.success","&aSuccessfully removed the crate!")));
    }

    @Override
    public void onConsoleUse(ConsoleCommandSender consoleCommandSender, String[] strings) {

    }
}
