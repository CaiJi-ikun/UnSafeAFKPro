package online.kbpf.unsafeafkpro.client.autoLogin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import online.kbpf.unsafeafkpro.client.config.ConfigManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class AutoLoginHandler {
    private static boolean justDisconnected = true;

    public static void init() {
        // 断开服务器时重置标记
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            justDisconnected = true;
        });

        // 进入服务器事件
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!client.isIntegratedServerRunning() && client.getCurrentServerEntry() != null) {
                // 如果不是刚刚断开重连，而是切子服，就跳过
                if (!justDisconnected) {
                    return;
                }
                justDisconnected = false;

                String ip = client.getCurrentServerEntry().address;

                // 从配置读取自动登录消息
                String msg = ConfigManager.getConfig().getServerMessages().get(ip);

                if (msg != null && !msg.isEmpty() && client.player != null) {
                    // 延迟到下一帧再执行
                    client.execute(() -> {
                        if (client.player != null) {
                            
                            client.player.networkHandler.sendChatCommand(msg);
                        }
                    });
                }
            }
        });
    }
}
