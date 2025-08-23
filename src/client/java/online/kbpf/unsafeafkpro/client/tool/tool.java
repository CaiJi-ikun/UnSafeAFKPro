package online.kbpf.unsafeafkpro.client.tool;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.text.Text;
import online.kbpf.unsafeafkpro.client.config.ConfigManager;
import online.kbpf.unsafeafkpro.client.config.ModConfig;

public class tool {
    private tool(){};

    private static long lastSendTime = 0; // 记录上一次发送的时间戳（毫秒）

    public static void exitGame() {
        ModConfig modConfig = ConfigManager.getConfig();
        String text = modConfig.getSafeAFKText();

        if (text != null && !text.isEmpty()) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.player != null) {
                long now = System.currentTimeMillis();

                // 判断是否冷却完毕（2000ms = 2s）
                if (now - lastSendTime < 2000) {
                    return; // 在冷却中，不发送
                }

                text = text.trim();

                // 特殊关键字 "exit" → 退出当前游戏
                if (text.equalsIgnoreCase("exit")) {
                    if (client.isIntegratedServerRunning()) {
                        // 单人世界 → 退出到主菜单
                        client.setScreen(new GameMenuScreen(true));
                    } else if (client.getCurrentServerEntry() != null) {
                        // 多人服务器 → 断开连接
                        client.player.networkHandler.getConnection().disconnect(Text.translatable("unsafeafk.exit"));
                    }
                    return;
                }

                if (text.startsWith("/")) {
                    String command = text.substring(1).trim();
                    if (!command.isEmpty()) {
                        client.player.networkHandler.sendChatCommand(command);
                    }
                } else {
                    if (!text.isEmpty()) {
                        client.player.networkHandler.sendChatMessage(text);
                    }
                }

                // 更新最后发送时间
                lastSendTime = now;
            }
        }
    }


}
