package dev.efekos.usercrates.commands;

import dev.efekos.usercrates.Main;
import dev.efekos.usercrates.commands.crate.*;
import me.efekos.simpler.annotations.Command;
import me.efekos.simpler.commands.CoreCommand;
import me.efekos.simpler.commands.SubCommand;
import me.efekos.simpler.translation.TranslateManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@Command(name = "crate",description = "Manage your crates!",permission = "usercrates.use",playerOnly = true)
public class Crate extends CoreCommand {
    public Crate(@NotNull String name) {
        super(name);
    }

    public Crate(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public @NotNull List<Class<? extends SubCommand>> getSubs() {
        return Arrays.asList(Help.class, Create.class, Delete.class, Manage.class, AddAccessor.class, RemoveAccessor.class, ChangeType.class, SetPrice.class, GetKey.class, SetLabel.class);
    }

    @Override
    public void renderHelpList(CommandSender commandSender, List<SubCommand> subCommands) {
        commandSender.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("help.header","&4-----&aHelp Menu&4-----")));

        for (SubCommand command : subCommands) {
            commandSender.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("help.format","&b%cmd% &6- &e%desc%")
                    .replace("%cmd%",command.getUsage())
                    .replace("%desc%",command.getDescription())
            ));
        }

        commandSender.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("help.footer","&4-------------------")));
    }
}
