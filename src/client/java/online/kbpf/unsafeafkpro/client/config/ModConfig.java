package online.kbpf.unsafeafkpro.client.config;

public class ModConfig {

    private int safeAFKHealth;
    private String safeAFKText;


    public ModConfig(){
        safeAFKHealth = 0;
        safeAFKText = "!s";
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
