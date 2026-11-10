# Create: Better Hammers

Adds hand hammers to Create for making metal sheets without a stationary Mechanical Press.

## Playtest

Install the same mod JAR on the Minecraft 1.21.1 client and dedicated server alongside NeoForge 21.1.233 or newer in the 21.1 series and Create 6.0.10.

| Hammer | Head material | Sheets | Durability |
| --- | --- | --- | ---: |
| Stone | Cobblestone | Iron, copper, and compatible zinc | 64 |
| Iron | Iron ingots | Everything Stone handles, plus brass and gold | 256 |

1. Craft a hammer with three head materials across the top, one head material on each side of the middle row, and a stick in the center of each of the bottom two rows.
2. Hold the hammer in your main hand and ingots in your offhand.
3. Stand beside raised stone, smooth stone, or an anvil and right-click the top. On a level floor, the work block sits on top of the floor.
4. Each successful strike turns one ingot into one sheet and uses one durability in Survival. It also starts a 20-tick cooldown shared by both hammer tiers.

Hold Shift over either hammer in your inventory for instructions. Zinc requires a matching Create pressing recipe that turns one zinc ingot into one zinc sheet or plate. If matching recipes disagree on the sheet, the hammer leaves the ingot untouched.

## Build and test

Use JDK 21. On Windows, run `gradlew.bat build` from the repository folder. On Linux or macOS, run `./gradlew build`. The JAR appears in `build/libs/`.

For a playtest, compare the two tiers on brass and gold. Hold right-click or swap hammers rapidly and check that sheets arrive no faster than once per second at 20 ticks per second. Try the side of a raised block, floor-level stone, raised dirt, and a full inventory. A rejected strike should leave the ingot and durability intact. Right-clicking an anvil with an empty offhand should still open the anvil menu.
