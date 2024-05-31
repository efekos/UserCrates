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

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import dev.efekos.usercrates.data.Crate;
import dev.efekos.usercrates.menu.CrateOpening;
import me.efekos.simpler.menu.MenuData;
import me.efekos.simpler.menu.MenuManager;
import me.efekos.simpler.translation.TranslateManager;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockIterator;

import java.util.*;

public class Utilities {

    public static ArmorStand makeHologram(World world, Location location, String text) {
        ArmorStand stand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);

        stand.setArms(false);
        stand.setBasePlate(false);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            stand.addEquipmentLock(slot, ArmorStand.LockType.ADDING_OR_CHANGING);
        }
        stand.setMarker(true);
        stand.setSmall(true);
        stand.setInvisible(true);
        stand.setMarker(true);
        stand.setCustomNameVisible(true);
        stand.setCustomName(text);
        stand.setAI(false);
        stand.setSilent(true);
        stand.setPersistent(true);

        return stand;
    }

    public static void refreshHolograms(Crate crate, World world) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(crate.getOwner());

        for (UUID uuid : crate.getHolograms()) {
            Entity armor_stand = world.getEntities().stream().filter(entity -> entity.getUniqueId().equals(uuid)).findFirst().get();
            armor_stand.remove();
        }

        crate.setHolograms(new ArrayList<>());

        if (crate.getLabel() != null) {
            crate.addHologram(makeHologram(world, crate.getLocation().add(0.5, 2, 0.5), crate.getLabel()).getUniqueId());
        }

        TextDisplay display = (TextDisplay) world.spawnEntity(crate.getLocation().add(0.5, 1.2, 0.5), EntityType.TEXT_DISPLAY);
        display.setAlignment(TextDisplay.TextAlignment.CENTER);
        display.setShadowed(true);
        display.setBillboard(Display.Billboard.CENTER);

        ArrayList<String> texts = new ArrayList<>();

        texts.add(Main.LANG_CONFIG.getString("hologram.1", "&e%player%'s Crate").replace("%player%", Objects.requireNonNull(player.getName())));

        switch (crate.getConsumeType()){
            case KEY -> texts.add(Main.LANG_CONFIG.getString("hologram.2k", "&6Key Required"));
            case BOTH_PRICE_KEY -> texts.add(Main.LANG_CONFIG.getString("hologram.2kp", "&6Use Key &d/ &ePrice: &a%price%").replace("%price%",crate.getPrice()+""));
            case PRICE -> texts.add(Main.LANG_CONFIG.getString("hologram.2", "&ePrice: &a%price%").replace("%price%",crate.getPrice()+""));
            case ONLY_ACCESSORS -> texts.add(Main.LANG_CONFIG.getString("hologram.2a", "&6Accessor Only"));
        }

        texts.add(Main.LANG_CONFIG.getString("hologram.3", "&7Right-Click to Open"));
        texts.add(Main.LANG_CONFIG.getString("hologram.4", "&7Left-Click to See Items"));

        display.setText(TranslateManager.translateColors(String.join("\n",texts)));

        crate.addHologram(display.getUniqueId());
    }

    public static ItemStack makeCrateKey(Crate crate, int amount) {
        ItemStack stack = new ItemStack(Material.TRIPWIRE_HOOK, amount);
        ItemMeta meta = stack.getItemMeta();

        assert meta != null;
        meta.setDisplayName(TranslateManager.translateColors(Main.LANG_CONFIG.getString("key.title", "&eCrate Key")));
        List<String> lore = new ArrayList<>();

        String ownerName = Bukkit.getOfflinePlayer(crate.getOwner()).getName();

        assert ownerName != null;
        lore.add(TranslateManager.translateColors(Main.LANG_CONFIG.getString("key.desc.1", "&6Opens &e%player%'s Crate&6.").replace("%player%", ownerName)));
        if (Main.economyAvaliable()) {
            lore.add(TranslateManager.translateColors(Main.LANG_CONFIG.getString("key.desc.2", "&6Worth &a%price% &6per key from the latest price.").replace("%price%", Main.getEconomy().format(crate.getPrice()))));
        }
        lore.add(TranslateManager.translateColors(Main.LANG_CONFIG.getString("key.desc.3", "&6Right-Click to &e%player%'s Crate &6with this key to open it!").replace("%player%", ownerName)));

        meta.setLore(lore);
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(Main.CRATE_UUID, PersistentDataType.STRING, crate.getUniqueId().toString());

        stack.setItemMeta(meta);
        return stack;
    }

    public static void openCrate(Player player, Crate crate, Chest chest, boolean withdraw) {
        List<ItemStack> avaliableContent = Arrays.asList(chest.getSnapshotInventory().getContents());

        if (avaliableContent.stream().noneMatch(Objects::nonNull)) {
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("open.nothing", "&cThere is nothing inside this crate.")));
            return;
        }

        if (withdraw) {
            Main.getEconomy().withdrawPlayer(player, crate.getPrice());
            OfflinePlayer op = Bukkit.getOfflinePlayer(crate.getOwner());
            Main.getEconomy().depositPlayer(op, crate.getPrice());
            player.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("open.spent", "&eSpent &a%price% &eon this crate.").replace("%price%", Main.getEconomy().format(crate.getPrice()))));

            if (Main.CONFIG.getBoolean("open.send-notification", true)) {
                if (op.isOnline()) {
                    Player p = op.getPlayer();
                    p.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("open.notification", "&b%player% &ebought your crate for &a%price%&e!").replace("%player%", player.getName()).replace("%price%", Main.getEconomy().format(crate.getPrice()))));
                }
            }
        }

        //actually opening crate

        // ˅ getting a random stack
        int random = (int) Math.round(Math.random() * ((avaliableContent.size() - 1) + 0.0));

        ItemStack stackToGive = avaliableContent.get(random);

        while (stackToGive == null) {
            random = (int) Math.round(Math.random() * ((avaliableContent.size() - 1) + 0.0));
            stackToGive = avaliableContent.get(random);
        }

        // ˅ removing the stack from chest inventory
        chest.getSnapshotInventory().setItem(random, new ItemStack(Material.AIR));
        chest.update(true);

        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 100, 100);

        // ˅ opening the crate with a fancy menu
        MenuData data = MenuManager.getMenuData(player);
        data.set("inventory", chest.getSnapshotInventory());
        data.set("winningItem", stackToGive);
        data.set("crateOwner", Bukkit.getOfflinePlayer(crate.getOwner()).getName());
        data.set("crateOwnerUUID", crate.getOwner());
        MenuManager.updateMenuData(player, data);
        MenuManager.Open(player, CrateOpening.class);
    }

    public static BaseComponent[] makeComponentsForValue(String baseText, String searchText, BaseComponent replacement) {
        BaseComponent[] components = TextComponent.fromLegacyText(baseText);

        for (int i = 0; i < components.length; i++) {
            BaseComponent component = components[i];

            if (component.toPlainText().contains(searchText)) {
                components[i] = replacement;
            }
        }

        return components;
    }

    public static Block getTargetBlock(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock;
    }

    public static boolean isAllowed(Player player, Location location) {
        boolean allowed = true;
        if (Main.getInstance().getServer().getPluginManager().isPluginEnabled("WorldGuard"))
            allowed = isAllowedWorldGuard(player, location);
        if (Main.getInstance().getServer().getPluginManager().isPluginEnabled("GriefPrevention"))
            allowed = allowed && isAllowedGriefPrevention(player, location);

        return allowed;
    }

    private static boolean isAllowedGriefPrevention(Player player, Location location) {
        Plugin plugin = Main.getInstance().getServer().getPluginManager().getPlugin("GriefPrevention");
        if(plugin==null)return true;
        if (!(plugin instanceof GriefPrevention)) return true;

        GriefPrevention griefPrevention = GriefPrevention.instance;
        Claim claim = griefPrevention.dataStore.getClaimAt(location, true, null);
        if (claim == null) return true;

        return claim.checkPermission(player.getUniqueId(), ClaimPermission.Build, null) == null;
    }

    private static boolean isAllowedWorldGuard(Player player, Location location) {
        Plugin plugin = Main.getInstance().getServer().getPluginManager().getPlugin("WorldGuard");
        if(plugin==null)return true;
        if (!(plugin instanceof WorldGuardPlugin)) {
            return true;
        }

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, BukkitAdapter.adapt(player.getWorld()))) {
            return true;
        }

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld())));
        if (manager == null) {
            return true;
        }
        RegionQuery query = container.createQuery();
        ApplicableRegionSet regions = query.getApplicableRegions(BukkitAdapter.adapt(location));
        boolean c = false;
        for (ProtectedRegion region : regions) {
            if (region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
                c = true;
            }
        }
        if (!c) return true;

        return regions.testState(localPlayer, Flags.INTERACT);
    }
}
