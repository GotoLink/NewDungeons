package newdungeons;

import static net.minecraftforge.common.ForgeDirection.UP;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTripWireSource;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class ModBlockTripWireSource extends BlockTripWireSource {
	protected ModBlockTripWireSource(int var1) {
		super(var1);
	}

	@Override
	public void func_72143_a(World par1World, int par2, int par3, int par4, int par5, int par6, boolean par7, int par8, int par9) {
		int l1 = par6 & 3;
		boolean flag1 = (par6 & 4) == 4;
		boolean flag2 = (par6 & 8) == 8;
		boolean flag3 = par5 == Block.tripWireSource.blockID || par5 == this.blockID;
		boolean flag4 = false;
		boolean flag5 = !par1World.isBlockSolidOnSide(par2, par3 - 1, par4, UP);
		int i2 = Direction.offsetX[l1];
		int j2 = Direction.offsetZ[l1];
		int k2 = 0;
		int[] aint = new int[42];
		int l2;
		int i3;
		int j3;
		int k3;
		int l3;
		for (i3 = 1; i3 < 42; ++i3) {
			l2 = par2 + i2 * i3;
			k3 = par4 + j2 * i3;
			j3 = par1World.getBlockId(l2, par3, k3);
			if (j3 == Block.tripWireSource.blockID || j3 == this.blockID) {
				l3 = par1World.getBlockMetadata(l2, par3, k3);
				if ((l3 & 3) == Direction.rotateOpposite[l1]) {
					k2 = i3;
				}
				break;
			}
			if (j3 != Block.tripWire.blockID && j3 != NewDungeons.modTripWire.blockID && i3 != par8) {
				aint[i3] = -1;
				flag3 = false;
			} else {
				l3 = i3 == par8 ? par9 : par1World.getBlockMetadata(l2, par3, k3);
				boolean flag6 = (l3 & 8) != 8;
				boolean flag7 = (l3 & 1) == 1;
				boolean flag8 = (l3 & 2) == 2;
				flag3 &= flag8 == flag5;
				flag4 |= flag6 && flag7;
				aint[i3] = l3;
				if (i3 == par8) {
					par1World.scheduleBlockUpdate(par2, par3, par4, par5, this.tickRate(par1World));
					flag3 &= flag6;
				}
			}
		}
		flag3 &= k2 > 1;
		flag4 &= flag3;
		i3 = (flag3 ? 4 : 0) | (flag4 ? 8 : 0);
		par6 = l1 | i3;
		if (k2 > 0) {
			l2 = par2 + i2 * k2;
			k3 = par4 + j2 * k2;
			j3 = Direction.rotateOpposite[l1];
			par1World.setBlockMetadataWithNotify(l2, par3, k3, j3 | i3, 3);
			this.notifyNeighborOfChange(par1World, l2, par3, k3, j3);
			this.playSoundEffect(par1World, l2, par3, k3, flag3, flag4, flag1, flag2);
		}
		this.playSoundEffect(par1World, par2, par3, par4, flag3, flag4, flag1, flag2);
		if (par5 > 0) {
			par1World.setBlockMetadataWithNotify(par2, par3, par4, par6, 3);
			if (par7) {
				this.notifyNeighborOfChange(par1World, par2, par3, par4, l1);
			}
		}
		if (flag1 != flag3) {
			for (l2 = 1; l2 < k2; ++l2) {
				k3 = par2 + i2 * l2;
				j3 = par4 + j2 * l2;
				l3 = aint[l2];
				if (l3 >= 0) {
					if (flag3) {
						l3 |= 4;
					} else {
						l3 &= -5;
					}
					par1World.setBlockMetadataWithNotify(k3, par3, j3, l3, 3);
				}
			}
		}
	}
}
