# Model sources

The pneumatic geometry is original project artwork, inspired by the approved reference design. Its 16×16 material swatches were exported from an original image-generation atlas; no Create, Badlion or other mod texture is bundled.

- `build_pneumatic_model.py` rebuilds the OBJ and MTL. The approved display transforms remain in the item JSON and are not regenerated.
- `build_standard_models.py` constructs stone, iron and diamond heads, sockets and tapered octagonal handles from geometry. It no longer reads or extrudes the old sprites. The wooden tenon and wedge show how the handle is seated in the head. It takes the established display transforms from the pneumatic item JSON.
- `hammer_mesh.py` supplies the shared mesh, triangulation and UV helpers. Both generators require only Python's standard library. Run `python3 tools/assets/build_pneumatic_model.py` and `python3 tools/assets/build_standard_models.py` from the repository to rebuild the OBJ/MTL files and basic-tier item JSONs.
- `pneumatic-materials.png` is the 64×32 technical export of the generated atlas. In row order its eight 16×16 tiles are brass, steel, copper, wood, diamond, iron, brass trim and charcoal. Exporting tiles is a nearest-neighbour technical conversion, not a repaint. The iron swatch in the runtime assets retains the lighter alpha.8 artwork after the in-game contrast check; the new atlas's darker iron tile is unused.

Material generation used the built-in image tool. Prompt: Minecraft Create-style material atlas, four square tiles across and two down, no gutters or labels; 16-pixel material language, subdued aged brass, charcoal forged steel, muted copper, walnut grain, cyan diamond end plate, neutral steel, brass trim and dark recess. Defined edge seams/bevels, restrained wear, quiet centres; no rendered objects, gradients, random camouflage or embedded cogs/bolts.

## Workshop visual revision (preview, after alpha.9)

`workshop-materials.png` is the current 48×48 atlas. Its nine 16×16 tiles, in row order, are brass, copper, dark steel, handle wood, diamond, stone, light iron, wooden cog and charcoal. `pneumatic_brass_trim.png` uses the brass tile. The older atlas and ordinary sprite PNGs are retained as source history; the new models use the workshop materials.

The built-in image-generation tool produced the original atlas, a simplification pass and a restrained surface-variation pass. The final edit prompt specified: preserve the exact three-by-three material order and lightness; use a 16×16 logical pixel language with a few broad low-contrast tonal clusters, sparse scratches and corner grime, interrupted wood grain, clearly light silver iron, subdued brass/copper, and teal diamond. Aim for 60% quiet material colour, 30% subtle tonal clusters and 10% localized wear. Avoid fine noise, camouflage, strong diagonal metal streaks, rendered parts or borders. The final atlas is exported with nearest-neighbour scaling to 48×48, then cropped into 16×16 tiles with ImageMagick. No external mod texture is copied.

The pneumatic gear is wooden with a metal hub, recessed behind overlapping copper framing. Copper pivot mounts connect the frame visually to the wrench jaws; their steel pins remain distinct. The old cover strips and side pipe are removed. A single pipe runs underneath from the steel striker into the handle socket. The approved pneumatic display transforms and jaw geometry are preserved.

See `docs/playtests/workshop-visual-review.md` for the review scope and recorded player checks. This checkpoint changes visuals only; it is not a new release candidate.

## Follow-up refinement

The underside pipe is 0.50 units thick, with larger fittings. A continuous copper casting replaces each separate rear frame rail/pivot plate. Diamond has an iron handle collar and wraparound central cuff.

`diamond_hammer_gem.png` is a new original 16×16 texture used only by the Diamond Hammer. It was generated with the built-in image tool and exported by nearest-neighbour scaling. Prompt: one edge-to-edge cyan/teal gem swatch at a logical 16×16 resolution, four or five broad angular planes, restrained pale highlights, no iron-like mottling, noise, scratches, glow, frame or rendered tool. Other material tiles are unchanged. See `docs/playtests/workshop-refinement.md` for the visual checks.

## Workshop wear polish (preview after alpha.10)

`workshop-worn-materials.png` is the current 64x48 export of an original material atlas produced with the built-in image tool, using the preceding workshop atlas as the palette reference. No external texture is copied. Its four-by-three tile layout is brass/copper/steel/rusted fastener; handle wood/diamond/stone/iron; cog wood/charcoal/end grain/wedge. `export_workshop_worn.py` uses ImageMagick nearest-neighbour crops to reproduce all runtime swatches. The handle swatch is 8x16 and fastener/endgrain/wedge swatches are 4x4; other materials remain 16x16.

Generation prompt: edit the supplied palette reference into a flat four-by-three production Minecraft material atlas, no gutters, labels, objects or perspective. Crisp 16x16 logical pixels per tile. Substantially used, scuffed and grimy surfaces: dark corner grime, broken small edge highlights and sparse short chips. Avoid fine noise, uniform speckles, dramatic bevels and large camouflage blobs. Aged ochre brass; oxidized red copper; scuffed dark forged steel; predominantly grey-brown rusted iron with a small worn grey centre and no clean silver. Coarse walnut with about four interrupted vertical bands and handling stains; cyan/teal diamond with around eight connected angular facets and gentle transitions, nicks and corner dirt; chipped grey stone with pits and earth-stained crevices; worked grey iron with dark scuffs and restrained corner rust. Greasy warm-brown wooden cog; quiet charcoal recess; simple three-colour end grain with one broad angular ring; simple three-colour wedge with a single lighter band and no fine grain. Wear must survive export to the runtime pixel sizes.

Tiny endgrain/wedge/fastener parts have dedicated material mappings in `hammer_mesh.py`. Wedge face mapping preserves physical aspect ratio instead of compressing a whole grain tile onto a narrow strip. Copper/brass overlap is fixed by recessing the housing roof/floor/rear wall by 0.05 units and reseating the top rivets; the copper frame silhouette and holding poses are unchanged. See `docs/playtests/workshop-wear-review.md`.
