package dev.efekos.usercrates;

import dev.efekos.usercrates.data.Crate;
import dev.efekos.usercrates.events.BlockEvents;
import dev.efekos.usercrates.events.EntityEvents;
import dev.efekos.usercrates.events.InventoryEvents;
import me.efekos.simpler.commands.CommandManager;
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
        CONFIG = new YamlConfig("config.yml",this);
        LANG_CONFIG = new YamlConfig("lang.yml",this);
        CRATES = new ListDataManager<>("Crates.json", this);
        CRATES.load(Crate[].class);
        CONFIG.setup();
        LANG_CONFIG.setup();
        instance = this;
        CRATE_UUID = new NamespacedKey(getInstance(),"crate_uuid");
        new Utilities.Glow().register();
        MenuManager.setPlugin(this);

        // setup events
        getServer().getPluginManager().registerEvents(new BlockEvents(),this);
        getServer().getPluginManager().registerEvents(new EntityEvents(),this);
        getServer().getPluginManager().registerEvents(new InventoryEvents(),this);

        // setup commands
        try {
            CommandManager.registerCoreCommand(this, dev.efekos.usercrates.commands.Crate.class);
        } catch (Exception e){
            e.printStackTrace();
        }

        //setup economy
        if(!setupEconomy()){
            getLogger().warning("Economy features will be disabled due to no Vault dependency found.");
        }

        getLogger().fine("Started successful");
    }

    private boolean setupEconomy()
    {
        if(getServer().getPluginManager().getPlugin("Vault")==null)return false;
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return economy != null;
    }

    @Override
    public void onDisable() {
        CRATES.save();
    }

    public static boolean economyAvaliable(){
        return economy != null;
    }

    public static Economy getEconomy() {
        return economy;
    }
}
