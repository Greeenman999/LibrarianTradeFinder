package de.greenman999.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import dev.isxander.yacl.gui.controllers.cycling.EnumController;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class TradeFinderConfig {
    public static final TradeFinderConfig INSTANCE = new TradeFinderConfig();

    public final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("librarian-trade-finder.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public boolean preventAxeBreaking = true;
    public boolean tpToVillager = false;

    public HashMap<Enchantment, EnchantmentOption> enchantments = new HashMap<>();

    public void save() {
        try {
            Files.deleteIfExists(configFile);

            JsonObject json = new JsonObject();
            json.addProperty("configVersion", 1);
            json.addProperty("preventAxeBreaking", preventAxeBreaking);
            json.addProperty("tpToVillager", tpToVillager);

            JsonObject enchantmentsJson = new JsonObject();
            enchantments.forEach((enchantment, enchantmentOption) -> enchantmentsJson.add(Registries.ENCHANTMENT.getEntry(enchantment).getKey().get().getValue().toString(), enchantmentOption.toJson()));
            json.add("enchantments", enchantmentsJson);

            Files.writeString(configFile, gson.toJson(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            if(!Files.exists(configFile)) {
                Files.createFile(configFile);
                Files.writeString(configFile, "{}");
            }
            JsonObject json = gson.fromJson(Files.readString(configFile), JsonObject.class);

            if(!(!json.has("configVersion") || json.get("configVersion").getAsInt() != 1)) {
                if (json.has("preventAxeBreaking"))
                    preventAxeBreaking = json.getAsJsonPrimitive("preventAxeBreaking").getAsBoolean();
                if (json.has("tpToVillager"))
                    tpToVillager = json.getAsJsonPrimitive("tpToVillager").getAsBoolean();
                if (json.has("enchantments")) {
                    JsonObject enchantmentsJson = json.getAsJsonObject("enchantments");
                    enchantmentsJson.entrySet().forEach(entry -> {
                        RegistryKey<Enchantment> enchantmentKey = RegistryKey.of(Registries.ENCHANTMENT.getKey(), Identifier.tryParse(entry.getKey()));
                        Enchantment enchantment = Registries.ENCHANTMENT.get(enchantmentKey);
                        if (enchantment != null) {
                            enchantments.put(enchantment, EnchantmentOption.fromJson(entry.getValue().getAsJsonObject()));
                        }
                    });
                }
            }

            for(Enchantment enchantment : Registries.ENCHANTMENT) {
                if(!enchantments.containsKey(enchantment)) {
                    if(!enchantment.isAvailableForEnchantedBookOffer()) continue;
                    enchantments.put(enchantment, new EnchantmentOption(enchantment, false));
                }
            }
            sortEnchantmentsMap();

            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sortEnchantmentsMap() {
        enchantments = enchantments.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(enchantment -> enchantment.getName(enchantment.getMaxLevel()).copy().formatted(Formatting.WHITE).getString())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    /*public Screen createGui(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.literal("Librarian Trade Finder Config"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("General"))
                        .option(Option.createBuilder(boolean.class)
                                .name(Text.literal("Prevent axe breaking"))
                                .tooltip(Text.literal("Stop the searching process if your axe is about to break."))
                                .binding(
                                        true,
                                        () -> preventAxeBreaking,
                                        value -> preventAxeBreaking = value
                                )
                                .controller(TickBoxController::new)
                                .build())
                        .option(Option.createBuilder(boolean.class)
                                .name(Text.literal("Teleport to Villager"))
                                .tooltip(Text.literal("Teleport to the villager before the lectern is placed, to pick up items that dropped behind the lectern."))
                                .binding(
                                        false,
                                        () -> tpToVillager,
                                        value -> tpToVillager = value
                                )
                                .controller(TickBoxController::new)
                                .build())
                        .build()
                )
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("List Enchantments"))
                        .options(getEnchantmentOptions())
                        .build())
                .save(this::save)
                .build()
                .generateScreen(parent);
    }

    public @NotNull Collection<Option<?>> getEnchantmentOptions() {
        return enchantments.entrySet().stream().map(entry -> Option.createBuilder(boolean.class)
                .name(entry.getKey().getName(entry.getKey().getMaxLevel()).copy().formatted(Formatting.WHITE))
                .tooltip(Text.literal("Search for this enchantment."))
                .binding(
                        false,
                        entry::getValue,
                        entry::setValue
                )
                .controller(TickBoxController::new)
                .build()).collect(Collectors.toList());
    }*/

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
            RegistryKey<Enchantment> enchantmentKey = RegistryKey.of(Registries.ENCHANTMENT.getKey(), Identifier.tryParse(json.getAsJsonPrimitive("enchantment").getAsString()));
            Enchantment enchantment = Registries.ENCHANTMENT.get(enchantmentKey);
            if (enchantment == null) return null;
            return new EnchantmentOption(enchantment, json.getAsJsonPrimitive("enabled").getAsBoolean(), json.getAsJsonPrimitive("level").getAsInt(), json.getAsJsonPrimitive("maxPrice").getAsInt());
        }

        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("enchantment", Registries.ENCHANTMENT.getEntry(enchantment).getKey().get().getValue().toString());
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

        public Enchantment getEnchantment() {
            return enchantment;
        }

        public String getName() {
            return enchantment.getName(level).copy().formatted(Formatting.WHITE).getString();
        }
    }

}
