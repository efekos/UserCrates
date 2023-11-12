package dev.efekos.usercrates.commands.crate;

import dev.efekos.usercrates.Main;
import dev.efekos.usercrates.Utilities;
import dev.efekos.usercrates.commands.Crate;
import dev.efekos.usercrates.data.CrateConsumeType;
import me.efekos.simpler.annotations.Command;
import me.efekos.simpler.commands.CoreCommand;
import me.efekos.simpler.commands.SubCommand;
import me.efekos.simpler.commands.syntax.ArgumentPriority;
import me.efekos.simpler.commands.syntax.impl.NumberArgument;
import me.efekos.simpler.commands.syntax.Syntax;
import me.efekos.simpler.translation.TranslateManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Command(name = "getkey",description = "Get a key for your crate",permission = "usercrates.getkey",playerOnly = true)
public class GetKey extends SubCommand {
    public GetKey(@NotNull String name) {
        super(name);
    }

    public GetKey(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public Class<? extends CoreCommand> getParent() {
        return Crate.class;
    }

    @Override
    public @NotNull Syntax getSyntax() {
        return new Syntax()
                .withArgument(new NumberArgument("amount", ArgumentPriority.REQUIRED,1,64));
    }

    @Override
    public void onPlayerUse(Player player, String[] args) {
        Block targetBlock = Utilities.getTargetBlock(player,5);

        if(targetBlock.getType()!= Material.CHEST){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("getkey.not-chest","&cYou need to look at the crate that you want to get a key for.")));
            return;
        }

        Chest chest = (Chest) targetBlock.getState();
        if(!chest.getPersistentDataContainer().has(Main.CRATE_UUID, PersistentDataType.STRING)){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("getkey.not-crate","&cYou are not looking at a crate.")));
            return;
        }

        UUID id = UUID.fromString(Objects.requireNonNull(chest.getPersistentDataContainer().get(Main.CRATE_UUID, PersistentDataType.STRING)));
        dev.efekos.usercrates.data.Crate crate = Main.CRATES.get(id);

        assert crate != null;
        if(!crate.getOwner().equals(player.getUniqueId())&&!player.hasPermission("usercrates.admin")){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("getkey.not-owner","&cThat crate is not yours.")));
            return;
        }

        // rest is cmd specific

        if(!Arrays.asList(CrateConsumeType.BOTH_PRICE_KEY,CrateConsumeType.KEY).contains(crate.getConsumeType())){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("getkey.not-keyable", "&cThis crate can't have a key. You need to buy the crate by opening it. If you wanted to make it able for keys, change its type using &b/crate changetype &cfirst.")));
            return;
        }

        ItemStack keyStack = Utilities.makeCrateKey(crate,Integer.parseInt(args[0]));

        player.getInventory().addItem(keyStack);

        player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("getkey.success","&aSuccessfully got &b%amount% &akeys!").replace("%amount%",args[0])));
    }

    @Override
    public void onConsoleUse(ConsoleCommandSender consoleCommandSender, String[] strings) {

    }
}
