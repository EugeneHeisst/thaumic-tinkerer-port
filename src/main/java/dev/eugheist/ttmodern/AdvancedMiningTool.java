package dev.eugheist.ttmodern;
/** Server harvest hook used by the original advanced KAMI tools. */
public interface AdvancedMiningTool extends AdvancedTool {
 default String toolType(){return "";}
 void startMining(net.minecraft.server.level.ServerPlayer player,net.minecraft.core.BlockPos pos);
}
