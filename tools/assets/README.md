# Model sources

The pneumatic geometry is original project artwork, inspired by the approved reference design. Its 16×16 material swatches were exported from an original image-generation atlas; no Create, Badlion or other mod texture is bundled.

- `build_pneumatic_model.py` rebuilds the OBJ and MTL. The approved display transforms remain in the item JSON and are not regenerated.
- `build_standard_models.py` reads the existing three 16×16 sprites and gives their heads 3.5 model units of depth and their handles 1.5. It requires Pillow and the Minecraft resource artifact produced by Gradle.
- `pneumatic-materials.png` is the 64×32 technical export of the generated atlas. In row order its eight 16×16 tiles are brass, steel, copper, wood, diamond, iron, brass trim and charcoal. Exporting tiles is a nearest-neighbour technical conversion, not a repaint. The iron swatch in the runtime assets retains the lighter alpha.8 artwork after the in-game contrast check; the new atlas's darker iron tile is unused.

Material generation used the built-in image tool. Prompt: Minecraft Create-style material atlas, four square tiles across and two down, no gutters or labels; 16-pixel material language, subdued aged brass, charcoal forged steel, muted copper, walnut grain, cyan diamond end plate, neutral steel, brass trim and dark recess. Defined edge seams/bevels, restrained wear, quiet centres; no rendered objects, gradients, random camouflage or embedded cogs/bolts.

The ordinary hammer texture PNGs remain unchanged. Their side-facing silhouette is preserved exactly by the generated geometry.
