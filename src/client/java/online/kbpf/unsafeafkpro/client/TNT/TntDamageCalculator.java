package online.kbpf.unsafeafkpro.client.TNT;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class TntDamageCalculator {




    /**
     * 计算单个TNT对目标实体可能造成的最大伤害。
     * @param tnt 爆炸源 TNT 实体
     * @param entity 目标实体 (通常是玩家)
     * @param world 客户端世界
     * @return 估算的伤害值
     */
    public static float getEstimatedDamage(TntEntity tnt, Entity entity, ClientWorld world) {
        if(world.getDifficulty().getName().equals("peaceful")) return 0;
        // 获取TNT的爆炸威力。在TntEntity类中，默认为4.0F。
        // 如果你的mod可以获取到非公开字段，可以直接读取。否则，先使用默认值。
        float power = 4.0F; // 默认威力

        // 1. 计算实体对爆炸的“暴露比例” (0.0 to 1.0)
        // 这部分完全复刻了服务端的 calculateReceivedDamage 方法
        float exposure = calculateExposure(tnt.getPos(), entity, world);

        // 如果完全被遮挡，则没有伤害
        if (exposure == 0.0f) {
            return 0.0f;
        }

        // 2. 根据距离计算伤害衰减
        double distance = tnt.getPos().distanceTo(entity.getPos());
        double explosionRadius = power * 2.0;

        // 如果实体在爆炸半径之外，则不受伤害
        if (distance >= explosionRadius) {
            return 0.0f;
        }

        // 距离越近，衰减越小
        double impact = (1.0 - (distance / explosionRadius)) * exposure;


        // 3. 计算基础伤害值
        // 这个公式是根据 Minecraft Wiki 和对游戏反混淆代码的研究得出的通用伤害公式
        float baseDamage = (float)((impact * impact + impact) * 7.0 * (double)power + 1.0);

        // 最终伤害

        if(world.getDifficulty().getName().equals("easy"))
            return (float) Math.min(baseDamage, baseDamage * 0.5 + 1);
        if(world.getDifficulty().getName().equals("normal"))
            return baseDamage;
        return (float) (baseDamage * 1.5);
    }

    /**
     * 通过向实体的碰撞箱发射多条射线，来计算实体暴露在爆炸中的比例。
     * 这是对服务端 `Explosion.calculateReceivedDamage` 方法的精确复刻。
     * @param sourcePos 爆炸源位置
     * @param entity 目标实体
     * @param world 客户端世界
     * @return 暴露比例 (0.0F to 1.0F)
     */
    private static float calculateExposure(Vec3d sourcePos, Entity entity, ClientWorld world) {
        Box box = entity.getBoundingBox();
        double d = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0);
        double e = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0);
        double f = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0);
        double g = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
        double h = (1.0 - Math.floor(1.0 / f) * f) / 2.0;

        if (d < 0.0 || e < 0.0 || f < 0.0) {
            return 0.0f;
        }

        int visiblePoints = 0;
        int totalPoints = 0;

        for (double k = 0.0; k <= 1.0; k += d) {
            for (double l = 0.0; l <= 1.0; l += e) {
                for (double m = 0.0; m <= 1.0; m += f) {
                    double n = MathHelper.lerp(k, box.minX, box.maxX);
                    double o = MathHelper.lerp(l, box.minY, box.maxY);
                    double p = MathHelper.lerp(m, box.minZ, box.maxZ);

                    Vec3d targetPos = new Vec3d(n + g, o, p + h);

                    // 进行射线检测，从实体的一个采样点射向爆炸中心
                    RaycastContext raycastContext = new RaycastContext(
                            targetPos,
                            sourcePos,
                            RaycastContext.ShapeType.COLLIDER, // 只考虑有碰撞体积的方块
                            RaycastContext.FluidHandling.NONE, // 忽略液体
                            entity
                    );

                    // 如果射线没有碰到任何方块 (MISS)，说明这个点是可见的
                    if (world.raycast(raycastContext).getType() == HitResult.Type.MISS) {
                        visiblePoints++;
                    }
                    totalPoints++;
                }
            }
        }

        if (totalPoints == 0) return 0.0f;

        return (float) visiblePoints / (float) totalPoints;
    }
}