package online.kbpf.unsafeafkpro.client.TNT;

public class TNTValue {
    private String Name;
    private float distance, damage, fuseTime;

    public TNTValue(String name, float distance, float damage, float fuseTime) {
        Name = name;
        this.distance = distance;
        this.damage = damage;
        this.fuseTime = fuseTime;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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
