package de.greenman999.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TradeFinderConfig {
    public static final TradeFinderConfig INSTANCE = new TradeFinderConfig();

    public final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("librarian-trade-finder.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public boolean preventAxeBreaking = true;

    public void save() {
        try {
            Files.deleteIfExists(configFile);

            JsonObject json = new JsonObject();
            json.addProperty("preventAxeBreaking", preventAxeBreaking);

            Files.writeString(configFile, gson.toJson(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            if (Files.notExists(configFile)) {
                save();
                return;
            }

            JsonObject json = gson.fromJson(Files.readString(configFile), JsonObject.class);

            if (json.has("preventAxeBreaking"))
                preventAxeBreaking = json.getAsJsonPrimitive("preventAxeBreaking").getAsBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                        .build())
                .save(this::save)
                .build()
                .generateScreen(parent);
    }

}
