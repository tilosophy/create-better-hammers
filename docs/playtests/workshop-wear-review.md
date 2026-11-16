# Workshop wear and texture mapping — preview after alpha.10

Approved scope: stronger material-specific wear on all hammers, quieter diamond facets,
coarser handles, simple handle ends/wedges, and removal of copper/brass Z-fighting.
This checkpoint keeps alpha.10 gameplay and version metadata; no new test JAR is supplied.

## Changes

- New original material atlas: chipped stone, scratched/pitted iron, tarnished brass/copper,
  worn dark steel, quieter connected diamond facets and clustered grime.
- Handles use an 8x16 swatch rather than 16x16: half the horizontal grain resolution,
  broad interrupted bands and worn patches.
- Fasteners, hubs and pivot pins use a dedicated dull rusty-metal swatch rather than bright iron.
- Wooden handle ends and wedges use dedicated 4x4 swatches. Mapping keeps the same scale
  along each face's two directions, so a thin wedge face samples a strip of artwork.
- Brass housing roof, floor and rear wall are recessed 0.05 model units from their previous
  planes; copper geometry remains fixed. Top rivet bases follow the lowered roof.
- All basic hammer geometry, all handheld transforms, crafting and combat remain unchanged.

## Verification

- Model generators reproduce the checked-in assets; face and UV index checks pass.
- Neither copper frame has a face coplanar with the brass housing, checked on the generated OBJ.
- Basic model vertex positions and every item JSON match alpha.10.
- Preview compilation, local development world setup and Minecraft client capture pass on
  Minecraft 1.21.1, NeoForge 21.1.233 and Create 6.0.10.
- Reviewed before/after models, handle tops, first-person views and both sides/top of Pneumatic.
- Captures use vanilla lighting without shaders. The reported shader flicker still needs
  confirmation in the player's shader setup when the next candidate is approved.
- No gameplay tests rerun: production Java and data resources are unchanged.

## Short review protocol

1. Compare wear and material identity at normal viewing size, especially Iron and Diamond.
2. Check handle grain and the simplified wooden ends/wedges across the basic tiers.
3. Check Pneumatic's rivets and copper/brass join from the near, upper and far views.

After approving a candidate: look around slowly while holding Pneumatic with the usual shaders,
then briefly without shaders, to check the previously flickering junction in motion.
