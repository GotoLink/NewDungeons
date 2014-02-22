package newdungeons;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenDesert;
import net.minecraft.world.biome.BiomeGenForest;
import net.minecraft.world.biome.BiomeGenJungle;
import net.minecraft.world.biome.BiomeGenOcean;
import net.minecraft.world.biome.BiomeGenSwamp;
import net.minecraft.world.biome.BiomeGenTaiga;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "newdungeons", name = "New Dungeons", version = "1.7.2")
public final class NewDungeons extends CommandBase implements IWorldGenerator {
	public static Block pressurePlatetest, modDimTorch, modTripWire, modTripWireSource;
	static ArrayList<DunLootItem> loot = new ArrayList<DunLootItem>(), potLoot = new ArrayList<DunLootItem>(), disLoot = new ArrayList<DunLootItem>();
	public static int maxSize = 110;
	public static int minSize = 10;
	public static int minRoomSize = 2;
	public static int rareity = 500;
	public static int minSquareBlocks = 500;
	public static String additionalItems = "";
	public static String biomesID = "ALL,-WATER";
	public static int height = 5;
    public static boolean ID_COMPATIBILITY = false;
	public static final boolean DEBUG = false;
	public static HashSet<Integer> biomes = new HashSet<Integer>();
	public static String dimensionsID = "0";
	public static HashSet<Integer> dimensions = new HashSet<Integer>();

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (dimensions.contains(world.provider.dimensionId)) {
			generateSurface(world, random, chunkX << 4, chunkZ << 4);
		}
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
        if(minRoomSize<2){
            minRoomSize = 2;
        }
        if(minSize<minRoomSize){
            minSize = minRoomSize;
        }
        if(maxSize<minSize+1){
            maxSize = minSize + 1;
        }
        if(rareity<1){
            rareity = 1;
        }
		++minRoomSize;
		addLoot();
		generateBiomeList();
		generateDimensionList();
		GameRegistry.registerWorldGenerator(this, 2);
	}

	@EventHandler
	public void preLoad(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		maxSize = config.get("Generation", "Max size", maxSize).getInt();
		minSize = config.get("Generation", "Min size", minSize).getInt();
		minRoomSize = config.get("Generation", "Room min size", minRoomSize).getInt();
		rareity = config.get("Generation", "Dungeon rarity", rareity, "higher=rarer").getInt();
		minSquareBlocks = config.get("Generation", "Min size in square blocks", minSquareBlocks).getInt();
		biomesID = config.get("Generation", "Biomes allowed", biomesID).getString();
		dimensionsID = config.get("Generation", "Dimensions allowed", dimensionsID).getString();
		additionalItems = config.get("Generation", "Add chest items", additionalItems, "Arguments: itemName rarity value maxStack minStack minDanger enchProb maxEnchant damageVal").getString();
        ID_COMPATIBILITY = !config.get("Generation", "Use custom blocks", !ID_COMPATIBILITY).getBoolean(DEBUG);
        config.save();
        if (!ID_COMPATIBILITY) {
            pressurePlatetest = new ModPressurePlate("stone", Material.rock).setHardness(0.5F).setStepSound(Block.soundTypePiston)
                    .setBlockName("pressurePlatePlayer").setTickRandomly(true);
            GameRegistry.registerBlock(pressurePlatetest, "PressurePlatePlayer");
            modDimTorch = new ModBlockTorch().setHardness(0.0F).setLightLevel(0.5F).setStepSound(Block.soundTypeWood).setBlockName("dimTorch").setBlockTextureName("torch_on")
                .setTickRandomly(true);
            GameRegistry.registerBlock(modDimTorch, "DimTorch");
            modTripWire = new ModBlockTripWire().setBlockName("tripWirePlayer").setBlockTextureName("trip_wire").setTickRandomly(true);
            modTripWireSource = new ModBlockTripWireSource().setBlockName("tripWireSourcePlayer").setBlockTextureName("trip_wire_source").setTickRandomly(true);
            GameRegistry.registerBlock(modTripWire, "TripWirePlayer");
            GameRegistry.registerBlock(modTripWireSource, "WireHookPlayer");
            if(event.getSide().isClient()){
                setModTripWireRender();
            }
            GameRegistry.addShapelessRecipe(new ItemStack(Blocks.torch, 6), Items.coal, modDimTorch);
            GameRegistry.addShapelessRecipe(new ItemStack(Blocks.torch, 6), Items.flint, modDimTorch);
            GameRegistry.addShapelessRecipe(new ItemStack(Blocks.torch, 8), Items.coal, Items.stick, modDimTorch);
        }
	}

    @SideOnly(Side.CLIENT)
    public void setModTripWireRender(){
        ModBlockTripWire.renderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(ModBlockTripWire.renderID, new TripWireRender());
    }

	public static void genDun1(World var0, Random var1, int var2, int var3, BiomeGenBase biome) {
		if (var1.nextInt(rareity) == 0) {
			int var6 = var1.nextInt(45) + 10;
			int var7;
			for (var7 = 0; var7 < 10; ++var7) {
				var6 = var0.getTopSolidOrLiquidBlock(var2, var3) - (var1.nextInt(30) + 20);
				if (var6 > 10 && var6 < 55) {
					break;
				}
			}
			var7 = 1;
			int var8 = 1;
			int var9;
			int var10;
			int var11;
			for (var9 = 0; var9 < 100 && (var7 * var8 < minSquareBlocks || var7 < 10 || var8 < 10); ++var9) {
				var10 = var1.nextInt(maxSize - minSize) + minSize + 1;
				var11 = var1.nextInt(maxSize - minSize) + minSize + 1;
				var7 = var1.nextInt(var10 - minSize) + minSize;
				var8 = var1.nextInt(var11 - minSize) + minSize;
			}
			var11 = var7 * var8 / (var1.nextInt(50) + 50);
			if (DEBUG) {
				var6 = 100;
			}
			if (var9 < 100) {
				makeDun1(var0, var1, var7, var8, height, var2, var6, var3, var11, biome);
			}
		}
	}

	public static void generateSurface(World var1, Random var2, int var3, int var4) {
		BiomeGenBase biome = var1.getWorldChunkManager().getBiomeGenAt(var3, var4);
		if (var1.getWorldInfo().isMapFeaturesEnabled() && biomes.contains(biome.biomeID)) {
			genDun1(var1, var2, var3, var4, biome);
			genDun5(var1, var2, var3, var4);
		}
	}

	public static void genVine(World var0, Random var1, int var2, int var3, int var4, BiomeGenBase var5) {
		byte var6 = 15;
		if (var5 instanceof BiomeGenDesert) {
			var6 = 50;
		}
		if (var5 instanceof BiomeGenSwamp) {
			var6 = 2;
		}
		if (var5 instanceof BiomeGenOcean) {
			var6 = 5;
		}
		if (var5 instanceof BiomeGenForest) {
			var6 = 10;
		}
		if (var5 instanceof BiomeGenJungle) {
			var6 = 2;
		}
		if (var1.nextInt(var6) == 0 && var0.isAirBlock(var2 - 1, var3, var4)) {
			setVines(var0, var2 - 1, var3, var4, 8);
		}
		if (var1.nextInt(var6) == 0 && var0.isAirBlock(var2 + 1, var3, var4)) {
			setVines(var0, var2 + 1, var3, var4, 2);
		}
		if (var1.nextInt(var6) == 0 && var0.isAirBlock(var2, var3, var4 - 1)) {
			setVines(var0, var2, var3, var4 - 1, 1);
		}
		if (var1.nextInt(var6) == 0 && var0.isAirBlock(var2, var3, var4 + 1)) {
			setVines(var0, var2, var3, var4 + 1, 4);
		}
		if (var1.nextInt(var6) == 0 && var0.isAirBlock(var2, var3 - 1, var4)) {
			setVines(var0, var2, var3 - 1, var4 + 1);
		}
	}

	public static boolean isEmpty(World var0, int var1, int var2, int var3) {
		Block var4 = var0.getBlock(var1, var2, var3);
		return var4 == Blocks.air || var4 == Blocks.vine || var4 == Blocks.web || var4 == modDimTorch;
	}

	private static void addLoot() {
		if (!additionalItems.equals("")) {
            String var1 = null;
            int var2 = 1;
            int var3 = 1;
            int var4 = 1;
            int var5 = 30;
            int var6 = 0;
            int var7 = 1;
            double var8 = 1.0D;
            double var10 = 1.0D;
            String[] var14;
			for (String item : additionalItems.split("_")) {
				var14 = item.split("-");
				try {
					switch (var14.length) {
					case 1:
					case 2:
						System.err.println("cannot add item" + var14[0] + "not enough arguments!");
						break;
					case 3:
						var1 = var14[0];
						var8 = Double.parseDouble(var14[1]);
						var10 = Double.parseDouble(var14[2]);
						break;
					case 4:
						var1 = var14[0];
						var8 = Double.parseDouble(var14[1]);
						var10 = Double.parseDouble(var14[2]);
						var2 = Integer.parseInt(var14[3]);
						break;
					case 5:
						var1 = var14[0];
						var8 = Double.parseDouble(var14[1]);
						var10 = Double.parseDouble(var14[2]);
						var2 = Integer.parseInt(var14[3]);
						var3 = Integer.parseInt(var14[4]);
						break;
					case 6:
						var1 = var14[0];
						var8 = Double.parseDouble(var14[1]);
						var10 = Double.parseDouble(var14[2]);
						var2 = Integer.parseInt(var14[3]);
						var3 = Integer.parseInt(var14[4]);
						var4 = Integer.parseInt(var14[5]);
						break;
					case 7:
						var1 = var14[0];
						var8 = Double.parseDouble(var14[1]);
						var10 = Double.parseDouble(var14[2]);
						var2 = Integer.parseInt(var14[3]);
						var3 = Integer.parseInt(var14[4]);
						var4 = Integer.parseInt(var14[5]);
						var6 = Integer.parseInt(var14[6]);
						break;
					case 8:
						var1 = var14[0];
						var8 = Double.parseDouble(var14[1]);
						var10 = Double.parseDouble(var14[2]);
						var2 = Integer.parseInt(var14[3]);
						var3 = Integer.parseInt(var14[4]);
						var4 = Integer.parseInt(var14[5]);
						var6 = Integer.parseInt(var14[6]);
						var5 = Integer.parseInt(var14[7]);
						break;
					case 9:
                    default:
						var1 = var14[0];
						var8 = Double.parseDouble(var14[1]);
						var10 = Double.parseDouble(var14[2]);
						var2 = Integer.parseInt(var14[3]);
						var3 = Integer.parseInt(var14[4]);
						var4 = Integer.parseInt(var14[5]);
						var6 = Integer.parseInt(var14[6]);
						var5 = Integer.parseInt(var14[7]);
						var7 = Integer.parseInt(var14[8]);
                        break;
					}
                    if(var1==null){
                        continue;
                    }
                    Block var15 = GameData.blockRegistry.get(var1);
                    if(var15!=null){
                        loot.add(new DunLootItem(var15, var2, var3, var8, var10, var4, var5, var6, var7, var7));
                    }else{
                        Item var20 = GameData.itemRegistry.get(var1);
                        if(var20!=null){
                            loot.add(new DunLootItem(var20, var2, var3, var8, var10, var4, var5, var6, var7, var7));
                        }
                    }
				} catch (Exception var18) {
					System.err.println("New Dungeons error: " + var18.toString());
				}

			}
		}
		loot.add(new DunLootItem(Items.diamond, 6, 1, 20.0D, 15.0D, 300));
		loot.add(new DunLootItem(Blocks.torch, 15, 1, 3.0D, 3.0D, 30));
		loot.add(new DunLootItem(Blocks.dirt, 64, 15, 13.0D, 1.0D, -1));
		loot.add(new DunLootItem(Items.apple, 3, 1, 5.0D, 5.0D, 50));
		loot.add(new DunLootItem(Items.bone, 5, 1, 7.0D, 2.0D, 0));
		loot.add(new DunLootItem(Items.arrow, 16, 1, 10.0D, 7.0D, 50));
		loot.add(new DunLootItem(Items.bow, 1, 1, 20.0D, 15.0D, 100, 20, 20));
		loot.add(new DunLootItem(Items.painting, 2, 1, 25.0D, 3.0D, 30));
		loot.add(new DunLootItem(Blocks.planks, 5, 1, 4.0D, 10.0D, 40, 0, 0, 0, 3));
		loot.add(new DunLootItem(Blocks.log, 5, 1, 16.0D, 15.0D, 60, 0, 0, 0, 3));
		loot.add(new DunLootItem(Items.leather, 5, 1, 10.0D, 8.0D, 20));
		loot.add(new DunLootItem(Items.leather_leggings, 1, 1, 20.0D, 15.0D, 50));
		loot.add(new DunLootItem(Items.leather_helmet, 1, 1, 20.0D, 15.0D, 50));
		loot.add(new DunLootItem(Items.leather_boots, 1, 1, 20.0D, 15.0D, 50));
		loot.add(new DunLootItem(Items.leather_chestplate, 1, 1, 20.0D, 15.0D, 50));
		loot.add(new DunLootItem(Items.iron_leggings, 1, 1, 35.0D, 30.0D, 250, 15, 15));
		loot.add(new DunLootItem(Items.iron_helmet, 1, 1, 35.0D, 30.0D, 250, 15, 15));
		loot.add(new DunLootItem(Items.iron_boots, 1, 1, 35.0D, 30.0D, 250, 15, 15));
		loot.add(new DunLootItem(Items.iron_chestplate, 1, 1, 35.0D, 30.0D, 250, 15, 15));
		loot.add(new DunLootItem(Items.bread, 5, 1, 7.0D, 5.0D, 50));
		loot.add(new DunLootItem(Items.bucket, 1, 1, 10.0D, 2.0D, 20));
		loot.add(new DunLootItem(Items.iron_ingot, 10, 1, 20.0D, 7.0D, 150));
		loot.add(new DunLootItem(Blocks.iron_ore, 10, 1, 10.0D, 5.0D, 100));
		loot.add(new DunLootItem(Items.cake, 1, 1, 40.0D, 20.0D, 60));
		loot.add(new DunLootItem(Items.beef, 1, 1, 5.0D, 4.0D, 5));
		loot.add(new DunLootItem(Items.cooked_beef, 1, 1, 6.0D, 8.0D, 5));
		loot.add(new DunLootItem(Blocks.melon_block, 3, 1, 17.0D, 10.0D, 30));
		loot.add(new DunLootItem(Items.melon_seeds, 5, 1, 8.0D, 7.0D, 30));
		loot.add(new DunLootItem(Blocks.pumpkin, 3, 1, 13.0D, 10.0D, 30));
		loot.add(new DunLootItem(Items.pumpkin_seeds, 5, 1, 8.0D, 7.0D, 30));
		loot.add(new DunLootItem(Blocks.cobblestone, 64, 15, 14.0D, 2.0D, 0));
		loot.add(new DunLootItem(Items.golden_leggings, 1, 1, 80.0D, 25.0D, 350, 17, 6));
		loot.add(new DunLootItem(Items.golden_helmet, 1, 1, 80.0D, 25.0D, 350, 17, 6));
		loot.add(new DunLootItem(Items.golden_boots, 1, 1, 80.0D, 25.0D, 350, 17, 6));
		loot.add(new DunLootItem(Items.golden_chestplate, 1, 1, 80.0D, 25.0D, 350, 17, 6));
		loot.add(new DunLootItem(Items.wooden_pickaxe, 1, 1, 20.0D, 2.0D, 70));
		loot.add(new DunLootItem(Items.stone_pickaxe, 1, 1, 20.0D, 5.0D, 70));
		loot.add(new DunLootItem(Items.iron_pickaxe, 1, 1, 50.0D, 15.0D, 100, 30, 20));
		loot.add(new DunLootItem(Items.diamond_pickaxe, 1, 1, 70.0D, 60.0D, 400, 50, 25));
		loot.add(new DunLootItem(Items.wooden_sword, 1, 1, 20.0D, 3.0D, 70));
		loot.add(new DunLootItem(Items.stone_sword, 1, 1, 20.0D, 6.0D, 70));
		loot.add(new DunLootItem(Items.iron_sword, 1, 1, 50.0D, 17.0D, 100, 30, 20));
		loot.add(new DunLootItem(Items.diamond_sword, 1, 1, 70.0D, 61.0D, 400, 50, 25));
		loot.add(new DunLootItem(Items.golden_apple, 1, 1, 1000.0D, 100.0D, 200));
		loot.add(new DunLootItem(Items.ender_pearl, 6, 1, 46.0D, 80.0D, 200));
		loot.add(new DunLootItem(Items.ender_eye, 3, 1, 57.0D, 130.0D, 300));
		loot.add(new DunLootItem(Items.redstone, 64, 32, 60.0D, 3.0D, 15));
		loot.add(new DunLootItem(Blocks.tnt, 3, 1, 30.0D, 36.0D, 300));
		loot.add(new DunLootItem(Items.saddle, 1, 1, 10.0D, 60.0D, 30));
		loot.add(new DunLootItem(Items.rotten_flesh, 4, 1, 2.0D, 10.0D, 0));
		loot.add(new DunLootItem(Items.coal, 7, 1, 15.0D, 18.0D, 20));
		loot.add(new DunLootItem(Items.stick, 7, 1, 3.0D, 2.0D, 0));
		loot.add(new DunLootItem(Items.flint_and_steel, 1, 1, 20.0D, 100.0D, 200));
		loot.add(new DunLootItem(Items.flint, 7, 1, 15.0D, 5.0D, 13));
		loot.add(new DunLootItem(Blocks.obsidian, 3, 1, 34.0D, 40.0D, 200));
		loot.add(new DunLootItem(Items.emerald, 3, 1, 40.0D, 50.0D, 200));
        loot.add(new DunLootItem(Items.record_13, 1, 1, 1000, 50.0D, 0));
        loot.add(new DunLootItem(Items.record_cat, 1, 1, 1002, 50.0D, 0));
        loot.add(new DunLootItem(Items.record_blocks, 1, 1, 1004, 50.0D, 0));
        loot.add(new DunLootItem(Items.record_chirp, 1, 1, 1006, 50.0D, 0));
        loot.add(new DunLootItem(Items.record_far, 1, 1, 1008, 50.0D, 0));
        loot.add(new DunLootItem(Items.record_mall, 1, 1, 1010, 50.0D, 0));
        loot.add(new DunLootItem(Items.record_mellohi, 1, 1, 1012, 50.0D, 0));
        loot.add(new DunLootItem(Items.record_stal, 1, 1, 1014, 50.0D, 0));
        loot.add(new DunLootItem(Items.record_strad, 1, 1, 1016, 50.0D, 0));
        loot.add(new DunLootItem(Items.record_ward, 1, 1, 1018, 50.0D, 0));
        loot.add(new DunLootItem(Items.record_11, 1, 1, 1020, 50.0D, 0));
		loot.add(new DunLootItem(Items.potionitem, 1, 1, 27.0D, 36.0D, 100, 0, 99999999));
		potLoot.add(new DunLootItem(Items.glowstone_dust, 4, 1, 1.0D, 1.0D, 0));
		potLoot.add(new DunLootItem(Items.redstone, 4, 1, 1.0D, 1.0D, 0));
		potLoot.add(new DunLootItem(Items.nether_wart, 4, 1, 1.0D, 1.0D, 0));
		potLoot.add(new DunLootItem(Items.sugar, 4, 1, 1.0D, 1.0D, 0));
		potLoot.add(new DunLootItem(Items.gunpowder, 4, 1, 1.0D, 1.0D, 0));
		potLoot.add(new DunLootItem(Items.spider_eye, 4, 1, 1.0D, 1.0D, 0));
		potLoot.add(new DunLootItem(Items.fermented_spider_eye, 4, 1, 5.0D, 4.0D, 0));
		potLoot.add(new DunLootItem(Items.gold_nugget, 4, 1, 1.0D, 1.0D, 0));
		potLoot.add(new DunLootItem(Items.melon, 4, 1, 1.0D, 1.0D, 0, 0, 0, 0, 1));
		potLoot.add(new DunLootItem(Items.ghast_tear, 4, 1, 7.0D, 1.0D, 0));
		potLoot.add(new DunLootItem(Items.magma_cream, 4, 1, 3.0D, 1.0D, 0));
		potLoot.add(new DunLootItem(Items.glass_bottle, 10, 1, 1.0D, 1.0D, 0));
		disLoot.add(new DunLootItem(Items.arrow, 5, 1, 1.0D, 1.0D, 10));
		disLoot.add(new DunLootItem(Items.snowball, 5, 1, 10.0D, 15.0D, 10));
		disLoot.add(new DunLootPotion(Items.potionitem, 6, 2, 1.0D, 0.0D, 0, 16420));
		disLoot.add(new DunLootPotion(Items.potionitem, 4, 2, 1.0D, 0.0D, 0, 16396));
		disLoot.add(new DunLootPotion(Items.potionitem, 5, 2, 1.0D, 0.0D, 0, 16428));
		disLoot.add(new DunLootPotion(Items.potionitem, 3, 2, 1.0D, 0.0D, 0, 16456));
		disLoot.add(new DunLootPotion(Items.potionitem, 3, 2, 1.0D, 0.0D, 0, 16458));
	}

	private static void genDun5(World var1, Random var2, int var3, int var4) {
		for (int var5 = 0; var5 < 10; ++var5) {
			if (var2.nextInt(2) == 0) {
				int var6 = var2.nextInt(16) + 1 + var3;
				int var7 = var2.nextInt(16) + 1 + var4;
				int var8;
				do {
					do {
						var8 = var2.nextInt(126) + 2;
						try {
							var1.getTopSolidOrLiquidBlock(var6, var7);
						} catch (Exception var12) {
							return;
						}
					} while (var1.getTopSolidOrLiquidBlock(var6, var7) == var8);
				} while (var1.getBlock(var6, var8 - 1, var7) == Blocks.water);
				if (var1.isAirBlock(var6, var8, var7) && !var1.isAirBlock(var6, var8 - 1, var7)) {
					var1.setBlock(var6, var8, var7, Blocks.chest);
					ArrayList<DunChest> var9 = new ArrayList<DunChest>();
					var9.add(new DunChest(var6, var8, var7));
					int var10 = 60 - var8;
					int var11 = var10 * 15;
					var10 *= 13;
					putLoot(var1, var2, var9, var10, var11);
				}
			}
		}
	}

	private static void generateBiomeList() {
		for (String txt : biomesID.split(",")) {
			if (txt.equalsIgnoreCase("ALL")) {
				for (int i = 0; i < BiomeGenBase.getBiomeGenArray().length; i++) {
					biomes.add(i);
				}
			} else if (txt.startsWith("-")) {
				txt = txt.substring(1).trim();
				try {
					biomes.remove(Integer.parseInt(txt));
				} catch (NumberFormatException e) {
					for (BiomeGenBase biome : BiomeDictionary.getBiomesForType(BiomeDictionary.Type.valueOf(txt.toUpperCase()))) {
						biomes.remove(biome.biomeID);
					}
				}
			} else {
				try {
					biomes.add(Integer.parseInt(txt));
				} catch (NumberFormatException e) {
					for (BiomeGenBase biome : BiomeDictionary.getBiomesForType(BiomeDictionary.Type.valueOf(txt.toUpperCase()))) {
						biomes.add(biome.biomeID);
					}
				}
			}
		}
	}

	private static void generateDimensionList() {
		for (String txt : dimensionsID.split(",")) {
			try {
				dimensions.add(Integer.parseInt(txt.trim()));
			} catch (NumberFormatException n) {
			}
		}
	}

	private static void genStone(World var0, Random var1, BiomeGenBase biome, int var2, int var3, int var4) {
		genStone(var0, var1, biome, var2, var3, var4, false);
	}

	private static void genStone(World var0, Random var1, BiomeGenBase biome, int var2, int var3, int var4, boolean var5) {
		Block var8 = Blocks.stonebrick;
		byte var9 = 0;
		boolean var10 = false;
		if (var1.nextInt(10) == 0 && var5) {
			var0.setBlockToAir(var2, var3, var4);
			genStone(var0, var1, biome, var2, var3 + 1, var4);
		} else {
			while (!var10) {
				int var6 = var1.nextInt(7);
				int var7 = var1.nextInt(100);
				if (var6 == 0 && var7 < 50) {
					var8 = Blocks.stonebrick;
					var10 = true;
				}
				if (var6 == 1 && var7 < 25) {
					var8 = Blocks.stonebrick;
					var9 = 1;
					var10 = true;
				}
				if (var6 == 2 && var7 < 25) {
					var8 = Blocks.stonebrick;
					var9 = 2;
					var10 = true;
				}
				if (var6 == 3 && var7 < 10) {
					var8 = Blocks.mossy_cobblestone;
					var10 = true;
				}
				if (var6 == 4 && var7 < 6) {
					var8 = Blocks.cobblestone;
					var10 = true;
				}
				if (var6 == 5 && var7 < 3) {
					var8 = Blocks.stone;
					var10 = true;
				}
				if (var6 == 6 && var7 < 7) {
					var8 = Blocks.stonebrick;
					var9 = 3;
					var10 = true;
				}
			}
			var0.setBlock(var2, var3, var4, var8, var9, 2);
		}
		genVine(var0, var1, var2, var3, var4, biome);
	}

	private static boolean makeDun1(World var0, Random var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, BiomeGenBase biome) {
		int var10 = 0;
		int var11 = 0;
		int var12 = 0;
		int var13 = 0;
		int var14;
		int var15;
		int var16;
		for (var14 = 0; var14 < var2 + 1; ++var14) {
			for (var15 = 0; var15 < var3 + 1; ++var15) {
				for (var16 = 1; var16 < var4 - 1; ++var16) {
					if (var0.getBlock(var5 + var14, var6 + var16, var7 + var15) != Blocks.chest) {
						var0.setBlockToAir(var5 + var14, var6 + var16, var7 + var15);
					}
				}
			}
		}
		for (var14 = 0; var14 < var2 + 1; ++var14) {
			for (var15 = 0; var15 < var3 + 1; ++var15) {
				genStone(var0, var1, biome, var5 + var14, var6, var7 + var15);
				if (!DEBUG) {
					genStone(var0, var1, biome, var5 + var14, var6 + var4 - 1, var7 + var15, true);
				}
				if (var14 == 0 || var14 == var2 || var15 == 0 || var15 == var3) {
					for (var16 = 1; var16 < var4 - 1; ++var16) {
						genStone(var0, var1, biome, var5 + var14, var6 + var16, var7 + var15);
					}
				}
			}
		}
		ArrayList<Dun1Node> var37 = new ArrayList<Dun1Node>();
		var15 = 0;
		int var17;
		int var18;
		int var21;
		boolean var20;
		int var23;
		int var22;
		int var25;
		int var24;
		int var26;
		while (var15 < var8 + var1.nextInt(var8 * 5) + var1.nextInt(var8) * -1) {
			var16 = var1.nextInt(var2 - 2) + 1;
			var17 = var1.nextInt(var3 - 2) + 1;
			var18 = var1.nextInt(10 - minRoomSize + 1) + minRoomSize;
			var18 = var1.nextInt(var18 - minRoomSize + 1) + minRoomSize;
			boolean var19 = true;
			var20 = true;
			var21 = 0;
			while (true) {
				if (var21 < var18) {
					if (var0.getBlock(var5 + var16 + var21, var6 + 1, var7 + var17).getMaterial() != Material.rock && var0.getBlock(var5 + var16 - var21, var6 + 1, var7 + var17).getMaterial() != Material.rock) {
						++var21;
						continue;
					}
					var20 = false;
				}
				for (var21 = 0; var21 < var18; ++var21) {
					if (var0.getBlock(var5 + var16, var6 + 1, var7 + var17 + var21).getMaterial() == Material.rock || var0.getBlock(var5 + var16, var6 + 1, var7 + var17 - var21).getMaterial() == Material.rock) {
						var19 = false;
						break;
					}
				}
				if (var19 || var20) {
					for (var21 = 1; var21 < var4 - 1; ++var21) {
						genStone(var0, var1, biome, var5 + var16, var6 + var21, var7 + var17);
					}
				}
				var21 = 1;
				var22 = 1;
				var23 = 1;
				var24 = 1;
				if (var19) {
					for (var25 = 1; var25 < var2 && var0.getBlock(var5 + var16 + var25, var6 + 1, var7 + var17).getMaterial() != Material.rock; ++var25) {
						++var21;
						for (var26 = 1; var26 < var4 - 1; ++var26) {
							genStone(var0, var1, biome, var5 + var16 + var25, var6 + var26, var7 + var17);
						}
					}
					for (var25 = 1; var25 < var2 && var0.getBlock(var5 + var16 - var25, var6 + 1, var7 + var17).getMaterial() != Material.rock; ++var25) {
						++var22;
						for (var26 = 1; var26 < var4 - 1; ++var26) {
							genStone(var0, var1, biome, var5 + var16 - var25, var6 + var26, var7 + var17);
						}
					}
				}
				if (var20) {
					for (var25 = 1; var25 < var3 && var0.getBlock(var5 + var16, var6 + 1, var7 + var17 + var25).getMaterial() != Material.rock; ++var25) {
						++var23;
						for (var26 = 1; var26 < var4 - 1; ++var26) {
							genStone(var0, var1, biome, var5 + var16, var6 + var26, var7 + var17 + var25);
						}
					}
					for (var25 = 1; var25 < var3 && var0.getBlock(var5 + var16, var6 + 1, var7 + var17 - var25).getMaterial() != Material.rock; ++var25) {
						++var24;
						for (var26 = 1; var26 < var4 - 1; ++var26) {
							genStone(var0, var1, biome, var5 + var16, var6 + var26, var7 + var17 - var25);
						}
					}
				}
				if (var21 > 1 || var22 > 1 || var23 > 1 || var24 > 1) {
					var37.add(new Dun1Node(var21, var22, var23, var24, var5 + var16, var6 + 1, var7 + var17));
				}
				++var15;
				break;
			}
		}
		var15 = var1.nextInt(var1.nextInt(2) + 1) + 1;
		int var27;
		int var28;
		int var40;
		int var41;
		for (var16 = 0; var16 < var37.size() - 1; ++var16) {
			var17 = var37.get(var16).wp;
			var18 = var37.get(var16).wn;
			var40 = var37.get(var16).lp;
			var41 = var37.get(var16).ln;
			var21 = var37.get(var16).x;
			var22 = var37.get(var16).z;
			var23 = var37.get(var16).y;
			boolean var29;
			boolean var31;
			boolean var30;
			boolean var32;
			int var33;
			for (var24 = var1.nextInt(var15) + 1; var24 > 0; --var24) {
				var25 = var1.nextInt(var17);
				var1.nextInt(var18);
				var1.nextInt(var40);
				var1.nextInt(var41);
				if (var17 > 2) {
					var29 = var0.getBlock(var21 + var25 + 1, var23, var22).getMaterial() == Material.rock;
					var30 = var0.getBlock(var21 + var25 - 1, var23, var22).getMaterial() == Material.rock;
					var31 = var0.getBlock(var21 + var25, var23, var22 + 1).getMaterial() == Material.rock;
					var32 = var0.getBlock(var21 + var25, var23, var22 - 1).getMaterial() == Material.rock;
					var33 = 0;
					if (var29) {
						++var33;
					}
					if (var30) {
						++var33;
					}
					if (var31) {
						++var33;
					}
					if (var32) {
						++var33;
					}
					if ((var29 && var30) ^ (var31 && var32) && var33 == 2) {
						var0.setBlockToAir(var21 + var25, var23, var22);
						var0.setBlockToAir(var21 + var25, var23 + 1, var22);
					}
				}
			}
			for (var24 = var1.nextInt(var15) + 1; var24 > 0; --var24) {
				var1.nextInt(var17);
				var26 = var1.nextInt(var18);
				var1.nextInt(var40);
				var1.nextInt(var41);
				if (var18 > 2) {
					var29 = var0.getBlock(var21 - var26 + 1, var23, var22).getMaterial() == Material.rock;
					var30 = var0.getBlock(var21 - var26 - 1, var23, var22).getMaterial() == Material.rock;
					var31 = var0.getBlock(var21 - var26, var23, var22 + 1).getMaterial() == Material.rock;
					var32 = var0.getBlock(var21 - var26, var23, var22 - 1).getMaterial() == Material.rock;
					var33 = 0;
					if (var29) {
						++var33;
					}
					if (var30) {
						++var33;
					}
					if (var31) {
						++var33;
					}
					if (var32) {
						++var33;
					}
					if ((var29 && var30) ^ (var31 && var32) && var33 == 2) {
						var0.setBlockToAir(var21 - var26, var23, var22);
						var0.setBlockToAir(var21 - var26, var23 + 1, var22);
					}
				}
			}
			for (var24 = var1.nextInt(var15) + 1; var24 > 0; --var24) {
				var1.nextInt(var17);
				var1.nextInt(var18);
				var27 = var1.nextInt(var40);
				var1.nextInt(var41);
				if (var40 > 2) {
					var29 = var0.getBlock(var21 + 1, var23, var22 + var27).getMaterial() == Material.rock;
					var30 = var0.getBlock(var21 - 1, var23, var22 + var27).getMaterial() == Material.rock;
					var31 = var0.getBlock(var21, var23, var22 + 1 + var27).getMaterial() == Material.rock;
					var32 = var0.getBlock(var21, var23, var22 - 1 + var27).getMaterial() == Material.rock;
					var33 = 0;
					if (var29) {
						++var33;
					}
					if (var30) {
						++var33;
					}
					if (var31) {
						++var33;
					}
					if (var32) {
						++var33;
					}
					if ((var29 && var30) ^ (var31 && var32) && var33 == 2) {
						var0.setBlockToAir(var21, var23, var22 + var27);
						var0.setBlockToAir(var21, var23 + 1, var22 + var27);
					}
				}
			}
			for (var24 = var1.nextInt(var15) + 1; var24 > 0; --var24) {
				var1.nextInt(var17);
				var1.nextInt(var18);
				var1.nextInt(var40);
				var28 = var1.nextInt(var41);
				if (var41 > 2) {
					var29 = var0.getBlock(var21 + 1, var23, var22 - var28).getMaterial() == Material.rock;
					var30 = var0.getBlock(var21 - 1, var23, var22 - var28).getMaterial() == Material.rock;
					var31 = var0.getBlock(var21, var23, var22 + 1 - var28).getMaterial() == Material.rock;
					var32 = var0.getBlock(var21, var23, var22 - 1 - var28).getMaterial() == Material.rock;
					var33 = 0;
					if (var29) {
						++var33;
					}
					if (var30) {
						++var33;
					}
					if (var31) {
						++var33;
					}
					if (var32) {
						++var33;
					}
					if ((var29 && var30) ^ (var31 && var32) && var33 == 2) {
						var0.setBlockToAir(var21, var23, var22 - var28);
						var0.setBlockToAir(var21, var23 + 1, var22 - var28);
					}
				}
			}
		}
		boolean var51;
		int var49;
		int var48;
		int var58;
		for (var17 = 0; var17 < 100; ++var17) {
			var18 = var1.nextInt(var2);
			var40 = var1.nextInt(var3);
			var20 = false;
			for (var21 = -1; var21 < 2; ++var21) {
				for (var22 = -1; var22 < 2; ++var22) {
					if (!var0.isAirBlock(var5 + var18 + var21, var6 + 1, var7 + var40 + var22)) {
						var20 = true;
						break;
					}
				}
				if (var20) {
					break;
				}
			}
			if (!var20) {
				var21 = var0.getTopSolidOrLiquidBlock(var5 + var18, var7 + var40);
				var12 = var21;
				if (var21 >= 60) {
					byte var43 = 5;
					var23 = -1;
					var24 = -1;
					var25 = 0;
					var51 = true;
					var27 = 1;
					for (var28 = var6 + 4; var28 < var21; ++var28) {
						for (var48 = -1; var48 < 2; ++var48) {
							for (var49 = -1; var49 < 2; ++var49) {
								var0.setBlockToAir(var5 + var18 + var48, var28, var7 + var40 + var49);
							}
						}
					}
					boolean var52 = false;
					if (var1.nextInt(3) == 0) {
						var52 = true;
					}
					if (!var52 && var1.nextInt(2) == 0) {
						var0.setBlock(var5 + var18, var6, var7 + var40, Blocks.water);
						if (var1.nextInt(5) != 0) {
							var0.setBlockToAir(var5 + var18, var6 - 1, var7 + var40);
						}
					}
					for (var48 = 0; var48 < var21 * 4; ++var48) {
						var0.setBlock(var5 + var18 + var23, var6 + 1 + var25, var7 + var40 + var24, Blocks.stone_slab, var43, 2);
						if (var52) {
							genStone(var0, var1, biome, var5 + var18, var6 + 1 + var25, var7 + var40);
						}
						if (var51) {
							var23 += var27;
							if (Math.abs(var23) == 1) {
								var51 = false;
							}
						} else {
							var24 += var27;
							if (Math.abs(var24) == 1) {
								var51 = true;
								var27 *= -1;
							}
						}
						if (var43 == 5) {
							var43 = 13;
						} else {
							var43 = 5;
							++var25;
						}
						if (var6 + var25 > var21 - 2) {
							break;
						}
					}
					var10 = var18;
					var11 = var40;
					var48 = var1.nextInt(9) - 4;
					var49 = var1.nextInt(9) - 4;
					var58 = var0.getTopSolidOrLiquidBlock(var5 + var18 + var48, var7 + var40 + var49);
					if (var58 > 0) {
						var0.setBlock(var5 + var18 + var48, var58, var7 + var40 + var49, Blocks.mob_spawner);
						TileEntityMobSpawner var57 = (TileEntityMobSpawner) var0.getTileEntity(var5 + var18 + var48, var58, var7 + var40 + var49);
						if (var57 != null) {
							var57.func_145881_a().setEntityName(pickMobSpawner(var1));
						} else {
							System.err.println("Failed to fetch mob spawner entity at (" + (var5 + var18 + var48) + ", " + var58 + ", " + (var7 + var40 + var49) + ")");
						}
					}
				}
				break;
			}
		}
		ArrayList<DunChest> var38 = new ArrayList<DunChest>();
		boolean var46;
		boolean var44;
		boolean var50;
		for (var40 = 0; var40 < var8 * 2; ++var40) {
			if (var38.size() < 0) {
				var40 = 999999999;
			}
			var41 = var1.nextInt(var2 - 2) + 1;
			var21 = var1.nextInt(var3 - 2) + 1;
			var44 = false;
			if (var0.isAirBlock(var5 + var41, var6 + 1, var7 + var21)) {
				for (var23 = -1; var23 <= 1; ++var23) {
					for (var24 = -1; var24 <= 1; ++var24) {
						if (var0.getBlock(var5 + var41 + var23, var6 + 1, var7 + var21 + var24).getMaterial() == Material.rock && Math.abs(var23) == 1 ^ Math.abs(var24) == 1) {
							var44 = true;
							break;
						}
					}
					if (var44) {
						break;
					}
				}
			}
			boolean var42 = var0.getBlock(var5 + var41 + 1, var6 + 1, var7 + var21).getMaterial() == Material.rock;
			var46 = var0.getBlock(var5 + var41 - 1, var6 + 1, var7 + var21).getMaterial() == Material.rock;
			var50 = var0.getBlock(var5 + var41, var6 + 1, var7 + var21 + 1).getMaterial() == Material.rock;
			var51 = var0.getBlock(var5 + var41, var6 + 1, var7 + var21 - 1).getMaterial() == Material.rock;
			if ((var42 && var46) ^ (var50 && var51)) {
				var44 = false;
			}
			if (var44) {
				var0.setBlock(var5 + var41, var6 + 1, var7 + var21, Blocks.chest);
				var38.add(new DunChest(var5 + var41, var6 + 1, var7 + var21));
			}
		}
		for (var40 = 0; var40 < var8 / 1.1D; ++var40) {
			var41 = var1.nextInt(var2 - 2) + 1;
			var21 = var1.nextInt(var3 - 2) + 1;
			var44 = false;
			if (var0.isAirBlock(var5 + var41, var6 + 1, var7 + var21)) {
				for (var23 = -1; var23 <= 1; ++var23) {
					for (var24 = -1; var24 <= 1; ++var24) {
						if (var0.getBlock(var5 + var41 + var23, var6 + 2, var7 + var21 + var24).getMaterial() == Material.rock && Math.abs(var23) == 1 ^ Math.abs(var24) == 1) {
							var44 = true;
							break;
						}
					}
					if (var44) {
						break;
					}
				}
			}
			if (var44) {
				--var13;
				var0.setBlock(var5 + var41, var6 + 2, var7 + var21, !ID_COMPATIBILITY?modDimTorch:Blocks.redstone_torch);
			}
		}
		for (var40 = 0; var40 < var8 * 6; ++var40) {
			var41 = var1.nextInt(var2 - 2) + 1;
			var21 = var1.nextInt(var3 - 2) + 1;
			if (var0.getBlock(var5 + var41, var6 + 2, var7 + var21).getMaterial() == Material.rock) {
				var0.setBlock(var5 + var41, var6 + 2, var7 + var21, Blocks.iron_bars);
			}
		}
		label1349: for (var40 = 0; var40 < var8 * 0.2D; ++var40) {
			var41 = var1.nextInt(var2 - 2) + 1;
			var21 = var1.nextInt(var3 - 2) + 1;
			var44 = true;
			for (var23 = -2; var23 < 3; ++var23) {
				for (var24 = -2; var24 < 3; ++var24) {
					if (!var0.isAirBlock(var5 + var41 + var23, var6 + 1, var7 + var21 + var24)) {
						var44 = false;
						break;
					}
				}
				if (!var44) {
					break;
				}
			}
			if (var44) {
				for (var23 = -1; var23 < 2; ++var23) {
					for (var24 = -1; var24 < 2; ++var24) {
						var0.setBlock(var5 + var41 + var23, var6, var7 + var21 + var24, Blocks.grass);
					}
				}
				ArrayList<Block> var45 = new ArrayList<Block>();
				var24 = var1.nextInt(5) + 3;
				for (var25 = 0; var25 < var24; ++var25) {
					var26 = var1.nextInt(6);
					if (var26 < 3) {
						var45.add(Blocks.brewing_stand);
					} else if (var26 < 5) {
						var45.add(Blocks.cauldron);
					} else if (var26 == 5) {
						var45.add(Blocks.chest);
					}
				}
				ArrayList<DunChest> var53 = new ArrayList<DunChest>();
                Block block;
				for (var26 = 0; var26 < var45.size(); ++var26) {
                    block = var45.get(var26);
					var28 = var1.nextInt(3) - 1;
					var48 = var1.nextInt(3) - 1;
					if (var0.isAirBlock(var5 + var41 + var28, var6 + 1, var7 + var21 + var48)) {
						var0.setBlock(var5 + var41 + var28, var6 + 1, var7 + var21 + var48, block);
						if (block == Blocks.chest) {
							var53.add(new DunChest(var5 + var41 + var28, var6 + 1, var7 + var21 + var48));
						}
						if (block == Blocks.cauldron) {
							var0.setBlockMetadataWithNotify(var5 + var41 + var28, var6 + 1, var7 + var21 + var48, var1.nextInt(3) + 1, 3);
						}
					}
				}
				var26 = 0;
				while (true) {
					if (var26 >= var53.size()) {
						break label1349;
					}
					var27 = var1.nextInt(10) + 5;
					for (var28 = 0; var28 < var27; ++var28) {
						var48 = var1.nextInt(potLoot.size());
						var49 = var53.get(var26).x;
						var58 = var53.get(var26).y;
						int var55 = var53.get(var26).z;
						TileEntityChest var63 = null;
						if (var0.getBlock(var49, var58, var55) == Blocks.chest) {
							var63 = (TileEntityChest) var0.getTileEntity(var49, var58, var55);
						}
						if (var63 == null) {
							break;
						}
						for (int var34 = 0; var34 < 1; ++var34) {
							ItemStack var35 = (potLoot.get(var48)).getItemStack(var1);
							if (var35 != null) {
								var63.setInventorySlotContents(var1.nextInt(var63.getSizeInventory()), var35);
							}
						}
					}
					var28 = var53.get(var26).x;
					var48 = var53.get(var26).y;
					var49 = var53.get(var26).z;
					TileEntityChest var56 = null;
					if (var0.getBlock(var28, var48, var49) == Blocks.chest) {
						var56 = (TileEntityChest) var0.getTileEntity(var28, var48, var49);
					}
					if (var56 != null) {
						var56.setInventorySlotContents(var1.nextInt(var56.getSizeInventory()), new ItemStack(Items.glass_bottle, var1.nextInt(7) + 2));
					}
					++var26;
				}
			}
		}
		for (var40 = 0; var40 < var8 / 5; ++var40) {
			var41 = var1.nextInt(var2 - 2) + 1;
			var21 = var1.nextInt(var3 - 2) + 1;
			var44 = true;
			var23 = var1.nextInt(2);
			var24 = 0;
			for (var25 = -1; var25 < 3; ++var25) {
				for (var26 = 1; var26 < 4; ++var26) {
					if (var23 == 1) {
						if (!isEmpty(var0, var5 + var41 + var25, var6 + var26, var7 + var21)) {
							var44 = false;
						}
					} else if (!isEmpty(var0, var5 + var41, var6 + var26, var7 + var21 + var25)) {
						var44 = false;
					}
					if (!var44) {
						break;
					}
				}
				if (!var44) {
					break;
				}
			}
			if (var44) {
				for (var25 = -1; var25 < 3; ++var25) {
					for (var26 = 0; var26 < 5; ++var26) {
						if ((var25 == -1 || var25 == 2 || var26 == 0 || var26 == 4) && var1.nextInt(6) != 1) {
							++var24;
							if (var23 == 1) {
								var0.setBlock(var5 + var41 + var25, var6 + var26, var7 + var21, Blocks.obsidian);
							} else {
								var0.setBlock(var5 + var41, var6 + var26, var7 + var21 + var25, Blocks.obsidian);
							}
						}
					}
				}
				if (var24 == 14) {
					for (var25 = 0; var25 < 2; ++var25) {
						for (var26 = 1; var26 < 4; ++var26) {
							if (var23 == 1) {
								var0.setBlock(var5 + var41 + var25, var6 + var26, var7 + var21, Blocks.portal);
							} else {
								var0.setBlock(var5 + var41, var6 + var26, var7 + var21 + var25, Blocks.portal);
							}
						}
					}
				}
				break;
			}
		}
		for (var40 = 0; var40 < var8; ++var40) {
			var41 = var1.nextInt(var2 - 2) + 1;
			var21 = var1.nextInt(var3 - 2) + 1;
			var44 = true;
			for (var23 = -3; var23 < 4; ++var23) {
				for (var24 = -3; var24 < 4; ++var24) {
					if (!var0.isAirBlock(var5 + var41 + var23, var6 + 1, var7 + var21 + var24)) {
						var44 = false;
						break;
					}
				}
				if (!var44) {
					break;
				}
			}
			if (var44) {
				for (var23 = -2; var23 < 3; ++var23) {
					for (var24 = -2; var24 < 3; ++var24) {
						var0.setBlock(var5 + var41 + var23, var6, var7 + var21 + var24, Blocks.planks);
						if ((Math.abs(var23) == 2 || Math.abs(var24) == 2) && var1.nextBoolean()) {
							var25 = var1.nextInt(3);
							for (var26 = 0; var26 < var25; ++var26) {
								var0.setBlock(var5 + var41 + var23, var6 + 1 + var26, var7 + var21 + var24, Blocks.bookshelf);
								genVine(var0, var1, var5 + var41 + var23, var6 + 1 + var26, var7 + var21 + var24, biome);
							}
						}
					}
				}
				var0.setBlock(var5 + var41, var6 + 1, var7 + var21, Blocks.enchanting_table);
				break;
			}
		}
		var40 = 0;
		while (var40 < var8 * 1.5D) {
			var41 = var1.nextInt(var2 - 2) + 1;
			var21 = var1.nextInt(var3 - 2) + 1;
			var44 = true;
			var23 = -2;
			while (true) {
				if (var23 < 3) {
					for (var24 = -2; var24 < 3; ++var24) {
						if (var0.getBlock(var5 + var41 + var23, var6 + 1, var7 + var21 + var24).getMaterial() == Material.rock) {
							var44 = false;
							break;
						}
					}
					if (var44) {
						++var23;
						continue;
					}
				}
				if (var44) {
					for (var23 = 1; var23 < var4 - 1; ++var23) {
						genStone(var0, var1, biome, var5 + var41, var6 + var23, var7 + var21);
					}
					for (var23 = -1; var23 < 2; ++var23) {
						for (var24 = -1; var24 < 2; ++var24) {
							if (var23 == 0 ^ var24 == 0 && var1.nextInt(4) == 0) {
								var0.setBlock(var5 + var41 + var23, var6 + 2, var7 + var21 + var24, modDimTorch);
							}
						}
					}
				}
				++var40;
				break;
			}
		}
		var40 = 0;
		while (var40 < var8) {
			var41 = var1.nextInt(var2 - 2) + 1;
			var21 = var1.nextInt(var3 - 2) + 1;
			var44 = true;
			var23 = -1;
			while (true) {
				if (var23 <= 1) {
					for (var24 = -1; var24 <= 1; ++var24) {
						if (var0.getBlock(var5 + var41 + var23, var6 + 1, var7 + var21 + var24).getMaterial() == Material.rock) {
							var44 = false;
							break;
						}
					}
					if (var44) {
						++var23;
						continue;
					}
				}
				if (var44) {
					var13 += 10;
					if (var6 + 1 > 0) {
						var0.setBlock(var5 + var41, var6 + 1, var7 + var21, Blocks.mob_spawner);
						TileEntityMobSpawner var59 = (TileEntityMobSpawner) var0.getTileEntity(var5 + var41, var6 + 1, var7 + var21);
						if (var59 != null) {
							String var60 = pickMobSpawner(var1);
							var59.func_145881_a().setEntityName(var60);
							if (var60.equals("Creeper")) {
								var13 += 10;
							}
						} else {
							System.err.println("Failed to fetch mob spawner entity at (" + (var5 + var41) + ", " + (var6 + 1) + ", " + (var7 + var21) + ")");
						}
					}
				}
				++var40;
				break;
			}
		}
		byte var47 = 2;
		if (biome instanceof BiomeGenDesert) {
			var47 = 8;
		}
		if (biome instanceof BiomeGenSwamp) {
			var47 = 1;
		}
		if (biome instanceof BiomeGenOcean) {
			var47 = 4;
		}
		if (biome instanceof BiomeGenTaiga) {
			var47 = 6;
		}
		if (biome instanceof BiomeGenJungle) {
			var47 = 0;
		}
		var41 = var8 / 2;
		for (var21 = 0; var21 < var41 * var47; ++var21) {
			var22 = var1.nextInt(var2 - 2) + 1;
			var23 = var1.nextInt(var3 - 2) + 1;
			var24 = var1.nextInt(var4 - 2) + var6 + 1;
			var25 = 0;
			for (var26 = -1; var26 <= 2; ++var26) {
				for (var27 = -1; var27 <= 2; ++var27) {
					for (var28 = -1; var28 <= 2; ++var28) {
						if (var0.getBlock(var5 + var22 + var26, var24 + var28, var7 + var23 + var27).getMaterial() == Material.rock) {
							++var25;
						}
					}
				}
			}
			if (var25 > 1 && var0.isAirBlock(var5 + var22, var24, var7 + var23)) {
				var13 += 2;
				var0.setBlock(var5 + var22, var24, var7 + var23, Blocks.web);
			}
		}
		var21 = 0;
		while (var21 < var8) {
			var22 = var1.nextInt(var2 - 2) + 1;
			var23 = var1.nextInt(var3 - 2) + 1;
			var46 = true;
			var25 = -7;
			while (true) {
				if (var25 <= 8) {
					for (var26 = -5; var26 <= 6; ++var26) {
						if (var0.getBlock(var5 + var22 + var25, var6 + 1, var7 + var23 + var26) == Blocks.mob_spawner) {
							var46 = false;
							break;
						}
					}
					if (var46) {
						++var25;
						continue;
					}
				}
				if (var0.getBlock(var5 + var22, var6 + 2, var7 + var23).getMaterial() == Material.rock) {
					var46 = false;
				}
				if (var46) {
					var13 += 4;
					if (!ID_COMPATIBILITY) {
						var0.setBlock(var5 + var22, var6 + 1, var7 + var23, pressurePlatetest);
					} else {
						var0.setBlock(var5 + var22, var6 + 1, var7 + var23, Blocks.stone_pressure_plate, -1, 2);
					}
					if (var1.nextInt(3) != 1) {
						var0.setBlock(var5 + var22, var6, var7 + var23, Blocks.gravel);
					}
					var0.setBlock(var5 + var22, var6 - 1, var7 + var23, Blocks.tnt);
					var0.setBlock(var5 + var22, var6 - 2, var7 + var23, Blocks.stone);
					var50 = true;
					for (var26 = -1; var26 < 2; ++var26) {
						for (var27 = -1; var27 < 2; ++var27) {
							if (var0.isAirBlock(var5 + var22 + var26, var6 - 3, var7 + var23 + var27)) {
								var50 = false;
							}
						}
					}
					for (var26 = -2; var26 < 3; ++var26) {
						for (var27 = -2; var27 < 3; ++var27) {
							if ((var26 == -2 || var26 == 2 || var27 == -2 || var27 == 2) && var0.isAirBlock(var5 + var22 + var26, var6 - 2, var7 + var23 + var27)) {
								var50 = false;
							}
						}
					}
					if (var1.nextInt(5) == 1 && var50) {
						for (var26 = -1; var26 < 2; ++var26) {
							for (var27 = -1; var27 < 2; ++var27) {
								var0.setBlock(var5 + var22 + var26, var6 - 3, var7 + var23 + var27, Blocks.lava);
							}
						}
					}
				}
				++var21;
				break;
			}
		}
		for (var21 = 0; var21 < 20; ++var21) {
			var22 = var1.nextInt(var2);
			var23 = var1.nextInt(var3);
			TileEntityDispenser var62;
			if (var1.nextBoolean() && var0.isAirBlock(var5 + var22, var6 + 1, var7 + var23)) {
				var46 = false;
				var50 = false;
				var26 = 0;
				var27 = 0;
				for (var28 = 0; var28 < 200; ++var28) {
					if (!var0.isAirBlock(var5 + var22, var6 + 1, var7 + var23 + var28)) {
						if (var0.getBlock(var5 + var22, var6 + 1, var7 + var23 + var28).getMaterial() == Material.rock) {
							var46 = true;
							var26 = var28 - 1;
						}
						break;
					}
				}
				if (var46) {
					for (var28 = 0; var28 < 200; ++var28) {
						if (!var0.isAirBlock(var5 + var22, var6 + 1, var7 + var23 - var28)) {
							if (var0.getBlock(var5 + var22, var6 + 1, var7 + var23 - var28).getMaterial() == Material.rock) {
								var50 = true;
								var27 = var28 - 1;
							}
							break;
						}
					}
				}
				if (var50 && var26 + var27 > 3) {
					var0.setBlock(var5 + var22, var6 + 1, var7 + var23 + var26, !ID_COMPATIBILITY?modTripWireSource:Blocks.tripwire_hook, 2, 2);
					var0.setBlock(var5 + var22, var6 + 1, var7 + var23 - var27, !ID_COMPATIBILITY?modTripWireSource:Blocks.tripwire_hook, 0, 2);
					for (var28 = -var27 + 1; var28 < var26; ++var28) {
						var0.setBlock(var5 + var22, var6 + 1, var7 + var23 + var28, !ID_COMPATIBILITY?modTripWire:Blocks.tripwire, 4, 2);
					}
					if (var1.nextBoolean()) {
						var0.setBlock(var5 + var22, var6 + 2, var7 + var23 - var27 - 1, Blocks.dispenser);
						var0.setBlockMetadataWithNotify(var5 + var22, var6 + 2, var7 + var23 - var27 - 1, 3, 3);
						var62 = null;
						if (var0.getBlock(var5 + var22, var6 + 2, var7 + var23 - var27 - 1) == Blocks.dispenser) {
							var62 = (TileEntityDispenser) var0.getTileEntity(var5 + var22, var6 + 2, var7 + var23 - var27 - 1);
						}
						if (var62 == null) {
							break;
						}
						var48 = var1.nextInt(5) + 1;
						for (var49 = 0; var49 < var48; ++var49) {
							var58 = var1.nextInt(disLoot.size());
							if (var1.nextInt((int) (disLoot.get(var58)).rareity) == 0) {
								var62.setInventorySlotContents(var1.nextInt(var62.getSizeInventory()), (disLoot.get(var58)).getItemStack(var1));
							} else {
								--var49;
							}
						}
					} else {
						var0.setBlock(var5 + var22, var6 + 2, var7 + var23 + var26 + 1, Blocks.dispenser);
						var0.setBlockMetadataWithNotify(var5 + var22, var6 + 2, var7 + var23 + var26 + 1, 2, 3);
						var62 = null;
						if (var0.getBlock(var5 + var22, var6 + 2, var7 + var23 + var26 + 1) == Blocks.dispenser) {
							var62 = (TileEntityDispenser) var0.getTileEntity(var5 + var22, var6 + 2, var7 + var23 + var26 + 1);
						}
						if (var62 == null) {
							break;
						}
						var48 = var1.nextInt(5) + 1;
						for (var49 = 0; var49 < var48; ++var49) {
							var58 = var1.nextInt(disLoot.size());
							if (var1.nextInt((int) (disLoot.get(var58)).rareity) == 0) {
								var62.setInventorySlotContents(var1.nextInt(var62.getSizeInventory()), (disLoot.get(var58)).getItemStack(var1));
							} else {
								--var49;
							}
						}
					}
				}
			} else if (var0.isAirBlock(var5 + var22, var6 + 1, var7 + var23)) {
				var46 = false;
				var50 = false;
				var26 = 0;
				var27 = 0;
				for (var28 = 0; var28 < 200; ++var28) {
					if (!var0.isAirBlock(var5 + var22 + var28, var6 + 1, var7 + var23)) {
						if (var0.getBlock(var5 + var22 + var28, var6 + 1, var7 + var23).getMaterial() == Material.rock) {
							var46 = true;
							var26 = var28 - 1;
						}
						break;
					}
				}
				if (var46) {
					for (var28 = 0; var28 < 200; ++var28) {
						if (!var0.isAirBlock(var5 + var22 - var28, var6 + 1, var7 + var23)) {
							if (var0.getBlock(var5 + var22 - var28, var6 + 1, var7 + var23).getMaterial() == Material.rock) {
								var50 = true;
								var27 = var28 - 1;
							}
							break;
						}
					}
				}
				if (var50 && var26 + var27 > 3) {
					var0.setBlock(var5 + var22 + var26, var6 + 1, var7 + var23, !ID_COMPATIBILITY?modTripWireSource:Blocks.tripwire_hook, 1, 2);
					var0.setBlock(var5 + var22 - var27, var6 + 1, var7 + var23, !ID_COMPATIBILITY?modTripWireSource:Blocks.tripwire_hook, 3, 2);
					for (var28 = -var27 + 1; var28 < var26; ++var28) {
						var0.setBlock(var5 + var22 + var28, var6 + 1, var7 + var23, !ID_COMPATIBILITY?modTripWire:Blocks.tripwire, 4, 2);
					}
					if (var1.nextBoolean()) {
						var0.setBlock(var5 + var22 - var27 - 1, var6 + 2, var7 + var23, Blocks.dispenser);
						var0.setBlockMetadataWithNotify(var5 + var22 - var27 - 1, var6 + 2, var7 + var23, 5, 3);
						var62 = null;
						if (var0.getBlock(var5 + var22 - var27 - 1, var6 + 2, var7 + var23) == Blocks.dispenser) {
							var62 = (TileEntityDispenser) var0.getTileEntity(var5 + var22 - var27 - 1, var6 + 2, var7 + var23);
						}
						if (var62 == null) {
							break;
						}
						var48 = var1.nextInt(5) + 1;
						for (var49 = 0; var49 < var48; ++var49) {
							var58 = var1.nextInt(disLoot.size());
							if (var1.nextInt((int) (disLoot.get(var58)).rareity) == 0) {
								var62.setInventorySlotContents(var1.nextInt(var62.getSizeInventory()), (disLoot.get(var58)).getItemStack(var1));
							} else {
								--var49;
							}
						}
					} else {
						var0.setBlock(var5 + var22 + var26 + 1, var6 + 2, var7 + var23, Blocks.dispenser);
						var0.setBlockMetadataWithNotify(var5 + var22 + var26 + 1, var6 + 2, var7 + var23, 4, 3);
						var62 = null;
						if (var0.getBlock(var5 + var22 + var26 + 1, var6 + 2, var7 + var23) == Blocks.dispenser) {
							var62 = (TileEntityDispenser) var0.getTileEntity(var5 + var22 + var26 + 1, var6 + 2, var7 + var23);
						}
						if (var62 == null) {
							break;
						}
						var48 = var1.nextInt(5) + 1;
						for (var49 = 0; var49 < var48; ++var49) {
							var58 = var1.nextInt(disLoot.size());
							if (var1.nextInt((int) (disLoot.get(var58)).rareity) == 0) {
								var62.setInventorySlotContents(var1.nextInt(var62.getSizeInventory()), (disLoot.get(var58)).getItemStack(var1));
							} else {
								--var49;
							}
						}
					}
				}
			}
		}
		double var54 = var13;
		double var61 = var54 / 300.0D * 7.0D;
		var25 = (int) (var61 + 2.0D);
		if (var12 >= 60) {
			for (var26 = -5; var26 < 6; ++var26) {
				for (var27 = -5; var27 < 6; ++var27) {
					if (var26 == -5 || var26 == 5 || var27 == -5 || var27 == 5) {
						for (var28 = var1.nextInt(var25) * -1; var28 < 100; ++var28) {
							if (var0.getBlock(var5 + var10 + var26, var12 + 1 - var28, var7 + var11 + var27).getMaterial() != Material.air
									&& var0.getBlock(var5 + var10 + var26, var12 + 1 - var28, var7 + var11 + var27).getMaterial() != Material.water
									&& var0.getBlock(var5 + var10 + var26, var12 + 1 - var28, var7 + var11 + var27).getMaterial() != Material.leaves
									&& var0.getBlock(var5 + var10 + var26, var12 + 1 - var28, var7 + var11 + var27).getMaterial() != Material.wood
									&& var0.getBlock(var5 + var10 + var26, var12 + 1 - var28, var7 + var11 + var27) != Blocks.tallgrass
									&& var0.getBlock(var5 + var10 + var26, var12 + 1 - var28, var7 + var11 + var27).getMaterial() != Material.craftedSnow) {
								if (var28 < 0) {
									var0.setBlockToAir(var5 + var10 + var26, var12 + 1 - var28, var7 + var11 + var27);
								}
							} else {
								genStone(var0, var1, biome, var5 + var10 + var26, var12 + 1 - var28, var7 + var11 + var27);
							}
							if (var12 + 1 - var28 < var6 + var4) {
								break;
							}
						}
					}
				}
			}
		}
		var26 = var13 * 2 * (var38.size() / 2);
		putLoot(var0, var1, var38, var13, var26);
		return true;
	}

	private static String pickMobSpawner(Random var0) {
		for (int var1 = 0; var1 < 5; ++var1) {
			int var2 = var0.nextInt(6);
			if (var2 == 0) {
				return "Skeleton";
			}
			if (var2 == 1) {
				return "Zombie";
			}
			if (var2 == 2) {
				return "Zombie";
			}
			if (var2 == 3) {
				return "Spider";
			}
			if (var2 == 5 && var0.nextInt(25) == 1) {
				return "Creeper";
			}
		}
		return "";
	}

	private static void putLoot(World var0, Random var1, ArrayList<DunChest> var2, int var3, int var4) {
		int var5 = 0;
		boolean var7 = false;
		int var19;
		for (int var8 = 0; var8 < 1000 && var4 > 10; ++var8) {
			for (int var9 = 0; var9 < 1000 && var4 > 10 && var2.size() > 0; ++var9) {
				if (var7) {
					++var5;
				}
				var7 = false;
				if (var5 >= var2.size()) {
					var5 = 0;
				}
				int var10 = var1.nextInt(loot.size());
				DunLootItem var11 = loot.get(var10);
				int var12 = 1;
				if (var11.maxStack - var11.minStack > 1) {
					var12 = var1.nextInt(var11.maxStack - var11.minStack) + var11.minStack;
				}
				for (var19 = 0; var19 < 100; ++var19) {
					var10 = var1.nextInt(loot.size());
					var11 = loot.get(var10);
					var12 = 1;
					if (var11.maxStack - var11.minStack > 1) {
						var12 = var1.nextInt(var11.maxStack - var11.minStack) + var11.minStack;
					}
					if (var1.nextInt((int) (loot.get(var10)).rareity) == 0) {
						break;
					}
				}
				if (var19 != 100 && var4 > (loot.get(var10)).value * var12 && loot.get(var10).minDanger <= var3 && var2.size() > 0) {
					var4 = (int) (var4 - (loot.get(var10)).value * var12);
					var7 = true;
					int var13 = var2.get(var5).x;
					int var14 = var2.get(var5).y;
					int var15 = var2.get(var5).z;
					var2.get(var5).looted = true;
					TileEntityChest var16 = null;
					if (var0.getBlock(var13, var14, var15) == Blocks.chest) {
						var16 = (TileEntityChest) var0.getTileEntity(var13, var14, var15);
					}
					if (var16 == null) {
						break;
					}
					for (int var17 = 0; var17 < 1; ++var17) {
						ItemStack var18 = (loot.get(var10)).getItemStack(var1);
						if ((loot.get(var10)).enchatProb > 0 && var1.nextInt((loot.get(var10)).enchatProb) == 0) {
							EnchantmentHelper.addRandomEnchantment(var1, var18, (loot.get(var10)).maxEnchatLev);
						}
						if (var18 != null) {
							var16.setInventorySlotContents(var1.nextInt(var16.getSizeInventory()), var18);
						}
					}
				}
			}
		}
		for (var19 = 0; var19 < var2.size(); ++var19) {
			if (!var2.get(var19).looted) {
				var0.setBlockToAir(var2.get(var19).x, var2.get(var19).y, var2.get(var19).z);
			}
		}
	}

	private static void setVines(World var0, int x, int y, int z) {
		var0.setBlock(x, y, z, Blocks.vine);
		int var5 = new Random().nextInt(5) + 1;
		while (var5 > 0) {
			--y;
			if (!var0.isAirBlock(x, y, z)) {
				return;
			}
			var0.setBlock(x, y, z, Blocks.vine);
			--var5;
		}
	}

	private static void setVines(World var0, int x, int y, int z, int meta) {
		var0.setBlock(x, y, z, Blocks.vine, meta, 2);
		int var6 = new Random().nextInt(5) + 1;
		while (var6 > 0) {
			--y;
			if (!var0.isAirBlock(x, y, z)) {
				return;
			}
			var0.setBlock(x, y, z, Blocks.vine, meta, 2);
			--var6;
		}
	}

    @Override
    public String getCommandName() {
        return "makeDungeon";
    }

    @Override
    public String getCommandUsage(ICommandSender var1) {
        return "commands.makedungeon.usage";
    }

    @Override
    public void processCommand(ICommandSender var1, String[] var2) {
        serverChat(var1, var2);
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event){
        event.registerServerCommand(this);
    }

    public void serverChat(ICommandSender var1, String[] var2) {
        if (var2!=null && var2.length >= 3) {
            int i = var1.getPlayerCoordinates().posX;
            int j = var1.getPlayerCoordinates().posY;
            int k = var1.getPlayerCoordinates().posZ;
            i = MathHelper.floor_double(func_110666_a(var1, (double) i, var2[0]));
            j = MathHelper.floor_double(func_110666_a(var1, (double) j, var2[1]));
            k = MathHelper.floor_double(func_110666_a(var1, (double) k, var2[2]));
            World world = var1.getEntityWorld();
            if (!world.blockExists(i, j, k))
            {
                throw new CommandException("commands.setblock.outOfWorld");
            }
            int sizeX = 5, sizeZ = 5;
            if(var2.length==5){
                sizeX = parseIntBounded(var1, var2[3], 5, 500);
                sizeZ = parseIntBounded(var1, var2[4], 5, 500);
            }
            Random var4 = new Random();
            int var14 = sizeX * sizeZ / (var4.nextInt(50) + 50);
            if(var14 < 4){
                var14 = 4;
            }
            if (makeDun1(world, var4, sizeX, sizeZ, 5, i, j, k, var14, world.getWorldChunkManager().getBiomeGenAt(i, k))) {
                notifyAdmins(var1, "dungeon.done");
            }
        }else{
            throw new WrongUsageException("commands.makedungeon.usage");
        }
    }
}
