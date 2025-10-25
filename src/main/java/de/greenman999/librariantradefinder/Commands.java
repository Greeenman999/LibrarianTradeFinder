package de.greenman999.librariantradefinder;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import static de.greenman999.librariantradefinder.LibrarianTradeFinder.openConfig;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class Commands {
    public static void registerCommands(){
        register("tradefinder");
        register("tf");
    }

    private static void register(String base) {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal(base)
                    .then(literal("select")
                        .then(literal("manual").executes(context -> {
                            TradeFinder.selectManual();
                            return 1;
                        }))
                        .executes(context -> (TradeFinder.select() ? 1 : 0)))
                    .then(literal("search").executes(context -> TradeFinder.searchList())
                        .then(argument("enchantment", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, RegistryKeys.ENCHANTMENT)).executes(context -> {
                            RegistryEntry<Enchantment> enchantmentRegistryEntry = context.getArgument("enchantment", RegistryEntry.class);
                            Enchantment enchantment = enchantmentRegistryEntry.value();

                            return TradeFinder.searchSingle(enchantment, 1, 64);
                        })
                            .then(argument("level", IntegerArgumentType.integer(1, 5)).executes(context -> {
                                RegistryEntry<Enchantment> enchantmentRegistryEntry = context.getArgument("enchantment", RegistryEntry.class);
                                Enchantment enchantment = enchantmentRegistryEntry.value();
                                int level = IntegerArgumentType.getInteger(context, "level");

                                return TradeFinder.searchSingle(enchantment, level, 64);
                            })
                                .then(argument("maxPrice", IntegerArgumentType.integer(1, 64)).executes(context -> {
                                    RegistryEntry<Enchantment> enchantmentRegistryEntry = context.getArgument("enchantment", RegistryEntry.class);
                                    Enchantment enchantment = enchantmentRegistryEntry.value();
                                    int level = IntegerArgumentType.getInteger(context, "level");
                                    int bookPrice = IntegerArgumentType.getInteger(context, "maxPrice");

                                    return TradeFinder.searchSingle(enchantment, level, bookPrice);
                                }))
                            )
                        )
                    )
                    .then(literal("config").executes(context -> {
                        openConfig(MinecraftClient.getInstance());
                        return 1;
                    }))
                    .then(literal("stop").executes(context -> {
                        TradeFinder.stop();
                        context.getSource().sendFeedback(Text.translatable("commands.tradefinder.stop.success").styled(style -> style.withColor(TextColor.fromFormatting(Formatting.GREEN))));
                        return 1;
                    }))
            );
        });
    }
}
