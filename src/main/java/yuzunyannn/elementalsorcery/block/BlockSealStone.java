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
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.elf.research.AncientPaper;
import yuzunyannn.elementalsorcery.elf.research.KnowledgeType;
import yuzunyannn.elementalsorcery.grimoire.mantra.Mantra;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.ItemAncientPaper;
import yuzunyannn.elementalsorcery.item.prop.ItemKeepsake;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

public class BlockSealStone extends Block implements Mapper {

	@Config
	private static float MANTRA_DROP_PROBABILITY_PER_LUCKY = 0.075f;

	public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.<EnumType>create("variant", EnumType.class);

	public BlockSealStone() {
		super(Material.ROCK);
		setUnlocalizedName("sealStone");
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

	public ItemStack getAncientPaper(World worldIn, @Nullable EntityPlayer player, int fortune) {
		Random rand = worldIn.rand;
		ItemStack stack = new ItemStack(ESInit.ITEMS.ANCIENT_PAPER, 1, ItemAncientPaper.EnumType.NORMAL.getMetadata());

		AncientPaper ap = new AncientPaper();

		boolean isMantra = false;
		for (int i = 0; i < fortune + 1; i++)
			isMantra = isMantra || rand.nextFloat() <= MANTRA_DROP_PROBABILITY_PER_LUCKY;
		isMantra = isMantra && player != null;

		float at = rand.nextFloat();
		float length = rand.nextFloat() * 0.5f + 0.05f + Math.min(0.2f, fortune / 50.0f);
		at = findRangeStart(length, at);
		int start = MathHelper.floor(at * 100);
		ap.setStart(start).setEnd(start + MathHelper.floor(length * 100));
		ap.setProgress(0);

		RandomHelper.WeightRandom<KnowledgeType> wr = new RandomHelper.WeightRandom();
		for (Entry<String, KnowledgeType> entry : KnowledgeType.REGISTRY.entrySet()) {
			if (isMantra) {
				List<Entry<String, Integer>> list = entry.getValue().getTopics();
				if (!list.isEmpty() && list.get(0).getKey().equals("Mantra")) wr.add(entry.getValue(), 10);
			} else wr.add(entry.getValue(), 10);
		}
		ap.setType(wr.get());

		// 如果是咒文
		if (isMantra) {
			RandomHelper.WeightRandom<Mantra> wMantras = new RandomHelper.WeightRandom();
			for (Entry<ResourceLocation, Mantra> entry : Mantra.REGISTRY.getEntries()) {
				Mantra mantra = entry.getValue();
				float rarity = mantra.getRarity(worldIn, player.getPosition());
				if (rarity <= 0) continue;
				rarity = rarity + (100 - rarity) * Math.min(0.5f, fortune / 25.0f);// 所有都向100靠拢
				wMantras.add(entry.getValue(), rarity);
			}
			Mantra mantra = wMantras.get();
			ap.setMantra(mantra);
		}

		ap.saveState(stack);
		return stack;
	}

	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
		if (worldIn.isRemote) return;
		EntityPlayer player = harvesters.get();

		Random rand = worldIn.rand;
		int tryTime = rand.nextInt(fortune + 2) + 1;

		if (player != null) {}

		for (int i = 0; i < tryTime; i++) {
			if (rand.nextFloat() > chance) continue;
			chance = chance * 0.75f;
			ItemStack stack = getAncientPaper(worldIn, player, fortune);
			spawnAsEntity(worldIn, pos, stack);
		}

		// 一些其他东西
		RandomHelper.WeightRandom<ItemStack> wr = new RandomHelper.WeightRandom();
		wr.add(new ItemStack(ESInit.ITEMS.ELEMENT_CRYSTAL), 1);
		wr.add(new ItemStack(ESInit.ITEMS.KEEPSAKE, 1, ItemKeepsake.EnumType.UNDELIVERED_LETTER.getMeta()), 3);
		wr.add(new ItemStack(ESInit.ITEMS.ORDER_CRYSTAL), 4);
		wr.add(new ItemStack(ESInit.ITEMS.MAGIC_CRYSTAL), 10);
		wr.add(new ItemStack(ESInit.ITEMS.RESONANT_CRYSTAL), 16);
		wr.add(new ItemStack(ESInit.ITEMS.MAGIC_STONE, 3), 40);
		wr.add(new ItemStack(ESInit.ITEMS.MAGIC_PIECE, 4 + rand.nextInt(4)), 80);

		tryTime = rand.nextInt(fortune + 5) + 2;
		for (int i = 0; i < tryTime; i++) spawnAsEntity(worldIn, pos, wr.get());
	}

	public static float findRangeStart(float length, float at) {
		float h = length / 2;
		if (at - h < 0) return 0;
		if (at + h > 1) return 1 - length;
		return at - h;
	}

}
