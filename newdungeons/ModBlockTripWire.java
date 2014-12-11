package newdungeons;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class ModBlockTripWire extends BlockTripWire {
    public static int renderID;
	public ModBlockTripWire() {
		super();
	}

	@Override
	public void onEntityCollidedWithBlock(World var1, int var2, int var3, int var4, Entity var5) {
		if (var5 instanceof EntityPlayer) {
			super.onEntityCollidedWithBlock(var1, var2, var3, var4, var5);
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
					Block i2 = par1World.getBlock(k1, par3, l1);
					if (i2 == Blocks.tripwire_hook || i2 == NewDungeons.modTripWireSource) {
						int j2 = par1World.getBlockMetadata(k1, par3, l1) & 3;
						if (j2 == Direction.rotateOpposite[i1]) {
							((BlockTripWireHook) i2).func_150136_a(par1World, k1, par3, l1, false, par1World.getBlockMetadata(k1, par3, l1), true, j1, par5);
						}
					} else if (i2 == Blocks.tripwire || i2 == NewDungeons.modTripWire) {
						++j1;
						continue;
					}
				}
				++i1;
				break;
			}
		}
	}

    public static boolean getRenderDir(IBlockAccess world, int x, int y, int z, int l, int i) {
        if(func_150139_a(world, x, y, z, l, i)){
            return true;
        }else{
            int j1 = x + Direction.offsetX[i];
            int k1 = z + Direction.offsetZ[i];
            Block block = world.getBlock(j1, y, k1);
            int l1;

            if (block == NewDungeons.modTripWireSource)
            {
                l1 = world.getBlockMetadata(j1, y, k1);
                int i2 = l1 & 3;
                return i2 == Direction.rotateOpposite[i];
            }
            else if (block == NewDungeons.modTripWire)
            {
                l1 = world.getBlockMetadata(j1, y, k1);
                boolean flag = (l & 2) == 2;
                boolean flag1 = (l1 & 2) == 2;
                return flag == flag1;
            }
        }
        return false;
    }

    @Override
    public int getRenderType(){
        return renderID;
    }
}
