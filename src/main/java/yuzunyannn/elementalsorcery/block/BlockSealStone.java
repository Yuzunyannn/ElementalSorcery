package yuzunyannn.elementalsorcery.block;

import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMultiTexture.Mapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.elf.research.AncientPaper;
import yuzunyannn.elementalsorcery.elf.research.KnowledgeType;
import yuzunyannn.elementalsorcery.item.ItemAncientPaper;
import yuzunyannn.elementalsorcery.item.prop.ItemBlessingJadePiece;
import yuzunyannn.elementalsorcery.item.prop.ItemKeepsake;
import yuzunyannn.elementalsorcery.logics.ESPlayerLogic;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

public class BlockSealStone extends Block implements Mapper {

	@Config
	private static float MANTRA_DROP_PROBABILITY_PER_LUCKY = 0.08f;

	public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.<EnumType>create("variant", EnumType.class);

	public BlockSealStone() {
		super(Material.ROCK);
		setTranslationKey("sealStone");
		setHarvestLevel("pickaxe", 1);
		setHardness(1.5F);
		setResistance(10.0F);
	}

	static public enum EnumType implements IStringSerializable {
		STONE("stone"),
		NETHERRACK("netherrack");

		final String name;

		EnumType(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		public int getMetadata() {
			return this.ordinal();
		}

		static public EnumType byMetadata(int meta) {
			return EnumType.values()[0x1 & meta];
		}
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (BlockElfPlank.EnumType type : BlockElfPlank.EnumType.values())
			items.add(new ItemStack(this, 1, type.getMetadata()));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).getMetadata();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { VARIANT });
	}

	@Override
	public String apply(ItemStack var1) {
		return "s";
	}

	public static Mantra randomMantra(Random rand, float fortune, boolean isSuperDrop) {
		return randomMantra(rand, fortune, isSuperDrop, null, null);
	}

	public static Mantra randomMantra(Random rand, float fortune, boolean isSuperDrop, @Nullable World world,
			@Nullable BlockPos pos) {
		RandomHelper.WeightRandom<Mantra> wMantras = new RandomHelper.WeightRandom();
		for (Entry<ResourceLocation, Mantra> entry : Mantra.REGISTRY.getEntries()) {
			Mantra mantra = entry.getValue();
			float rarity = mantra.getRarity(world, pos);
			if (rarity <= 0) continue;
			rarity = rarity + (100 - rarity) * Math.min(0.5f, fortune / 25.0f);// 所有都向100靠拢
			wMantras.add(entry.getValue(), rarity);
		}
		Mantra mantra = wMantras.get(rand);
		if (isSuperDrop) {
			// 超级掉落选择随机几次更稀有的
			int rarityTryTimes = 1;
			float raritier = mantra.getRarity(world, pos);
			for (int i = 0; i < rarityTryTimes; i++) {
				Mantra check = wMantras.get(rand);
				float r = check.getRarity(world, pos);
				if (r < raritier) {
					mantra = check;
					raritier = r;
				}
			}
		}
		return mantra;
	}

	public static AncientPaper randomAncientPaper(Random rand, float fortune, boolean isSuperDrop) {
		AncientPaper ap = new AncientPaper();
		double at = rand.nextFloat();
		double length = rand.nextFloat() * 0.5 + 0.05 + Math.min(0.2, fortune / 50.0);
		if (isSuperDrop) length += rand.nextFloat() * 0.1 + 0.1;
		at = findRangeStart(length, at);
		int start = MathHelper.floor(at * 100);
		ap.setStart(start).setEnd(Math.min(100, start + MathHelper.ceil(length * 100)));
		return ap.setProgress(0);
	}

	public static ItemStack getAncientPaper(World worldIn, @Nullable BlockPos dropPos, int fortune,
			boolean isSuperDrop) {
		Random rand = worldIn.rand;
		ItemStack stack = new ItemStack(ESObjects.ITEMS.ANCIENT_PAPER, 1,
				ItemAncientPaper.EnumType.NORMAL.getMetadata());

		boolean isMantra = false;
		for (int i = 0; i < fortune + 1; i++)
			isMantra = isMantra || rand.nextFloat() <= MANTRA_DROP_PROBABILITY_PER_LUCKY;
		if (isSuperDrop) isMantra = isMantra || rand.nextBoolean();
		// 只有传入pos的的时候才会掉mantra
		isMantra = isMantra && dropPos != null;

		AncientPaper ap = randomAncientPaper(rand, fortune, isSuperDrop);

		RandomHelper.WeightRandom<KnowledgeType> wr = new RandomHelper.WeightRandom();
		for (Entry<String, KnowledgeType> entry : KnowledgeType.REGISTRY.entrySet()) {
			if (isMantra) {
				List<Entry<String, Integer>> list = entry.getValue().getTopics();
				if (!list.isEmpty() && list.get(0).getKey().equals("Mantra")) wr.add(entry.getValue(), 10);
			} else wr.add(entry.getValue(), 10);
		}
		ap.setType(wr.get());

		// 如果是咒文
		if (isMantra) ap.setMantra(randomMantra(rand, fortune, isSuperDrop, worldIn, dropPos));

//		if (ESAPI.isDevelop) {
//			ap.setProgress(1);
//		}

		ap.saveState(stack);
		return stack;
	}

	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
		if (worldIn.isRemote) return;
		EntityPlayer player = harvesters.get();
		boolean isSuperDrop = false;

		if (player != null) {
			fortune = (int) (fortune * (1 + player.getLuck() / 32f));
			ItemStack stack = player.getHeldItemMainhand();
			if (stack.getItem() == ESObjects.ITEMS.DRAGON_BREATH_PICKAXE) {
				isSuperDrop = true;
				fortune = fortune + 1;
			}

			if (ESPlayerLogic.checkPlayerFlagAndSet(player, ESPlayerLogic.FIRST_TUTORIAL)) {
				spawnAsEntity(worldIn, pos, new ItemStack(ESObjects.ITEMS.TUTORIAL_PAD));
			}
		}

		Random rand = worldIn.rand;
		int tryTime = rand.nextInt(fortune + 2) + 1;

		for (int i = 0; i < tryTime; i++) {
			if (rand.nextFloat() > chance) continue;
			chance = chance * 0.75f;
			ItemStack stack = getAncientPaper(worldIn, player == null ? null : pos, fortune, isSuperDrop);
			spawnAsEntity(worldIn, pos, stack);
		}

		// 一些其他东西
		RandomHelper.WeightRandom<ItemStack> wr = new RandomHelper.WeightRandom();
		wr.add(new ItemStack(ESObjects.ITEMS.TUTORIAL_PAD), 0.5f);
		wr.add(new ItemStack(ESObjects.ITEMS.ELEMENT_CRYSTAL), 1.5);
		wr.add(ItemBlessingJadePiece.createPiece(0), 3);
		wr.add(new ItemStack(ESObjects.ITEMS.KEEPSAKE, 1, ItemKeepsake.EnumType.UNDELIVERED_LETTER.getMeta()), 4);
		wr.add(new ItemStack(ESObjects.ITEMS.ORDER_CRYSTAL), 5);
		wr.add(new ItemStack(ESObjects.ITEMS.MAGIC_CRYSTAL), 10);
		wr.add(new ItemStack(ESObjects.ITEMS.RESONANT_CRYSTAL), 16);
		wr.add(new ItemStack(ESObjects.ITEMS.MAGIC_STONE, 3), 30);
		wr.add(new ItemStack(ESObjects.ITEMS.MAGIC_PIECE, 4 + rand.nextInt(4)), 80);

		tryTime = rand.nextInt(fortune + 5) + 2;
		for (int i = 0; i < tryTime; i++) spawnAsEntity(worldIn, pos, wr.get());
	}

	public static double findRangeStart(double length, double at) {
		double h = length / 2;
		if (at - h < 0) return 0;
		if (at + h > 1) return 1 - length;
		return at - h;
	}

}
