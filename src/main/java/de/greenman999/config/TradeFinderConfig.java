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
    public TradeMode mode = TradeMode.SINGLE;

    public HashMap<Enchantment, Boolean> enchantments = new HashMap<>();

    public void save() {
        try {
            Files.deleteIfExists(configFile);

            JsonObject json = new JsonObject();
            json.addProperty("preventAxeBreaking", preventAxeBreaking);
            json.addProperty("mode", mode.name());

            JsonObject enchantmentsJson = new JsonObject();
            enchantments.forEach((enchantment, enabled) -> enchantmentsJson.addProperty(Registries.ENCHANTMENT.getEntry(enchantment).getKey().get().getValue().toString(), enabled));
            json.add("enchantments", enchantmentsJson);

            Files.writeString(configFile, gson.toJson(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            JsonObject json = gson.fromJson(Files.readString(configFile), JsonObject.class);

            if (json.has("preventAxeBreaking"))
                preventAxeBreaking = json.getAsJsonPrimitive("preventAxeBreaking").getAsBoolean();
            if (json.has("mode"))
                mode = TradeMode.valueOf(json.getAsJsonPrimitive("mode").getAsString());
            if (json.has("enchantments")) {
                JsonObject enchantmentsJson = json.getAsJsonObject("enchantments");
                enchantmentsJson.entrySet().forEach(entry -> {
                    RegistryKey<Enchantment> enchantmentKey = RegistryKey.of(Registries.ENCHANTMENT.getKey(), Identifier.tryParse(entry.getKey()));
                    Enchantment enchantment = Registries.ENCHANTMENT.get(enchantmentKey);
                    if (enchantment != null) {
                        enchantments.put(enchantment, entry.getValue().getAsBoolean());
                    }
                });
            }

            for(Enchantment enchantment : Registries.ENCHANTMENT) {
                if(!enchantments.containsKey(enchantment)) {
                    enchantments.put(enchantment, false);
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

    public Screen createGui(Screen parent) {
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
                        .option(Option.createBuilder(TradeMode.class)
                                .name(Text.literal("Trade Finding Mode"))
                                .tooltip(Text.literal("Single: Search for a single enchantment. \nList: Search for a list of enchantments."))
                                .binding(
                                        TradeMode.SINGLE,
                                        () -> mode,
                                        value -> mode = value
                                )
                                .controller(EnumController::new)
                                .build()
                        )
                        .build())
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
    }

    public enum TradeMode {
        SINGLE,
        LIST
    }

}
