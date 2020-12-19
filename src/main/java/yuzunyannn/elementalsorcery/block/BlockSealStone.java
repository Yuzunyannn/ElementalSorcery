package yuzunyannn.elementalsorcery.block;

import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.elf.research.AncientPaper;
import yuzunyannn.elementalsorcery.elf.research.KnowledgeType;
import yuzunyannn.elementalsorcery.grimoire.mantra.Mantra;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.ItemAncientPaper;
import yuzunyannn.elementalsorcery.util.RandomHelper;

public class BlockSealStone extends Block {

	public BlockSealStone() {
		super(Material.ROCK);
		setUnlocalizedName("sealStone");
		setHarvestLevel("pickaxe", 1);
		setHardness(1.5F);
		setResistance(10.0F);
	}

	public ItemStack getAncientPaper(World worldIn, @Nullable EntityPlayer player, int fortune) {
		Random rand = worldIn.rand;
		ItemStack stack = new ItemStack(ESInit.ITEMS.ANCIENT_PAPER, 1, ItemAncientPaper.EnumType.NORMAL.getMetadata());

		AncientPaper ap = new AncientPaper();

		boolean isMantra = false;
		for (int i = 0; i < fortune + 1; i++) isMantra = isMantra || rand.nextFloat() <= 0.02f;
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
		wr.add(new ItemStack(ESInit.ITEMS.ORDER_CRYSTAL), 3);
		wr.add(new ItemStack(ESInit.ITEMS.MAGIC_CRYSTAL), 10);
		wr.add(new ItemStack(ESInit.ITEMS.RESONANT_CRYSTAL), 12);
		wr.add(new ItemStack(ESInit.ITEMS.MAGIC_STONE), 30);
		wr.add(new ItemStack(ESInit.ITEMS.MAGIC_PIECE), 80);
		tryTime = rand.nextInt(fortune + 3) + 1;
		for (int i = 0; i < tryTime; i++) spawnAsEntity(worldIn, pos, wr.get());
	}

	public static float findRangeStart(float length, float at) {
		float h = length / 2;
		if (at - h < 0) return 0;
		if (at + h > 1) return 1 - length;
		return at - h;
	}

}
