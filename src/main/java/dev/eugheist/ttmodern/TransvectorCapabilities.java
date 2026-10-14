package dev.eugheist.ttmodern;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.energy.IEnergyStorage;

/** Resolve on every operation: cached automation cannot retain a detached target inventory. */
final class TransvectorCapabilities {
    static IItemHandler items(TransvectorEntity tile,Direction side){return new IItemHandler(){
        private IItemHandler target(){return tile.forward(Capabilities.ItemHandler.BLOCK,side);}
        public int getSlots(){var t=target();return t==null?0:t.getSlots();}
        public ItemStack getStackInSlot(int slot){var t=target();return valid(t,slot)?t.getStackInSlot(slot):ItemStack.EMPTY;}
        public ItemStack insertItem(int slot,ItemStack stack,boolean simulate){var t=target();return valid(t,slot)?t.insertItem(slot,stack,simulate):stack;}
        public ItemStack extractItem(int slot,int amount,boolean simulate){var t=target();return valid(t,slot)?t.extractItem(slot,amount,simulate):ItemStack.EMPTY;}
        public int getSlotLimit(int slot){var t=target();return valid(t,slot)?t.getSlotLimit(slot):0;}
        public boolean isItemValid(int slot,ItemStack stack){var t=target();return valid(t,slot)&&t.isItemValid(slot,stack);}
        private boolean valid(IItemHandler t,int slot){return t!=null&&slot>=0&&slot<t.getSlots();}
    };}
    static IFluidHandler fluids(TransvectorEntity tile,Direction side){return new IFluidHandler(){
        private IFluidHandler target(){return tile.forward(Capabilities.FluidHandler.BLOCK,side);}
        public int getTanks(){var t=target();return t==null?0:t.getTanks();}
        public FluidStack getFluidInTank(int tank){var t=target();return valid(t,tank)?t.getFluidInTank(tank):FluidStack.EMPTY;}
        public int getTankCapacity(int tank){var t=target();return valid(t,tank)?t.getTankCapacity(tank):0;}
        public boolean isFluidValid(int tank,FluidStack stack){var t=target();return valid(t,tank)&&t.isFluidValid(tank,stack);}
        public int fill(FluidStack stack,FluidAction action){var t=target();return t==null?0:t.fill(stack,action);}
        public FluidStack drain(FluidStack stack,FluidAction action){var t=target();return t==null?FluidStack.EMPTY:t.drain(stack,action);}
        public FluidStack drain(int amount,FluidAction action){var t=target();return t==null?FluidStack.EMPTY:t.drain(amount,action);}
        private boolean valid(IFluidHandler t,int tank){return t!=null&&tank>=0&&tank<t.getTanks();}
    };}
    static IEnergyStorage energy(TransvectorEntity tile,Direction side){return new IEnergyStorage(){
        private IEnergyStorage target(){return tile.forward(Capabilities.EnergyStorage.BLOCK,side);}
        public int receiveEnergy(int amount,boolean simulate){var t=target();return t==null?0:t.receiveEnergy(amount,simulate);}
        public int extractEnergy(int amount,boolean simulate){var t=target();return t==null?0:t.extractEnergy(amount,simulate);}
        public int getEnergyStored(){var t=target();return t==null?0:t.getEnergyStored();}
        public int getMaxEnergyStored(){var t=target();return t==null?0:t.getMaxEnergyStored();}
        public boolean canExtract(){var t=target();return t!=null&&t.canExtract();}
        public boolean canReceive(){var t=target();return t!=null&&t.canReceive();}
    };}
}
