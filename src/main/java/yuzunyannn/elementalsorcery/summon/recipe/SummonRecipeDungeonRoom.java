package yuzunyannn.elementalsorcery.summon.recipe;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.dungeon.DungeonArea;
import yuzunyannn.elementalsorcery.dungeon.DungeonFakeArea;
import yuzunyannn.elementalsorcery.summon.Summon;
import yuzunyannn.elementalsorcery.summon.SummonDungeonRoom;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;

public class SummonRecipeDungeonRoom extends SummonRecipe {

	public static ItemStack createVestKeepsake(int areaId, int roomId, @Nullable EntityLivingBase summoner) {
		ItemStack stack = new ItemStack(Items.APPLE);
		NBTTagCompound nbt = new NBTTagCompound();
		stack.setTagCompound(nbt);
		nbt.setInteger("roomId", roomId);
		nbt.setInteger("areaId", areaId);
		if (summoner != null) nbt.setInteger("summoner", summoner.getEntityId());
		return stack;
	}

	@Override
	public boolean canBeKeepsake(ItemStack keepsake, World world, BlockPos pos) {
		return false;
	}

	@Override
	public Summon createSummon(ItemStack keepsake, World world, BlockPos pos) {
		NBTTagCompound nbt = keepsake.getTagCompound();
		SummonDungeonRoom summon = new SummonDungeonRoom(world);
		if (nbt != null) {
			summon.deserializeNBT(nbt);
			if (nbt.hasKey("summoner", NBTTag.TAG_NUMBER)) {
				int id = nbt.getInteger("summoner");
				summon.setCreativeBuild(EntityHelper.isCreative(world.getEntityByID(id)));
			}
		}
		if (ESAPI.isDevelop && summon.isDevTest()) return createDevTestSummon(summon, world, pos);
		return summon;
	}

	protected Summon createDevTestSummon(SummonDungeonRoom real, World world, BlockPos pos) {
		SummonDungeonRoom summon = new SummonDungeonRoom(world) {
			@Override
			public DungeonArea getArea() {
				return DungeonFakeArea.getOrCreate();
			}
		};
		summon.deserializeNBT(real.serializeNBT());
		return summon;
	}
}
