# Ifrit's Flame raw-metal blocks — 2026-10-29

Tracks https://github.com/EugeneHeisst/thaumcraft-port/issues/18. The focus is implemented in Thaumic Tinkerer, despite the issue being filed in the host repository.

The focus previously required an ordinary furnace recipe. Raw iron, gold and copper storage blocks have none in vanilla 1.21.1. Version 0.1.14 adds focus-only fallbacks to the corresponding refined storage blocks, keeping data-pack smelting recipes first. No global furnace recipes are added. Warmup, focus upgrades, vis charges, permissions, block-entity exclusion and protection events retain their existing behavior.

A native test client on a disposable world exercised the actual SmeltFocus.smelt method with a survival fake player and charged/empty wands. All three raw blocks convert on tick 21 after twenty warmup calls, costing 45 Ignis and 12 Perditio centivis once. Sand still becomes glass. Empty wands and block entities are unchanged; a cancelled BreakEvent consumes no vis and leaves the target intact. TC4Modern 0.17.190 + Tinkerer 0.1.13 reproduce all three rejected conversions; 0.17.191 + 0.1.14 pass. All 14 Forbidden Magic integration GameTests also pass with 0.1.14.

Audit source and full results: https://github.com/EugeneHeisst/thaumcraft-port/blob/main/docs/validation/issues-2026-10-29.md. Local logs: ../compat-test/issues-20261029. Release was built from an isolated clean worktree, preserving unrelated in-progress enchanter changes in the main working directory.
