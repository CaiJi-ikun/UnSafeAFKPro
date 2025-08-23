package online.kbpf.unsafeafkpro.client.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

import online.kbpf.unsafeafkpro.client.config.ConfigManager;
import online.kbpf.unsafeafkpro.client.config.ModConfig;

import java.util.Map;


@Environment(EnvType.CLIENT)
public class RegisterCommand {

    public RegisterCommand() {
        register();
    }

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("safeafk")
                    .then(ClientCommandManager.literal("safeafk")
                            .then(ClientCommandManager.literal("health")
                                    // 为 "health" 子命令添加一个名为 "healthValue" 的整数参数
                                    .then(ClientCommandManager.argument("healthValue", IntegerArgumentType.integer(0)) // 可以设置最小值，例如0
                                            // 将命令的执行逻辑链接到 saveHealth 方法
                                            .executes(RegisterCommand::saveHealth)
                                    )
                            )
                            .then(ClientCommandManager.literal("text")
                                    .then(ClientCommandManager.argument("sendText", StringArgumentType.string())
                                            .executes(RegisterCommand::saveText)
                                    )
                            )
                            .then(ClientCommandManager.literal("info")
                                    .executes(RegisterCommand::getAFKInfo))

                    )
                    .then(ClientCommandManager.literal("safetnt")
                            .then(ClientCommandManager.literal("info")
                                    .executes(RegisterCommand::getTNTInfo))
                            .then(ClientCommandManager.literal("tnthud")
                                    .then(ClientCommandManager.argument("true/false", BoolArgumentType.bool())
                                            .executes(RegisterCommand::saveTNTHud)))
                            .then(ClientCommandManager.literal("safetnt")
                                    .then(ClientCommandManager.argument("true/false", BoolArgumentType.bool())
                                            .executes(RegisterCommand::saveSafeTNT)))
                            .then(ClientCommandManager.literal("tnthudlocation")
                                    .then(ClientCommandManager.argument("X", IntegerArgumentType.integer())
                                            .then(ClientCommandManager.argument("Y", IntegerArgumentType.integer())
                                                    .executes(RegisterCommand::saveTNTLocation))))
                            .then(ClientCommandManager.literal("tntnametag")
                                    .then(ClientCommandManager.argument("true/false", BoolArgumentType.bool())
                                            .executes(RegisterCommand::saveTNTNameTag)))
                    )
                    .then(ClientCommandManager.literal("autologin")
                            .then(ClientCommandManager.literal("info")
                                    .executes(RegisterCommand::getAutoLogin))
                            .then(ClientCommandManager.literal("text")
                                    .then(ClientCommandManager.argument("message", StringArgumentType.string())
                                            .executes(RegisterCommand::saveAutoLogin))
                            )

                    )

