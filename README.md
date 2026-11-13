# Create: Better Hammers

Adds hand hammers to Create for making metal sheets without a stationary Mechanical Press.

## Playtest

Install the same mod JAR on the Minecraft 1.21.1 client and dedicated server alongside NeoForge 21.1.233 or newer in the 21.1 series and Create 6.0.10.

| Hammer | Head material | Sheets | Durability |
| --- | --- | --- | ---: |
| Stone | Cobblestone | Iron, copper, and compatible zinc | 64 |
| Iron | Iron ingots | Everything Stone handles, plus brass and gold | 256 |
| Diamond | Diamonds | Everything Iron handles, plus compatible Create pressing sheets and plates | 1,024 |
| Pneumatic | Diamond Hammer and Create parts | Everything Diamond handles, plus Sturdy Sheets | 1,024 |

1. Craft a Stone, Iron, or Diamond Hammer with three head materials across the top, one head material on each side of the middle row, and a stick in the center of each of the bottom two rows.
2. Hold the hammer in your main hand and the material in your offhand.
3. Stand beside raised stone, smooth stone, or an anvil and right-click the top. On a level floor, the work block sits on top of the floor.
4. Each successful strike on these surfaces turns one input item into one sheet and uses one durability in Survival (or air for a powered Pneumatic Hammer). It also starts a 20-tick cooldown shared by all four tiers.

Hold Shift over a hammer in your inventory for instructions. For zinc, the hammer follows the Mechanical Press's regular recipe choice when it yields exactly one guaranteed zinc sheet or plate.

Diamond and Pneumatic also follow the Mechanical Press's regular recipe choice for additional sheets and plates from Create, addons, and datapacks. The selected recipe must yield one guaranteed sheet or plate. Recipe packs can mark additional outputs with the `create_better_hammers:hammerable_sheets` item tag.

### Pneumatic Hammer

In a crafting table, place Gold Sheet, Iron Sheet, and Gold Sheet across the top. Place a small Cogwheel on each side of a Diamond Hammer in the middle row. Place a Create Wrench in the bottom center.

Use the Pneumatic Hammer to rotate and sneak-pick up Create machines. It also works with Create's radial rotation menu and its instant left-click pickup of plain minecarts in Survival.

A charged Create backtank spends air on ordinary sheet strikes and landed hits, in place of durability. The Pneumatic Hammer deals 11 damage at full charge and recharges in about 1.25 seconds without air or 1 second with air. Sheet hammering keeps its usual 20-tick cooldown.

### Sturdy Sheets

1. Hold a Pneumatic Hammer in your main hand and Create's Powdered Obsidian in your offhand.
2. Stand beside a raised magma block and right-click the top. The first strike shows your progress and leaves the powder and durability intact.
3. Strike the same block again after the cooldown. The second strike uses one Powdered Obsidian, puts one Sturdy Sheet in your inventory, and applies eight points of durability wear in Survival. You can hold right-click through both strikes.

Switching either held item or moving out of reach resets the first strike. If your inventory is full, make room and try the second strike again.

Stone, Iron, and Diamond deal 9, 10, and 11 damage at full charge and recharge in about 1.25 seconds. Hammers use vanilla attack sounds and first-person motion and disable shields like axes. Landed hits ordinarily cost one durability. Supported enchantments are Unbreaking, Mending, Smite, and Bane of Arthropods.

## Build and test

Use JDK 21. On Windows, run `gradlew.bat build` from the repository folder. On Linux or macOS, run `./gradlew build`. The JAR appears in `build/libs/`.

For a playtest, compare the tiers on brass, gold, and an addon plate such as electrum. With the crosshair attack indicator enabled, compare a fully charged axe hit and hammer hit on separate mobs. Hold right-click or swap hammers rapidly and check that sheets arrive no faster than once per second at 20 ticks per second. Try the side of a raised block, floor-level stone, raised dirt, and a full inventory. A rejected strike should leave the ingot and durability intact. Right-clicking an anvil with an empty offhand should still open the anvil menu.
