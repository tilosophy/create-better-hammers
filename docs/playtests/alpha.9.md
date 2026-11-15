# Alpha.9 — Model family and powered combat

Development target: Minecraft 1.21.1, NeoForge 21.1.233, Create 6.0.10, Java 21.

## Changes

- Stone, Iron and Diamond have solid heads and handles using their existing artwork and handheld transforms.
- Pneumatic keeps the alpha.8 pose and main silhouette. Revised materials, one recessed steel cog per side, a covered lower mechanism, a wrench pivot, two top rivets, and a pipe connecting two fittings.
- A completed second strike immediately replaces the progress message with “Sturdy Sheet crafted.”
- An equipped Create backtank with air gives Pneumatic +1 attack damage, alongside its existing speed and air-use benefits. Empty tanks and other tools receive no bonus.

## Install

Close Minecraft and replace the old Create Better Hammers JAR in `mods` with alpha.9. Keep only one version. For multiplayer, update both client and server and restart both. Use an existing test world; no new chunks or world are needed. Disable any old hammer-preview resource pack.

## Short test

1. Compare all four hammers at rest and swinging, with your usual FOV/shaders. Check Pneumatic against alpha.8 screenshots: its position should be unchanged.
2. Inspect F5, inventory, an item frame and the left-hand setting. Check solid heads, the diamond end plate, gear recess, jaw pivot and material shading. Briefly compare without shaders if a surface looks odd.
3. Make a Sturdy Sheet: Powdered Obsidian in offhand, Pneumatic in main hand, two right-clicks on raised magma, waiting for the cooldown. Expect “1 of 2 strikes”, then “Sturdy Sheet crafted.” immediately after completion.
4. In Survival, hold Pneumatic with no enchantments/effects. Check the damage values below with no tank, a tank with air, and an empty tank. Switch to Diamond while wearing the charged tank: it should still be 11.
5. Hit a mob after the attack indicator refills: a tank with air should lose air instead of hammer durability. Remove/empty the tank and repeat: hammer durability should decrease. No button is held to charge an attack.
6. Quick regression: make an ordinary sheet, rotate a Create block, then sneak-pick it up. Interrupt a first Sturdy Sheet strike by changing tools and confirm two new strikes are needed.

## Numerical damage check

In a test world with cheats enabled, hold the tool, wait a moment, and run:

```mcfunction
/attribute @s minecraft:generic.attack_damage get
```

| Tool and equipment | Expected attack damage |
| --- | ---: |
| Pneumatic, no tank or empty tank | 11 |
| Pneumatic, equipped backtank with air | 12 |
| Diamond, with or without a backtank | 11 |

These are damage points, not hearts, and assume no effects or other equipment modifying attack damage. Armour and enchantments can change actual damage received by a target. A dynamic bonus may not appear in the ordinary item stat tooltip; the command checks the player's actual attribute.

For recovery, `/attribute @s minecraft:generic.attack_speed get` should report approximately 0.8 without air and 1.0 with air while holding Pneumatic.

Send screenshots for visual feedback, noting the view, FOV and shaders. Send `latest.log` if textures are missing, a render error appears, or the game crashes.
