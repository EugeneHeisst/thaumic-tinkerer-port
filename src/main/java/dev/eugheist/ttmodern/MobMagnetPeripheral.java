package dev.eugheist.ttmodern;
/** TileMobMagnet adds these two methods without changing tt_magnet identity. */
public final class MobMagnetPeripheral extends MagnetPeripheral {
 MobMagnetPeripheral(MagnetEntity tile){super(tile);}
 @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final boolean getAdultSearch(){return tile.adult;}
 @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final void setAdultSearch(boolean adult){if(live()){tile.adult=adult;tile.changed();}}
}
