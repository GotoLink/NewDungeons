package newdungeons;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.world.World;

public class ModBlockTorch extends BlockTorch {
	protected ModBlockTorch() {
		super();
	}

	@Override
	public boolean func_149727_a(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
		onInteract(world, x, y, z, player);
		return false;
	}

	@Override
	public void func_149699_a(World world, int x, int y, int z, EntityPlayer player) {
		onInteract(world, x, y, z, player);
	}

	@Override
	public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
		if (var5 == Blocks.fire) {
			this.burn(var1, var2, var3, var4);
		}
		super.func_149695_a(var1, var2, var3, var4, var5);
	}

	private void burn(World var1, int var2, int var3, int var4) {
		var1.func_147465_d(var2, var3, var4, Blocks.torch, var1.getBlockMetadata(var2, var3, var4), 3);
		var1.playSoundEffect(var2 + 0.5F, var3 + 0.5F, var4 + 0.5F, "random.fizz", 0.5F, 2.6F + (var1.rand.nextFloat() - var1.rand.nextFloat()) * 0.8F);
		int var5 = var1.getBlockMetadata(var2, var3, var4);
		double var6 = var2 + 0.5F;
		double var8 = var3 + 0.7F;
		double var10 = var4 + 0.5F;
		double var12 = 0.2199999988079071D;
		double var14 = 0.27000001072883606D;
		int var16;
		if (var5 == 1) {
			for (var16 = 0; var16 < 8; ++var16) {
				var1.spawnParticle("flame", var6 - var14, var8 + var12 + var16 * 0.01D, var10, 0.0D, 0.03D, 0.0D);
			}
		} else if (var5 == 2) {
			for (var16 = 0; var16 < 8; ++var16) {
				var1.spawnParticle("flame", var6 + var14, var8 + var12 + var16 * 0.01D, var10, 0.0D, 0.03D, 0.0D);
			}
		} else if (var5 == 3) {
			for (var16 = 0; var16 < 8; ++var16) {
				var1.spawnParticle("flame", var6, var8 + var12 + var16 * 0.01D, var10 - var14, 0.0D, 0.03D, 0.0D);
			}
		} else if (var5 == 4) {
			for (var16 = 0; var16 < 8; ++var16) {
				var1.spawnParticle("flame", var6, var8 + var12 + var16 * 0.01D, var10 + var14, 0.0D, 0.03D, 0.0D);
			}
		} else {
			for (var16 = 0; var16 < 8; ++var16) {
				var1.spawnParticle("flame", var6, var8 + var16 * 0.01D, var10, 0.0D, 0.03D, 0.0D);
			}
		}
	}

	private void onInteract(World world, int x, int y, int z, EntityPlayer player) {
		if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.flint_and_steel) {
			this.burn(world, x, y, z);
		}
	}
}
