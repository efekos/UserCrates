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

package dev.efekos.usercrates.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.efekos.arn.annotation.Command;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.annotation.Container;
import dev.efekos.arn.annotation.block.BlockCommandBlock;
import dev.efekos.arn.annotation.block.BlockConsole;
import dev.efekos.usercrates.Main;
import dev.efekos.usercrates.Utilities;
import me.efekos.simpler.translation.TranslateManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.network.chat.IChatBaseComponent;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Container
public class Commands {

    public static final DynamicCommandExceptionType NO_CRATE_EXCEPTION = new DynamicCommandExceptionType((p)->IChatBaseComponent.b(TranslateManager.translateColors(Main.LANG_CONFIG.getString((String) p, "&cYou need to look at the crate that you want to add an accessor."))));
    public static final DynamicCommandExceptionType NOT_CRATE_OWNER = new DynamicCommandExceptionType((p)->IChatBaseComponent.b(TranslateManager.translateColors(Main.LANG_CONFIG.getString((String) p, "&cThat crate is not yours."))));
    public static final SimpleCommandExceptionType NLTC_EXCEPTION = new SimpleCommandExceptionType(IChatBaseComponent.b(TranslateManager.translateColors(Main.LANG_CONFIG.getString("accessor.add.not-crate", "&cYou are not looking at a crate."))));

    public static final DynamicCommandExceptionType GENERIC_EXCEPTION = new DynamicCommandExceptionType((o)->IChatBaseComponent.b((String) o));

    @Command(value = "crate.accessor.add", description = "Add an accessor to your crate.", permission = "usercrates.accessor.add")
    @BlockConsole @BlockCommandBlock
    public int addAccessor(Player player, @CommandArgument Player target) throws CommandSyntaxException {
        Block targetBlock = Utilities.getTargetBlock(player, 5);

        if (targetBlock.getType() != Material.CHEST) throw NO_CRATE_EXCEPTION.create("accessor.add.not-chest");

        Chest chest = (Chest) targetBlock.getState();
        if (!chest.getPersistentDataContainer().has(Main.CRATE_UUID, PersistentDataType.STRING)) throw NLTC_EXCEPTION.create();

        UUID id = UUID.fromString(Objects.requireNonNull(chest.getPersistentDataContainer().get(Main.CRATE_UUID, PersistentDataType.STRING)));
        dev.efekos.usercrates.data.Crate crate = Main.CRATES.get(id);

        assert crate != null;
        if (!crate.getOwner().equals(player.getUniqueId()) && !player.hasPermission("usercrates.admin")) throw NOT_CRATE_OWNER.create("accessor.add.not-owner");

        UUID accessorId = target.getUniqueId();

        if (crate.getAccessors().contains(accessorId)) throw GENERIC_EXCEPTION.create(TranslateManager.translateColors(Main.LANG_CONFIG.getString("accessor.add.exists", "&b%player% &cis already an accessor of this crate.").replace("%player%", target.getName())));

        crate.addAccessor(accessorId);
        Main.CRATES.update(crate.getUniqueId(), crate);
        player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("accessor.add.success", "&aSuccessfully added &b%player% &aas an accessor to this crate!").replace("%player%", target.getName())));
        return 0;
    }

    @Command(value = "crate.accessor.remove", description = "Remove an accessor from your crate.", permission = "usercrates.accessor.add")
    @BlockConsole @BlockCommandBlock
    public int removeAccessor(Player player, @CommandArgument Player target) throws CommandSyntaxException {
        Block targetBlock = Utilities.getTargetBlock(player, 5);

        if (targetBlock.getType() != Material.CHEST) throw NO_CRATE_EXCEPTION.create("accessor.add.not-chest");

        Chest chest = (Chest) targetBlock.getState();
        if (!chest.getPersistentDataContainer().has(Main.CRATE_UUID, PersistentDataType.STRING)) throw NLTC_EXCEPTION.create();

        UUID id = UUID.fromString(Objects.requireNonNull(chest.getPersistentDataContainer().get(Main.CRATE_UUID, PersistentDataType.STRING)));
        dev.efekos.usercrates.data.Crate crate = Main.CRATES.get(id);

        assert crate != null;
        if (!crate.getOwner().equals(player.getUniqueId()) && !player.hasPermission("usercrates.admin")) throw NOT_CRATE_OWNER.create("accessor.add.not-owner");

        UUID accessorId = target.getUniqueId();

        if (!crate.getAccessors().contains(accessorId)) throw GENERIC_EXCEPTION.create(TranslateManager.translateColors(Main.LANG_CONFIG.getString("accessor.add.exists", "&b%player% &cis already an accessor of this crate.").replace("%player%", target.getName())));

        crate.setAccessors(crate.getAccessors().stream().filter(uuid -> !uuid.equals(accessorId)).collect(Collectors.toList()));
        Main.CRATES.update(crate.getUniqueId(), crate);
        player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("accessor.remove.unexists", "&b%player% &cis not an accessor of this crate already.").replace("%player%", target.getName())));
        return 0;
    }

}
