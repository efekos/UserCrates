package dev.efekos.usercrates.commands.crate;

import dev.efekos.usercrates.commands.Crate;
import me.efekos.simpler.annotations.Command;
import me.efekos.simpler.commands.CoreCommand;
import me.efekos.simpler.commands.SubCommand;
import me.efekos.simpler.commands.syntax.Syntax;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

@Command(name = "help",description = "Help menu.")
public class Help extends SubCommand {
    public Help(@NotNull String name) {
        super(name);
    }

    public Help(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public Class<? extends CoreCommand> getParent() {
        return Crate.class;
    }

    @Override
    public @NotNull Syntax getSyntax() {
        return new Syntax();
    }

    @Override
    public void onPlayerUse(Player player, String[] strings) {
        Crate c = new Crate("crate");
        ArrayList<SubCommand> subCommands = new ArrayList<>();
        for(Class<? extends SubCommand> sub:c.getSubs()){
            try {
                Constructor<? extends SubCommand> constructor = sub.getConstructor(String.class);
                me.efekos.simpler.annotations.Command commandA = sub.getAnnotation(me.efekos.simpler.annotations.Command.class);
                SubCommand command = constructor.newInstance(commandA.name());
                subCommands.add(command);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        c.renderHelpList(player,subCommands);
    }

    @Override
    public void onConsoleUse(ConsoleCommandSender sender, String[] strings) {
        Crate c = new Crate("crate");
        ArrayList<SubCommand> subCommands = new ArrayList<>();
        for(Class<? extends SubCommand> sub:c.getSubs()){
            try {
                Constructor<? extends SubCommand> constructor = sub.getConstructor(String.class);
                me.efekos.simpler.annotations.Command commandA = sub.getAnnotation(me.efekos.simpler.annotations.Command.class);
                SubCommand command = constructor.newInstance(commandA.name());
                subCommands.add(command);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        c.renderHelpList(sender,subCommands);
    }
}
