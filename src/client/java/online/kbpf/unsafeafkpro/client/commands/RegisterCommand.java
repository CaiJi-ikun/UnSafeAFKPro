package online.kbpf.unsafeafkpro.client.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import online.kbpf.unsafeafkpro.client.config.ConfigManager;
import online.kbpf.unsafeafkpro.client.config.ModConfig;

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
                                    .executes(RegisterCommand::getInfo))

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

    private static int getInfo(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        ModConfig modConfig = ConfigManager.getConfig();
        context.getSource().sendFeedback(Text.literal("发送消息:" + modConfig.getSafeAFKText() + " 安全血量:" + modConfig.getSafeAFKHealth()));
        return Command.SINGLE_SUCCESS;
    }
}