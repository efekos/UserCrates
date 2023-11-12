package dev.efekos.usercrates.commands.crate.args;

import me.efekos.simpler.commands.syntax.Argument;
import me.efekos.simpler.commands.syntax.ArgumentHandleResult;
import me.efekos.simpler.commands.syntax.ArgumentPriority;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class AnyArgument extends Argument {
    private final String holder;
    private final ArgumentPriority priority;


    public AnyArgument(String holder, ArgumentPriority priority) {
        this.holder = holder;
        this.priority = priority;
    }

    @Override
    public String getPlaceHolder() {
        return holder;
    }

    @Override
    public ArrayList<String> getList(Player player, String s) {
        return new ArrayList<>();
    }

    @Override
    public ArgumentPriority getPriority() {
        return priority;
    }

    @Override
    public ArgumentHandleResult handleCorrection(String s) {
        return ArgumentHandleResult.success();
    }
}
