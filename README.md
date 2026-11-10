# Create: Better Hammers

Adds a new tool to Create for making metal sheets without a stationary Mechanical Press.

## Stone Hammer prototype

Install the same mod JAR on the Minecraft 1.21.1 client and dedicated server alongside NeoForge 21.1.233 or newer in the 21.1 series and Create 6.0.10.

1. Craft a Stone Hammer using six cobblestone across the top two rows of a crafting table and a stick in the bottom-middle slot.
2. Hold the hammer in your main hand and iron ingots in your offhand.
3. Stand beside a raised stone, smooth stone, or anvil block and right-click its top. On a level floor, the work block sits on top of the floor.
4. Each successful strike consumes one ingot, puts one Create iron sheet in your inventory, and uses one durability in Survival. A fresh hammer has 64 durability.

Hold Shift over the hammer in your inventory for instructions. This prototype covers the sheet-making interaction with iron.

## Build and test

Use JDK 21. On Windows, run `gradlew.bat build` from the repository folder. On Linux or macOS, run `./gradlew build`. The JAR appears in `build/libs/`.

For a playtest, try all three supported surfaces, then try the side of a raised block, floor-level stone, and raised dirt. Check that rejected strikes leave the ingot and durability intact. Try a full inventory with and without a partial stack of iron sheets. Right-clicking an anvil with an empty offhand should still open the anvil menu.
