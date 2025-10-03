package de.greenman999.librariantradefinder.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.greenman999.librariantradefinder.LibrarianTradeFinder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TradeFinderConfig {
    public static final TradeFinderConfig INSTANCE = new TradeFinderConfig();

    public final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("librarian-trade-finder.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public boolean preventAxeBreaking = true;
    public boolean tpToVillager = false;
    public boolean legitMode = true;
    public boolean slowMode = false;
    public boolean autoBuy = true;

    public HashMap<Enchantment, EnchantmentOption> enchantments = new HashMap<>();

    private final HashMap<String, EnchantmentOption> enchantmentConfigs = new HashMap<>();

    private static Registry<Enchantment> currentEnchantmentRegistry;

    private static Registry<Enchantment> getEnchantmentRegistry(boolean refresh) {
        if (refresh || currentEnchantmentRegistry == null) {
            if (currentEnchantmentRegistry == null && !refresh) {
                LibrarianTradeFinder.LOGGER.warn("Enchantment registry is null!");
            }
            assert MinecraftClient.getInstance().world != null;
            currentEnchantmentRegistry = MinecraftClient.getInstance().world
                    .getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        }
        return currentEnchantmentRegistry;
    }

    private static Registry<Enchantment> getEnchantmentRegistry() {
        return getEnchantmentRegistry(false);
    }

    public void save() {
        try {
            Files.deleteIfExists(configFile);

            JsonObject json = new JsonObject();
            json.addProperty("configVersion", 1);
            json.addProperty("preventAxeBreaking", preventAxeBreaking);
            json.addProperty("tpToVillager", tpToVillager);
            json.addProperty("legitMode", legitMode);
            json.addProperty("slowMode", slowMode);
            json.addProperty("autoBuy", autoBuy);

            JsonObject enchantmentsJson = new JsonObject();

            enchantmentConfigs.forEach((resLocation, enchantmentOption) ->
                    enchantmentsJson.add(resLocation, enchantmentOption.toJson()));

            if (MinecraftClient.getInstance().world != null) {
                Registry<Enchantment> enchantmentRegistry = getEnchantmentRegistry();
                enchantments.forEach((enchantment, enchantmentOption) -> enchantmentsJson.add(
                        enchantmentRegistry.getEntry(enchantment).getKey().orElseThrow()
                                .getValue().toString(),
                        enchantmentOption.toJson()));
                json.add("enchantments", enchantmentsJson);
            }

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

            Registry<Enchantment> enchantmentRegistry = getEnchantmentRegistry(true);

            enchantments.clear();
            enchantmentConfigs.clear();

            if(!(!json.has("configVersion") || json.get("configVersion").getAsInt() != 1)) {
                if (json.has("preventAxeBreaking"))
                    preventAxeBreaking = json.getAsJsonPrimitive("preventAxeBreaking").getAsBoolean();
                if (json.has("tpToVillager"))
                    tpToVillager = json.getAsJsonPrimitive("tpToVillager").getAsBoolean();
                if (json.has("legitMode"))
                    legitMode = json.getAsJsonPrimitive("legitMode").getAsBoolean();
                if (json.has("slowMode"))
                    slowMode = json.getAsJsonPrimitive("slowMode").getAsBoolean();
                if (json.has("autoBuy"))
                    autoBuy = json.getAsJsonPrimitive("autoBuy").getAsBoolean();
                if (json.has("enchantments")) {
                    JsonObject enchantmentsJson = json.getAsJsonObject("enchantments");
                    enchantmentsJson.entrySet().forEach(entry -> {
                        EnchantmentOption enchantmentOption = EnchantmentOption.fromJson(entry.getValue().getAsJsonObject());
                        if (enchantmentOption != null) {
                            enchantmentConfigs.put(entry.getKey(), enchantmentOption);
                        }
                    });
                }
            }
            
            final TagKey<Enchantment> tradeableTag = TagKey.of(enchantmentRegistry.getKey(),
                    Identifier.of("minecraft","tradeable"));
            for (Enchantment enchantment : enchantmentRegistry) {
                RegistryKey<Enchantment> enchantmentKey = enchantmentRegistry.getKey(enchantment).orElseThrow();
                boolean availableAsTrade = enchantmentRegistry.getOrThrow(enchantmentKey).isIn(tradeableTag);
                if (!availableAsTrade) continue;
                String resLocation = enchantmentKey.getValue().toString();
                EnchantmentOption enchantmentOption =
                        enchantmentConfigs.containsKey(resLocation)
                                ? enchantmentConfigs.get(resLocation)
                                : new EnchantmentOption(enchantment, false);
                enchantments.put(enchantment, enchantmentOption);
            }

            sortEnchantmentsMap();

            save();
            LibrarianTradeFinder.LOGGER.info("Config file loaded successfully. Enchantments size: {}", enchantments.size());
        } catch (IOException e) {
            LibrarianTradeFinder.LOGGER.error("Failed to load config file", e);
        }
    }

    private void sortEnchantmentsMap() {
        enchantments = enchantments.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(enchantment -> Enchantment.getName(RegistryEntry.of(enchantment), enchantment.getMaxLevel()).copy().formatted(Formatting.WHITE).getString())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public static class EnchantmentOption {

        public Enchantment enchantment;
        public boolean enabled;
        public int level;
        public int maxPrice;

        EnchantmentOption(Enchantment enchantment, boolean enabled, int level, int maxPrice) {
            this.enchantment = enchantment;
            this.enabled = enabled;
            this.level = level;
            this.maxPrice = maxPrice;
        }

        public EnchantmentOption(Enchantment enchantment, boolean enabled) {
            this(enchantment, enabled, enchantment.getMaxLevel(), 64);
        }

        public static EnchantmentOption fromJson(JsonObject json) {
            RegistryKey<Enchantment> enchantmentKey = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.tryParse(json.getAsJsonPrimitive("enchantment").getAsString()));
            Enchantment enchantment = getEnchantmentRegistry().get(enchantmentKey);
            if (enchantment == null) return null;
            return new EnchantmentOption(enchantment, json.getAsJsonPrimitive("enabled").getAsBoolean(), json.getAsJsonPrimitive("level").getAsInt(), json.getAsJsonPrimitive("maxPrice").getAsInt());
        }

        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("enchantment", getEnchantmentRegistry().getEntry(enchantment).getKey().orElseThrow().getValue().toString());
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
            return Enchantment.getName(RegistryEntry.of(enchantment), level).copy().formatted(Formatting.WHITE).getString();
        }
    }

}
