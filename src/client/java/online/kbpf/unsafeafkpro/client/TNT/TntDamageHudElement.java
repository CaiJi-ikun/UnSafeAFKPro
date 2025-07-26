package online.kbpf.unsafeafkpro.client.TNT;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import online.kbpf.unsafeafkpro.client.config.ConfigManager;
import online.kbpf.unsafeafkpro.client.config.ModConfig;

// 标记这个类只在客户端存在
@Environment(EnvType.CLIENT)
public class TntDamageHudElement implements HudElement {



    private int timer = 0;

    /**
     * 这是新的渲染方法。
     * DrawContext 是新的、统一的绘图工具，替代了直接操作 MatrixStack。
     */
    @Override
    public void render(DrawContext context, RenderTickCounter renderTickCounter) {

        ModConfig modConfig = ConfigManager.getConfig();
        MinecraftClient client = MinecraftClient.getInstance();

        if(timer > 0) timer--;

        if(client.player != null && client.player.getGameMode() != null && client.player.getGameMode().isSurvivalLike())
        {
            if (modConfig.isTNTHud() || modConfig.isSafeTNT()) {
                double MaxCalculationRadius = ConfigManager.getConfig().getTNTDistance();

                double MaxCalculationRadiusSQ = MaxCalculationRadius * MaxCalculationRadius;

                if (client.player == null || client.world == null) {
                    return;
                }

                int yOffset = ConfigManager.getConfig().getTNTHudY();

                // 遍历世界中的所有实体
                int count = 0;
                for (Entity entity : client.world.getEntities()) {
                    if (!(entity instanceof TntEntity tnt)) {
                        continue;
                    }

                    // 使用平方距离进行半径检查以优化性能
                    double distanceSq = client.player.squaredDistanceTo(tnt);
                    if (distanceSq > MaxCalculationRadiusSQ) {
                        continue;
                    }
                    if(count >= 20) break;
                    count++;

                    // 只有在半径内的TNT才会执行昂贵的伤害计算
                    float damage = TntDamageCalculator.getEstimatedDamage(tnt, client.player, client.world);

                    // 1. 调用 getFuse() 获取剩余的游戏刻
                    int fuseTicks = tnt.getFuse();

                    // 2. 将游戏刻转换为秒 (使用20.0f以获得浮点数结果)
                    float fuseSeconds = fuseTicks / 20.0f;

                    if (damage > 0.1f) {
                        double distance = Math.sqrt(distanceSq); // 仅在需要显示时才计算真正的距离
                        final float finalDamage = DamageCalculator.getFinalTntDamage(damage);

                        String displayText = String.format("TNT(%.1fm)[%.2fs|%.2fHP]", distance, fuseSeconds, finalDamage);
                        Text text = Text.literal(displayText).formatted(finalDamage >= 10.0f ? Formatting.RED : Formatting.YELLOW);

                        // 使用 DrawContext 来绘制文本
                        // 它已经包含了 MatrixStack，用法更简洁
                        if (modConfig.isTNTHud())
                            context.drawTextWithShadow(client.textRenderer, text, modConfig.getTNTHudX(), yOffset, 0xFF00FF00);
                        yOffset += 10;
                        if (damage > client.player.getHealth() && fuseTicks <= 5 && timer <= 0) {
                            if (modConfig.isSafeTNT()) {
                                String messageToSend = modConfig.getSafeAFKText();
                                if (!(messageToSend == null || messageToSend.trim().isEmpty())) {
                                    client.player.networkHandler.sendChatMessage(messageToSend);
                                    timer = 20;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}