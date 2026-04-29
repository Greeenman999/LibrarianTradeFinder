package de.greenman999.librariantradefinder.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.greenman999.librariantradefinder.LibrarianTradeFinder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
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
    public boolean showCostRange = true;
    public boolean debugLogOffers = false;

    public HashMap<Enchantment, EnchantmentOption> enchantments = new HashMap<>();

    private final HashMap<String, EnchantmentOption> enchantmentConfigs = new HashMap<>();

    private static Registry<Enchantment> currentEnchantmentRegistry;

    private static Registry<Enchantment> getEnchantmentRegistry(boolean refresh) {
        if (refresh || currentEnchantmentRegistry == null) {
            if (currentEnchantmentRegistry == null && !refresh) {
                LibrarianTradeFinder.LOGGER.warn("Enchantment registry is null!");
            }
            assert Minecraft.getInstance().level != null;
            currentEnchantmentRegistry = Minecraft.getInstance().level
                    .registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        }
        return currentEnchantmentRegistry;
    }

    private static Registry<Enchantment> getEnchantmentRegistry() {
        return getEnchantmentRegistry(false);
    }

    /**
     * Compute the minimum possible librarian trade price for a (enchantment, level) pair, applying
     * the vanilla {@code 2× double-trade-price} multiplier when the enchantment is tagged for it
     * (Mending, Frost Walker, Curses, etc.). Result is capped at the vanilla 64-emerald maximum.
     */
    public static int computedMinPrice(Enchantment enchantment, int level) {
        int base = 2 + 3 * level;
        if (isDoubleTradePrice(enchantment)) base *= 2;
        return Math.min(64, base);
    }

    /**
     * Compute the maximum possible librarian trade price for a (enchantment, level) pair. Same rules
     * as {@link #computedMinPrice} but with the upper-bound coefficient.
     */
    public static int computedMaxPrice(Enchantment enchantment, int level) {
        int base = 6 + 13 * level;
        if (isDoubleTradePrice(enchantment)) base *= 2;
        return Math.min(64, base);
    }

    /**
     * Clamp a desired price into the valid {@code [computedMinPrice, computedMaxPrice]} range so
     * we never store a target price the server can never roll.
     */
    public static int clampPrice(Enchantment enchantment, int level, int desired) {
        return Math.max(computedMinPrice(enchantment, level),
                        Math.min(desired, computedMaxPrice(enchantment, level)));
    }

    private static boolean isDoubleTradePrice(Enchantment enchantment) {
        try {
            return getEnchantmentRegistry().wrapAsHolder(enchantment)
                    .is(EnchantmentTags.DOUBLE_TRADE_PRICE);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Look up an EnchantmentOption by registry resource key rather than Enchantment object reference.
     * Direct {@code enchantments.get(enchantment)} fails on 1.21+ because Holder/registry indirection
     * means the {@code Enchantment} instance returned from a server-sent offer can be a different
     * Java object than the one we stored at config-load time. We fall back to comparing resource keys.
     */
    public EnchantmentOption findOptionForEnchantment(Enchantment enchantment) {
        EnchantmentOption direct = enchantments.get(enchantment);
        if (direct != null) {
            return direct;
        }
        Registry<Enchantment> registry = getEnchantmentRegistry();
        var offerKey = registry.getResourceKey(enchantment).orElse(null);
        if (offerKey == null) {
            return null;
        }
        for (var entry : enchantments.entrySet()) {
            var entryKey = registry.getResourceKey(entry.getKey()).orElse(null);
            if (entryKey != null && entryKey.equals(offerKey)) {
                return entry.getValue();
            }
        }
        return null;
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
            json.addProperty("showCostRange", showCostRange);
            json.addProperty("debugLogOffers", debugLogOffers);

            JsonObject enchantmentsJson = new JsonObject();

            // Always seed from the on-disk cache first so a save from the title screen (where
            // the level isn't loaded and the live `enchantments` map is empty) preserves the
            // user's per-enchant settings instead of overwriting the file with nothing.
            enchantmentConfigs.forEach((resLocation, enchantmentOption) ->
                    enchantmentsJson.add(resLocation, enchantmentOption.toJson()));

            if (Minecraft.getInstance().level != null) {
                Registry<Enchantment> enchantmentRegistry = getEnchantmentRegistry();
                enchantments.forEach((enchantment, enchantmentOption) -> enchantmentsJson.add(
                        enchantmentRegistry.wrapAsHolder(enchantment).unwrapKey().orElseThrow()
                                .identifier().toString(),
                        enchantmentOption.toJson()));
            }
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
                if (json.has("showCostRange"))
                    showCostRange = json.getAsJsonPrimitive("showCostRange").getAsBoolean();
                if (json.has("debugLogOffers"))
                    debugLogOffers = json.getAsJsonPrimitive("debugLogOffers").getAsBoolean();
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
            
            final TagKey<Enchantment> tradeableTag = TagKey.create(enchantmentRegistry.key(),
                    Identifier.fromNamespaceAndPath("minecraft","tradeable"));
            for (Enchantment enchantment : enchantmentRegistry) {
                ResourceKey<Enchantment> enchantmentKey = enchantmentRegistry.getResourceKey(enchantment).orElseThrow();
                boolean availableAsTrade = enchantmentRegistry.getOrThrow(enchantmentKey).is(tradeableTag);
                if (!availableAsTrade) continue;
                String resLocation = enchantmentKey.identifier().toString();
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
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(enchantment -> Enchantment.getFullname(Holder.direct(enchantment), enchantment.getMaxLevel()).copy().withStyle(ChatFormatting.WHITE).getString())))
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
            // Default a freshly-enabled enchantment to the cheapest possible price for its max level,
            // so users get "best deal only" out of the box without having to know the formula.
            this(enchantment, enabled, enchantment.getMaxLevel(),
                 computedMinPrice(enchantment, enchantment.getMaxLevel()));
        }

        public static EnchantmentOption fromJson(JsonObject json) {
            ResourceKey<Enchantment> enchantmentKey = ResourceKey.create(Registries.ENCHANTMENT, Identifier.tryParse(json.getAsJsonPrimitive("enchantment").getAsString()));
            Enchantment enchantment = getEnchantmentRegistry().getValue(enchantmentKey);
            if (enchantment == null) return null;
            return new EnchantmentOption(enchantment, json.getAsJsonPrimitive("enabled").getAsBoolean(), json.getAsJsonPrimitive("level").getAsInt(), json.getAsJsonPrimitive("maxPrice").getAsInt());
        }

        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("enchantment", getEnchantmentRegistry().wrapAsHolder(enchantment).unwrapKey().orElseThrow().identifier().toString());
            json.addProperty("enabled", enabled);
            json.addProperty("level", level);
            json.addProperty("maxPrice", maxPrice);
            return json;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public void setMaxPrice(int maxPrice) {
            // Note: deliberately NOT clamping here. The config GUI calls this every render frame
            // with whatever is currently in the maxPrice text field, including intermediate
            // mid-typing values; clamping per-frame fights the user's edit. Clamping happens
            // on the explicit-entry paths (Commands.java) and on the constructor default.
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
            return Enchantment.getFullname(Holder.direct(enchantment), level).copy().withStyle(ChatFormatting.WHITE).getString();
        }
    }

}
