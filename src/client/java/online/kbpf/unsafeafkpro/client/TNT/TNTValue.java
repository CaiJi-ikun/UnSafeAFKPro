package online.kbpf.unsafeafkpro.client.TNT;

import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;

public class TNTValue {
    private float distance, damage, fuseTime;
    private Entity entity;


    public TNTValue(Entity entity, float distance, float damage, float fuseTime) {
        this.distance = distance;
        this.damage = damage;
        this.fuseTime = fuseTime;
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getFuseTime() {
        return fuseTime;
    }

    public void setFuseTime(float fuseTime) {
        this.fuseTime = fuseTime;
    }
}
