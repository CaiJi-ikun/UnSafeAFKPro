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
import net.minecraft.text.Text;

import online.kbpf.unsafeafkpro.client.config.ConfigManager;
import online.kbpf.unsafeafkpro.client.config.ModConfig;


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

        context.getSource().sendFeedback(Text.literal("安全血量已设置为:" + healthValue));
        return Command.SINGLE_SUCCESS;
    }

    private static int saveText(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final String Text = StringArgumentType.getString(context, "sendText");

        ModConfig modConfig = ConfigManager.getConfig();
        modConfig.setSafeAFKText(Text);
        ConfigManager.setConfig(modConfig);
        ConfigManager.saveConfig();

        context.getSource().sendFeedback(net.minecraft.text.Text.literal("发送消息设置为:" + Text));
        return Command.SINGLE_SUCCESS;
    }

    private static int getAFKInfo(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        ModConfig modConfig = ConfigManager.getConfig();
        context.getSource().sendFeedback(Text.literal("发送消息:" + ConfigManager.getConfig().getSafeAFKText() + " 安全血量:" + ConfigManager.getConfig().getSafeAFKHealth()));
        return Command.SINGLE_SUCCESS;
    }

    private static int getTNTInfo(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        ModConfig modConfig = ConfigManager.getConfig();
        context.getSource().sendFeedback(Text.literal("tnt安全挂机:" + ConfigManager.getConfig().isSafeTNT()));
        context.getSource().sendFeedback(Text.literal("tntHud:" + ConfigManager.getConfig().isTNTHud()));
        context.getSource().sendFeedback(Text.literal("TNTHud显示位置X:" + ConfigManager.getConfig().getTNTHudX()));
        context.getSource().sendFeedback(Text.literal("TNTHud显示位置Y:" + ConfigManager.getConfig().getTNTHudY()));
        context.getSource().sendFeedback(Text.literal("========================================="));
        context.getSource().sendFeedback(Text.literal("TNTHud关闭仍会在后台计算相关数数值以用于safeTNT"));
        context.getSource().sendFeedback(Text.literal("将safeTNT和TNTHud都关闭,后台会停止计算TNT数值"));
        context.getSource().sendFeedback(Text.literal("safeTNT使用safeAFK聊天消息"));
        return Command.SINGLE_SUCCESS;
    }

    private static int saveTNTHud(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final boolean TNTHud = BoolArgumentType.getBool(context, "true/false");

        ModConfig modConfig = ConfigManager.getConfig();
        modConfig.setTNTHud(TNTHud);
        ConfigManager.setConfig(modConfig);
        ConfigManager.saveConfig();

        context.getSource().sendFeedback(Text.literal("TNTHud已设置为:" + TNTHud));
        return Command.SINGLE_SUCCESS;
    }

    private static int saveSafeTNT(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final boolean safeTNT = BoolArgumentType.getBool(context, "true/false");

        ModConfig modConfig = ConfigManager.getConfig();
        modConfig.setSafeTNT(safeTNT);
        ConfigManager.setConfig(modConfig);
        ConfigManager.saveConfig();

        context.getSource().sendFeedback(Text.literal("TNTHud已设置为:" + safeTNT));
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

        context.getSource().sendFeedback(Text.literal("TNTHud已设置为:" + X + " " + Y));
        return Command.SINGLE_SUCCESS;
    }

}