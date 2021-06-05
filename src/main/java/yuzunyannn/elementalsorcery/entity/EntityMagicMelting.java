package yuzunyannn.elementalsorcery.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class EntityMagicMelting extends Entity implements IEntityAdditionalSpawnData {

	protected static final DataParameter<ItemStack> ITEM = EntityDataManager
			.<ItemStack>createKey(EntityMagicMelting.class, DataSerializers.ITEM_STACK);

	public EntityMagicMelting(World worldIn) {
		super(worldIn);
		this.setSize(0.3f, 0.3f);
	}

	public EntityMagicMelting(World worldIn, BlockPos pos, ItemStack ore) {
		this(worldIn);
		this.setPosition(pos.getX() + 0.12 + 0.72 * rand.nextFloat(), pos.getY(),
				pos.getZ() + 0.12 + 0.72 * rand.nextFloat());
		this.setItem(ore);
	}

	@Override
	protected void entityInit() {
		dataManager.register(ITEM, ItemStack.EMPTY);
	}

	public ItemStack getItem() {
		return dataManager.get(ITEM);
	}

	public void setItem(ItemStack ore) {
		dataManager.set(ITEM, ore);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(progress);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		progress = additionalData.readInt();
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		this.setItem(new ItemStack(nbt.getCompoundTag("ore")));
		progress = nbt.getShort("progress");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setTag("ore", this.getItem().serializeNBT());
		nbt.setShort("progress", (short) progress);
	}

	protected void drop(ItemStack drop) {
		BlockPos pos = this.getPosition();
		final int size = 2;
		int xs = rand.nextBoolean() ? size : -size;
		int xd = xs / Math.abs(xs);
		int zs = rand.nextBoolean() ? size : -size;
		int zd = zs / Math.abs(zs);
		for (int x = -xs; xd > 0 ? x <= xs : x >= xs; x += xd) {
			for (int z = -zs; zd > 0 ? z <= zs : z >= zs; z += zd) {
				drop = BlockHelper.insertInto(world, pos.add(x, -1, z), EnumFacing.UP, drop);
				if (drop.isEmpty()) return;
			}
		}
		Block.spawnAsEntity(world, this.getPosition().up(), drop);
	}

	protected void drop() {
		this.drop(this.getItem());
	}

	protected int progress = 0;
	public float drift = 0;
	public float prevDrift = 0;

	public int getProgress() {
		return progress;
	}

	public int getMaxProgress() {
		ItemStack stack = this.getItem();
		return 20 * ((int) (MathHelper.sqrt(stack.getCount()) + 1) * 20);
	}

	protected ItemStack getSmeltingResult() {
		ItemStack stack = this.getItem();
		if (stack.isEmpty()) return ItemStack.EMPTY;
		stack = FurnaceRecipes.instance().getSmeltingResult(stack);
		return stack;
	}

	protected float getGrowRate() {
		return 2;
	}

	@Override
	public void onUpdate() {
		this.progress++;
		if (world.isRemote) {
			updateClient();
			return;
		}
		// 检测
		if (this.progress % 4 == 0) {
			ItemStack stack = this.getItem();
			if (stack.isEmpty()) {
				this.setDead();
				return;
			}
			IBlockState state = world.getBlockState(this.getPosition());
			if (state != Blocks.LAVA.getDefaultState()) {
				this.drop();
				this.setDead();
				return;
			}
		}
		int max = this.getMaxProgress();
		if (this.progress >= max) {
			this.progress = max;
			ItemStack result = getSmeltingResult().copy();
			if (!result.isEmpty()) {
				float n = this.getGrowRate();
				int count = this.getItem().getCount();
				result.setCount((int) (count * n));
				this.drop(result);
			} else this.drop();
			this.setDead();
		}
	}

	@SideOnly(Side.CLIENT)
	public void updateClient() {
		prevDrift = drift;
		drift += 0.1f;

		if (progress % 20 != 0) return;
		EffectElementMove effect = new EffectElementMove(world, this.getPositionVector()
				.addVector(rand.nextDouble() * 0.25 - 0.125, 0.8, rand.nextDouble() * 0.25 - 0.125));
		effect.g = 0.001;
		effect.motionY = 0.03 * rand.nextDouble() + 0.01;
		effect.setColor(ESInit.ELEMENTS.FIRE.getColor(ElementStack.EMPTY));
		Effect.addEffect(effect);
	}

}
