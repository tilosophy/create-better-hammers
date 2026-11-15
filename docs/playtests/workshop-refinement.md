# Workshop refinement after the visual review

Preview-only revision of the approved workshop models; alpha.9 gameplay and version remain unchanged.

## Approved changes

- Pneumatic underside pipe grows from 0.28 to 0.50 model units across (about 79%), with larger seated fittings.
- Each side frame's rear rail and pivot mount is now a single copper mesh, with a shared outer plane and depth reaching into the wrench root. The steel pivot pin stays visible.
- Diamond receives an iron handle collar and central band around its head, leaving both striking faces exposed. The handle tenon and wedge remain visible above the band.
- Diamond uses a dedicated 16×16 gem texture with broad facets. Pneumatic's diamond face plate and all Stone/Iron assets remain unchanged.
- The approved handheld display transforms remain unchanged.

## Short review protocol

1. Compare Pneumatic before/after: pipe thickness, connected rear frame and pivot support.
2. Compare Diamond before/after: iron cuff wraps around the head and extends onto the handle; both gem ends remain exposed.
3. Check the held views and inventory lineup for readability and clipping.

This revision does not introduce new combat, crafting or air-consumption behavior. Earlier player confirmations remain recorded in `workshop-visual-review.md`.

## Verification

- Compiled with Java 21, Minecraft 1.21.1 and NeoForge 21.1.233.
- Started a local development server and rendered all five review captures in the Minecraft client with Create 6.0.10, without shaders.
- Inspected both comparison views, both held views, and the family lineup.
- Confirmed Stone/Iron models and pre-existing textures are unchanged, and all four handheld display transforms match the previous revision.
- Checked mesh indices, deterministic generation, and whitespace. No gameplay regression suite was rerun for this visual-only checkpoint.
- The tools/preview helpers are opt-in development sources; they must not be packaged in a release. Run a clean normal build for the next candidate.
