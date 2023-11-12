package dev.efekos.usercrates.commands.crate;

import dev.efekos.usercrates.Main;
import dev.efekos.usercrates.Utilities;
import dev.efekos.usercrates.data.Crate;
import dev.efekos.usercrates.data.CrateConsumeType;
import me.efekos.simpler.annotations.Command;
import me.efekos.simpler.commands.CoreCommand;
import me.efekos.simpler.commands.SubCommand;
import me.efekos.simpler.commands.syntax.Syntax;
import me.efekos.simpler.translation.TranslateManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Command(name = "manage",description = "Manager one of your crates",playerOnly = true,permission = "usercrates.manage")
public class Manage extends SubCommand {
    public Manage(@NotNull String name) {
        super(name);
    }

    public Manage(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
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
    public void onPlayerUse(Player player, String[] args) {
        Block targetBlock = Utilities.getTargetBlock(player,5);

        if(targetBlock.getType()!= Material.CHEST){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.not-chest","&cYou need to look at the crate that you want to manage.")));
            return;
        }

        Chest chest = (Chest) targetBlock.getState();
        if(!chest.getPersistentDataContainer().has(Main.CRATE_UUID, PersistentDataType.STRING)){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.not-crate","&cYou are not looking at a crate.")));
            return;
        }

        UUID id = UUID.fromString(Objects.requireNonNull(chest.getPersistentDataContainer().get(Main.CRATE_UUID, PersistentDataType.STRING)));
        Crate crate = Main.CRATES.get(id);

        assert crate != null;
        if(!crate.getOwner().equals(player.getUniqueId())&&!player.hasPermission("usercrates.admin")){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.not-owner","&cThat crate is not yours.")));
            return;
        }


        OfflinePlayer owner = Bukkit.getOfflinePlayer(crate.getOwner());
        List<String> accessorNames = new ArrayList<>();

        for (UUID accessor : crate.getAccessors()) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(accessor);
            accessorNames.add(p.getName());
        }


        player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.header","&4--&a%player%'s Crate&4--").replace("%player%",player.getName())));
        player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.owner","&eOwner: &b%player%").replace("%player%", Objects.requireNonNull(owner.getName()))));
        player.spigot().sendMessage(
                new TextComponent(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.accessors","&eAccessors: &b%accessors%").replace("%accessors%",String.join(", ",accessorNames)))+" "),
                Arrays.stream(new ComponentBuilder(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.add-accessor","&l&a[ADD]")))
                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/crate addaccessor "))
                        .create()).findFirst().get()
        );
        player.spigot().sendMessage(new TextComponent(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.type","&eUsing Type: &b%type%").replace("%type%",Main.LANG_CONFIG.getString("manage.types."+crate.getConsumeType().toString(),"Unknown")))),
                Arrays.stream(new ComponentBuilder(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.change"," &l&a[CHANGE]")))
                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/crate changetype "))
                        .create()).findFirst().get()
        );
        if(Main.economyAvaliable()&&Arrays.asList(CrateConsumeType.BOTH_PRICE_KEY,CrateConsumeType.KEY).contains(crate.getConsumeType())){ // means this crate should have a price
            player.spigot().sendMessage(new TextComponent(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.price","&ePrice: &a%price%").replace("%price%",Main.getEconomy().format(crate.getPrice())))),
                    Arrays.stream(new ComponentBuilder(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.change"," &l&a[CHANGE]")))
                            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/crate setprice "))
                            .create()).findFirst().get()
                    );
        }
        player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.footer","&4------------------------")));
    }

    @Override
    public void onConsoleUse(ConsoleCommandSender consoleCommandSender, String[] strings) {

    }
}
