package newdungeons;

import java.util.Random;

import net.minecraft.block.BlockStone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ModBlockTest extends BlockStone {
	public ModBlockTest(int var1) {
		super(var1);
	}

	@Override
	public void onBlockClicked(World var1, int var2, int var3, int var4, EntityPlayer var5) {
		//System.out.println("clicky click");
		this.onBlockActivated(var1, var2, var3, var4, var5);
	}

	public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5) {
		var1.setBlock(var2, var3 + 1, var4, 2);
		new Random();
		//this.blockIndexInTexture = var6.nextInt(256);
		return true;
	}
}
