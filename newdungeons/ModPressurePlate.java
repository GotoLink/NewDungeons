package newdungeons;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.EnumMobType;
import net.minecraft.block.material.Material;

public class ModPressurePlate extends BlockPressurePlate {
	protected ModPressurePlate(int var1, String var2, EnumMobType var3, Material var4) {
		super(var1, var2, var4, var3);
	}

	@Override
	public int idDropped(int var1, Random var2, int var3) {
		return Block.pressurePlateStone.idDropped(0, (Random) null, 0);
	}
}
