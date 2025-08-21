package online.kbpf.unsafeafkpro.client.config;

import java.util.HashMap;
import java.util.Map;

public class ModConfig {

    private int safeAFKHealth;
    private String safeAFKText;
    private boolean safeTNT, TNTHud, TNTNameTag;
    private int TNTHudX, TNTHudY, TNTDistance;
    private Map<String, String> serverMessages = new HashMap<>();


    public ModConfig(){
        safeAFKHealth = 0;
        safeAFKText = "";
        safeTNT = false;
        TNTHudX = 10;
        TNTHudY = 10;
        TNTDistance = 10;
        TNTHud = false;
        TNTNameTag = true;
    }

    public Map<String, String> getServerMessages() {
        return serverMessages;
    }

    public void setServerMessages(Map<String, String> serverMessages) {
        this.serverMessages = serverMessages;
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

    public boolean isTNTNameTag() {
        return TNTNameTag;
    }

    public void setTNTNameTag(boolean TNTNameTag) {
        this.TNTNameTag = TNTNameTag;
    }
}
