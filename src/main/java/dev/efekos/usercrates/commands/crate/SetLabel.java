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
import dev.efekos.usercrates.commands.Crate;
import me.efekos.simpler.annotations.Command;
import me.efekos.simpler.commands.CoreCommand;
import me.efekos.simpler.commands.SubCommand;
import me.efekos.simpler.commands.syntax.ArgumentPriority;
import me.efekos.simpler.commands.syntax.Syntax;
import me.efekos.simpler.commands.syntax.impl.StringArgument;
import me.efekos.simpler.translation.TranslateManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Command(name = "setlabel",description = "Change the label of your crate",permission = "usercrates.setlabel",playerOnly = true)
public class SetLabel extends SubCommand {
    public SetLabel(@NotNull String name) {
        super(name);
    }

    public SetLabel(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public Class<? extends CoreCommand> getParent() {
        return Crate.class;
    }

    @Override
    public @NotNull Syntax getSyntax() {
        return new Syntax()
                .withArgument(new StringArgument("label", ArgumentPriority.REQUIRED,1,128));
    }

    @Override
    public void onPlayerUse(Player player, String[] args) {
        Block targetBlock = Utilities.getTargetBlock(player,5);

        if(targetBlock.getType()!= Material.CHEST){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("setlabel.not-chest","&cYou need to look at the crate that you want to change the label.")));
            return;
        }

        Chest chest = (Chest) targetBlock.getState();
        if(!chest.getPersistentDataContainer().has(Main.CRATE_UUID, PersistentDataType.STRING)){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("setlabel.not-crate","&cYou are not looking at a crate.")));
            return;
        }

        UUID id = UUID.fromString(Objects.requireNonNull(chest.getPersistentDataContainer().get(Main.CRATE_UUID, PersistentDataType.STRING)));
        dev.efekos.usercrates.data.Crate crate = Main.CRATES.get(id);

        assert crate != null;
        if(!crate.getOwner().equals(player.getUniqueId())&&!player.hasPermission("usercrates.admin")){
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("setlabel.not-owner","&cThat crate is not yours.")));
            return;
        }

        // rest is cmd specific

        crate.setLabel(String.join(" ",args));

        Utilities.refreshHolograms(crate, chest.getWorld());

        Main.CRATES.update(crate.getUniqueId(),crate);

        player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("setlabel.success","&aSuccessfully changed crate's label to &b%label%!").replace("%label%",String.join(" ",args))));
    }

    @Override
    public void onConsoleUse(ConsoleCommandSender consoleCommandSender, String[] strings) {

    }
}
