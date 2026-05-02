package dev.perfboost.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class PerfBoostConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("perfboost.json");

    // ── Entity Tick Throttling ──────────────────────────────────────────────
    /** Enable entity tick throttling for entities far from the player. */
    public boolean entityTickThrottling = true;

    /** Entities beyond this many blocks tick every N frames instead of every frame. */
    public int throttleDistanceBlocks = 32;

    /** Far entities tick once every this many client ticks. */
    public int throttleInterval = 4;

    // ── Dynamic Simulation Distance ────────────────────────────────────────
    /** Automatically reduce simulation distance when FPS falls below the target. */
    public boolean dynamicSimDistance = true;

    /** Target FPS; below this, simulation distance is reduced. */
    public int targetFps = 60;

    /** Minimum simulation distance that dynamic scaling will allow. */
    public int minSimDistance = 4;

    /** Simulation distance to restore when FPS recovers. */
    public int defaultSimDistance = 10;

    /**
     * How many consecutive ticks FPS must stay below targetFps before
     * the simulation distance is reduced.
     */
    public int fpsDropGraceTicks = 40;

    // ── Aggressive Frustum Culling ─────────────────────────────────────────
    /**
     * Enable stricter frustum culling: entity bounding boxes are shrunk
     * slightly before the visibility test, hiding entities that are nearly
     * off-screen and saving draw calls.
     */
    public boolean aggressiveFrustumCull = true;

    /**
     * How many blocks to shrink each axis of the bounding box before the
     * frustum test.  Larger values cull more aggressively but may cause
     * entities near the screen edge to pop in/out.
     */
    public double frustumShrinkBlocks = 0.5;

    // ── Particle Limiter ───────────────────────────────────────────────────
    /** Reduce the number of particles spawned when FPS is low. */
    public boolean particleLimiter = true;

    /** When FPS < targetFps, only this fraction of particle spawn calls go through. */
    public float particleKeepFraction = 0.4f;

    // ─────────────────────────────────────────────────────────────────────

    public static PerfBoostConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader r = Files.newBufferedReader(CONFIG_PATH)) {
                return GSON.fromJson(r, PerfBoostConfig.class);
            } catch (IOException e) {
                PerfBoostConfigLogger.warn("Failed to read config, using defaults: " + e.getMessage());
            }
        }
        PerfBoostConfig defaults = new PerfBoostConfig();
        defaults.save();
        return defaults;
    }

    public void save() {
        try (Writer w = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(this, w);
        } catch (IOException e) {
            PerfBoostConfigLogger.warn("Failed to save config: " + e.getMessage());
        }
    }

    // Tiny helper so we don't pull the main logger into a static context before init
    private static class PerfBoostConfigLogger {
        static void warn(String msg) {
            System.err.println("[PerfBoost/Config] WARN: " + msg);
        }
    }
}
