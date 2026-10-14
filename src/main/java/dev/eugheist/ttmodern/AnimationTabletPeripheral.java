package dev.eugheist.ttmodern;

/** Build164 tablet API. Only loaded by the optional ComputerCraft integration. */
public final class AnimationTabletPeripheral implements dan200.computercraft.api.peripheral.IPeripheral {
 private final AnimationTabletEntity tile;
 AnimationTabletPeripheral(AnimationTabletEntity tile){this.tile=tile;}
 private boolean live(){var level=tile.getLevel();return level!=null&&!tile.isRemoved()&&level.getBlockEntity(tile.getBlockPos())==tile;}
 public String getType(){return "tt_animationTablet";}
 public Object getTarget(){return tile;}
 public boolean equals(dan200.computercraft.api.peripheral.IPeripheral other){return other instanceof AnimationTabletPeripheral p&&p.tile==tile;}
 @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final boolean getRedstone(){return tile.redstone;}
 @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final void setRedstone(boolean value){if(live()){tile.redstone=value;tile.changed();}}
 @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final boolean getLeftClick(){return tile.leftClick;}
 @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final void setLeftClick(boolean value){if(live()){tile.leftClick=value;tile.changed();}}
 @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final int getRotation(){return tile.facing().get3DDataValue()-2;}
 @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final void setRotation(double value)throws dan200.computercraft.api.lua.LuaException{
  int rotation=(int)value;
  // Legacy negative metadata could corrupt the block; modern horizontal states cannot represent it.
  if(!Double.isFinite(value)||rotation<0||rotation>3)throw new dan200.computercraft.api.lua.LuaException("Invalid value: "+rotation+".");
  if(live())tile.getLevel().setBlock(tile.getBlockPos(),tile.getBlockState().setValue(AnimationTabletBlock.FACING,net.minecraft.core.Direction.from3DDataValue(rotation+2)),3);
 }
 @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final boolean hasItem(){return !tile.inventory.getStackInSlot(0).isEmpty();}
 @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final boolean trigger(){return live()&&tile.trigger();}
}