                    // 添加 "health" 子命令
            );
        });
    }


    private static int saveHealth(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final int healthValue = IntegerArgumentType.getInteger(context, "healthValue");

        ModConfig modConfig = ConfigManager.getConfig();
        modConfig.setSafeAFKHealth(healthValue);
        ConfigManager.setConfig(modConfig);
        ConfigManager.saveConfig();

        context.getSource().sendFeedback(Text.translatable("unsafeafk.command.safehealth", healthValue));
        return Command.SINGLE_SUCCESS;
    }

    private static int saveText(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final String Text = StringArgumentType.getString(context, "sendText");

        ModConfig modConfig = ConfigManager.getConfig();
        modConfig.setSafeAFKText(Text);
        ConfigManager.setConfig(modConfig);
        ConfigManager.saveConfig();

        context.getSource().sendFeedback(net.minecraft.text.Text.translatable("unsafeafk.command.safetext", Text));
        context.getSource().sendFeedback(net.minecraft.text.Text.translatable("unsafeafk.command.safetexthelp"));
        return Command.SINGLE_SUCCESS;
    }

    private static int getAFKInfo(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        ModConfig modConfig = ConfigManager.getConfig();
        context.getSource().sendFeedback(
                Text.translatable("unsafeafk.command.safetext", modConfig.getSafeAFKText())
        );
        context.getSource().sendFeedback(
                Text.translatable("unsafeafk.command.safehealth", modConfig.getSafeAFKHealth())
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int getTNTInfo(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        ModConfig modConfig = ConfigManager.getConfig();
        context.getSource().sendFeedback(Text.translatable("unsafeafk.command.safetnt.safeafk", modConfig.isSafeTNT()));
        context.getSource().sendFeedback(Text.translatable("unsafeafk.command.safetnt.hud", modConfig.isTNTHud()));
        context.getSource().sendFeedback(Text.translatable("unsafeafk.command.safetnt.nametag", modConfig.isTNTNameTag()));
        context.getSource().sendFeedback(Text.translatable("unsafeafk.command.safetnt.hudX", modConfig.getTNTHudX()));
        context.getSource().sendFeedback(Text.translatable("unsafeafk.command.safetnt.hudY", modConfig.getTNTHudY()));
        context.getSource().sendFeedback(Text.literal("========================================="));
        context.getSource().sendFeedback(Text.translatable("unsafeafk.command.safetnt.info1"));
        context.getSource().sendFeedback(Text.translatable("unsafeafk.command.safetnt.info2"));
        return Command.SINGLE_SUCCESS;
    }

    private static int saveTNTHud(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final boolean TNTHud = BoolArgumentType.getBool(context, "true/false");

        ModConfig modConfig = ConfigManager.getConfig();
        modConfig.setTNTHud(TNTHud);
        ConfigManager.setConfig(modConfig);
        ConfigManager.saveConfig();

        context.getSource().sendFeedback(Text.translatable("unsafeafk.command.safetnt.hud", TNTHud));
        return Command.SINGLE_SUCCESS;
    }

    private static int saveSafeTNT(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final boolean safeTNT = BoolArgumentType.getBool(context, "true/false");

        ModConfig modConfig = ConfigManager.getConfig();
        modConfig.setSafeTNT(safeTNT);
        ConfigManager.setConfig(modConfig);
        ConfigManager.saveConfig();

        context.getSource().sendFeedback(Text.translatable("unsafeafk.command.safetnt.safeafk", safeTNT));
        return Command.SINGLE_SUCCESS;
    }

    private static int saveTNTLocation(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final int X = IntegerArgumentType.getInteger(context, "X");
        final int Y = IntegerArgumentType.getInteger(context, "Y");

        ModConfig modConfig = ConfigManager.getConfig();
        modConfig.setTNTHudX(X);
        modConfig.setTNTHudY(Y);
        ConfigManager.setConfig(modConfig);
        ConfigManager.saveConfig();

        context.getSource().sendFeedback(Text.translatable("unsafeafk.command.safetnt.hudX", X));
        context.getSource().sendFeedback(Text.translatable("unsafeafk.command.safetnt.hudY", Y));
        return Command.SINGLE_SUCCESS;
    }

    private static int saveTNTNameTag(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final boolean TNTNameTag = BoolArgumentType.getBool(context, "true/false");

        ModConfig modConfig = ConfigManager.getConfig();
        modConfig.setTNTNameTag(TNTNameTag);
        ConfigManager.setConfig(modConfig);
        ConfigManager.saveConfig();

        context.getSource().sendFeedback(Text.translatable("unsafeafk.command.safetnt.nametag", TNTNameTag));
        return Command.SINGLE_SUCCESS;
    }

    private static int saveAutoLogin(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final String message = StringArgumentType.getString(context, "message");

        MinecraftClient client = MinecraftClient.getInstance();
        if (!client.isIntegratedServerRunning() && client.getCurrentServerEntry() != null) {
            ModConfig modConfig = ConfigManager.getConfig();
            Map<String, String> map = modConfig.getServerMessages();
            String address = client.getCurrentServerEntry().address;
            map.put(address, message);

            modConfig.setServerMessages(map);
            ConfigManager.setConfig(modConfig);
            ConfigManager.saveConfig();

            context.getSource().sendFeedback(Text.translatable("unsafeafk.command.autologin.text", modConfig.getServerMessages().get(address)));
            context.getSource().sendFeedback(Text.translatable("unsafeafk.command.autologin.texthelp"));
            return Command.SINGLE_SUCCESS;
        } else throw new RuntimeException("无法获取服务器ip"); // 这里也要改成翻译

    }

    private static int getAutoLogin(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        ModConfig modConfig = ConfigManager.getConfig();
        MinecraftClient client = MinecraftClient.getInstance();
        if (!client.isIntegratedServerRunning() && client.getCurrentServerEntry() != null) {
            context.getSource().sendFeedback(
                    Text.translatable("unsafeafk.command.autologin.text", modConfig.getServerMessages().get(client.getCurrentServerEntry().address))
            );
            return Command.SINGLE_SUCCESS;
        } else throw new RuntimeException(I18n.translate("unsafeafk.command.autologin.error"));

    }


}