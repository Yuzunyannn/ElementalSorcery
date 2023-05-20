package yuzunyannn.elementalsorcery.summon;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ESData;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.item.ItemParchment;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.json.Json;

public class SummonDungeonStone extends SummonCommon {

	protected int stoneCount;
	protected boolean dropPage;

	public SummonDungeonStone(World world, BlockPos pos) {
		this(world, pos, 0x2079a4);
	}

	public SummonDungeonStone(World world, BlockPos pos, int color) {
		super(world, pos, color);
		this.stoneCount = world.rand.nextInt(5) + 8;
	}

	@Override
	public void initSummoner(EntityLivingBase summoner) {
		dropPage = this.world.rand.nextFloat() < 0.05f;
		if (summoner instanceof EntityPlayer) {
			if (!ESData.getPlayerFlag(summoner, ESData.PLAYER_FLAG_DUNGEON_PAGE)) {
				ESData.setPlayerFlag(summoner, ESData.PLAYER_FLAG_DUNGEON_PAGE, true);
				dropPage = true;
			}
		}
	}

	@Override
	public void initData() {
		this.size = 3;
		this.height = 4;
	}

	@Override
	public boolean update() {
		if (world.isRemote) return true;
		if (tick++ < 20 * 5) return true;
		if (tick % 2 != 0) return true;
		Vec3d pos = new Vec3d(this.pos).add(0.5, 0.1 + 1.5, 0.5);
		Random rand = world.rand;
		if (stoneCount <= 0) return false;
		stoneCount--;
		this.spawn(pos, rand);
		return stoneCount > 0;
	}

	public void spawn(Vec3d pos, Random rand) {
		ItemHelper.dropItem(world, pos, new ItemStack(ESObjects.ITEMS.DUNGEON_STONE));
		if (stoneCount == 0 && dropPage) {
			ItemStack parchment = ItemParchment.getParchment(Json.idFormat("dungeon/dungeon_guide", null));
			ItemHelper.dropItem(world, pos, parchment);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setByte("sc", (byte) this.stoneCount);
		if (dropPage) nbt.setBoolean("dp", true);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.stoneCount = nbt.getInteger("sc");
		this.dropPage = nbt.getBoolean("dp");
	}

}
