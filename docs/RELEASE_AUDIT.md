# Create: Better Hammers — Beta release audit

Date: 2026-11-17. Version: **0.1.0-beta.1**.

## Verified in this release pass

- Clean Gradle build succeeded with Java 21, Minecraft 1.21.1, NeoForge 21.1.233 and Create 6.0.10.
- All three dedicated-server GameTests passed: material progression/cooldowns, combat/enchantments, and pneumatic behavior.
- A subsequent normal clean build succeeded with test-source injection disabled; these are the delivered JARs.
- All nine compiled gameplay classes and all 37 gameplay resources match the approved alpha.11 runtime JAR byte for byte.
- All 13 packaged JSON files parse; Java class versions are 21.
- Correct beta metadata, MIT license, third-party notices and exact owner-supplied logo are present.
- No preview helpers, test classes/structures or bundled dependency JARs are packaged.
- Source JAR verified against current Java source; it is not an installable second mod.
- Supplied icon and banner preserved unchanged. The icon is in the runtime JAR; the banner is included separately for the listing.

## Player checks and limits

The owner approved alpha.11 swings, textures, left-handed holding, inventory display, sheet crafting and the pneumatic Z-fighting correction. Screenshots are complete and supplied separately by the owner. Real two-player testing is deferred at the owner's request. Automated tests do not replace that test. Other permitted dependency versions and arbitrary addon recipes are not universally certified.

## Upload checklist

1. Upload `create-better-hammers-0.1.0-beta.1.jar` as the main mod file, labelled **Beta**, **Minecraft 1.21.1**, **NeoForge**.
2. Use RELEASE_DESCRIPTION.md for the project description, PLAYER_GUIDE.md for instructions, and RELEASE_NOTES.md for the file changelog.
3. Declare Create as required; use the supplied approved icon, banner and your own screenshots.
4. Offer the sources JAR only as optional source code. Never install it in mods alongside the runtime JAR.
5. This review ZIP is for release preparation, not a mod or modpack to install.

## Source and release provenance

The beta source is prepared for the owner-authorized merge through GitHub pull request #1:
https://github.com/tilosophy/create-better-hammers/pull/1

The source changes from alpha.11 are limited to beta metadata, exact approved artwork, documentation and packaging. The delivered runtime SHA-256 is `7a86ccd14b89fc49bc9e3aba610cdc9b8361f3c55964b91943fed95998a423db`. A repository merge does not create a hosted GitHub Release or upload the mod to CurseForge.

See the downloadable review bundle's package-verification.json and SHA256SUMS.txt for full artifact checksums.
