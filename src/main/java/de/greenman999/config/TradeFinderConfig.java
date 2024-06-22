package de.greenman999.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.greenman999.LibrarianTradeFinder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.minecraft.registry.RegistryKeys.ENCHANTMENT;

public class TradeFinderConfig {
    public static final TradeFinderConfig INSTANCE = new TradeFinderConfig();

    public final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("librarian-trade-finder.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public boolean preventAxeBreaking = true;
    public boolean tpToVillager = false;
    public boolean legitMode = true;
    public boolean slowMode = false;

    public HashMap<String, EnchantmentOption> enchantments = new HashMap<>();

    public void save() {
        try {
            Files.deleteIfExists(configFile);

            JsonObject json = new JsonObject();
            json.addProperty("configVersion", 1);
            json.addProperty("preventAxeBreaking", preventAxeBreaking);
            json.addProperty("tpToVillager", tpToVillager);
            json.addProperty("legitMode", legitMode);
            json.addProperty("slowMode", slowMode);

            JsonObject enchantmentsJson = new JsonObject();
            enchantments.forEach((enchantmentKey, enchantmentOption) -> {
                enchantmentsJson.add(enchantmentKey, enchantmentOption.toJson());
            });
            json.add("enchantments", enchantmentsJson);

            Files.writeString(configFile, gson.toJson(json));
        } catch (IOException e) {
            LibrarianTradeFinder.LOGGER.error("Failed to save config file", e);
        }
    }

    public void load() {
        try {
            if(!Files.exists(configFile)) {
                Files.createFile(configFile);
                Files.writeString(configFile, "{}");
            }
            JsonObject json = gson.fromJson(Files.readString(configFile), JsonObject.class);

            final Map<String, RegistryEntry<Enchantment>> registeredEnchantments = BuiltinRegistries.createWrapperLookup().getWrapperOrThrow(ENCHANTMENT)
                .streamEntries()
                .collect(Collectors.toMap(RegistryEntry::getIdAsString, Function.identity()));

            if(!(!json.has("configVersion") || json.get("configVersion").getAsInt() != 1)) {
                if (json.has("preventAxeBreaking"))
                    preventAxeBreaking = json.getAsJsonPrimitive("preventAxeBreaking").getAsBoolean();
                if (json.has("tpToVillager"))
                    tpToVillager = json.getAsJsonPrimitive("tpToVillager").getAsBoolean();
                if (json.has("legitMode"))
                    legitMode = json.getAsJsonPrimitive("legitMode").getAsBoolean();
                if (json.has("slowMode"))
                    slowMode = json.getAsJsonPrimitive("slowMode").getAsBoolean();
                if (json.has("enchantments")) {
                    JsonObject enchantmentsJson = json.getAsJsonObject("enchantments");
                    enchantmentsJson.entrySet().stream().filter(entry -> registeredEnchantments.containsKey(entry.getKey()))
                        .forEach(entry -> {
                            final RegistryEntry<Enchantment> enchantmentRegistryEntry = registeredEnchantments.get(entry.getKey());
                            enchantments.put(
                                enchantmentRegistryEntry.getIdAsString(), 
                                    EnchantmentOption.fromJson(enchantmentRegistryEntry, entry.getValue().getAsJsonObject())
                                );
                        });
                }
            }

            for(RegistryEntry<Enchantment> registryEntry : registeredEnchantments.values()) {
                if(registryEntry != null && !enchantments.containsKey(registryEntry.getIdAsString())) {
                    enchantments.put(registryEntry.getIdAsString(), new EnchantmentOption(registryEntry, false));
                }
            }
            sortEnchantmentsMap();

            save();
        } catch (IOException e) {
            LibrarianTradeFinder.LOGGER.error("Failed to load config file", e);
        }
    }

    private void sortEnchantmentsMap() {
        enchantments = enchantments.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparing((entry) -> Enchantment.getName(entry.enchantment, entry.enchantment.value().getMaxLevel()).copy().formatted(Formatting.WHITE).getString())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public static class EnchantmentOption {

        public RegistryEntry<Enchantment> enchantment;
        public boolean enabled;
        public int level;
        public int maxPrice;

        EnchantmentOption(RegistryEntry<Enchantment> enchantment, boolean enabled, int level, int maxPrice) {
            this.enchantment = enchantment;
            this.enabled = enabled;
            this.level = level;
            this.maxPrice = maxPrice;
        }

        public EnchantmentOption(RegistryEntry<Enchantment> enchantment, boolean enabled) {
            this(enchantment, enabled, enchantment.value().getMaxLevel(), 64);
        }

        public static EnchantmentOption fromJson(RegistryEntry<Enchantment> enchantmentRegistryEntry, JsonObject json) {
            return new EnchantmentOption(enchantmentRegistryEntry, json.getAsJsonPrimitive("enabled").getAsBoolean(), json.getAsJsonPrimitive("level").getAsInt(), json.getAsJsonPrimitive("maxPrice").getAsInt());
        }

        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("enchantment", enchantment.getIdAsString());
            json.addProperty("enabled", enabled);
            json.addProperty("level", level);
            json.addProperty("maxPrice", maxPrice);
            return json;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public void setMaxPrice(int maxPrice) {
            this.maxPrice = maxPrice;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public int getLevel() {
            return level;
        }

        public int getMaxPrice() {
            return maxPrice;
        }

        public String getName() {
            return Enchantment.getName(enchantment, level).copy().formatted(Formatting.WHITE).getString();
        }
    }

}
