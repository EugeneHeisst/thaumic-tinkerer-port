package dev.eugheist.ttmodern;
/** Build164 base magnet commands, also inherited by the mob variant. */
public class MagnetPeripheral implements dan200.computercraft.api.peripheral.IPeripheral {
 protected final MagnetEntity tile;
 MagnetPeripheral(MagnetEntity tile){this.tile=tile;}
 protected final boolean live(){var level=tile.getLevel();return level!=null&&!tile.isRemoved()&&level.getBlockEntity(tile.getBlockPos())==tile;}
 public String getType(){return "tt_magnet";}
 public Object getTarget(){return tile;}
 public boolean equals(dan200.computercraft.api.peripheral.IPeripheral other){return other instanceof MagnetPeripheral p&&p.tile==tile;}
 @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final boolean isPulling(){return tile.getBlockState().getValue(MagnetBlock.PULLING);}
 @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final void setPulling(boolean pulling){var level=tile.getLevel();if(live())level.setBlock(tile.getBlockPos(),tile.getBlockState().setValue(MagnetBlock.PULLING,pulling),3);}
 @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final int getSignal(){return tile.getLevel()==null?0:tile.getLevel().getBestNeighborSignal(tile.getBlockPos());}
}
