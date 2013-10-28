package newdungeons;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHalfSlab;
import net.minecraft.block.EnumMobType;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityMobSpawner;
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
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IChatListener;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "newdungeons", name = "New Dungeons", version = "1.6.4")
public final class NewDungeons implements IWorldGenerator, IChatListener {
	public static Block pressurePlatetest, modDimTorch, modTripWire, modTripWireSource;
	static ArrayList loot = new ArrayList(), potLoot = new ArrayList(), disLoot = new ArrayList();
	public static int OmaxSize = 110;
	public static int OminSize = 10;
	public static int OminRoomSize = 2;
	public static int Orareity = 500;
	public static int OMinSquareBlocks = 500;
	public static String OadditionalItems = "";
	public static int pressurePlateID = 2152;
	public static int dimTorchID = 2153;
	public static int tripWireID = 2154;
	public static int tripWireSrcID = 2155;
	public static String biomesID = "ALL,-WATER";
	public static int Oheight = 5;
	public static final boolean Odebug = false, OidCompatibility = false;
	public static Set<Integer> biomes = new HashSet();
	public static String dimensionsID = "0";
	public static Set<Integer> dimensions = new HashSet();

	@Override
	public Packet3Chat clientChat(NetHandler handler, Packet3Chat message) {
		return message;
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (dimensions.contains(world.provider.dimensionId)) {
			generateSurface(world, random, chunkX << 4, chunkZ << 4);
		}
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		++OminRoomSize;
		addLoot();
		generateBiomeList();
		generateDimensionList();
		if (!OidCompatibility) {
			pressurePlatetest = (new ModPressurePlate(pressurePlateID, "stone", EnumMobType.players, Material.rock)).setHardness(0.5F).setStepSound(Block.soundStoneFootstep)
					.setUnlocalizedName("pressurePlatePlayer").setTickRandomly(true);
			GameRegistry.registerBlock(pressurePlatetest, "PressurePlatePlayer");
		}
		modDimTorch = (new ModBlockTorch(dimTorchID)).setHardness(0.0F).setLightValue(0.5F).setStepSound(Block.soundWoodFootstep).setUnlocalizedName("Dim Torch").setTextureName("torch_on")
				.setTickRandomly(true);
		modTripWire = (new ModBlockTripWire(tripWireID)).setUnlocalizedName("tripWirePlayer").setTextureName("trip_wire").setTickRandomly(true);
		modTripWireSource = (new ModBlockTripWireSource(tripWireSrcID)).setUnlocalizedName("tripWireSourcePlayer").setTextureName("trip_wire_source").setTickRandomly(true);
		GameRegistry.registerBlock(modTripWire, "TripWirePlayer");
		GameRegistry.registerBlock(modTripWireSource, "WireHookPlayer");
		GameRegistry.registerBlock(modDimTorch, "DimTorch");
		LanguageRegistry.addName(modDimTorch, "Dim Torch");
		GameRegistry.addShapelessRecipe(new ItemStack(Block.torchWood, 6), Item.coal, modDimTorch);
		GameRegistry.addShapelessRecipe(new ItemStack(Block.torchWood, 6), Item.flint, modDimTorch);
		GameRegistry.addShapelessRecipe(new ItemStack(Block.torchWood, 8), Item.coal, Item.stick, modDimTorch);
		GameRegistry.registerWorldGenerator(this);
		NetworkRegistry.instance().registerChatListener(this);
	}

	@EventHandler
	public void preLoad(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		OmaxSize = config.get("Generation", "Max size", OmaxSize).getInt();
		OminSize = config.get("Generation", "Min size", OminSize).getInt();
		OminRoomSize = config.get("Generation", "Room min size", OminRoomSize).getInt();
		Orareity = config.get("Generation", "Dungeon rarity", Orareity, "higher=rarer").getInt();
		OMinSquareBlocks = config.get("Generation", "Min size in square blocks", OMinSquareBlocks).getInt();
		biomesID = config.get("Generation", "Biomes allowed", biomesID).getString();
		dimensionsID = config.get("Generation", "Dimensions allowed", dimensionsID).getString();
		OadditionalItems = config.get("Generation", "Add chest items", OadditionalItems, "Arguments: itemId rarity value maxStack minStack minDanger enchProb maxEnchant damageVal").getString();
		pressurePlateID = config.getBlock("Player sensitive pressure plate", pressurePlateID).getInt();
		dimTorchID = config.getBlock("Dim torch", dimTorchID).getInt();
		tripWireID = config.getBlock("Player sensitive trip wire", tripWireID).getInt();
		tripWireSrcID = config.getBlock("Player sensitive wire hook", tripWireSrcID).getInt();
		config.save();
	}

	@Override
	public Packet3Chat serverChat(NetHandler handler, Packet3Chat message) {
		serverChat((NetServerHandler) handler, message.message);
		return message;
	}

	public void serverChat(NetServerHandler var1, String var2) {
		String[] var3 = var2.split(" ");
		if (var3[0].equals("/makeDun") && var3.length == 3) {
			World var4 = var1.getPlayer().worldObj;
			Random var5 = new Random();
			int var6 = Integer.parseInt(var3[1]);
			int var7 = Integer.parseInt(var3[2]);
			double var8 = var1.getPlayer().lastTickPosX - var6 / 2;
			double var10 = var1.getPlayer().lastTickPosZ - var7 / 2;
			byte var12 = 5;
			int var13 = 100;
			int var14;
			if (!Odebug) {
				var13 = var5.nextInt(45) + 10;
				for (var14 = 0; var14 < 10; ++var14) {
					try {
						var13 = var4.getTopSolidOrLiquidBlock((int) var8, (int) var10) - (var5.nextInt(30) + 20);
					} catch (Exception var16) {
						return;
					}
					if (var13 > 10 && var13 < 55) {
						break;
					}
				}
			}
			var14 = var6 * var7 / (var5.nextInt(50) + 50);
			if (makeDun1(var4, var5, var6, var7, var12, (int) var8, var13, (int) var10, var14, var4.getWorldChunkManager().getBiomeGenAt((int) var8, (int) var10))) {
				var1.getPlayer().addChatMessage("successfully made a dungeon! look around.");
			}
		}
	}

