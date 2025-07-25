package online.kbpf.unsafeafkpro.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigManager {

    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ModConfig config;

    public ConfigManager(){
        loadConfig();
    }

    private static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("UnSafeAFKPro" + ".json");
    }

    public static void saveConfig() {
        // 使用 try-with-resources 确保 FileWriter 在使用后被正确关闭
        try (FileWriter writer = new FileWriter(getConfigPath().toFile())) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            // 在日志中打印错误，方便调试
            System.err.println("无法保存配置文件 for " + "UnSafeAFKPro");
            e.printStackTrace();
        }
    }

    /**
     * 从文件加载配置。如果文件不存在，则创建并保存一个默认配置。
     */
    public static void loadConfig() {
        Path configFile = getConfigPath();

        if (configFile.toFile().exists()) {
            // 文件存在，尝试读取
            try (FileReader reader = new FileReader(configFile.toFile())) {
                config = GSON.fromJson(reader, ModConfig.class);
            } catch (IOException e) {
                System.err.println("无法读取配置文件 for " + "UnSafeAFKPro");
                e.printStackTrace();
                // 如果读取失败，也创建一个新的默认配置
                config = new ModConfig();
            }
        } else {
            // 文件不存在，创建一个新的默认配置并保存它
            config = new ModConfig();
            saveConfig();
        }
    }

    public static ModConfig getConfig() {
        if (config == null) loadConfig();
        return config;
    }

    public static void setConfig(ModConfig config) {
        ConfigManager.config = config;
    }
}
