package dev.perfboost.util;

import dev.perfboost.PerfBoost;
import net.minecraft.client.MinecraftClient;

/**
 * Tracks a rolling FPS average and drives the dynamic simulation-distance
 * adjustment when {@link dev.perfboost.config.PerfBoostConfig#dynamicSimDistance}
 * is enabled.
 */
public class FpsTracker {

    // Rolling average window (in client ticks = ~50 ms each at 20 TPS)
    private static final int WINDOW = 20;

    private final int[] samples = new int[WINDOW];
    private int head = 0;
    private int sum = 0;
    private int filled = 0;

    /** Ticks spent continuously below targetFps. */
    private int lowFpsTicks = 0;

    /** Whether simulation distance has already been reduced this "dip". */
    private boolean simReduced = false;

    public void tick(MinecraftClient client) {
        if (client.world == null || client.player == null) return;

        int fps = client.getCurrentFps();

        // Update rolling average
        sum -= samples[head];
        samples[head] = fps;
        sum += fps;
        head = (head + 1) % WINDOW;
        if (filled < WINDOW) filled++;

        if (!PerfBoost.config.dynamicSimDistance) return;

        int avg = getAverageFps();
        int target = PerfBoost.config.targetFps;

        if (avg < target) {
            lowFpsTicks++;
            if (lowFpsTicks >= PerfBoost.config.fpsDropGraceTicks && !simReduced) {
                int current = client.options.getSimulationDistance().getValue();
                int minDist = PerfBoost.config.minSimDistance;
                if (current > minDist) {
                    int next = Math.max(minDist, current - 2);
                    client.options.getSimulationDistance().setValue(next);
                    PerfBoost.LOGGER.info(
                            "[PerfBoost] FPS avg {}  < target {}  → SimDist {} → {}",
                            avg, target, current, next);
                    simReduced = true;
                }
            }
        } else {
            lowFpsTicks = 0;
            if (simReduced) {
                // FPS has recovered – restore default distance
                int def = PerfBoost.config.defaultSimDistance;
                int current = client.options.getSimulationDistance().getValue();
                if (current < def) {
                    client.options.getSimulationDistance().setValue(def);
                    PerfBoost.LOGGER.info(
                            "[PerfBoost] FPS recovered ({} avg) → SimDist {} → {}",
                            avg, current, def);
                }
                simReduced = false;
            }
        }
    }

    public int getAverageFps() {
        if (filled == 0) return 60;
        return sum / filled;
    }

    public boolean isLowFps() {
        return getAverageFps() < PerfBoost.config.targetFps;
    }
}
