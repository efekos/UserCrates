/*
 * MIT License
 *
 * Copyright (c) 2024 efekos
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.efekos.usercrates.commands.crate;

import dev.efekos.usercrates.Main;
import dev.efekos.usercrates.Utilities;
import dev.efekos.usercrates.data.Crate;
import dev.efekos.usercrates.data.CrateConsumeType;
import me.efekos.simpler.annotations.Command;
import me.efekos.simpler.commands.CoreCommand;
import me.efekos.simpler.commands.SubCommand;
import me.efekos.simpler.commands.syntax.ArgumentPriority;
import me.efekos.simpler.commands.syntax.Syntax;
import me.efekos.simpler.commands.syntax.impl.IntegerArgument;
import me.efekos.simpler.commands.syntax.impl.StringArgument;
import me.efekos.simpler.translation.TranslateManager;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(name = "create", description = "Creates a crate using the block you look at", playerOnly = true, permission = "usercrates.create")
public class Create extends SubCommand {
    public Create(@NotNull String name) {
        super(name);
    }

    public Create(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public Class<? extends CoreCommand> getParent() {
        return dev.efekos.usercrates.commands.Crate.class;
    }

    @Override
    public @NotNull Syntax getSyntax() {
        return new Syntax()
                .withArgument(new IntegerArgument("amount", ArgumentPriority.OPTIONAL, 0, 2147483647))
                .withArgument(new StringArgument("label", ArgumentPriority.OPTIONAL, 0, 128));
    }

    @Override
    public void onPlayerUse(Player player, String[] args) {
        Block targetBlock = Utilities.getTargetBlock(player, 5);


        if (targetBlock.getType() != Material.CHEST) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("create.not-chest", "&cYou need look at a chest to make it a crate.")));
            return;
        }

        Chest chest = (Chest) targetBlock.getState();
        if (chest.getPersistentDataContainer().has(Main.CRATE_UUID, PersistentDataType.STRING)) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("create.already-crate", "&cThere is already a crate there.")));
            return;
        }

        if (!Utilities.isAllowed(player, targetBlock.getLocation())) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("create.cant-interact", "&cYou don't have access to interact with that chest.")));
            return;
        }

        Crate data = new Crate(targetBlock.getLocation(), player.getUniqueId(), new ArrayList<>(), Main.economyAvaliable() ? CrateConsumeType.BOTH_PRICE_KEY : CrateConsumeType.KEY, args[1]);
        World world = targetBlock.getWorld();

        chest.getPersistentDataContainer().set(Main.CRATE_UUID, PersistentDataType.STRING, data.getUniqueId().toString());

        try {
            int price = Integer.parseInt(args[0]);
            data.setPrice(price);
        } catch (Exception e) {
            if (!(e instanceof NumberFormatException)) {
                int price = 50;
                data.setPrice(price);
            } else e.printStackTrace();
        }

        Utilities.refreshHolograms(data, world);

        Main.CRATES.add(data);

        chest.update();
        player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("create.success", "&aSuccessfully created a crate at that block! You can put your items to your crate, set a price and get keys for it, and add accessors to manage the crate.")));
        if (!Main.economyAvaliable())
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("create.no-econ", "&6You won't be able to make your crate for sale, because this server does not have an economy.")));
    }

    @Override
    public void onConsoleUse(ConsoleCommandSender consoleCommandSender, String[] strings) {

    }
}
