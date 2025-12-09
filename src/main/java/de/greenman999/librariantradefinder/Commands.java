package de.greenman999.librariantradefinder;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.enchantment.Enchantment;

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
                        .then(argument("enchantment", ResourceArgument.resource(registryAccess, Registries.ENCHANTMENT)).executes(context -> {
                            Holder<Enchantment> enchantmentRegistryEntry = context.getArgument("enchantment", Holder.class);
                            Enchantment enchantment = enchantmentRegistryEntry.value();

                            return TradeFinder.searchSingle(enchantment, 1, 64);
                        })
                            .then(argument("level", IntegerArgumentType.integer(1, 5)).executes(context -> {
                                Holder<Enchantment> enchantmentRegistryEntry = context.getArgument("enchantment", Holder.class);
                                Enchantment enchantment = enchantmentRegistryEntry.value();
                                int level = IntegerArgumentType.getInteger(context, "level");

                                return TradeFinder.searchSingle(enchantment, level, 64);
                            })
                                .then(argument("maxPrice", IntegerArgumentType.integer(1, 64)).executes(context -> {
                                    Holder<Enchantment> enchantmentRegistryEntry = context.getArgument("enchantment", Holder.class);
                                    Enchantment enchantment = enchantmentRegistryEntry.value();
                                    int level = IntegerArgumentType.getInteger(context, "level");
                                    int bookPrice = IntegerArgumentType.getInteger(context, "maxPrice");

                                    return TradeFinder.searchSingle(enchantment, level, bookPrice);
                                }))
                            )
                        )
                    )
                    .then(literal("config").executes(context -> {
                        openConfig();
                        return 1;
                    }))
                    .then(literal("stop").executes(context -> {
                        TradeFinder.stop();
                        context.getSource().sendFeedback(Component.translatable("commands.tradefinder.stop.success").withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.GREEN))));
                        return 1;
                    }))
            );
        });
    }
}