	public static void genDun1(World var0, Random var1, int var2, int var3, BiomeGenBase biome) {
		if (var1.nextInt(Orareity) == 0) {
			int var4 = var2;
			int var5 = var3;
			int var6 = var1.nextInt(45) + 10;
			int var7;
			for (var7 = 0; var7 < 10; ++var7) {
				var6 = var0.getTopSolidOrLiquidBlock(var4, var5) - (var1.nextInt(30) + 20);
				if (var6 > 10 && var6 < 55) {
					break;
				}
			}
			var7 = 1;
			int var8 = 1;
			int var9;
			int var10;
			int var11;
			for (var9 = 0; var9 < 100 && (var7 * var8 < OMinSquareBlocks || var7 < 10 || var8 < 10); ++var9) {
				var10 = var1.nextInt(OmaxSize - OminSize) + OminSize + 1;
				var11 = var1.nextInt(OmaxSize - OminSize) + OminSize + 1;
				var7 = var1.nextInt(var10 - OminSize) + OminSize;
				var8 = var1.nextInt(var11 - OminSize) + OminSize;
			}
			var11 = var7 * var8 / (var1.nextInt(50) + 50);
			if (Odebug) {
				var6 = 100;
			}
			if (var9 < 100) {
				makeDun1(var0, var1, var7, var8, Oheight, var4, var6, var5, var11, biome);
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
		if (var1.nextInt(var6) == 0 && var0.getBlockId(var2 - 1, var3, var4) == 0) {
			setVines(var0, var2 - 1, var3, var4, 8);
		}
		if (var1.nextInt(var6) == 0 && var0.getBlockId(var2 + 1, var3, var4) == 0) {
			setVines(var0, var2 + 1, var3, var4, 2);
		}
		if (var1.nextInt(var6) == 0 && var0.getBlockId(var2, var3, var4 - 1) == 0) {
			setVines(var0, var2, var3, var4 - 1, 1);
		}
		if (var1.nextInt(var6) == 0 && var0.getBlockId(var2, var3, var4 + 1) == 0) {
			setVines(var0, var2, var3, var4 + 1, 4);
		}
		if (var1.nextInt(var6) == 0 && var0.getBlockId(var2, var3 - 1, var4) == 0) {
			setVines(var0, var2, var3 - 1, var4 + 1);
		}
	}

	public static boolean isEmpty(World var0, int var1, int var2, int var3) {
		int var4 = var0.getBlockId(var1, var2, var3);
		return var4 == 0 || var4 == Block.vine.blockID || var4 == Block.web.blockID || var4 == modDimTorch.blockID;
	}

	private static void addLoot() {
		int var1 = 1;
		int var2 = 1;
		int var3 = 1;
		int var4 = 1;
		int var5 = 30;
		int var6 = 0;
		int var7 = 1;
		double var8 = 1.0D;
		double var10 = 1.0D;
		if (!OadditionalItems.equals("")) {
			String[] var12 = OadditionalItems.split("_");
			for (int var13 = 0; var13 < var12.length; ++var13) {
				String[] var14 = var12[var13].split("-");
				try {
					switch (var14.length) {
					case 1:
					case 2:
						System.err.println("cannot add id" + Integer.parseInt(var14[0]) + "not enough arguments!");
						break;
					case 3:
						var1 = Integer.parseInt(var14[0]);
						var8 = Double.parseDouble(var14[1]);
						var10 = Double.parseDouble(var14[2]);
						break;
					case 4:
						var1 = Integer.parseInt(var14[0]);
						var8 = Double.parseDouble(var14[1]);
						var10 = Double.parseDouble(var14[2]);
						var2 = Integer.parseInt(var14[3]);
						break;
					case 5:
						var1 = Integer.parseInt(var14[0]);
						var8 = Double.parseDouble(var14[1]);
						var10 = Double.parseDouble(var14[2]);
						var2 = Integer.parseInt(var14[3]);
						var3 = Integer.parseInt(var14[4]);
						break;
					case 6:
						var1 = Integer.parseInt(var14[0]);
						var8 = Double.parseDouble(var14[1]);
						var10 = Double.parseDouble(var14[2]);
						var2 = Integer.parseInt(var14[3]);
						var3 = Integer.parseInt(var14[4]);
						var4 = Integer.parseInt(var14[5]);
						break;
					case 7:
						var1 = Integer.parseInt(var14[0]);
						var8 = Double.parseDouble(var14[1]);
						var10 = Double.parseDouble(var14[2]);
						var2 = Integer.parseInt(var14[3]);
						var3 = Integer.parseInt(var14[4]);
						var4 = Integer.parseInt(var14[5]);
						var6 = Integer.parseInt(var14[6]);
						break;
					case 8:
						var1 = Integer.parseInt(var14[0]);
						var8 = Double.parseDouble(var14[1]);
						var10 = Double.parseDouble(var14[2]);
						var2 = Integer.parseInt(var14[3]);
						var3 = Integer.parseInt(var14[4]);
						var4 = Integer.parseInt(var14[5]);
						var6 = Integer.parseInt(var14[6]);
						var5 = Integer.parseInt(var14[7]);
						break;
					case 9:
						var1 = Integer.parseInt(var14[0]);
						var8 = Double.parseDouble(var14[1]);
						var10 = Double.parseDouble(var14[2]);
						var2 = Integer.parseInt(var14[3]);
						var3 = Integer.parseInt(var14[4]);
						var4 = Integer.parseInt(var14[5]);
						var6 = Integer.parseInt(var14[6]);
						var5 = Integer.parseInt(var14[7]);
						var7 = Integer.parseInt(var14[8]);
					}
				} catch (NumberFormatException var18) {
					System.err.println("New Dungeons error: " + var18.toString());
				}
				Block var15 = Block.blocksList[var1];
				loot.add(new DunLootItem(var15, var2, var3, var8, var10, var4, var5, var6, var7, var7));
				Item var20 = Item.itemsList[var1];
				loot.add(new DunLootItem(var20, var2, var3, var8, var10, var4, var5, var6, var7, var7));
			}
		}
		loot.add(new DunLootItem(Item.diamond, 6, 1, 20.0D, 15.0D, 300));
		loot.add(new DunLootItem(Block.torchWood, 15, 1, 3.0D, 3.0D, 30));
		loot.add(new DunLootItem(Block.dirt, 64, 15, 13.0D, 1.0D, -1));
		loot.add(new DunLootItem(Item.appleRed, 3, 1, 5.0D, 5.0D, 50));
		loot.add(new DunLootItem(Item.bone, 5, 1, 7.0D, 2.0D, 0));
		loot.add(new DunLootItem(Item.arrow, 16, 1, 10.0D, 7.0D, 50));
		loot.add(new DunLootItem(Item.bow, 1, 1, 20.0D, 15.0D, 100, 20, 20));
		loot.add(new DunLootItem(Item.painting, 2, 1, 25.0D, 3.0D, 30));
		loot.add(new DunLootItem(Block.planks, 5, 1, 4.0D, 10.0D, 40, 0, 0, 0, 3));
		loot.add(new DunLootItem(Block.wood, 5, 1, 16.0D, 15.0D, 60, 0, 0, 0, 3));
		loot.add(new DunLootItem(Item.leather, 5, 1, 10.0D, 8.0D, 20));
		loot.add(new DunLootItem(Item.legsLeather, 1, 1, 20.0D, 15.0D, 50));
		loot.add(new DunLootItem(Item.helmetLeather, 1, 1, 20.0D, 15.0D, 50));
		loot.add(new DunLootItem(Item.bootsLeather, 1, 1, 20.0D, 15.0D, 50));
		loot.add(new DunLootItem(Item.plateLeather, 1, 1, 20.0D, 15.0D, 50));
		loot.add(new DunLootItem(Item.legsIron, 1, 1, 35.0D, 30.0D, 250, 15, 15));
		loot.add(new DunLootItem(Item.helmetIron, 1, 1, 35.0D, 30.0D, 250, 15, 15));
		loot.add(new DunLootItem(Item.bootsIron, 1, 1, 35.0D, 30.0D, 250, 15, 15));
		loot.add(new DunLootItem(Item.plateIron, 1, 1, 35.0D, 30.0D, 250, 15, 15));
		loot.add(new DunLootItem(Item.bread, 5, 1, 7.0D, 5.0D, 50));
		loot.add(new DunLootItem(Item.bucketEmpty, 1, 1, 10.0D, 2.0D, 20));
		loot.add(new DunLootItem(Item.ingotIron, 10, 1, 20.0D, 7.0D, 150));
		loot.add(new DunLootItem(Block.oreIron, 10, 1, 10.0D, 5.0D, 100));
		loot.add(new DunLootItem(Item.cake, 1, 1, 40.0D, 20.0D, 60));
		loot.add(new DunLootItem(Item.beefRaw, 1, 1, 5.0D, 4.0D, 5));
		loot.add(new DunLootItem(Item.beefCooked, 1, 1, 6.0D, 8.0D, 5));
		loot.add(new DunLootItem(Block.melon, 3, 1, 17.0D, 10.0D, 30));
		loot.add(new DunLootItem(Item.melonSeeds, 5, 1, 8.0D, 7.0D, 30));
		loot.add(new DunLootItem(Block.pumpkin, 3, 1, 13.0D, 10.0D, 30));
		loot.add(new DunLootItem(Item.pumpkinSeeds, 5, 1, 8.0D, 7.0D, 30));
		loot.add(new DunLootItem(Block.cobblestone, 64, 15, 14.0D, 2.0D, 0));
		loot.add(new DunLootItem(Item.legsGold, 1, 1, 80.0D, 25.0D, 350, 17, 6));
		loot.add(new DunLootItem(Item.helmetGold, 1, 1, 80.0D, 25.0D, 350, 17, 6));
		loot.add(new DunLootItem(Item.bootsGold, 1, 1, 80.0D, 25.0D, 350, 17, 6));
		loot.add(new DunLootItem(Item.plateGold, 1, 1, 80.0D, 25.0D, 350, 17, 6));
		loot.add(new DunLootItem(Item.pickaxeWood, 1, 1, 20.0D, 2.0D, 70));
		loot.add(new DunLootItem(Item.pickaxeStone, 1, 1, 20.0D, 5.0D, 70));
		loot.add(new DunLootItem(Item.pickaxeIron, 1, 1, 50.0D, 15.0D, 100, 30, 20));
		loot.add(new DunLootItem(Item.pickaxeDiamond, 1, 1, 70.0D, 60.0D, 400, 50, 25));
		loot.add(new DunLootItem(Item.swordWood, 1, 1, 20.0D, 3.0D, 70));
		loot.add(new DunLootItem(Item.swordStone, 1, 1, 20.0D, 6.0D, 70));
		loot.add(new DunLootItem(Item.swordIron, 1, 1, 50.0D, 17.0D, 100, 30, 20));
		loot.add(new DunLootItem(Item.swordDiamond, 1, 1, 70.0D, 61.0D, 400, 50, 25));
		loot.add(new DunLootItem(Item.appleGold, 1, 1, 1000.0D, 100.0D, 200));
		loot.add(new DunLootItem(Item.enderPearl, 6, 1, 46.0D, 80.0D, 200));
		loot.add(new DunLootItem(Item.eyeOfEnder, 3, 1, 57.0D, 130.0D, 300));
		loot.add(new DunLootItem(Item.redstone, 64, 32, 60.0D, 3.0D, 15));
		loot.add(new DunLootItem(Block.tnt, 3, 1, 30.0D, 36.0D, 300));
		loot.add(new DunLootItem(Item.saddle, 1, 1, 10.0D, 60.0D, 30));
		loot.add(new DunLootItem(Item.rottenFlesh, 4, 1, 2.0D, 10.0D, 0));
		loot.add(new DunLootItem(Item.coal, 7, 1, 15.0D, 18.0D, 20));
		loot.add(new DunLootItem(Item.stick, 7, 1, 3.0D, 2.0D, 0));
		loot.add(new DunLootItem(Item.flintAndSteel, 1, 1, 20.0D, 100.0D, 200));
		loot.add(new DunLootItem(Item.flint, 7, 1, 15.0D, 5.0D, 13));
		loot.add(new DunLootItem(Block.obsidian, 3, 1, 34.0D, 40.0D, 200));
		loot.add(new DunLootItem(Item.emerald, 3, 1, 40.0D, 50.0D, 200));
		for (int var19 = 0; var19 <= 10; ++var19) {
			loot.add(new DunLootItem(Item.itemsList[Item.record13.itemID + var19], 1, 1, 1000 + var19 * 2, 50.0D, 0));
		}
		loot.add(new DunLootItem(Item.itemsList[Item.potion.itemID], 1, 1, 27.0D, 36.0D, 100, 0, 99999999));
		potLoot.add(new DunLootItem(Item.glowstone, 4, 1, 1.0D, 1.0D, 0));
		potLoot.add(new DunLootItem(Item.redstone, 4, 1, 1.0D, 1.0D, 0));
		potLoot.add(new DunLootItem(Item.netherStalkSeeds, 4, 1, 1.0D, 1.0D, 0));
		potLoot.add(new DunLootItem(Item.sugar, 4, 1, 1.0D, 1.0D, 0));
		potLoot.add(new DunLootItem(Item.gunpowder, 4, 1, 1.0D, 1.0D, 0));
		potLoot.add(new DunLootItem(Item.spiderEye, 4, 1, 1.0D, 1.0D, 0));
		potLoot.add(new DunLootItem(Item.fermentedSpiderEye, 4, 1, 5.0D, 4.0D, 0));
		potLoot.add(new DunLootItem(Item.goldNugget, 4, 1, 1.0D, 1.0D, 0));
		potLoot.add(new DunLootItem(Item.melon, 4, 1, 1.0D, 1.0D, 0, 0, 0, 0, 1));
		potLoot.add(new DunLootItem(Item.ghastTear, 4, 1, 7.0D, 1.0D, 0));
		potLoot.add(new DunLootItem(Item.magmaCream, 4, 1, 3.0D, 1.0D, 0));
		potLoot.add(new DunLootItem(Item.glassBottle, 10, 1, 1.0D, 1.0D, 0));
		disLoot.add(new DunLootItem(Item.arrow, 5, 1, 1.0D, 1.0D, 10));
		disLoot.add(new DunLootItem(Item.snowball, 5, 1, 10.0D, 15.0D, 10));
		disLoot.add(new DunLootPotion(Item.itemsList[Item.potion.itemID], 6, 2, 1.0D, 0.0D, 0, 16420));
		disLoot.add(new DunLootPotion(Item.itemsList[Item.potion.itemID], 4, 2, 1.0D, 0.0D, 0, 16396));
		disLoot.add(new DunLootPotion(Item.itemsList[Item.potion.itemID], 5, 2, 1.0D, 0.0D, 0, 16428));
		disLoot.add(new DunLootPotion(Item.itemsList[Item.potion.itemID], 3, 2, 1.0D, 0.0D, 0, 16456));
		disLoot.add(new DunLootPotion(Item.itemsList[Item.potion.itemID], 3, 2, 1.0D, 0.0D, 0, 16458));
	}

	private static void genDun5(World var1, Random var2, int var3, int var4) {
		for (int var5 = 0; var5 < 10; ++var5) {
			if (var2.nextInt(2) == 0) {
				int var6 = var2.nextInt(16) + 1 + var3;
				int var7 = var2.nextInt(16) + 1 + var4;
				int var8;
				do {
					do {
						var8 = var2.nextInt(128);
						try {
							var1.getTopSolidOrLiquidBlock(var6, var7);
						} catch (Exception var12) {
							return;
						}
					} while (var1.getTopSolidOrLiquidBlock(var6, var7) == var8);
				} while (var1.getBlockId(var6, var8 - 1, var7) == Block.waterStill.blockID);
				if (var1.getBlockId(var6, var8, var7) == 0 && var1.getBlockId(var6, var8 - 1, var7) != 0) {
					var1.setBlock(var6, var8, var7, Block.chest.blockID);
					ArrayList var9 = new ArrayList();
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
				for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
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
		int var8 = Block.stoneBrick.blockID;
		byte var9 = 0;
		boolean var10 = false;
		if (var1.nextInt(10) == 0 && var5) {
			var0.setBlock(var2, var3, var4, 0);
			genStone(var0, var1, biome, var2, var3 + 1, var4);
		} else {
			while (!var10) {
				int var6 = var1.nextInt(7);
				int var7 = var1.nextInt(100);
				if (var6 == 0 && var7 < 50) {
					var8 = Block.stoneBrick.blockID;
					var10 = true;
				}
				if (var6 == 1 && var7 < 25) {
					var8 = Block.stoneBrick.blockID;
					var9 = 1;
					var10 = true;
				}
				if (var6 == 2 && var7 < 25) {
					var8 = Block.stoneBrick.blockID;
					var9 = 2;
					var10 = true;
				}
				if (var6 == 3 && var7 < 10) {
					var8 = Block.cobblestoneMossy.blockID;
					var10 = true;
				}
				if (var6 == 4 && var7 < 6) {
					var8 = Block.cobblestone.blockID;
					var10 = true;
				}
				if (var6 == 5 && var7 < 3) {
					var8 = Block.stone.blockID;
					var10 = true;
				}
				if (var6 == 6 && var7 < 7) {
					var8 = Block.stoneBrick.blockID;
					var9 = 3;
					var10 = true;
				}
			}
			var0.setBlock(var2, var3, var4, var8, var9, 3);
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
					if (var0.getBlockId(var5 + var14, var6 + var16, var7 + var15) != Block.chest.blockID) {
						var0.setBlock(var5 + var14, var6 + var16, var7 + var15, 0);
					}
				}
			}
		}
		for (var14 = 0; var14 < var2 + 1; ++var14) {
			for (var15 = 0; var15 < var3 + 1; ++var15) {
				genStone(var0, var1, biome, var5 + var14, var6, var7 + var15);
				if (!Odebug) {
					genStone(var0, var1, biome, var5 + var14, var6 + var4 - 1, var7 + var15, true);
				}
				if (var14 == 0 || var14 == var2 || var15 == 0 || var15 == var3) {
					for (var16 = 1; var16 < var4 - 1; ++var16) {
						genStone(var0, var1, biome, var5 + var14, var6 + var16, var7 + var15);
					}
				}
			}
		}
		ArrayList var37 = new ArrayList();
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
			var18 = var1.nextInt(10 - OminRoomSize + 1) + OminRoomSize;
			var18 = var1.nextInt(var18 - OminRoomSize + 1) + OminRoomSize;
			boolean var19 = true;
			var20 = true;
			var21 = 0;
			while (true) {
				if (var21 < var18) {
					if (var0.getBlockMaterial(var5 + var16 + var21, var6 + 1, var7 + var17) != Material.rock && var0.getBlockMaterial(var5 + var16 - var21, var6 + 1, var7 + var17) != Material.rock) {
						++var21;
						continue;
					}
					var20 = false;
				}
				for (var21 = 0; var21 < var18; ++var21) {
					if (var0.getBlockMaterial(var5 + var16, var6 + 1, var7 + var17 + var21) == Material.rock || var0.getBlockMaterial(var5 + var16, var6 + 1, var7 + var17 - var21) == Material.rock) {
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
					for (var25 = 1; var25 < var2 && var0.getBlockMaterial(var5 + var16 + var25, var6 + 1, var7 + var17) != Material.rock; ++var25) {
						++var21;
						for (var26 = 1; var26 < var4 - 1; ++var26) {
							genStone(var0, var1, biome, var5 + var16 + var25, var6 + var26, var7 + var17);
						}
					}
					for (var25 = 1; var25 < var2 && var0.getBlockMaterial(var5 + var16 - var25, var6 + 1, var7 + var17) != Material.rock; ++var25) {
						++var22;
						for (var26 = 1; var26 < var4 - 1; ++var26) {
							genStone(var0, var1, biome, var5 + var16 - var25, var6 + var26, var7 + var17);
						}
					}
				}
				if (var20) {
					for (var25 = 1; var25 < var3 && var0.getBlockMaterial(var5 + var16, var6 + 1, var7 + var17 + var25) != Material.rock; ++var25) {
						++var23;
						for (var26 = 1; var26 < var4 - 1; ++var26) {
							genStone(var0, var1, biome, var5 + var16, var6 + var26, var7 + var17 + var25);
						}
					}
					for (var25 = 1; var25 < var3 && var0.getBlockMaterial(var5 + var16, var6 + 1, var7 + var17 - var25) != Material.rock; ++var25) {
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
			var17 = ((Dun1Node) var37.get(var16)).wp;
			var18 = ((Dun1Node) var37.get(var16)).wn;
			var40 = ((Dun1Node) var37.get(var16)).lp;
			var41 = ((Dun1Node) var37.get(var16)).ln;
			var21 = ((Dun1Node) var37.get(var16)).x;
			var22 = ((Dun1Node) var37.get(var16)).z;
			var23 = ((Dun1Node) var37.get(var16)).y;
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
					var29 = var0.getBlockMaterial(var21 + var25 + 1, var23, var22) == Material.rock;
					var30 = var0.getBlockMaterial(var21 + var25 - 1, var23, var22) == Material.rock;
					var31 = var0.getBlockMaterial(var21 + var25, var23, var22 + 1) == Material.rock;
					var32 = var0.getBlockMaterial(var21 + var25, var23, var22 - 1) == Material.rock;
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
						var0.setBlock(var21 + var25, var23, var22, 0);
						var0.setBlock(var21 + var25, var23 + 1, var22, 0);
					}
				}
			}
			for (var24 = var1.nextInt(var15) + 1; var24 > 0; --var24) {
				var1.nextInt(var17);
				var26 = var1.nextInt(var18);
				var1.nextInt(var40);
				var1.nextInt(var41);
				if (var18 > 2) {
					var29 = var0.getBlockMaterial(var21 - var26 + 1, var23, var22) == Material.rock;
					var30 = var0.getBlockMaterial(var21 - var26 - 1, var23, var22) == Material.rock;
					var31 = var0.getBlockMaterial(var21 - var26, var23, var22 + 1) == Material.rock;
					var32 = var0.getBlockMaterial(var21 - var26, var23, var22 - 1) == Material.rock;
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
						var0.setBlock(var21 - var26, var23, var22, 0);
						var0.setBlock(var21 - var26, var23 + 1, var22, 0);
					}
				}
			}
			for (var24 = var1.nextInt(var15) + 1; var24 > 0; --var24) {
				var1.nextInt(var17);
				var1.nextInt(var18);
				var27 = var1.nextInt(var40);
				var1.nextInt(var41);
				if (var40 > 2) {
					var29 = var0.getBlockMaterial(var21 + 1, var23, var22 + var27) == Material.rock;
					var30 = var0.getBlockMaterial(var21 - 1, var23, var22 + var27) == Material.rock;
					var31 = var0.getBlockMaterial(var21, var23, var22 + 1 + var27) == Material.rock;
					var32 = var0.getBlockMaterial(var21, var23, var22 - 1 + var27) == Material.rock;
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
						var0.setBlock(var21, var23, var22 + var27, 0);
						var0.setBlock(var21, var23 + 1, var22 + var27, 0);
					}
				}
			}
			for (var24 = var1.nextInt(var15) + 1; var24 > 0; --var24) {
				var1.nextInt(var17);
				var1.nextInt(var18);
				var1.nextInt(var40);
				var28 = var1.nextInt(var41);
				if (var41 > 2) {
					var29 = var0.getBlockMaterial(var21 + 1, var23, var22 - var28) == Material.rock;
					var30 = var0.getBlockMaterial(var21 - 1, var23, var22 - var28) == Material.rock;
					var31 = var0.getBlockMaterial(var21, var23, var22 + 1 - var28) == Material.rock;
					var32 = var0.getBlockMaterial(var21, var23, var22 - 1 - var28) == Material.rock;
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
						var0.setBlock(var21, var23, var22 - var28, 0);
						var0.setBlock(var21, var23 + 1, var22 - var28, 0);
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
					if (var0.getBlockMaterial(var5 + var18 + var21, var6 + 1, var7 + var40 + var22) != Material.air) {
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
								var0.setBlock(var5 + var18 + var48, var28, var7 + var40 + var49, 0);
							}
						}
					}
					boolean var52 = false;
					if (var1.nextInt(3) == 0) {
						var52 = true;
					}
					if (!var52 && var1.nextInt(2) == 0) {
						var0.setBlock(var5 + var18, var6, var7 + var40, Block.waterStill.blockID);
						if (var1.nextInt(5) != 0) {
							var0.setBlockToAir(var5 + var18, var6 - 1, var7 + var40);
						}
					}
					for (var48 = 0; var48 < var21 * 4; ++var48) {
						var0.setBlock(var5 + var18 + var23, var6 + 1 + var25, var7 + var40 + var24, BlockHalfSlab.stoneSingleSlab.blockID, var43, 3);
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
					var0.setBlock(var5 + var18 + var48, var58, var7 + var40 + var49, Block.mobSpawner.blockID);
					TileEntityMobSpawner var57 = (TileEntityMobSpawner) var0.getBlockTileEntity(var5 + var18 + var48, var58, var7 + var40 + var49);
					if (var57 != null) {
						var57.getSpawnerLogic().setMobID(pickMobSpawner(var1));
					} else {
						System.err.println("Failed to fetch mob spawner entity at (" + (var5 + var18 + var48) + ", " + var58 + ", " + (var7 + var40 + var49) + ")");
					}
				}
				break;
			}
		}
		ArrayList var38 = new ArrayList();
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
			if (var0.getBlockMaterial(var5 + var41, var6 + 1, var7 + var21) == Material.air) {
				for (var23 = -1; var23 <= 1; ++var23) {
					for (var24 = -1; var24 <= 1; ++var24) {
						if (var0.getBlockMaterial(var5 + var41 + var23, var6 + 1, var7 + var21 + var24) == Material.rock && Math.abs(var23) == 1 ^ Math.abs(var24) == 1) {
							var44 = true;
							break;
						}
					}
					if (var44) {
						break;
					}
				}
			}
			boolean var42 = var0.getBlockMaterial(var5 + var41 + 1, var6 + 1, var7 + var21) == Material.rock;
			var46 = var0.getBlockMaterial(var5 + var41 - 1, var6 + 1, var7 + var21) == Material.rock;
			var50 = var0.getBlockMaterial(var5 + var41, var6 + 1, var7 + var21 + 1) == Material.rock;
			var51 = var0.getBlockMaterial(var5 + var41, var6 + 1, var7 + var21 - 1) == Material.rock;
			if ((var42 && var46) ^ (var50 && var51)) {
				var44 = false;
			}
			if (var44) {
				var0.setBlock(var5 + var41, var6 + 1, var7 + var21, Block.chest.blockID);
				var38.add(new DunChest(var5 + var41, var6 + 1, var7 + var21));
			}
		}
		for (var40 = 0; var40 < var8 / 1.1D; ++var40) {
			var41 = var1.nextInt(var2 - 2) + 1;
			var21 = var1.nextInt(var3 - 2) + 1;
			var44 = false;
			if (var0.getBlockMaterial(var5 + var41, var6 + 1, var7 + var21) == Material.air) {
				for (var23 = -1; var23 <= 1; ++var23) {
					for (var24 = -1; var24 <= 1; ++var24) {
						if (var0.getBlockMaterial(var5 + var41 + var23, var6 + 2, var7 + var21 + var24) == Material.rock && Math.abs(var23) == 1 ^ Math.abs(var24) == 1) {
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
				var0.setBlock(var5 + var41, var6 + 2, var7 + var21, modDimTorch.blockID);
			}
		}
		for (var40 = 0; var40 < var8 * 6; ++var40) {
			var41 = var1.nextInt(var2 - 2) + 1;
			var21 = var1.nextInt(var3 - 2) + 1;
			var44 = false;
			if (var0.getBlockMaterial(var5 + var41, var6 + 2, var7 + var21) == Material.rock) {
				var44 = true;
			}
			if (var44) {
				var0.setBlock(var5 + var41, var6 + 2, var7 + var21, Block.fenceIron.blockID);
			}
		}
		label1349: for (var40 = 0; var40 < var8 * 0.2D; ++var40) {
			var41 = var1.nextInt(var2 - 2) + 1;
			var21 = var1.nextInt(var3 - 2) + 1;
			var44 = true;
			for (var23 = -2; var23 < 3; ++var23) {
				for (var24 = -2; var24 < 3; ++var24) {
					if (var0.getBlockId(var5 + var41 + var23, var6 + 1, var7 + var21 + var24) != 0) {
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
						var0.setBlock(var5 + var41 + var23, var6, var7 + var21 + var24, Block.grass.blockID);
					}
				}
				ArrayList var45 = new ArrayList();
				var24 = var1.nextInt(5) + 3;
				for (var25 = 0; var25 < var24; ++var25) {
					var26 = var1.nextInt(6);
					if (var26 < 3) {
						var45.add(Integer.valueOf(Block.brewingStand.blockID));
					} else if (var26 < 5) {
						var45.add(Integer.valueOf(Block.cauldron.blockID));
					} else if (var26 == 5) {
						var45.add(Integer.valueOf(Block.chest.blockID));
					}
				}
				ArrayList var53 = new ArrayList();
				for (var26 = 0; var26 < var45.size(); ++var26) {
					var27 = ((Integer) var45.get(var26)).intValue();
					var28 = var1.nextInt(3) - 1;
					var48 = var1.nextInt(3) - 1;
					if (var0.getBlockId(var5 + var41 + var28, var6 + 1, var7 + var21 + var48) == 0) {
						var0.setBlock(var5 + var41 + var28, var6 + 1, var7 + var21 + var48, var27);
						if (var27 == Block.chest.blockID) {
							var53.add(new DunChest(var5 + var41 + var28, var6 + 1, var7 + var21 + var48));
						}
						if (var27 == Block.cauldron.blockID) {
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
						var49 = ((DunChest) var53.get(var26)).x;
						var58 = ((DunChest) var53.get(var26)).y;
						int var55 = ((DunChest) var53.get(var26)).z;
						TileEntityChest var63 = null;
						if (var0.getBlockId(var49, var58, var55) == Block.chest.blockID) {
							var63 = (TileEntityChest) var0.getBlockTileEntity(var49, var58, var55);
						}
						if (var63 == null) {
							break;
						}
						for (int var34 = 0; var34 < 1; ++var34) {
							ItemStack var35 = ((DunLootItem) potLoot.get(var48)).getItemStack(var1);
							if (var35 != null) {
								var63.setInventorySlotContents(var1.nextInt(var63.getSizeInventory()), var35);
							}
						}
					}
					var28 = ((DunChest) var53.get(var26)).x;
					var48 = ((DunChest) var53.get(var26)).y;
					var49 = ((DunChest) var53.get(var26)).z;
					TileEntityChest var56 = null;
					if (var0.getBlockId(var28, var48, var49) == Block.chest.blockID) {
						var56 = (TileEntityChest) var0.getBlockTileEntity(var28, var48, var49);
					}
					if (var56 != null) {
						var56.setInventorySlotContents(var1.nextInt(var56.getSizeInventory()), new ItemStack(Item.glassBottle, var1.nextInt(7) + 2));
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
								var0.setBlock(var5 + var41 + var25, var6 + var26, var7 + var21, Block.obsidian.blockID);
							} else {
								var0.setBlock(var5 + var41, var6 + var26, var7 + var21 + var25, Block.obsidian.blockID);
							}
						}
					}
				}
				if (var24 == 14) {
					for (var25 = 0; var25 < 2; ++var25) {
						for (var26 = 1; var26 < 4; ++var26) {
							if (var23 == 1) {
								var0.setBlock(var5 + var41 + var25, var6 + var26, var7 + var21, Block.portal.blockID);
							} else {
								var0.setBlock(var5 + var41, var6 + var26, var7 + var21 + var25, Block.portal.blockID);
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
					if (var0.getBlockId(var5 + var41 + var23, var6 + 1, var7 + var21 + var24) != 0) {
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
						var0.setBlock(var5 + var41 + var23, var6, var7 + var21 + var24, Block.planks.blockID);
						if ((Math.abs(var23) == 2 || Math.abs(var24) == 2) && var1.nextBoolean()) {
							var25 = var1.nextInt(3);
							for (var26 = 0; var26 < var25; ++var26) {
								var0.setBlock(var5 + var41 + var23, var6 + 1 + var26, var7 + var21 + var24, Block.bookShelf.blockID);
								genVine(var0, var1, var5 + var41 + var23, var6 + 1 + var26, var7 + var21 + var24, biome);
							}
						}
					}
				}
				var0.setBlock(var5 + var41, var6 + 1, var7 + var21, Block.enchantmentTable.blockID);
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
						if (var0.getBlockMaterial(var5 + var41 + var23, var6 + 1, var7 + var21 + var24) == Material.rock) {
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
								var0.setBlock(var5 + var41 + var23, var6 + 2, var7 + var21 + var24, modDimTorch.blockID);
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
						if (var0.getBlockMaterial(var5 + var41 + var23, var6 + 1, var7 + var21 + var24) == Material.rock) {
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
					var0.setBlock(var5 + var41, var6 + 1, var7 + var21, Block.mobSpawner.blockID);
					TileEntityMobSpawner var59 = (TileEntityMobSpawner) var0.getBlockTileEntity(var5 + var41, var6 + 1, var7 + var21);
					if (var59 != null) {
						String var60 = pickMobSpawner(var1);
						var59.getSpawnerLogic().setMobID(var60);
						if (var60 == "Creeper") {
							var13 += 10;
						}
					} else {
						System.err.println("Failed to fetch mob spawner entity at (" + (var5 + var41) + ", " + (var6 + 1) + ", " + (var7 + var21) + ")");
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
						if (var0.getBlockMaterial(var5 + var22 + var26, var24 + var28, var7 + var23 + var27) == Material.rock) {
							++var25;
						}
					}
				}
			}
			if (var25 > 1 && var0.getBlockId(var5 + var22, var24, var7 + var23) == 0) {
				var13 += 2;
				var0.setBlock(var5 + var22, var24, var7 + var23, Block.web.blockID);
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
						if (var0.getBlockId(var5 + var22 + var25, var6 + 1, var7 + var23 + var26) == Block.mobSpawner.blockID) {
							var46 = false;
							break;
						}
					}
					if (var46) {
						++var25;
						continue;
					}
				}
				if (var0.getBlockMaterial(var5 + var22, var6 + 2, var7 + var23) == Material.rock) {
					var46 = false;
				}
				if (var46) {
					var13 += 4;
					if (!OidCompatibility) {
						var0.setBlock(var5 + var22, var6 + 1, var7 + var23, pressurePlatetest.blockID);
					} else {
						var0.setBlock(var5 + var22, var6 + 1, var7 + var23, Block.pressurePlateStone.blockID, -1, 3);
					}
					if (var1.nextInt(3) != 1) {
						var0.setBlock(var5 + var22, var6, var7 + var23, Block.gravel.blockID);
					}
					var0.setBlock(var5 + var22, var6 - 1, var7 + var23, Block.tnt.blockID);
					var0.setBlock(var5 + var22, var6 - 2, var7 + var23, Block.stone.blockID);
					var50 = true;
					for (var26 = -1; var26 < 2; ++var26) {
						for (var27 = -1; var27 < 2; ++var27) {
							if (var0.getBlockMaterial(var5 + var22 + var26, var6 - 3, var7 + var23 + var27) == Material.air) {
								var50 = false;
							}
						}
					}
					for (var26 = -2; var26 < 3; ++var26) {
						for (var27 = -2; var27 < 3; ++var27) {
							if ((var26 == -2 || var26 == 2 || var27 == -2 || var27 == 2) && var0.getBlockMaterial(var5 + var22 + var26, var6 - 2, var7 + var23 + var27) == Material.air) {
								var50 = false;
							}
						}
					}
					if (var1.nextInt(5) == 1 && var50) {
						for (var26 = -1; var26 < 2; ++var26) {
							for (var27 = -1; var27 < 2; ++var27) {
								var0.setBlock(var5 + var22 + var26, var6 - 3, var7 + var23 + var27, Block.lavaStill.blockID);
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
			if (var1.nextBoolean() && var0.getBlockId(var5 + var22, var6 + 1, var7 + var23) == 0) {
				var46 = false;
				var50 = false;
				var26 = 0;
				var27 = 0;
				for (var28 = 0; var28 < 200; ++var28) {
					if (var0.getBlockId(var5 + var22, var6 + 1, var7 + var23 + var28) != 0) {
						if (var0.getBlockMaterial(var5 + var22, var6 + 1, var7 + var23 + var28) == Material.rock) {
							var46 = true;
							var26 = var28 - 1;
						}
						break;
					}
				}
				if (var46) {
					for (var28 = 0; var28 < 200; ++var28) {
						if (var0.getBlockId(var5 + var22, var6 + 1, var7 + var23 - var28) != 0) {
							if (var0.getBlockMaterial(var5 + var22, var6 + 1, var7 + var23 - var28) == Material.rock) {
								var50 = true;
								var27 = var28 - 1;
							}
							break;
						}
					}
				}
				if (var50 && var26 + var27 > 3) {
					var0.setBlock(var5 + var22, var6 + 1, var7 + var23 + var26, modTripWireSource.blockID, 2, 2);
					var0.setBlock(var5 + var22, var6 + 1, var7 + var23 - var27, modTripWireSource.blockID, 0, 2);
					for (var28 = -var27 + 1; var28 < var26; ++var28) {
						var0.setBlock(var5 + var22, var6 + 1, var7 + var23 + var28, modTripWire.blockID, 4, 2);
					}
					if (var1.nextBoolean()) {
						var0.setBlock(var5 + var22, var6 + 2, var7 + var23 - var27 - 1, Block.dispenser.blockID);
						var0.setBlockMetadataWithNotify(var5 + var22, var6 + 2, var7 + var23 - var27 - 1, 3, 3);
						var62 = null;
						if (var0.getBlockId(var5 + var22, var6 + 2, var7 + var23 - var27 - 1) == Block.dispenser.blockID) {
							var62 = (TileEntityDispenser) var0.getBlockTileEntity(var5 + var22, var6 + 2, var7 + var23 - var27 - 1);
						}
						if (var62 == null) {
							break;
						}
						var48 = var1.nextInt(5) + 1;
						for (var49 = 0; var49 < var48; ++var49) {
							var58 = var1.nextInt(disLoot.size());
							if (var1.nextInt((int) ((DunLootItem) disLoot.get(var58)).rareity) == 0) {
								var62.setInventorySlotContents(var1.nextInt(var62.getSizeInventory()), ((DunLootItem) disLoot.get(var58)).getItemStack(var1));
							} else {
								--var49;
							}
						}
					} else {
						var0.setBlock(var5 + var22, var6 + 2, var7 + var23 + var26 + 1, Block.dispenser.blockID);
						var0.setBlockMetadataWithNotify(var5 + var22, var6 + 2, var7 + var23 + var26 + 1, 2, 3);
						var62 = null;
						if (var0.getBlockId(var5 + var22, var6 + 2, var7 + var23 + var26 + 1) == Block.dispenser.blockID) {
							var62 = (TileEntityDispenser) var0.getBlockTileEntity(var5 + var22, var6 + 2, var7 + var23 + var26 + 1);
						}
						if (var62 == null) {
							break;
						}
						var48 = var1.nextInt(5) + 1;
						for (var49 = 0; var49 < var48; ++var49) {
							var58 = var1.nextInt(disLoot.size());
							if (var1.nextInt((int) ((DunLootItem) disLoot.get(var58)).rareity) == 0) {
								var62.setInventorySlotContents(var1.nextInt(var62.getSizeInventory()), ((DunLootItem) disLoot.get(var58)).getItemStack(var1));
							} else {
								--var49;
							}
						}
					}
				}
			} else if (var0.getBlockId(var5 + var22, var6 + 1, var7 + var23) == 0) {
				var46 = false;
				var50 = false;
				var26 = 0;
				var27 = 0;
				for (var28 = 0; var28 < 200; ++var28) {
					if (var0.getBlockId(var5 + var22 + var28, var6 + 1, var7 + var23) != 0) {
						if (var0.getBlockMaterial(var5 + var22 + var28, var6 + 1, var7 + var23) == Material.rock) {
							var46 = true;
							var26 = var28 - 1;
						}
						break;
					}
				}
				if (var46) {
					for (var28 = 0; var28 < 200; ++var28) {
						if (var0.getBlockId(var5 + var22 - var28, var6 + 1, var7 + var23) != 0) {
							if (var0.getBlockMaterial(var5 + var22 - var28, var6 + 1, var7 + var23) == Material.rock) {
								var50 = true;
								var27 = var28 - 1;
							}
							break;
						}
					}
				}
				if (var50 && var26 + var27 > 3) {
					var0.setBlock(var5 + var22 + var26, var6 + 1, var7 + var23, modTripWireSource.blockID, 1, 2);
					var0.setBlock(var5 + var22 - var27, var6 + 1, var7 + var23, modTripWireSource.blockID, 3, 2);
					for (var28 = -var27 + 1; var28 < var26; ++var28) {
						var0.setBlock(var5 + var22 + var28, var6 + 1, var7 + var23, modTripWire.blockID, 4, 2);
					}
					if (var1.nextBoolean()) {
						var0.setBlock(var5 + var22 - var27 - 1, var6 + 2, var7 + var23, Block.dispenser.blockID);
						var0.setBlockMetadataWithNotify(var5 + var22 - var27 - 1, var6 + 2, var7 + var23, 5, 3);
						var62 = null;
						if (var0.getBlockId(var5 + var22 - var27 - 1, var6 + 2, var7 + var23) == Block.dispenser.blockID) {
							var62 = (TileEntityDispenser) var0.getBlockTileEntity(var5 + var22 - var27 - 1, var6 + 2, var7 + var23);
						}
						if (var62 == null) {
							break;
						}
						var48 = var1.nextInt(5) + 1;
						for (var49 = 0; var49 < var48; ++var49) {
							var58 = var1.nextInt(disLoot.size());
							if (var1.nextInt((int) ((DunLootItem) disLoot.get(var58)).rareity) == 0) {
								var62.setInventorySlotContents(var1.nextInt(var62.getSizeInventory()), ((DunLootItem) disLoot.get(var58)).getItemStack(var1));
							} else {
								--var49;
							}
						}
					} else {
						var0.setBlock(var5 + var22 + var26 + 1, var6 + 2, var7 + var23, Block.dispenser.blockID);
						var0.setBlockMetadataWithNotify(var5 + var22 + var26 + 1, var6 + 2, var7 + var23, 4, 3);
						var62 = null;
						if (var0.getBlockId(var5 + var22 + var26 + 1, var6 + 2, var7 + var23) == Block.dispenser.blockID) {
							var62 = (TileEntityDispenser) var0.getBlockTileEntity(var5 + var22 + var26 + 1, var6 + 2, var7 + var23);
						}
						if (var62 == null) {
							break;
						}
						var48 = var1.nextInt(5) + 1;
						for (var49 = 0; var49 < var48; ++var49) {
							var58 = var1.nextInt(disLoot.size());
							if (var1.nextInt((int) ((DunLootItem) disLoot.get(var58)).rareity) == 0) {
								var62.setInventorySlotContents(var1.nextInt(var62.getSizeInventory()), ((DunLootItem) disLoot.get(var58)).getItemStack(var1));
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
							if (var0.getBlockMaterial(var5 + var10 + var26, var12 + 1 - var28, var7 + var11 + var27) != Material.air
									&& var0.getBlockMaterial(var5 + var10 + var26, var12 + 1 - var28, var7 + var11 + var27) != Material.water
									&& var0.getBlockMaterial(var5 + var10 + var26, var12 + 1 - var28, var7 + var11 + var27) != Material.leaves
									&& var0.getBlockMaterial(var5 + var10 + var26, var12 + 1 - var28, var7 + var11 + var27) != Material.wood
									&& var0.getBlockId(var5 + var10 + var26, var12 + 1 - var28, var7 + var11 + var27) != Block.tallGrass.blockID
									&& var0.getBlockMaterial(var5 + var10 + var26, var12 + 1 - var28, var7 + var11 + var27) != Material.snow) {
								if (var28 < 0) {
									var0.setBlock(var5 + var10 + var26, var12 + 1 - var28, var7 + var11 + var27, 0);
								}
								if (var28 == 0) {
									;
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

	private static void putLoot(World var0, Random var1, ArrayList var2, int var3, int var4) {
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
				DunLootItem var11 = (DunLootItem) loot.get(var10);
				int var12 = 1;
				if (var11.maxStack - var11.minStack > 1) {
					var12 = var1.nextInt(var11.maxStack - var11.minStack) + var11.minStack;
				}
				for (var19 = 0; var19 < 100; ++var19) {
					var10 = var1.nextInt(loot.size());
					var11 = (DunLootItem) loot.get(var10);
					var12 = 1;
					if (var11.maxStack - var11.minStack > 1) {
						var12 = var1.nextInt(var11.maxStack - var11.minStack) + var11.minStack;
					}
					if (var1.nextInt((int) ((DunLootItem) loot.get(var10)).rareity) == 0) {
						break;
					}
				}
				if (var19 != 100 && var4 > ((DunLootItem) loot.get(var10)).value * var12 && ((DunLootItem) loot.get(var10)).minDanger <= var3 && var2.size() > 0) {
					var4 = (int) (var4 - ((DunLootItem) loot.get(var10)).value * var12);
					var7 = true;
					int var13 = ((DunChest) var2.get(var5)).x;
					int var14 = ((DunChest) var2.get(var5)).y;
					int var15 = ((DunChest) var2.get(var5)).z;
					((DunChest) var2.get(var5)).looted = true;
					TileEntityChest var16 = null;
					if (var0.getBlockId(var13, var14, var15) == Block.chest.blockID) {
						var16 = (TileEntityChest) var0.getBlockTileEntity(var13, var14, var15);
					}
					if (var16 == null) {
						break;
					}
					for (int var17 = 0; var17 < 1; ++var17) {
						ItemStack var18 = ((DunLootItem) loot.get(var10)).getItemStack(var1);
						if (((DunLootItem) loot.get(var10)).enchatProb > 0 && var1.nextInt(((DunLootItem) loot.get(var10)).enchatProb) == 0) {
							EnchantmentHelper.addRandomEnchantment(var1, var18, ((DunLootItem) loot.get(var10)).maxEnchatLev);
						}
						if (var18 != null) {
							var16.setInventorySlotContents(var1.nextInt(var16.getSizeInventory()), var18);
						}
					}
				}
			}
		}
		for (var19 = 0; var19 < var2.size(); ++var19) {
			if (!((DunChest) var2.get(var19)).looted) {
				var0.setBlock(((DunChest) var2.get(var19)).x, ((DunChest) var2.get(var19)).y, ((DunChest) var2.get(var19)).z, 0);
			}
		}
	}

	private static void setVines(World var0, int x, int y, int z) {
		var0.setBlock(x, y, z, Block.vine.blockID);
		int var5 = new Random().nextInt(5) + 1;
		while (var5 > 0) {
			--y;
			if (var0.getBlockId(x, y, z) != 0) {
				return;
			}
			var0.setBlock(x, y, z, Block.vine.blockID);
			--var5;
		}
	}

	private static void setVines(World var0, int x, int y, int z, int meta) {
		var0.setBlock(x, y, z, Block.vine.blockID, meta, 3);
		int var6 = new Random().nextInt(5) + 1;
		while (var6 > 0) {
			--y;
			if (var0.getBlockId(x, y, z) != 0) {
				return;
			}
			var0.setBlock(x, y, z, Block.vine.blockID, meta, 3);
			--var6;
		}
	}
}
