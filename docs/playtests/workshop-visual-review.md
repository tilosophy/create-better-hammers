# Workshop visual review after alpha.9

This is a visual review checkpoint, not a new distributable candidate. Gameplay and the version identifier remain alpha.9.

## Confirmed by the player

- Swinging works.
- Pneumatic reports 11 attack damage without air and 12 with air; Diamond receives no backtank bonus.
- Removing the backtank restores unpowered behaviour.
- Create block pickup, rotation-menu access and actual rotation work.
- Completing the second Sturdy Sheet strike displays “Sturdy Sheet crafted.”

## Approved visual scope

- Replace the extruded ordinary sprites with constructed heads, sockets and tapered wooden handles. Stone is roughly dressed; Iron is forged; Diamond has more defined striking faces. Keep the workshop appearance simple.
- Pneumatic keeps its approved pose and wrench-jaw shape.
- Use a wooden cog with a steel hub, recessed behind overlapping casing edges. Remove the independent dark cover strip.
- Move the copper pipe beneath the head to connect the striker and handle socket.
- Use aged copper for the side frame and wrench pivot mounting; retain brass on the main housing and jaws.
- Coarser material grain and grime around seams, with restrained wear on exposed surfaces.

## Review protocol

1. Judge the four-tier lineup: head construction, handle proportions and material distinction.
2. Check first-person views: approved Pneumatic orientation and comparable positioning for the basic tiers.
3. Inspect Pneumatic side/angled views: wood cog partly concealed by casing, steel hub, copper pivot mounting and connected underside pipe.
4. Compare material wear at normal viewing scale, rather than only enlarged inventory renders.

Actual Minecraft development-client captures use 1.21.1 / NeoForge 21.1.233 / Create 6.0.10 without shaders. Final appearance with the player's shaders remains a subsequent in-game test.

## Local verification

- All four meshes have valid face and UV indices.
- Both source generators reproduce the checked-in assets exactly.
- Pneumatic display transforms are unchanged from the approved alpha.9 pose.
- Gameplay code and version metadata are unchanged in this checkpoint.
