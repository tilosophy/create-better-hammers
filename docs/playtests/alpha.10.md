# Alpha.10 — Approved workshop models

Target: Minecraft 1.21.1, NeoForge 21.1.233, Create 6.0.10, Java 21.

## Changes

- Stone and Iron have constructed workshop heads and tapered wooden handles.
- Diamond has broad gem facets and an iron cuff, widened to leave a visible border around the handle opening.
- All three basic tiers share the exposed wooden handle end and wedge detail.
- Pneumatic has a recessed wooden cog with a metal hub, a continuous copper frame/pivot support, a thicker underside pipe, and worn workshop materials.
- Approved holding angles and alpha.9 gameplay are unchanged, including the Sturdy Sheet completion message and 11/12 unpowered/powered Pneumatic damage.

## Install

Close Minecraft and replace the previous Create Better Hammers JAR in `mods` with `create-better-hammers-0.1.0-alpha.10.jar`. Keep only one version. For multiplayer, replace it on both client and server and restart both. Disable any hammer-preview resource pack. Existing test worlds are suitable; no new world or chunks are needed.

## Short testing protocol

1. Inspect all four hammers at rest and swinging with your usual shaders/FOV. Check first person, F5, inventory, and an item frame.
2. Check Diamond's cuff and exposed handle top; check Pneumatic's copper frame/pivot connection, recessed cog, and underside pipe. Compare once without shaders if shading looks odd.
3. Make an ordinary sheet, then a Sturdy Sheet with Pneumatic on raised magma: expect “1 of 2 strikes”, then “Sturdy Sheet crafted.” after the second strike.
4. Rotate a Create block and sneak-pick it up with Pneumatic.
5. In Survival, compare a landed hit with and without a backtank containing air: air replaces durability for the powered hit. Remove the tank and confirm ordinary durability use resumes.

Optional numerical check, with no enchantments or attribute-changing effects: hold Pneumatic and run `/attribute @s minecraft:generic.attack_damage get`. Expect 11 without usable air and 12 with it; Diamond remains 11. Full attack strength means waiting for the attack indicator to refill, then left-clicking—not holding a charge button.

Report screenshots for visual issues. Include `latest.log` if textures are missing, errors appear, or the game crashes.

## Build verification

- All three required dedicated-server GameTests passed: progression, combat and pneumatic behavior.
- A subsequent normal `clean build` passed.
- Packaged metadata reports alpha.10; all approved assets match source, JSON parses, and model/material texture references resolve.
- Runtime JAR excludes preview helpers, GameTest helpers and review resource overrides.
- No gameplay Java changes from alpha.9. Shader/modpack appearance still requires the player test above.

Runtime JAR SHA-256: `e88098bd9be1200785284b41eb125cc3ad65cdc72042ee63f84649b24cb42eaa`
