package online.kbpf.unsafeafkpro.client;

import net.fabricmc.api.ClientModInitializer;
import online.kbpf.unsafeafkpro.client.commands.RegisterCommand;
import online.kbpf.unsafeafkpro.client.config.ConfigManager;

public class UnsafeAFKProClient implements ClientModInitializer {




    @Override
    public void onInitializeClient() {
        ConfigManager.loadConfig();
        RegisterCommand.register();
    }
}
