# Infused fire: build 164 parity notes

Work in progress. All six placeable fires, fixed/dynamic transformations, scheduling,
configuration, research and recipes are implemented. Native server and client
model and actual client-world behavior audits pass. Remaining visual parity
review is noted below.

Source: exact ThaumicTinkerer_2.5_1.7.10_164.jar, decompiled BlockFireBase,
BlockFireAir/Water/Ignis/Earth/Order/Chaos, ItemBlockFire and BlockTuple.
SRG block fields decoded using the local MCP stable fields.csv.

The base's Block overload constructs BlockTuple(block) with metadata zero. Thus
legacy input metadata families share transformations, while output metadata is
zero. Modern separate block IDs need explicit expansion: six old woods, nine old
red flowers, both sands, seven old stone variants and three dirt variants. Do not
add post-1.7 wood/ore families without an explicit compatibility decision.
Legacy tallgrass metadata zero becomes DEAD_BUSH. Legacy snow becomes SNOW_BLOCK;
snow_layer is the modern SNOW. Gold BLOCK, not gold ore, becomes quartz ore.
Dandelion's self-transformation is intentional in the source table. Only still
water is a target; both flowing/still lava are targets. Cake configuration gates
only Aer water transformation, not the entire Aer table.

Original fire schedule: 200 + random(3), initially 200 + random(10), Perditio
base delay 1 instead of 200. Tick processing gated by doFireTick and enableFire.
Rain or lack of adjacent transformation targets removes fire; original code
continues processing after removal. There is a 1/20 direct six-neighbor conversion
attempt, followed by six-neighbor catch-fire and a 3 x 4 x 3 air spread scan.
Ordo decay depends on number of drop entries: 2 -> divisor 2, >=3 -> 1, else 3.
Decay failure in the direct-neighbor branch removes the input. Ordo initially
excludes stone, glass, sand and gravel; otherwise tries nine identical drops in
crafting, then nine smelted drops. Results are cached. Cache invalidation on modern
recipe reload must be addressed. Perditio transforms the other five fires into
ordinary fire, rather than converting arbitrary terrain.

Coordinate defect requiring an explicit safe adaptation and world regression:
setBlockWithTransmutationTarget takes x,y,z but accesses x,z,y; the area-scan caller
passes x,z,y deliberately, while six-neighbor callers pass x,y,z. Context-sensitive
Ordo lookup additionally receives inconsistent coordinates. A modern BlockPos
implementation must use the actual target consistently and must not mutate an
unrelated swapped Y/Z location. Keep a decoy block at that location in tests.

Additional legacy issue: Ordo checks metadata-specific cache keys internally,
while the base's block-only lookup requests metadata zero. Avoid null lookup/crash
when migrating state-based recipes; document modern adaptation and test both
state variants and empty/non-block crafting output.

Next: dynamic world recipe resolver; bounded loaded-chunk BlockPos propagation;
original recipe/research/config/assets; native rain/gamerule/decay/coordinate/
reload/TNT tests and actual client fire rendering checks, all in isolated worlds.

## Ordo resolver implemented

`OrderFireResolver` resolves actual block loot using the native recipe manager:
nine identical drop items, then nine furnace results if direct crafting has no
recipe. Counts and custom names do not affect legacy item equality. Empty,
differing-item and non-block results cannot become targets. The original excluded
stone/glass/sand/gravel families stay excluded. No inventory or world changes are
made by lookup. Loaded-chunk checks precede lookup. Results (including misses) are
cached per server level and full block state, with weak level keys. A server
resource reload listener clears the cache on every datapack reload.

Vanilla iron/gold ore raw-metal drops are adapted back to their original ore-block
inputs before crafting/smelting, preserving the original iron/gold-block results.
This adaptation applies to those two exact blocks; other/modded loot uses its
actual modern recipes. State-aware lookup replaces the old metadata-zero/null
lookup failure rather than reproducing a crash.

Native isolated-server audit covers seven ore types, real loot and furnace/nine
craft recipes, original exclusions, differing/empty/same-item drop entries, the
entry-count decay formula, names ignored, and a real datapack reload: a cached
miss for obsidian becomes diamond block after adding a temporary nine-obsidian
recipe, then becomes a miss again after removing the pack. The temporary pack is
removed and original pack selection restored. Full earlier content regressions
also pass. Logs: `/tmp/tt-ordo-final-server.log`, build `/tmp/tt-ordo-build.log`.

## Six world blocks implemented

`InfusedFireBlock` extends native FireBlock for fire collision/damage, outline,
sound and smoke. The build-164 scheduled algorithm supplies the six-neighbor
conversion/catch and 3 x 4 x 3 air spread scan. Every world operation now uses
one BlockPos target; no swapped Y/Z writes. Only loaded chunks and valid build
height are queried or modified. Rain/biome humidity, age 0..15, normal/entropy
delays, doFireTick and enableFire gating are included. Direct Ordo decay failure
removes its target as in build 164. Normal flammability remains active for other
blocks; TNT is primed once using the native caught-fire hook. Transformation
results resist the same fire. Entropy transforms the other five fires to vanilla
fire. Disabled ticks are not rescheduled, matching the original algorithm.

