package online.kbpf.unsafeafkpro.client.TNT;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld; // 导入客户端世界
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.tag.DamageTypeTags;

public class DamageCalculator {

    // 创建一个包含所有盔甲槽位的静态数组，方便复用
    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };

    /**
     * 计算实体在受到特定伤害后，经过护甲和附魔减免的最终伤害。
     * @param rawDamage 原始伤害值
     * @param damageSource 伤害来源（必须提供）
     * @return 最终伤害值
     */
    public static float getFinalDamage(float rawDamage, DamageSource damageSource) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return rawDamage;
        }

        // 步骤一：计算盔甲减伤
        float damageAfterArmor = DamageUtil.getDamageLeft(
                player, rawDamage, damageSource,
                (float) player.getArmor(),
                (float) player.getAttributeValue(EntityAttributes.ARMOR_TOUGHNESS)
        );

        // 步骤二：根据Wiki规则，在客户端手动计算附魔保护
        int totalEpf = calculateProtectionFromWiki(player, damageSource);

        // 步骤三：应用附魔减伤
        float finalDamage = DamageUtil.getInflictedDamage(damageAfterArmor, totalEpf);
        finalDamage = applyResistance(finalDamage, player, damageSource);

        return finalDamage;
    }

    // --- 为了方便使用，我们可以提供一个专门用于TNT计算的重载方法 ---

    /**
     * 专门用于计算TNT爆炸伤害的便捷方法。
     * @param rawTntDamage TNT的原始伤害
     * @return 最终受到的伤害
     */
    public static float getFinalTntDamage(float rawTntDamage) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) {
            // 如果没有世界，无法创建伤害源，直接返回原始伤害
            return rawTntDamage;
        }

        // 在这里创建我们需要的、模拟的爆炸伤害来源
        DamageSource tntSource = world.getDamageSources().explosion(null, null);

        // 调用核心计算方法
        return getFinalDamage(rawTntDamage, tntSource);
    }

    private static int calculateProtectionFromWiki(ClientPlayerEntity player, DamageSource source) {
        int totalEpf = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack armorStack = player.getEquippedStack(slot);
            if (armorStack.isEmpty()) continue;
            ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(armorStack);
            for (RegistryEntry<Enchantment> enchantmentEntry : enchantments.getEnchantments()) {
                int level = enchantments.getLevel(enchantmentEntry);
                if (level <= 0) continue;
                if (enchantmentEntry.matchesKey(Enchantments.PROTECTION)) {
                    totalEpf += level;
                } else if (enchantmentEntry.matchesKey(Enchantments.FIRE_PROTECTION) && source.isIn(DamageTypeTags.IS_FIRE)) {
                    totalEpf += level * 2;
                } else if (enchantmentEntry.matchesKey(Enchantments.BLAST_PROTECTION) && source.isIn(DamageTypeTags.IS_EXPLOSION)) {
                    totalEpf += level * 2;
                } else if (enchantmentEntry.matchesKey(Enchantments.PROJECTILE_PROTECTION) && source.isIn(DamageTypeTags.IS_PROJECTILE)) {
                    totalEpf += level * 2;
                }
            }
        }
        return Math.min(totalEpf, 20);
    }

    private static float applyResistance(float damage, ClientPlayerEntity player, DamageSource source) {
        // 检查伤害源是否会绕过抗性效果
        if (source.isIn(DamageTypeTags.BYPASSES_RESISTANCE)) {
            return damage;
        }

        // 检查玩家是否有抗性提升效果
        StatusEffectInstance resistanceEffect = player.getStatusEffect(StatusEffects.RESISTANCE);
        if (resistanceEffect != null) {
            int amplifier = resistanceEffect.getAmplifier(); // 获取增幅等级 (效果等级-1)
            int level = amplifier + 1; // 实际效果等级

            // 每级减伤20%，所以乘以 (1.0f - level * 0.2f)
            damage *= (1.0f - (float)level * 0.2f);

            // 确保伤害不会变成负数
            return Math.max(damage, 0.0f);
        }

        return damage;
    }
}