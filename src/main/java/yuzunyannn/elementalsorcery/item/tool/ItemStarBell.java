package yuzunyannn.elementalsorcery.item.tool;

import java.util.List;
import java.util.function.BiFunction;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.block.BlockElfSapling;
import yuzunyannn.elementalsorcery.explore.ExploreSlimeChunk;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemStarBell extends Item {

	static public BiFunction<World, EntityPlayer, Boolean> customHandle;

	public ItemStarBell() {
		this.setHasSubtypes(true);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab)) return;
		items.add(new ItemStack(this, 1, 0));
		items.add(new ItemStack(this, 1, 1));
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		if (stack.getMetadata() == 0) return "item.starBell.normal";
		return "item.starBell.star";
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		int meta = stack.getMetadata();

		world.playSound(null, player.posX, player.posY + 1, player.posZ, SoundEvents.BLOCK_NOTE_BELL,
				SoundCategory.RECORDS, 1.0F, 0);

		AxisAlignedBB aabb = WorldHelper.createAABB(player.getPosition(), 6, 3, 2);
		List<EntityCreature> entities = world.getEntitiesWithinAABB(EntityCreature.class, aabb);
		for (EntityCreature creature : entities) {
			if (creature instanceof EntityAnimal) {
				PathNavigate navigator = creature.getNavigator();
				if (navigator.noPath() && creature.getRNG().nextInt(3) == 0)
					navigator.tryMoveToEntityLiving(player, creature.getMoveHelper().getSpeed());
			} else if (creature instanceof IMob) {
				if (creature.getAttackTarget() == null) creature.setAttackTarget(player);
			}
		}

		if (meta == 0) {
			if (world.isRemote) player.sendMessage(new TextComponentTranslation("info.ding.ring.ring"));
		} else starBellRing(world, player);

		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	public static void starBellRing(World world, EntityPlayer player) {
		if (world.isRemote) return;

		if (customHandle != null && customHandle.apply(world, player)) return;

		ITextComponent text = new TextComponentTranslation("info.ding.ring.ring");
		boolean canContinue = true;
		if (canContinue) {
			BlockPos pos = player.getPosition();
			for (int i = 0; i < 5; i++) {
				pos = pos.down();
				if (pos.getY() <= 0) break;
				if (world.getBlockState(pos).getMaterial() == Material.LAVA) {
					ITextComponent sound = new TextComponentTranslation("info.sound.crisp");
					text.appendSibling(new TextComponentTranslation("info.star.bell", sound));
					canContinue = false;
					break;
				}
			}
		}
		if (canContinue) out: {
			BlockPos pos = player.getPosition();
			int size = 3;
			for (int x = -size; x <= size; x++) {
				for (int z = -size; z <= size; z++) {
					for (int y = -size; y <= size; y++) {
						BlockPos at = pos.add(x, y, z);
						IBlockState state = world.getBlockState(at);
						Block block = state.getBlock();
						boolean ok = block == ESObjects.BLOCKS.SEAL_STONE || block == Blocks.DIAMOND_ORE
								|| block == Blocks.EMERALD_ORE || block == ESObjects.BLOCKS.SCARLET_CRYSTAL_ORE;
						if (ok) {
							ITextComponent sound = new TextComponentTranslation("info.sound.long");
							text.appendSibling(new TextComponentTranslation("info.star.bell", sound));
							canContinue = false;
							break out;
						}
					}
				}
			}
		}
		if (canContinue) {
			BlockPos pos = player.getPosition();
			boolean isSpChunk = world.provider.getDimensionType() == DimensionType.OVERWORLD
					&& (BlockElfSapling.chunkCanGrow(world, pos) || ExploreSlimeChunk.isSlimeChunk(world, pos));
			if (isSpChunk) {
				ITextComponent sound = new TextComponentTranslation("info.sound.deep");
				text.appendSibling(new TextComponentTranslation("info.star.bell", sound));
				canContinue = false;
			}
		}

		player.sendMessage(text);

	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

}
