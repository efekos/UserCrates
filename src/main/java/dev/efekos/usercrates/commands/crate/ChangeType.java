package dev.efekos.usercrates.commands.crate;

import dev.efekos.usercrates.Main;
import dev.efekos.usercrates.Utilities;
import dev.efekos.usercrates.commands.Crate;
import dev.efekos.usercrates.commands.crate.args.CrateTypeArgument;
import dev.efekos.usercrates.data.CrateConsumeType;
import me.efekos.simpler.annotations.Command;
import me.efekos.simpler.commands.CoreCommand;
import me.efekos.simpler.commands.SubCommand;
import me.efekos.simpler.commands.syntax.ArgumentPriority;
import me.efekos.simpler.commands.syntax.Syntax;
import me.efekos.simpler.translation.TranslateManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Command(name = "changetype",description = "Change the type of your crate",permission = "usercrates.changetype",playerOnly = true)
public class ChangeType extends SubCommand {
    public ChangeType(@NotNull String name) {
        super(name);
    }

    public ChangeType(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public Class<? extends CoreCommand> getParent() {
        return Crate.class;
    }

    @Override
    public @NotNull Syntax getSyntax() {
        return new Syntax()
                .withArgument(new CrateTypeArgument(ArgumentPriority.REQUIRED));
    }

    @Override
    public void onPlayerUse(Player player, String[] args) {
        Block targetBlock = Utilities.getTargetBlock(player,5);

        if(targetBlock.getType()!= Material.CHEST){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("changetype.not-chest","&cYou need to look at the crate that you want to change type.")));
            return;
        }

        Chest chest = (Chest) targetBlock.getState();
        if(!chest.getPersistentDataContainer().has(Main.CRATE_UUID, PersistentDataType.STRING)){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("changetype.not-crate","&cYou are not looking at a crate.")));
            return;
        }

        UUID id = UUID.fromString(Objects.requireNonNull(chest.getPersistentDataContainer().get(Main.CRATE_UUID, PersistentDataType.STRING)));
        dev.efekos.usercrates.data.Crate crate = Main.CRATES.get(id);

        assert crate != null;
        if(!crate.getOwner().equals(player.getUniqueId())&&!player.hasPermission("usercrates.admin")){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("changetype.not-owner","&cThat crate is not yours.")));
            return;
        }

        // rest is cmd specific

        CrateConsumeType newType = CrateConsumeType.valueOf(args[0]);

        if(Arrays.asList(CrateConsumeType.PRICE,CrateConsumeType.BOTH_PRICE_KEY).contains(newType)&&!Main.economyAvaliable()){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("changetype.no-econ", "&cYou can't make your crate for sale, because this server does not have an economy.")));
            return;
        }

        crate.setConsumeType(newType);

        Utilities.refreshHolograms(crate, chest.getWorld());

        Main.CRATES.update(crate.getUniqueId(),crate);

        player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("changetype.success","&aSuccessfully changed crate's type to &b%type%!").replace("%type%",Main.LANG_CONFIG.getString("manage.types."+crate.getConsumeType().toString(),"Unknown"))));
    }

    @Override
    public void onConsoleUse(ConsoleCommandSender consoleCommandSender, String[] strings) {

    }
}
