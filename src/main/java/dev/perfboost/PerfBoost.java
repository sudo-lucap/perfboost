package dev.perfboost;

import dev.perfboost.config.PerfBoostConfig;
import dev.perfboost.util.FpsTracker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerfBoost implements ClientModInitializer {

    public static final String MOD_ID = "perfboost";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static PerfBoostConfig config;
    public static FpsTracker fpsTracker;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[PerfBoost] Initializing...");

        config = PerfBoostConfig.load();
        fpsTracker = new FpsTracker();

        // Register per-tick logic
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            fpsTracker.tick(client);
        });

        LOGGER.info("[PerfBoost] Ready. Entity throttle={}, DynSimDist={}, FrustumCull={}",
                config.entityTickThrottling, config.dynamicSimDistance, config.aggressiveFrustumCull);
    }
}
