package online.kbpf.unsafeafkpro.client.TNT;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import online.kbpf.unsafeafkpro.client.config.ConfigManager;
import online.kbpf.unsafeafkpro.client.config.ModConfig;

import java.util.ArrayList;

// 标记这个类只在客户端存在
@Environment(EnvType.CLIENT)
public class TntDamageHudElement implements HudElement {

    private final int ALPHA = 0xFF;
    private final int COLOR_SAFE = 0x00FF00;   // 绿色
    private final int COLOR_DANGER = 0xFF0000; // 红色
    private final float MAX_FUSE_FOR_COLOR = 4.0f;    // TNT引信时间上限

    private int timer = 0;

    private long gameTick = 0;

    private ArrayList<TNTValue> TNTList = new ArrayList<>();

    /**
     * 这是新的渲染方法。
     * DrawContext 是新的、统一的绘图工具，替代了直接操作 MatrixStack。
     */
    @Override
    public void render(DrawContext context, RenderTickCounter renderTickCounter) {

        ModConfig modConfig = ConfigManager.getConfig();

        if(!modConfig.isSafeTNT() && !modConfig.isTNTHud() && !modConfig.isTNTNameTag())
            return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null)
            return;

        if(client.player.getGameMode() != null && !client.player.getGameMode().isSurvivalLike())
            return;


        if (gameTick < client.world.getTime() || gameTick > client.world.getTime()) {
            if(timer > 0) timer--;

            double MaxCalculationRadius = ConfigManager.getConfig().getTNTDistance();
            double MaxCalculationRadiusSQ = MaxCalculationRadius * MaxCalculationRadius;

            double MaxRenderRadiusSQ = 400;
            // 遍历世界中的所有实体
            int count = 0;
            TNTList.clear();
            float Health = client.player.getHealth();
            for (Entity entity : client.world.getEntities()) {
                if (!(entity instanceof TntEntity tnt)) {
                    continue;
                }

                // 使用平方距离进行半径检查以优化性能
                double distanceSq = client.player.squaredDistanceTo(tnt);
                if (distanceSq > MaxCalculationRadiusSQ) {
                    if(modConfig.isTNTNameTag() && distanceSq <  MaxRenderRadiusSQ) {
                        double fuse = tnt.getFuse() / 20.0f;
                        tnt.setCustomName(Text.literal(String.format("%.2fs", fuse)).setStyle(Style.EMPTY.withColor((ALPHA << 24) | getGradientColor((float) fuse, 0, MAX_FUSE_FOR_COLOR, COLOR_DANGER, COLOR_SAFE))));
                        tnt.setCustomNameVisible(true);
                    }
                    continue;
                }
                if(count >= 20) break;
                count++;

                // 只有在半径内的TNT才会执行昂贵的伤害计算
                float damage = TntDamageCalculator.getEstimatedDamage(tnt, client.player, client.world);
                final float finalDamage = DamageCalculator.getFinalTntDamage(damage);

                int fuseTicks = tnt.getFuse();
                float fuseSeconds = fuseTicks / 20.0f;
                double distance = Math.sqrt(distanceSq);


                final float MAX_DAMAGE_FOR_COLOR = client.player.getHealth(); // 满血是20，作为伤害颜色的上限

                if (modConfig.isTNTNameTag()) {

                    MutableText nameBuilder = Text.literal(String.format("%.2fm", distance)).setStyle(Style.EMPTY.withColor((ALPHA << 24) | getGradientColor((float) distance, 0, 10, COLOR_DANGER, COLOR_SAFE)));
                    nameBuilder.append(Text.literal("|").formatted(Formatting.GRAY));
                    nameBuilder.append(Text.literal(String.format("%.2fs", fuseSeconds)).setStyle(Style.EMPTY.withColor((ALPHA << 24) | getGradientColor(fuseSeconds, 0, MAX_FUSE_FOR_COLOR, COLOR_DANGER, COLOR_SAFE))));
                    nameBuilder.append(Text.literal("|").formatted(Formatting.GRAY));
                    nameBuilder.append(Text.literal(String.format("%.2fHP", finalDamage)).setStyle(Style.EMPTY.withColor((ALPHA << 24) | getGradientColor(finalDamage, 2, MAX_DAMAGE_FOR_COLOR, COLOR_SAFE, COLOR_DANGER))));

                    tnt.setCustomName(nameBuilder);
                    tnt.setCustomNameVisible(true);
                }


                TNTList.add(new TNTValue(tnt, (float) distance, finalDamage, fuseSeconds));

                if (fuseTicks <= 10 && timer <= 0 && modConfig.isSafeTNT()) {
                    Health = Health - finalDamage;
                    if(Health > 0.5) continue;
                    String messageToSend = modConfig.getSafeAFKText();
                    if (!(messageToSend == null || messageToSend.trim().isEmpty())) {
                        client.player.networkHandler.sendChatMessage(messageToSend);
                        timer = 40;
                    }
                }
            }

        }

        gameTick = client.world.getTime();

        int yOffset = ConfigManager.getConfig().getTNTHudY();
        for (TNTValue tntValue : TNTList) {

            String displayText = String.format("TNT(%.1fm)[%.2fs|%.2fHP]", tntValue.getDistance(), tntValue.getFuseTime(), tntValue.getDamage());
            Text text = Text.literal(displayText).formatted(tntValue.getDamage() >= 10.0f ? Formatting.RED : Formatting.YELLOW);

            if (modConfig.isTNTHud())
                context.drawTextWithShadow(client.textRenderer, text, modConfig.getTNTHudX(), yOffset, 0xFF00FF00);
            yOffset += 10;
        }
    }

    private static int getGradientColor(float value, float minValue, float maxValue, int startColor, int endColor) {
        // 1. 确保值和范围有效，避免除以零
        if (maxValue <= minValue) {
            return startColor;
        }

        // 2. 计算进度 (p)。并使用 clamp 将其限制在 0.0 到 1.0 之间
        float p = (value - minValue) / (maxValue - minValue);
        p = Math.max(0.0f, Math.min(1.0f, p)); // Clamp p to [0, 1]

        // 3. 将起始和结束颜色分解为 R, G, B 分量
        int startR = (startColor >> 16) & 0xFF;
        int startG = (startColor >> 8) & 0xFF;
        int startB = startColor & 0xFF;

        int endR = (endColor >> 16) & 0xFF;
        int endG = (endColor >> 8) & 0xFF;
        int endB = endColor & 0xFF;

        // 4. 对每个颜色通道进行线性插值
        int r = (int) (startR + (endR - startR) * p);
        int g = (int) (startG + (endG - startG) * p);
        int b = (int) (startB + (endB - startB) * p);

        // 5. 将插值后的R, G, B分量重新合成为一个整数颜色值
        return (r << 16) | (g << 8) | b;
    }
}