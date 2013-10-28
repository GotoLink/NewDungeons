package newdungeons;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockTripWireSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class ModBlockTripWire extends BlockTripWire {
	protected ModBlockTripWire(int var1) {
		super(var1);
	}

	@Override
	public void onEntityCollidedWithBlock(World var1, int var2, int var3, int var4, Entity var5) {
		if (var5 instanceof EntityPlayer) {
			super.onEntityCollidedWithBlock(var1, var2, var3, var4, var5);
		}
	}

	@Override
	protected void func_72149_e(World par1World, int par2, int par3, int par4, int par5) {
		int i1 = 0;
		while (i1 < 2) {
			int j1 = 1;
			while (true) {
				if (j1 < 42) {
					int k1 = par2 + Direction.offsetX[i1] * j1;
					int l1 = par4 + Direction.offsetZ[i1] * j1;
					int i2 = par1World.getBlockId(k1, par3, l1);
					if (i2 == Block.tripWireSource.blockID || i2 == NewDungeons.modTripWireSource.blockID) {
						int j2 = par1World.getBlockMetadata(k1, par3, l1) & 3;
						if (j2 == Direction.rotateOpposite[i1]) {
							((BlockTripWireSource) Block.blocksList[i2]).func_72143_a(par1World, k1, par3, l1, i2, par1World.getBlockMetadata(k1, par3, l1), true, j1, par5);
						}
					} else if (i2 == Block.tripWire.blockID || i2 == this.blockID) {
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
