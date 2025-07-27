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

import java.util.ArrayList;

// 标记这个类只在客户端存在
@Environment(EnvType.CLIENT)
public class TntDamageHudElement implements HudElement {



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

        if(!modConfig.isSafeTNT() && !modConfig.isTNTHud())
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
                    continue;
                }
                if(count >= 20) break;
                count++;

                // 只有在半径内的TNT才会执行昂贵的伤害计算
                float damage = TntDamageCalculator.getEstimatedDamage(tnt, client.player, client.world);

                if (damage <= 0.1f)
                    continue;

                int fuseTicks = tnt.getFuse();
                float fuseSeconds = fuseTicks / 20.0f;

                double distance = Math.sqrt(distanceSq);
                final float finalDamage = DamageCalculator.getFinalTntDamage(damage);

                TNTList.add(new TNTValue("TNT", (float) distance, finalDamage, fuseSeconds));

                if (fuseTicks <= 5 && timer <= 0 && modConfig.isSafeTNT()) {
                    Health =- finalDamage;
                    if(Health > 0.5) continue;
                    String messageToSend = modConfig.getSafeAFKText();
                    if (!(messageToSend == null || messageToSend.trim().isEmpty())) {
                        client.player.networkHandler.sendChatMessage(messageToSend);
                        timer = 40;
                    }
                }
            }
            gameTick = client.world.getTime();
        }

        int yOffset = ConfigManager.getConfig().getTNTHudY();
        for (TNTValue tntValue : TNTList) {

            String displayText = String.format("TNT(%.1fm)[%.2fs|%.2fHP]", tntValue.getDistance(), tntValue.getFuseTime(), tntValue.getDamage());
            Text text = Text.literal(displayText).formatted(tntValue.getDamage() >= 10.0f ? Formatting.RED : Formatting.YELLOW);

            if (modConfig.isTNTHud())
                context.drawTextWithShadow(client.textRenderer, text, modConfig.getTNTHudX(), yOffset, 0xFF00FF00);
            yOffset += 10;
        }
    }
}