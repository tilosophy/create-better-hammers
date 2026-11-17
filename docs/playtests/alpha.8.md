# Alpha.8 — Pneumatic Hammer visual playtest

Minecraft 1.21.1 / NeoForge 21.1.233 / Create 6.0.10 is the development test environment. Keep your existing compatible modpack versions for this visual comparison.

## Install

1. Close Minecraft. Replace the previous Create Better Hammers JAR in the instance's `mods` folder with `create-better-hammers-0.1.0-alpha.8.jar`; keep only one version installed.
2. For multiplayer, install the same candidate on the server and restart it too.
3. Reopen your existing test world. No new world, new chunks, or separate resource pack is needed. Disable any earlier hammer-preview resource pack if one was installed.

## Short testing protocol

1. **At rest:** use your usual FOV and shaders; compare Pneumatic with Diamond. Check height, angle, screen coverage and the hidden handle bottom. Part of the rear wrench is intentionally outside the first-person view.
2. **In motion:** swing at air, hit a mob, and hammer a sheet. Check for clipping or an awkward return to rest.
3. **Other views:** inspect F5, the left-hand setting, the inventory/hotbar, an item frame, and a dropped hammer. Check the wrench opening and the diamond plate on the end.
4. **Materials:** compare with Create's wrench in daylight and indoors. Check brass colour, texture detail and readability; briefly compare with shaders off if something looks odd.
5. **Quick regression:** craft a normal sheet, make a Sturdy Sheet with two strikes on raised magma, then rotate and sneak-pick up a Create block.

If anything looks wrong, send a screenshot and note the view, FOV and whether shaders were enabled. A log is useful for missing textures, rendering errors or a crash.

## Scope

This candidate changes only the Pneumatic Hammer's presentation and the version identifier. Stone, Iron and Diamond retain their existing visuals. Recipes, combat values, air use, durability, cooldowns and crafting messages are unchanged from alpha.7. The previously discussed powered +1 damage and Sturdy Sheet completion-message changes are not part of this visual candidate.

The mesh and material artwork are original project assets; the material atlas was generated for this project and exported to eight 16×16 texture swatches. NeoForge's built-in OBJ loader renders the mesh; no additional mod is required for the model.
