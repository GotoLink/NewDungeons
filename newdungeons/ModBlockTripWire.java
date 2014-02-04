package newdungeons;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class ModBlockTripWire extends BlockTripWire {
	protected ModBlockTripWire() {
		super();
	}

	@Override
	public void func_149670_a(World var1, int var2, int var3, int var4, Entity var5) {
		if (var5 instanceof EntityPlayer) {
			super.func_149670_a(var1, var2, var3, var4, var5);
		}
	}

	@Override
	public void func_150138_a(World par1World, int par2, int par3, int par4, int par5) {
		int i1 = 0;
		while (i1 < 2) {
			int j1 = 1;
			while (true) {
				if (j1 < 42) {
					int k1 = par2 + Direction.offsetX[i1] * j1;
					int l1 = par4 + Direction.offsetZ[i1] * j1;
					Block i2 = par1World.func_147439_a(k1, par3, l1);
					if (i2 == Blocks.tripwire_hook || i2 == NewDungeons.modTripWireSource) {
						int j2 = par1World.getBlockMetadata(k1, par3, l1) & 3;
						if (j2 == Direction.rotateOpposite[i1]) {
							((BlockTripWireHook) i2).func_150136_a(par1World, k1, par3, l1, false, par1World.getBlockMetadata(k1, par3, l1), true, j1, par5);
						}
					} else if (i2 == Blocks.tripwire || i2 == this) {
						++j1;
						continue;
					}
				}
				++i1;
				break;
			}
		}
	}
}
