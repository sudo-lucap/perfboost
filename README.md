# PerfBoost — Fabric 1.21.1 Performance Mod

Custom-built to complement your existing mod stack (Sodium-Vulkan, ImmediatelyFast,
Dynamic FPS, FerriteCore, ModernFix, More Culling, CCME, EntityCulling).

---

## Features

| Feature | What it does | Config key |
|---|---|---|
| **Entity Tick Throttling** | Entities >32 blocks away tick every 4 frames instead of every frame | `entityTickThrottling` |
| **Dynamic Sim Distance** | Reduces simulation distance when FPS < 60, restores it when FPS recovers | `dynamicSimDistance` |
| **Aggressive Frustum Cull** | Shrinks entity bounding boxes by 0.5 blocks before visibility test | `aggressiveFrustumCull` |
| **Particle Limiter** | Drops 60% of particle spawns when FPS is low | `particleLimiter` |

---

## Building

### Requirements
- **Java 21** (e.g. Eclipse Temurin 21)
- Git (optional)

### Steps

```bash
# 1. Enter the project directory
cd perfboost

# 2. Run the Gradle build (downloads Fabric toolchain automatically)
./gradlew build          # Linux / macOS
gradlew.bat build        # Windows

# 3. Find your .jar in:
#    build/libs/perfboost-1.0.0.jar
```

Copy `build/libs/perfboost-1.0.0.jar` into your `.minecraft/mods/` folder.

> **Windows shortcut**: If you don't have the Gradle wrapper yet, run:
> ```
> gradle wrapper --gradle-version 8.8
> ```

---

## Config

On first launch, a config file is created at:

```
.minecraft/config/perfboost.json
```

```json
{
  "entityTickThrottling": true,
  "throttleDistanceBlocks": 32,
  "throttleInterval": 4,

  "dynamicSimDistance": true,
  "targetFps": 60,
  "minSimDistance": 4,
  "defaultSimDistance": 10,
  "fpsDropGraceTicks": 40,

  "aggressiveFrustumCull": true,
  "frustumShrinkBlocks": 0.5,

  "particleLimiter": true,
  "particleKeepFraction": 0.4
}
```

Edit and save — changes take effect on next game launch.

---

## Compatibility Notes

| Your Mod | Interaction |
|---|---|
| **CCME** | No conflict — CCME is chunk scheduling; this mod touches entity ticking |
| **EntityCulling** | Complementary — EntityCulling uses async occlusion; this mod acts at frustum-test time |
| **More Culling** | Complementary — More Culling targets block/tile entities; this targets living entities |
| **ImmediatelyFast** | No conflict — ImmediatelyFast batches draw calls after this mod's culling already ran |
| **Dynamic FPS** | No conflict — Dynamic FPS only activates when window is unfocused |
| **Sodium-Vulkan (dev)** | ⚠ Dev build — if you see rendering glitches, disable `aggressiveFrustumCull` first |

---

## Troubleshooting

- **Entities pop in/out at screen edges** → reduce `frustumShrinkBlocks` to `0.2` or set `aggressiveFrustumCull: false`
- **Sim distance keeps yo-yoing** → increase `fpsDropGraceTicks` to `80` or `120`
- **Distant mobs frozen** → increase `throttleInterval` to `2` or disable `entityTickThrottling`
