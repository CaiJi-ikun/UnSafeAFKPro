package online.kbpf.unsafeafkpro.client.config;

public class ModConfig {

    private int safeAFKHealth;
    private String safeAFKText;
    private boolean safeTNT, TNTHud;
    private int TNTHudX, TNTHudY, TNTDistance;


    public ModConfig(){
        safeAFKHealth = 0;
        safeAFKText = "";
        safeTNT = false;
        TNTHudX = 10;
        TNTHudY = 10;
        TNTDistance = 10;
        TNTHud = true;
    }



    public boolean isTNTHud() {
        return TNTHud;
    }

    public void setTNTHud(boolean TNTHud) {
        this.TNTHud = TNTHud;
    }

    public int getTNTHudX() {
        return TNTHudX;
    }

    public void setTNTHudX(int TNTHudX) {
        this.TNTHudX = TNTHudX;
    }

    public int getTNTHudY() {
        return TNTHudY;
    }

    public void setTNTHudY(int TNTHudY) {
        this.TNTHudY = TNTHudY;
    }

    public int getTNTDistance() {
        return TNTDistance;
    }

    public void setTNTDistance(int TNTDistance) {
        this.TNTDistance = TNTDistance;
    }

    public boolean isSafeTNT() {
        return safeTNT;
    }

    public void setSafeTNT(boolean safeTNT) {
        this.safeTNT = safeTNT;
    }

    public int getSafeAFKHealth() {
        return safeAFKHealth;
    }

    public void setSafeAFKHealth(int safeAFKHealth) {
        this.safeAFKHealth = safeAFKHealth;
    }

    public String getSafeAFKText() {
        return safeAFKText;
    }

    public void setSafeAFKText(String safeAFKText) {
        this.safeAFKText = safeAFKText;
    }
}
