package online.kbpf.unsafeafkpro.client.autoLogin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import online.kbpf.unsafeafkpro.client.config.ConfigManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class AutoLoginHandler {

    public static void init() {
        // 进入服务器事件
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!client.isIntegratedServerRunning() && client.getCurrentServerEntry() != null) {
                String ip = client.getCurrentServerEntry().address;

                // 从配置读取自动登录消息
                String msg = ConfigManager.getConfig().getServerMessages().get(ip);

                if (msg != null && !msg.isEmpty() && client.player != null) {
//                    client.player.sendChatMessage(msg);
                    client.player.networkHandler.sendChatCommand(msg);
                }
            }
        });
    }
}
