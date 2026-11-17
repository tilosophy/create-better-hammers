# Alpha.11 — Workshop wear polish

Target: Minecraft 1.21.1, NeoForge 21.1.233, Create 6.0.10, Java 21.

## Changes

- Stronger wear, grime and tarnish across the four hammers; rusted fasteners replace bright silver details.
- Coarser handle grain and dedicated simple textures for wooden ends and wedges.
- Quieter Diamond facets with more localized surface detail.
- Separated the overlapping brass/copper surfaces that caused the reported Z-fighting; top rivets remain seated on the housing.
- Approved holding angles, head shapes, crafting and combat are unchanged.

## Install

Close Minecraft. Replace the old Create Better Hammers JAR in `mods` with `create-better-hammers-0.1.0-alpha.11.jar`; keep only one version. Disable old hammer-preview resource packs. For multiplayer, install the same JAR on client and server and restart both. Existing test worlds are suitable; no new world or chunks are required.

## Short test protocol

1. Inspect all four hammers at rest and swinging with your usual shaders. Check wear, handle grain, Diamond facets and the small wooden wedges.
2. Hold Pneumatic and slowly change your view against light and dark backgrounds. Check the upper and rear copper/brass joins for flickering. Repeat briefly without shaders if anything looks odd.
3. Inspect inventory, F5, an item frame and the left-hand setting for texture or orientation problems.
4. Make one ordinary sheet, then a Sturdy Sheet with two strikes on raised magma. Confirm the completion message; rotate and sneak-pick up a Create block.
5. In Survival, land a hit with a backtank containing air, then remove it and repeat: air should replace durability only while powered.

No need to repeat the full tier/damage matrix unless a behavior changes. If needed, an unenchanted Pneumatic Hammer reports 11 attack damage without usable air and 12 with it using `/attribute @s minecraft:generic.attack_damage get`; Diamond remains 11.

Send screenshots for visual feedback. For flicker, note whether it occurs with shaders disabled. Include `latest.log` for missing textures, errors or crashes.

## Verification

- All three dedicated-server GameTests passed on the target versions above.
- A subsequent normal `clean build` passed with test-source injection disabled.
- Packaged assets match the approved source exactly; model/material references, JSON, version metadata, MIT license and Java 21 class versions passed inspection.
- No preview capture classes, GameTest classes or test structures are in the runtime JAR.
- The previously approved Minecraft preview assets are unchanged. Testing with the actual modpack/shaders and a real two-player server remains manual.

Runtime JAR: `create-better-hammers-0.1.0-alpha.11.jar` (99571 bytes).

SHA-256: `8d8c3cd7a75267eea48b7296df4f5c1fa592cd46b7de0eb42a5124f755473dd2`
