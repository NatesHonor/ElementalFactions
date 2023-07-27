package com.nate.elemental.utils.shops.elixir;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.nate.elemental.Factions;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ElixirConfig {
    private final Factions plugin;
    private final FileConfiguration config;
    private final File configFile;

    public ElixirConfig() {
        this.plugin = Factions.getInstance();
        this.configFile = new File(plugin.getDataFolder(), "elixirs.yml");
        this.config = YamlConfiguration.loadConfiguration(configFile);
        if (!configFile.exists()) {
            plugin.saveResource("elixirs.yml", false);
        }
    }

    public List<Elixir> getElixirs() {
        List<Elixir> elixirs = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection("elixirs");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection elixirSection = section.getConfigurationSection(key);
                if (elixirSection != null) {
                    String name = ChatColor.translateAlternateColorCodes('&', elixirSection.getString("name"));
                    List<String> effects = elixirSection.getStringList("effects");
                    if (name != null && effects != null) {
                        List<PotionEffect> parsedEffects = new ArrayList<>();
                        for (String effectString : effects) {
                            PotionEffect effect = parsePotionEffect(effectString);
                            if (effect != null) {
                                parsedEffects.add(effect);
                            }
                        }
                        elixirs.add(new Elixir(name, parsedEffects));
                    } else {
                        plugin.getLogger().warning("Invalid elixir: " + key);
                    }
                }
            }
        } else {
            plugin.getLogger().warning("No elixirs found in the configuration file.");
        }
        return elixirs;
    }

    private PotionEffect parsePotionEffect(String effectString) {
        String[] parts = effectString.split(":");
        if (parts.length >= 2) {
            PotionEffectType type = PotionEffectType.getByName(parts[0]);
            int durationSeconds = Integer.parseInt(parts[1]);
            int amplifier = parts.length >= 3 ? Integer.parseInt(parts[2]) : 0;
            boolean isDurationInTicks = parts.length >= 4 && parts[3].equalsIgnoreCase("ticks");

            if (type != null) {
                int durationTicks = isDurationInTicks ? durationSeconds : durationSeconds * 20;
                return new PotionEffect(type, durationTicks, amplifier - 1);
            }
        }
        return null;
    }

    public boolean saveElixir(Elixir elixir) {
        ConfigurationSection section = config.getConfigurationSection("elixirs");
        if (section == null) {
            section = config.createSection("elixirs");
        }
        ConfigurationSection elixirSection = section.createSection(elixir.getName());
        elixirSection.set("name", elixir.getName());
        List<String> effects = new ArrayList<>();
        for (PotionEffect effect : elixir.getEffects()) {
            String effectString = effect.getType().getName() + ":" + (effect.getAmplifier() + 1) + ":"
                    + effect.getDuration();
            effects.add(effectString);
        }
        elixirSection.set("effects", effects);
        try {
            config.save(configFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static class Elixir {
        private final String name;
        private final List<PotionEffect> effects;

        public Elixir(String name, List<PotionEffect> effects) {
            this.name = ChatColor.translateAlternateColorCodes('&', name);
            this.effects = effects;
        }

        public String getName() {
            return name;
        }

        public List<PotionEffect> getEffects() {
            return effects;
        }
    }
}
