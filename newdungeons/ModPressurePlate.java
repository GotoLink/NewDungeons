package newdungeons;

import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

import java.util.Random;

public final class ModPressurePlate extends BlockPressurePlate {
	protected ModPressurePlate(String var2, Material var4) {
		super(var2, var4, Sensitivity.players);
	}

	@Override
	public Item getItemDropped(int var1, Random var2, int var3) {
		return Blocks.stone_pressure_plate.getItemDropped(0, var2, var3);
	}
}
