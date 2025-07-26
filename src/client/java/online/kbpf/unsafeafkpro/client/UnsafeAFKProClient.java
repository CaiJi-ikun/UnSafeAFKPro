package online.kbpf.unsafeafkpro.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import online.kbpf.unsafeafkpro.client.TNT.TntDamageHudElement;
import online.kbpf.unsafeafkpro.client.commands.RegisterCommand;
import online.kbpf.unsafeafkpro.client.config.ConfigManager;
import net.minecraft.util.Identifier;

public class UnsafeAFKProClient implements ClientModInitializer {

    private static final Identifier TNT_DAMAGE_HUD_ID = Identifier.of("unsafeafkpro", "tnt_damage_hud");



    @Override
    public void onInitializeClient() {
        ConfigManager.loadConfig();
        RegisterCommand.register();
        TntDamageHudElement tntHud = new TntDamageHudElement();
        HudElementRegistry.addLast(TNT_DAMAGE_HUD_ID, tntHud);
    }
}