Placement is allowed freely, matching the original canPlaceBlockAt override.
Neighbor updates remove unsupported fire; native client updates rely on server
support checks. Initial/successor scheduled ticks use the original delay ranges.
The old code continues catch/spread after removal of its origin; that behavior
is retained. Animation/render geometry adapts to modern multipart fire models,
using the original two animated textures per element and original item icons.

All six BRIGHT_NITOR research children retain coordinates, secondary/concealed
flags, complexity, aspects, shard catalysts and crucible costs. Common config
adds enableFire/enableCake (both default true); disableFire also excludes addon
fire recipes from the merged crucible map. Existing blocks/items stay registered
regardless of the config, as in the original.

Portal check: original BlockPortal$Size (MCP amq) bytecode a(aji) accepts AIR
material, exact vanilla FIRE, or PORTAL. An infused fire is none of these, so its
original attempted portal creation cannot form a portal. The modern custom fire
does not invoke vanilla onPlace/portal logic or inherit its fast tick schedule.

Native world audit `/tmp/tt-fire-world-final-server.log` covers five actual tick
conversions; entropy -> native fire; original delays; zero drops/collision;
placement schedule; gamerule/config freezing; swapped-axis decoy conservation;
air spread; no-target extinguish; actual rain; Ordo decay failure; exactly one
primed TNT (discarded in test cleanup); no forced chunk loading; research parents,
crucible entries and disabled recipe filtering. Full earlier regressions pass.
`/tmp/tt-fire-model-client.log` checks all item models/textures and all block state
models/quads (including all six fire age/attachment combinations), hidden/muted.
Build `/tmp/tt-fire-world-build.log` passes. No production deployment.

Modern flame geometry uses the native 1.4-block-high multipart adaptation; the
original colored animated sprites are preserved. Side-wall smoke is verified below.

## Actual integrated-world regression and contact fix

`InfusedFireClientAudit` extends the isolated real-client test to six displayed
fires, native entity movement through each fire, a seventh vanilla fire control,
and natural server scheduled ticks. The gallery screenshot is
`run-enchanter-audit/enchanter-audit/infused-fire-gallery.png`, visually inspected:
yellow/blue/red/green/pale purple/dark purple original sprites render distinctly.
Native collision fixtures deliberately call Entity.move each server tick on
NoAI cows; stationary NoAI entities were not exercising the movement collision
path. Contact runs for 45 ticks; all seven cows lose 5 HP and have 160 remaining
fire ticks, then are discarded. No direct entityInside call substitutes for the
native collision path.

The test uncovered a real migration omission: custom fires were absent from
minecraft:fire. Entity.move checks that tag before retaining ignition buildup.
Added all six blocks with replace=false, preserving vanilla/mod entries, and a
native tag-membership regression. Natural cadence fixtures are separated so
Perditio cannot spread into the ordinary-fire fixture: ordinary fire stays until
at least tick 200 and first extinguishes at elapsed 203 (allowed 200..209).
Entropy extinguishes naturally by elapsed 11 (initial 1..10). No manual tick call
is used in the integrated cadence test. Logs:
`/tmp/tt-fire-contact-fixed-client.log` (BUILD SUCCESSFUL, all earlier GUI/gear/
platform checks included), `/tmp/tt-fire-contact-fixed-server.log` (full PASS),
`/tmp/tt-fire-contact-fix-build.log` (PASS).

`python3 tools/audit-server.py --fire-disabled` launches only the isolated server
with enableFire=false and restores its previous config afterwards. Native startup
checks prove all six fire research/recipes absent, block/item registrations and
BRIGHT_NITOR retained, and world fire ticks frozen. Log
`/tmp/tt-fire-disabled-server.log` PASS; test config restored byte-for-byte. The
normal server audit mode remains available and passed after that config run.

## Side-wall smoke correction

Inherited modern BaseFireBlock.animateTick called canBurn on the custom
FireBlock instance, whose igniteOdds map is empty (vanilla bootstrap populates
only Blocks.FIRE). Build 164 explicitly asks vanilla fire's contextual
canCatchFire instead. InfusedFireSmoke restores those contextual checks: three
particles over sturdy or flammable support, otherwise two per flammable west,
east, north, south and upper neighbor, within the original 0.1-block face band.
A nonflammable elemental transformation target does not create smoke merely by
being transformable. Ambient sound keeps the original 1/24 chance and pitch.

InfusedFireSmokeAudit checks all five wall orientations, face bounds and both
solid/flammable support using actual world blocks. Client regression invokes
actual animateTick against a temporary client-world wood wall and identifies two
new native LargeSmokeParticle instances through ParticleEngine.iterateParticles;
the prior wall state is restored. Server `/tmp/tt-fire-smoke-server.log` full PASS;
actual integrated client `/tmp/tt-fire-smoke-client.log` BUILD SUCCESSFUL (all
previous GUI/gear/platform/contact/cadence checks retained; ordinary tick observed
205). Build `/tmp/tt-fire-smoke-build.log` PASS. All tests hidden/muted, no live
client or production changes.
