# Create: Better Hammers — Player Guide

For **0.1.0-beta.1**, Minecraft Java **1.21.1 / NeoForge**.

## Install or update

1. Close Minecraft; stop the server before updating a multiplayer installation.
2. Install NeoForge and Create with their required dependencies. The development target is NeoForge **21.1.233** and Create **6.0.10**, using Java 21.
3. Put `create-better-hammers-0.1.0-beta.1.jar` in the instance's `mods` folder. For multiplayer, install the same version on client and server.
4. Remove the older Better Hammers JAR so only one remains. Disable old hammer-preview resource packs.
5. Restart. Existing alpha.11 test worlds and hammer items retain their IDs; this release introduces no world generation and needs no new chunks.

Use the runtime JAR in `mods`. The `-sources.jar` is for developers and must not be installed as a second mod.

## Craft your hammer

Stone, Iron and Diamond each use five head materials and two sticks. Use cobblestone, iron ingots or diamonds respectively.

| Crafting row | Left | Centre | Right |
|---|---|---|---|
| Top | Head material | Head material | Head material |
| Middle | Head material | Stick | Head material |
| Bottom | Empty | Stick | Empty |

The Pneumatic Hammer upgrades a Diamond Hammer:

| Crafting row | Left | Centre | Right |
|---|---|---|---|
| Top | Gold Sheet | Iron Sheet | Gold Sheet |
| Middle | Cogwheel | Diamond Hammer | Cogwheel |
| Bottom | Empty | Create Wrench | Empty |

These are ordinary small Cogwheels. The upgrade recipe consumes its ingredients.

## Make ordinary sheets

1. Place **stone**, **smooth stone** or an **undamaged anvil** on top of a level floor. Other stone variants and damaged anvils are not included by default.
2. Stand on the floor beside the work block. Keep your feet on the ground.
3. Hold the hammer in your main hand and the input material in your offhand.
4. Aim at the work block's **top face** and right-click. Hold right-click to repeat.

The work block must be at the same block Y coordinate as your feet, next to you. A stone floor under your feet is too low; standing on the work block or clicking its side will not work.

| Hammer | Supported ordinary sheets | Base durability |
|---|---|---:|
| Stone | Iron, copper, compatible zinc | 64 |
| Iron | Stone materials plus brass and gold | 256 |
| Diamond | Iron materials plus compatible pressing sheets/plates | 1,024 |
| Pneumatic | Same ordinary materials as Diamond | 1,024 |

One successful ordinary strike consumes one input, produces one sheet and applies one durability wear in Survival unless the Pneumatic Hammer uses backtank air. All four tiers share a **20-tick cooldown**: one second at 20 ticks per second. Switching hammers cannot bypass it. Leave inventory space for the result.

Iron, copper, brass and gold use their standard Create sheet outputs. Zinc requires a suitable regular Create pressing recipe whose selected output is exactly one guaranteed zinc sheet/plate. Diamond and Pneumatic can process additional inputs when Create's selected regular pressing recipe yields exactly one guaranteed sheet/plate. Chance outputs, multiple outputs and stacks larger than one are rejected.

The mod recognizes ordinary sheet/plate item names. A datapack can additionally identify outputs with the item tag `create_better_hammers:hammerable_sheets`; the pressing recipe requirements still apply. Adding an item to this tag alone does not create a recipe.

## Make Sturdy Sheets

1. Place a **magma block** on top of the floor and stand beside it.
2. Hold a **Pneumatic Hammer** in your main hand and **Create's Powdered Obsidian** in your offhand.
3. Right-click the top. The first strike shows progress without consuming powder or applying durability wear.
4. Strike the same block again after the cooldown, or keep holding right-click.
5. Completion consumes one Powdered Obsidian, gives one Sturdy Sheet and applies **eight durability wear** in Survival.

Backtank air does **not** replace the eight-point Sturdy Sheet durability cost. The first strike starts the normal shared cooldown. Switching held items, changing the offhand stack, leaving reach, leaving the valid standing position or changing the work block cancels pending progress. A full inventory blocks completion; make room and try again. Partial progress is not saved through reconnects.

Magma is specifically for Sturdy Sheets. Use the ordinary work surfaces for other materials.

## Use the Pneumatic Hammer as a wrench

Right-click supported Create blocks to rotate them; sneak-right-click to pick up blocks that support Create's wrench pickup. Create's radial rotation menu and instant Survival left-click pickup of plain minecarts are also integrated. This targets Create's wrench behavior; it does not promise compatibility with every third-party wrench system.

## Backtank power and combat

Wear a Create backtank containing air and hold the Pneumatic Hammer in your main hand. Ordinary sheet strikes and landed combat hits can spend air instead of durability. When usable air runs out, those actions return to durability wear.

| Hammer | Base fully charged attack damage | Base attack speed | Approximate recovery |
|---|---:|---:|---:|
| Stone | 9 | 0.8 | 1.25 s |
| Iron | 10 | 0.8 | 1.25 s |
| Diamond | 11 | 0.8 | 1.25 s |
| Pneumatic, without air | 11 | 0.8 | 1.25 s |
| Pneumatic, with air | 12 | 1.0 | 1 s |

Damage is in points, before armour, enchantments, critical hits and other modifiers; two points equal one heart. Recovery assumes normal server speed. A **charged attack** means waiting for the normal attack indicator to refill, then left-clicking. There is no hold-to-charge action.

Backtank bonuses apply only to Pneumatic combat. Ordinary sheet crafting retains its 20-tick cooldown. Hammers can disable shields like axes. Supported enchantments are **Unbreaking, Mending, Smite and Bane of Arthropods**; normal enchantment compatibility rules still apply. Durability values above describe base wear before enchantment effects.

## Quick troubleshooting

| Symptom | Check |
|---|---|
| Raised-surface message | Stand beside a block placed on the floor, stay grounded, and click its top. |
| Higher-tier message | Use Iron for brass/gold, or Diamond/Pneumatic for additional compatible sheets. |
| Zinc unavailable | Confirm your modpack provides the zinc sheet/plate and a qualifying pressing recipe. |
| Material cannot be hammered | Check the selected pressing recipe and output rules above. |
| No room for output | Free an inventory slot or space in a matching output stack. |
| Sturdy progress resets | Stay beside the same magma block and keep both held stacks unchanged. |
| Backtank does not protect Sturdy crafting | Expected: completion still applies eight durability wear. |
| Anvil opens its menu | Hold the input material in your offhand; an empty offhand preserves normal anvil use. |
| Old textures appear | Disable any preview resource pack and check for duplicate hammer JARs. |

Hold Shift over a hammer for in-game guidance. When reporting an issue, include Minecraft, NeoForge, Create and Better Hammers versions, reproduction steps, and the relevant log or crash report.
