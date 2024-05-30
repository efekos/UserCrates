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

package dev.efekos.usercrates;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.efekos.arn.annotation.Command;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.annotation.Container;
import dev.efekos.arn.annotation.Helper;
import dev.efekos.arn.annotation.block.BlockCommandBlock;
import dev.efekos.arn.annotation.block.BlockConsole;
import dev.efekos.usercrates.data.Crate;
import dev.efekos.usercrates.data.CrateConsumeType;
import me.efekos.simpler.translation.TranslateManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.network.chat.IChatBaseComponent;

import java.util.*;
import java.util.stream.Collectors;

@Container
@Helper("crate.help")
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

    @Command(value = "crate.changetype", description = "Change the type of your crate", permission = "usercrates.changetype")
    @BlockConsole @BlockCommandBlock
    public int changeCrateType(Player player, @CommandArgument CrateConsumeType type) {
        Block targetBlock = Utilities.getTargetBlock(player, 5);

        if (targetBlock.getType() != Material.CHEST) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("changetype.not-chest", "&cYou need to look at the crate that you want to change type.")));
            return 1;
        }

        Chest chest = (Chest) targetBlock.getState();
        if (!chest.getPersistentDataContainer().has(Main.CRATE_UUID, PersistentDataType.STRING)) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("changetype.not-crate", "&cYou are not looking at a crate.")));
            return 1;
        }

        UUID id = UUID.fromString(Objects.requireNonNull(chest.getPersistentDataContainer().get(Main.CRATE_UUID, PersistentDataType.STRING)));
        dev.efekos.usercrates.data.Crate crate = Main.CRATES.get(id);

        assert crate != null;
        if (!crate.getOwner().equals(player.getUniqueId()) && !player.hasPermission("usercrates.admin")) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("changetype.not-owner", "&cThat crate is not yours.")));
            return 1;
        }

        // rest is cmd specific

        if (type.doesRequireEconomy() && !Main.economyAvaliable()) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("changetype.no-econ", "&cYou can't make your crate for sale, because this server does not have an economy.")));
            return 1;
        }

        crate.setConsumeType(type);
        Utilities.refreshHolograms(crate, chest.getWorld());
        Main.CRATES.update(crate.getUniqueId(), crate);

        player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("changetype.success", "&aSuccessfully changed crate's type to &b%type%!").replace("%type%", Main.LANG_CONFIG.getString("manage.types." + crate.getConsumeType().toString(), "Unknown"))));
        return 0;
    }

    @Command(value = "crate.create",description = "Create a crate.",permission = "usercrates.create")
    @BlockConsole @BlockCommandBlock
    public int createCrate(Player player, @CommandArgument int amount, @CommandArgument String label) {
        Block targetBlock = Utilities.getTargetBlock(player, 5);

        if (targetBlock.getType() != Material.CHEST) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("create.not-chest", "&cYou need look at a chest to make it a crate.")));
            return 1;
        }

        Chest chest = (Chest) targetBlock.getState();
        if (chest.getPersistentDataContainer().has(Main.CRATE_UUID, PersistentDataType.STRING)) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("create.already-crate", "&cThere is already a crate there.")));
            return 1;
        }

        if (!Utilities.isAllowed(player, targetBlock.getLocation())) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("create.cant-interact", "&cYou don't have access to interact with that chest.")));
            return 1;
        }

        dev.efekos.usercrates.data.Crate data = new Crate(targetBlock.getLocation(), player.getUniqueId(), new ArrayList<>(), Main.economyAvaliable() ? CrateConsumeType.BOTH_PRICE_KEY : CrateConsumeType.KEY, label);
        World world = targetBlock.getWorld();

        chest.getPersistentDataContainer().set(Main.CRATE_UUID, PersistentDataType.STRING, data.getUniqueId().toString());

        data.setPrice(amount);

        Utilities.refreshHolograms(data, world);

        Main.CRATES.add(data);

        chest.update();
        player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("create.success", "&aSuccessfully created a crate at that block! You can put your items to your crate, set a price and get keys for it, and add accessors to manage the crate.")));
        if (!Main.economyAvaliable())
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("create.no-econ", "&6You won't be able to make your crate for sale, because this server does not have an economy.")));
        return 0;
    }

    @Command(value = "crate.delete",description = "Delete a create.",permission = "usercrates.delete")
    @BlockConsole @BlockCommandBlock
    public int deleteCreate(Player player) {
        Block targetBlock = Utilities.getTargetBlock(player, 5);

        if (targetBlock.getType() != Material.CHEST) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("delete.not-chest", "&cYou need to look at the crate that you want to delete.")));
            return 1;
        }

        Chest chest = (Chest) targetBlock.getState();
        if (!chest.getPersistentDataContainer().has(Main.CRATE_UUID, PersistentDataType.STRING)) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("delete.not-crate", "&cYou are not looking at a crate.")));
            return 1;
        }

        UUID id = UUID.fromString(chest.getPersistentDataContainer().get(Main.CRATE_UUID, PersistentDataType.STRING));
        Crate crate = Main.CRATES.get(id);
        if (!crate.getOwner().equals(player.getUniqueId()) && !player.hasPermission("usercrates.admin")) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("delete.not-owner", "&cThat crate is not yours.")));
            return 1;
        }

        player.getWorld().getEntities().stream().filter(entity -> crate.getHolograms().contains(entity.getUniqueId())).forEach(Entity::remove);

        chest.getPersistentDataContainer().remove(Main.CRATE_UUID);
        Main.CRATES.delete(id);
        chest.update();

        player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("delete.success", "&aSuccessfully removed the crate!")));
        return 0;
    }

    @Command(value = "crate.getkey",description = "Get a key for your crate.",permission = "usercrates.getkey")
    @BlockConsole @BlockCommandBlock
    public int getKey(Player player,@CommandArgument int amount) {
        Block targetBlock = Utilities.getTargetBlock(player, 5);

        if (targetBlock.getType() != Material.CHEST) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("getkey.not-chest", "&cYou need to look at the crate that you want to get a key for.")));
            return 1;
        }

        Chest chest = (Chest) targetBlock.getState();
        if (!chest.getPersistentDataContainer().has(Main.CRATE_UUID, PersistentDataType.STRING)) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("getkey.not-crate", "&cYou are not looking at a crate.")));
            return 1;
        }

        UUID id = UUID.fromString(Objects.requireNonNull(chest.getPersistentDataContainer().get(Main.CRATE_UUID, PersistentDataType.STRING)));
        dev.efekos.usercrates.data.Crate crate = Main.CRATES.get(id);

        assert crate != null;
        if (!crate.getOwner().equals(player.getUniqueId()) && !player.hasPermission("usercrates.admin")) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("getkey.not-owner", "&cThat crate is not yours.")));
            return 1;
        }

        // rest is cmd specific

        if (!Arrays.asList(CrateConsumeType.BOTH_PRICE_KEY, CrateConsumeType.KEY).contains(crate.getConsumeType())) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("getkey.not-keyable", "&cThis crate can't have a key. You need to buy the crate by opening it. If you wanted to make it able for keys, change its type using &b/crate changetype &cfirst.")));
            return 1;
        }

        ItemStack keyStack = Utilities.makeCrateKey(crate, amount);

        player.getInventory().addItem(keyStack);
        player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("getkey.success", "&aSuccessfully got &b%amount% &akeys!").replace("%amount%", amount+"")));
        return 0;
    }

    @Command(value = "crate.manage",description = "Manage your crate.",permission = "usercrates.manage")
    @BlockConsole @BlockCommandBlock
    public int manageCrate(Player player) {
        Block targetBlock = Utilities.getTargetBlock(player, 5);

        if (targetBlock.getType() != Material.CHEST) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.not-chest", "&cYou need to look at the crate that you want to manage.")));
            return 1;
        }

        Chest chest = (Chest) targetBlock.getState();
        if (!chest.getPersistentDataContainer().has(Main.CRATE_UUID, PersistentDataType.STRING)) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.not-crate", "&cYou are not looking at a crate.")));
            return 1;
        }

        UUID id = UUID.fromString(Objects.requireNonNull(chest.getPersistentDataContainer().get(Main.CRATE_UUID, PersistentDataType.STRING)));
        Crate crate = Main.CRATES.get(id);

        assert crate != null;
        if (!crate.getOwner().equals(player.getUniqueId()) && !player.hasPermission("usercrates.admin")) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.not-owner", "&cThat crate is not yours.")));
            return 1;
        }


        OfflinePlayer owner = Bukkit.getOfflinePlayer(crate.getOwner());
        List<String> accessorNames = new ArrayList<>();

        for (UUID accessor : crate.getAccessors()) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(accessor);
            accessorNames.add(p.getName());
        }


        player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.header", "&4--&a%player%'s Crate&4--").replace("%player%", player.getName())));
        player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.owner", "&eOwner: &b%player%").replace("%player%", Objects.requireNonNull(owner.getName()))));
        player.spigot().sendMessage(
                new TextComponent(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.accessors", "&eAccessors: &b%accessors%").replace("%accessors%", String.join(", ", accessorNames))) + " "),
                Arrays.stream(new ComponentBuilder(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.add-accessor", "&l&a[ADD]")))
                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/crate accessor add "))
                        .create()).findFirst().get()
        );
        player.spigot().sendMessage(new TextComponent(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.type", "&eUsing Type: &b%type%").replace("%type%", Main.LANG_CONFIG.getString("manage.types." + crate.getConsumeType().toString(), "Unknown")))),
                Arrays.stream(new ComponentBuilder(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.change", " &l&a[CHANGE]")))
                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/crate changetype "))
                        .create()).findFirst().get()
        );
        if (Main.economyAvaliable() && Arrays.asList(CrateConsumeType.BOTH_PRICE_KEY, CrateConsumeType.KEY).contains(crate.getConsumeType())) { // means this crate should have a price
            player.spigot().sendMessage(new TextComponent(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.price", "&ePrice: &a%price%").replace("%price%", Main.getEconomy().format(crate.getPrice())))),
                    Arrays.stream(new ComponentBuilder(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.change", " &l&a[CHANGE]")))
                            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/crate setprice "))
                            .create()).findFirst().get()
            );
        }
        player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("manage.footer", "&4------------------------")));
        return 0;
    }

    @Command(value = "crate.setlabel",description = "Change label of your crate.", permission = "usercrates.setlabel")
    @BlockConsole @BlockCommandBlock
    public int changeLabel(Player player,@CommandArgument String label) {
        Block targetBlock = Utilities.getTargetBlock(player, 5);

        if (targetBlock.getType() != Material.CHEST) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("setlabel.not-chest", "&cYou need to look at the crate that you want to change the label.")));
            return 1;
        }

        Chest chest = (Chest) targetBlock.getState();
        if (!chest.getPersistentDataContainer().has(Main.CRATE_UUID, PersistentDataType.STRING)) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("setlabel.not-crate", "&cYou are not looking at a crate.")));
            return 1;
        }

        UUID id = UUID.fromString(Objects.requireNonNull(chest.getPersistentDataContainer().get(Main.CRATE_UUID, PersistentDataType.STRING)));
        dev.efekos.usercrates.data.Crate crate = Main.CRATES.get(id);

        assert crate != null;
        if (!crate.getOwner().equals(player.getUniqueId()) && !player.hasPermission("usercrates.admin")) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("setlabel.not-owner", "&cThat crate is not yours.")));
            return 1;
        }

        // rest is cmd specific

        crate.setLabel(label);
        Utilities.refreshHolograms(crate, chest.getWorld());
        Main.CRATES.update(crate.getUniqueId(), crate);

        player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("setlabel.success", "&aSuccessfully changed crate's label to &b\"%label%\"&a!").replace("%label%", label)));
        return 0;
    }

    @Command(value = "crate.setprice",description = "Change price of your crate.",permission = "usercrates.setprice")
    @BlockConsole @BlockCommandBlock
    public int changePrice(Player player,@CommandArgument int price) {
        if (!Main.economyAvaliable()) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("setprice.no-econ", "&cYou can't set a price for any crate, because this server does not have an economy.")));
            return 1;
        }


        Block targetBlock = Utilities.getTargetBlock(player, 5);

        if (targetBlock.getType() != Material.CHEST) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("setprice.not-chest", "&cYou need to look at the crate that you want to change the price.")));
            return 1;
        }

        Chest chest = (Chest) targetBlock.getState();
        if (!chest.getPersistentDataContainer().has(Main.CRATE_UUID, PersistentDataType.STRING)) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("setprice.not-crate", "&cYou are not looking at a crate.")));
            return 1;
        }

        UUID id = UUID.fromString(Objects.requireNonNull(chest.getPersistentDataContainer().get(Main.CRATE_UUID, PersistentDataType.STRING)));
        dev.efekos.usercrates.data.Crate crate = Main.CRATES.get(id);

        assert crate != null;
        if (!crate.getOwner().equals(player.getUniqueId()) && !player.hasPermission("usercrates.admin")) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("setprice.not-owner", "&cThat crate is not yours.")));
            return 1;
        }

        // rest is cmd specific

        if (!Arrays.asList(CrateConsumeType.BOTH_PRICE_KEY, CrateConsumeType.PRICE).contains(crate.getConsumeType())) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("setprice.not-pricable", "&cThis crate can't have a price. You need to get a key using &b/crate getkey&c. If you wanted to make it for sale, change its type to a buyable type using &b/crate changetype &cfirst.")));
            return 1;
        }


        crate.setPrice(price);
        Utilities.refreshHolograms(crate, chest.getWorld());
        Main.CRATES.update(crate.getUniqueId(), crate);

        player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("setprice.success", "&aSuccessfully changed crate's price to &b%price%!").replace("%price%", Main.getEconomy().format(price))));
        return 0;
    }

}