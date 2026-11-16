# Release readiness after alpha.11 packaging

This is a remaining-work review, not authorization to publish or change the approved feature set.

## Already established

- Four tiers, material progression, raised work surfaces and shared sheet cooldown are implemented.
- Sturdy Sheet completion text, Create block rotation/pickup and powered/unpowered behavior have player confirmations.
- Automated dedicated-server tests cover progression, recipes, cooldown/resource accounting, combat, shield disabling, supported enchantments, pneumatic air use, two-strike crafting and full-inventory rejection.
- Original model/material source and generation notes are checked into the repository. MIT license is present.
- Approved model proportions and holding transforms are preserved; alpha.11 packages the wear polish.

## Still to confirm before public release

1. Player approval of alpha.11 in the actual modpack/shader setup, including the previously flickering copper/brass seam and alternate item views.
2. One short real multiplayer session on a dedicated server with two players: simultaneous crafting, visible swings/results, wrench interactions, tank depletion/removal and reconnecting after a partial Sturdy Sheet craft. The existing automated server tests do not substitute for this client/server check.
3. Final release audit: check the exact supported Create/NeoForge versions, current dependency metadata, any notices needed for reused APIs/assets, and the packaged runtime JAR. Keep compatibility claims limited to versions actually verified, especially the Create-specific wrench injections.
4. Release preparation: agree on the public version, finalize the description/player guide and screenshots, choose an icon/banner if wanted, and prepare the hosting page. The repository currently has no dedicated mod icon/banner or release-page draft. README and playtest notes can supply most of the guide content.
5. User approval to merge, tag and publish the verified release artifact. No public release is created by this playtest build.

No further gameplay feature is proposed as a release prerequisite. Extra tiers, integrations and cosmetic animation can remain future work.
