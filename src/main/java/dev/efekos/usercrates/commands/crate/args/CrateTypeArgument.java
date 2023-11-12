package dev.efekos.usercrates.commands.crate.args;

import dev.efekos.usercrates.data.CrateConsumeType;
import me.efekos.simpler.commands.syntax.Argument;
import me.efekos.simpler.commands.syntax.ArgumentHandleResult;
import me.efekos.simpler.commands.syntax.ArgumentPriority;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CrateTypeArgument extends Argument {
    private final ArgumentPriority priority;

    private static final List<String> options = Arrays.stream(CrateConsumeType.values()).map(Enum::toString).collect(Collectors.toList());

    public CrateTypeArgument(ArgumentPriority priority) {
        this.priority = priority;
    }

    @Override
    public String getPlaceHolder() {
        return "type";
    }

    @Override
    public ArrayList<String> getList(Player player, String s) {
        return (ArrayList<String>) options;
    }

    @Override
    public ArgumentPriority getPriority() {
        return priority;
    }

    @Override
    public ArgumentHandleResult handleCorrection(String s) {
        if(options.contains(s)) return ArgumentHandleResult.success();
        else return ArgumentHandleResult.fail(s + " is not a crate type.");
    }
}
