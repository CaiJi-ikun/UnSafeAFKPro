package online.kbpf.unsafeafkpro.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import online.kbpf.unsafeafkpro.client.config.ConfigManager;
import online.kbpf.unsafeafkpro.client.tool.tool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    // 用于存储上一刻的生命值，以检测生命值的“下降”而不是持续处于低血量
    @Unique
    private float unsafeafkpro_previousHealth = -1.0f;

    /**
     * @Inject 注解告诉 Mixin 处理器将我们的代码注入到目标方法中。
     * method = "tick" 指定了我们要注入的目标是 tick() 方法。
     * at = @At("TAIL") 指定了注入点在 tick() 方法的末尾，确保我们获取的是最新的玩家状态。
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {


        // 将 this 转换为 ClientPlayerEntity 实例以访问其方法
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;

        // 获取当前生命值
        float currentHealth = player.getHealth();

        // 首次运行时，初始化 previousHealth 并跳过后续逻辑
        if (unsafeafkpro_previousHealth == -1.0f) {
            this.unsafeafkpro_previousHealth = currentHealth;
            return;
        }

        // 从配置中获取安全血量和要发送的消息
        int safeHealthLevel = ConfigManager.getConfig().getSafeAFKHealth();
        String messageToSend = ConfigManager.getConfig().getSafeAFKText();


        // 核心逻辑：当 当前生命值 < 安全值 并且 当前生命值 < 上一刻的生命值
        // 这确保了只在生命值“刚刚下降”到阈值以下时触发一次
        if (currentHealth < safeHealthLevel && currentHealth < this.unsafeafkpro_previousHealth) {

            // 使用客户端网络处理器发送聊天消息到服务器
            tool.exitGame();
        }

        // 在每一刻的最后，更新“上一刻的生命值”记录，为下一刻的比较做准备
        this.unsafeafkpro_previousHealth = currentHealth;
    }
}