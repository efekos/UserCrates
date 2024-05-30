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

import dev.efekos.arn.Arn;
import dev.efekos.usercrates.data.Crate;
import dev.efekos.usercrates.events.BlockEvents;
import dev.efekos.usercrates.events.EntityEvents;
import dev.efekos.usercrates.events.InventoryEvents;
import me.efekos.simpler.config.ListDataManager;
import me.efekos.simpler.config.YamlConfig;
import me.efekos.simpler.menu.MenuManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public static YamlConfig CONFIG;
    public static YamlConfig LANG_CONFIG;
    public static ListDataManager<Crate> CRATES;

    public static NamespacedKey CRATE_UUID;

    private static Main instance;

    private static Economy economy = null;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // random setup stuff
        CONFIG = new YamlConfig("config.yml", this);
        LANG_CONFIG = new YamlConfig("lang.yml", this);
        CRATES = new ListDataManager<>("Crates.json", this);
        CRATES.load();
        CONFIG.setup();
        LANG_CONFIG.setup();
        instance = this;
        CRATE_UUID = new NamespacedKey(getInstance(), "crate_uuid");
        MenuManager.setPlugin(this);

        // setup events
        getServer().getPluginManager().registerEvents(new BlockEvents(), this);
        getServer().getPluginManager().registerEvents(new EntityEvents(), this);
        getServer().getPluginManager().registerEvents(new InventoryEvents(), this);

        // setup commands
        Arn.run(Main.class);

        //setup economy
        if (!setupEconomy()) {
            getLogger().warning("Economy features will be disabled due to no Vault dependency found.");
        }

        getLogger().fine("Started successful");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) economy = economyProvider.getProvider();

        return economy != null;
    }

    @Override
    public void onDisable() {
        CRATES.save();
    }

    public static boolean economyAvaliable() {
        return economy != null;
    }

    public static Economy getEconomy() {
        return economy;
    }
}
