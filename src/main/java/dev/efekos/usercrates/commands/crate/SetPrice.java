package dev.efekos.usercrates.commands.crate;

import dev.efekos.usercrates.Main;
import dev.efekos.usercrates.Utilities;
import dev.efekos.usercrates.commands.Crate;
import dev.efekos.usercrates.data.CrateConsumeType;
import me.efekos.simpler.annotations.Command;
import me.efekos.simpler.commands.CoreCommand;
import me.efekos.simpler.commands.SubCommand;
import me.efekos.simpler.commands.syntax.ArgumentPriority;
import me.efekos.simpler.commands.syntax.impl.IntegerArgument;
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

@Command(name = "setprice",description = "Change the price of your crate",permission = "usercrates.setprice",playerOnly = true)
public class SetPrice extends SubCommand {
    public SetPrice(@NotNull String name) {
        super(name);
    }

    public SetPrice(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public Class<? extends CoreCommand> getParent() {
        return Crate.class;
    }

    @Override
    public @NotNull Syntax getSyntax() {
        return new Syntax()
                .withArgument(new IntegerArgument("price", ArgumentPriority.REQUIRED,1,2147483647));
    }

    @Override
    public void onPlayerUse(Player player, String[] args) {
        if(!Main.economyAvaliable()){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("setprice.no-econ", "&cYou can't set a price for any crate, because this server does not have an economy.")));
            return;
        }


        Block targetBlock = Utilities.getTargetBlock(player,5);

        if(targetBlock.getType()!= Material.CHEST){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("setprice.not-chest","&cYou need to look at the crate that you want to change the price.")));
            return;
        }

        Chest chest = (Chest) targetBlock.getState();
        if(!chest.getPersistentDataContainer().has(Main.CRATE_UUID, PersistentDataType.STRING)){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("setprice.not-crate","&cYou are not looking at a crate.")));
            return;
        }

        UUID id = UUID.fromString(Objects.requireNonNull(chest.getPersistentDataContainer().get(Main.CRATE_UUID, PersistentDataType.STRING)));
        dev.efekos.usercrates.data.Crate crate = Main.CRATES.get(id);

        assert crate != null;
        if(!crate.getOwner().equals(player.getUniqueId())&&!player.hasPermission("usercrates.admin")){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("setprice.not-owner","&cThat crate is not yours.")));
            return;
        }

        // rest is cmd specific

        if(!Arrays.asList(CrateConsumeType.BOTH_PRICE_KEY,CrateConsumeType.PRICE).contains(crate.getConsumeType())){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("setprice.not-pricable", "&cThis crate can't have a price. You need to get a key using &b/crate getkey&c. If you wanted to make it for sale, change its type to a buyable type using &b/crate changetype &cfirst.")));
            return;
        }


        crate.setPrice(Integer.parseInt(args[0]));

        Utilities.refreshHolograms(crate, chest.getWorld());

        Main.CRATES.update(crate.getUniqueId(),crate);

        player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("setprice.success","&aSuccessfully changed crate's price to &b%price%!").replace("%price%",Main.getEconomy().format(Integer.parseInt(args[0])))));
    }

    @Override
    public void onConsoleUse(ConsoleCommandSender consoleCommandSender, String[] strings) {

    }
}
