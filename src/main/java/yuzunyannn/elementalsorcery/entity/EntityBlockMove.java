package yuzunyannn.elementalsorcery.entity;

import java.util.Collection;
import java.util.Random;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.building.BlockItemTypeInfo;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.ESFakePlayer;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.ExceptionHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;

public class EntityBlockMove extends Entity implements IEntityAdditionalSpawnData {

	public static class MoveTrace implements INBTSerializable<NBTTagCompound> {

		protected Vec3d from;
		protected Vec3d to;
		protected String order = "xyz";

		public MoveTrace(Vec3d from, Vec3d to) {
			this.from = from;
			this.to = to;
		}

		public MoveTrace(NBTTagCompound nbt) {
			this.deserializeNBT(nbt);
		}

		public void setOrder(String order) {
			this.order = order;
		}

		public void randomOrder(Random rand) {
			this.setOrder((new String[] { "xyz", "xzy", "yxz", "yzx", "zxy", "zyx" })[rand.nextInt(6)]);
		}

		private double getAxis(char axis, Vec3d vec) {
			switch (axis) {
			case 'x':
				return vec.x;
			case 'y':
				return vec.y;
			case 'z':
				return vec.z;
			default:
				return 0;
			}
		}

		private Vec3d addAxis(char axis, Vec3d vec, double num) {
			switch (axis) {
			case 'x':
				return new Vec3d(vec.x + num, vec.y, vec.z);
			case 'y':
				return new Vec3d(vec.x, vec.y + num, vec.z);
			case 'z':
				return new Vec3d(vec.x, vec.y, vec.z + num);
			default:
				return vec;
			}
		}

		public Vec3d getPosition(double rate) {
			Vec3d lenthVec = new Vec3d(to.x - from.x, to.y - from.y, to.z - from.z);
			double l = Math.abs(lenthVec.x) + Math.abs(lenthVec.y) + Math.abs(lenthVec.z);
			char c1 = order.charAt(0);
			double r1 = Math.abs(getAxis(c1, lenthVec)) / l;
			if (rate < r1) {
				double r = rate / r1 * 3.1415926f;
				r = -(MathHelper.cos((float) r) - 1) / 2;
				return addAxis(c1, from, getAxis(c1, lenthVec) * r);
			}
			char c2 = order.charAt(1);
			double r2 = Math.abs(getAxis(c2, lenthVec)) / l;
			if (rate >= r1 && rate <= r1 + r2) {
				double r = (rate - r1) / r2 * 3.1415926f;
				r = -(MathHelper.cos((float) r) - 1) / 2;
				Vec3d ret = addAxis(c1, from, getAxis(c1, lenthVec));
				return addAxis(c2, ret, getAxis(c2, lenthVec) * r);
			}
			char c3 = order.charAt(2);
			double r3 = Math.abs(getAxis(c3, lenthVec)) / l;
			if (rate >= 1 - r3 && rate <= 1) {
				double r = (rate - 1 + r3) / r3 * 3.1415926f;
				r = -(MathHelper.cos((float) r) - 1) / 2;
				Vec3d ret = addAxis(c1, from, getAxis(c1, lenthVec));
				ret = addAxis(c2, ret, getAxis(c2, lenthVec));
				return addAxis(c3, ret, getAxis(c3, lenthVec) * r);
			}
			return to;
		}

		public double getTotalLength() {
			Vec3d lenthVec = new Vec3d(to.x - from.x, to.y - from.y, to.z - from.z);
			return Math.abs(lenthVec.x) + Math.abs(lenthVec.y) + Math.abs(lenthVec.z);
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			NBTHelper.setVec3d(nbt, "from", from);
			NBTHelper.setVec3d(nbt, "to", to);
			nbt.setString("order", order);
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			from = NBTHelper.getVec3d(nbt, "from");
			to = NBTHelper.getVec3d(nbt, "to");
			order = nbt.getString("order");
		}

	}

	/** 根据blockState获取面向 */
	public static EnumFacing getFacingFromState(IBlockState state) {
		if (state == null) return EnumFacing.NORTH;
		Collection<IProperty<?>> properties = state.getPropertyKeys();
		for (IProperty<?> property : properties) {
			if (property instanceof PropertyDirection) {
				EnumFacing facing = state.getValue((PropertyDirection) property);
				if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) return EnumFacing.NORTH;
				return facing == null ? EnumFacing.NORTH : facing;
			}
		}
		return EnumFacing.NORTH;
	}

	public static final byte FLAG_FORCE_DESTRUCT = 0x01; // 是否可以破坏目标点的方块
	public static final byte FLAG_DESTRUCT_DROP = 0x02; // 破坏目标点的方块，是否掉落
	public static final byte FLAG_INTO_CHEST = 0x04; // 如果目标点是箱子，进入箱子
	public static final byte FLAG_FORCE_DROP = 0x08; // 到目标点是否强制掉落，但优先进入箱子

	protected ItemStack stack = ItemStack.EMPTY;
	protected IBlockState state;
	protected MoveTrace trace;
	protected EnumFacing facing;
	protected float rate = 0;
	protected int flags = FLAG_INTO_CHEST;
	protected int color = 0;
	protected EntityPlayer player;

	public EntityBlockMove(World worldIn) {
		super(worldIn);
		this.setSize(1, 1);
	}

	public EntityBlockMove(World worldIn, BlockPos from, BlockPos to) {
		this(worldIn, null, from, to);
	}

	public EntityBlockMove(World worldIn, BlockPos from, Vec3d to) {
		this(worldIn, null, from, to);
	}

	public EntityBlockMove(World worldIn, EntityPlayer player, BlockPos from, BlockPos to) {
		this(worldIn, player, from, new Vec3d(to.getX() + 0.5, to.getY(), to.getZ() + 0.5));
	}

	/** 飞一个已有方块的构造 */
	public EntityBlockMove(World worldIn, EntityPlayer player, BlockPos from, Vec3d to) {
		super(worldIn);
		this.setSize(1, 1);
		this.player = player;
		this.trace = new MoveTrace(new Vec3d(from.getX() + 0.5, from.getY(), from.getZ() + 0.5), to);
		this.state = world.getBlockState(from);
		this.stack = new BlockItemTypeInfo(state).getItemStack();
		this.facing = getFacingFromState(this.state).getOpposite().rotateY();
		this.setPosition(this.trace.from.x, this.trace.from.y, this.trace.from.z);
		this.trace.randomOrder(this.rand);
	}

	public EntityBlockMove(World worldIn, Vec3d from, BlockPos to, IBlockState state) {
		this(worldIn, null, from, to, ItemStack.EMPTY, state);
	}

	public EntityBlockMove(World worldIn, EntityPlayer player, Vec3d from, BlockPos to, ItemStack flyItem,
			IBlockState state) {
		this(worldIn, player, from, new Vec3d(to.getX() + 0.5, to.getY(), to.getZ() + 0.5), flyItem, state, null);
	}

	/** 飞一个方块的构造函数 */
	public EntityBlockMove(World worldIn, EntityPlayer player, Vec3d from, Vec3d to, ItemStack flyItem,
			IBlockState state, EnumFacing facing) {
		super(worldIn);
		this.setSize(1, 1);
		this.player = player;
		this.trace = new MoveTrace(from, to);
		this.stack = flyItem;
		this.state = state;
		if (facing == null) this.facing = getFacingFromState(state).getOpposite().rotateY();
		else this.facing = facing;
		if (this.stack.isEmpty() && state != null) this.stack = ItemHelper.toItemStack(state);
		this.setPosition(from.x, from.y, from.z);
		this.rate = -1;
		this.trace.randomOrder(this.rand);
	}

	public void setFlag(int flag, boolean open) {
		if (open) flags = flags | flag;
		else flags = flags & ~flag;
	}

	public boolean hasFlag(int flag) {
		return (flags & flag) != 0;
	}

	public MoveTrace getTrace() {
		return trace;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public IBlockState getBlockState() {
		return state;
	}

	@Override
	protected void entityInit() {
	}

	protected float addPerTick = 0;

	@Override
	public void onUpdate() {
		if (trace == null || stack.isEmpty()) {
			this.setDead();
			return;
		}
		if (this.rate < 0) {
			this.rate = this.rate + 0.1f;
			return;
		}
		if (addPerTick <= 0) {
			if (trace.getTotalLength() == 0) addPerTick = 1;
			else addPerTick = 1 / (float) ((trace.getTotalLength() / 5.0f) * 20);
		}
		this.lastTickPosX = this.posX;
		this.lastTickPosY = this.posY;
		this.lastTickPosZ = this.posZ;
		this.rotationYaw = this.rate = this.rate + addPerTick;
		Vec3d at = trace.getPosition(this.rate);
		this.prevPosX = this.posX = at.x;
		this.prevPosY = this.posY = at.y;
		this.prevPosZ = this.posZ = at.z;
		if (world.isRemote) this.updateClient();
		if (rate > 1) {
			try {
				this.doFinish();
			} catch (Exception e) {
				ElementalSorcery.logger.warn("EntityBlockMove放置物品时出现异常！", e);
				ExceptionHelper.warnSend(world, "EntityBlockMove放置物品时出现异常！");
			}
			this.setDead();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch,
			int posRotationIncrements, boolean teleport) {
		// 客户端在小范围内不接受服务器的更新
		if (Math.abs(x - posX) > 1 || Math.abs(y - posY) > 1 || Math.abs(z - posZ) > 1) {
			this.rate = yaw;
		}
		float f = this.width / 2.0F;
		float f1 = this.height;
		this.setEntityBoundingBox(new AxisAlignedBB(posX - f, posY, posZ - f, posX + f, posY + f1, posZ + f));
	}

	public float scale = 1;
	public float lastTickSacle = 1;

	@SideOnly(Side.CLIENT)
	public void updateClient() {
		lastTickSacle = scale;
		if (rate > 0.8) {
			boolean calcScale = false;
			if (this.hasFlag(FLAG_FORCE_DROP)) calcScale = true;
			else if (!this.hasFlag(FLAG_FORCE_DESTRUCT)) {
				BlockPos to = new BlockPos(trace.to);
				calcScale = !world.isAirBlock(to);
			}
			if (calcScale) scale = (1 - Math.min(rate, 1)) * 10 / 2 * 0.75f + 0.25f;
		}
		if (rate >= 0 && color > 0) {
			if (ticksExisted % 6 != 0) return;
			BlockPos to = new BlockPos(trace.to);
			AxisAlignedBB aabb = Block.FULL_BLOCK_AABB;
			if (state != null) aabb = state.getBoundingBox(world, to);
			Vec3d pos = this.getPositionVector().addVector(-this.width / 2, 0, -this.width / 2);
			for (int x = 0; x <= 1; x++) {
				for (int y = 0; y <= 1; y++) {
					for (int z = 0; z <= 1; z++) {
						Vec3d offset = new Vec3d(aabb.minX * (1 - x) + aabb.maxX * x,
								aabb.minY * (1 - y) + aabb.maxY * y, aabb.minZ * (1 - z) + aabb.maxZ * z);
						offset = new Vec3d(offset.x + (0.5 - offset.x) * (1 - scale),
								offset.y + (0.5 - offset.y) * (1 - scale), offset.z + (0.5 - offset.z) * (1 - scale));
						Vec3d at = pos.add(offset);
						EffectElementMove effect = new EffectElementMove(world, at);
						effect.setColor(color);
						effect.g = 0.0001f;
						effect.lifeTime = 20;
						effect.dalpha = 1.0f / effect.lifeTime;
						Effect.addEffect(effect);
					}
				}
			}
		}
	}

	protected void doFinish() {
		BlockPos to = new BlockPos(trace.to);
		// 尝试放入箱子
		if (this.hasFlag(FLAG_INTO_CHEST)) {
			stack = BlockHelper.insertInto(world, to, null, stack);
			if (stack.isEmpty()) return;
		}
		// 强制掉落
		if (this.hasFlag(FLAG_FORCE_DROP)) {
			Block.spawnAsEntity(world, to, stack);
			return;
		}
		if (!world.isAirBlock(to)) {
			// 没放进去，就尝试破坏
			if (!this.hasFlag(FLAG_FORCE_DESTRUCT)) {
				Block.spawnAsEntity(world, to, stack);
				return;
			}
			if (!world.isRemote) world.destroyBlock(to, this.hasFlag(FLAG_DESTRUCT_DROP));
		}
		ItemStack drop = putBlock(world, player, to, stack, state, facing, null);
		if (!drop.isEmpty()) Block.spawnAsEntity(world, to, drop);
	}

	static public NBTTagCompound handleTileSave(NBTTagCompound tileSave, TileEntity tile) {
		BlockPos at = tile.getPos();

		tileSave.setInteger("x", at.getX());
		tileSave.setInteger("y", at.getY());
		tileSave.setInteger("z", at.getZ());

		ResourceLocation id = TileEntity.getKey(tile.getClass());
		if (id == null) {
			ElementalSorcery.logger.warn("回复tile数据时，找不到tile:" + tile.getClass());
			return new NBTTagCompound();
		}
		tileSave.setString("id", id.toString());

		return tileSave;
	}

	private static void loadTileSave(World world, BlockPos at, NBTTagCompound tileSave) {
		if (tileSave == null) return;
		TileEntity tile = world.getTileEntity(at);
		if (tile == null) return;
		NBTTagCompound nbt = handleTileSave(tileSave.copy(), tile);
		tile.deserializeNBT(nbt);
	}

	public static ItemStack putBlock(World world, @Nullable EntityPlayer player, BlockPos to, ItemStack stack,
			@Nullable IBlockState state, @Nullable EnumFacing facing, @Nullable NBTTagCompound tileSave) {
		if (!BlockHelper.isReplaceBlock(world, to)) return stack;
		facing = facing == null ? getFacingFromState(state) : facing;
		// 放置方块
		Item item = stack.getItem();
		if (item instanceof ItemBlock) {
			ItemBlock itemBlock = (ItemBlock) item;
			IBlockState toState = itemBlock.getBlock().getStateFromMeta(stack.getItemDamage());
			if (world.isRemote) {
				world.setBlockState(to, toState);
				return ItemStack.EMPTY;
			}

			Block block = toState.getBlock();
			if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON) {
				// 活塞
				if (state != null) toState = state.withProperty(BlockPistonBase.EXTENDED, false);
				else toState = toState.withProperty(BlockPistonBase.FACING, facing);
				world.setBlockState(to, toState);

			} else {

				if (state != null) {
					if (state.getBlock() == Blocks.UNLIT_REDSTONE_TORCH) {
						// 红石火把
						facing = state.getValue(BlockRedstoneTorch.FACING);
						toState = toState.withProperty(BlockRedstoneTorch.FACING, facing);
					} else {
						if (itemBlock instanceof ItemSlab) toState = state;
						else if (itemBlock.getBlock() == state.getBlock()) toState = state;
					}
				}

				boolean needPlace = false;
				needPlace = needPlace || block instanceof BlockDoublePlant;

				if (needPlace) {
					if (player == null) player = ESFakePlayer.get((WorldServer) world);
					itemBlock.placeBlockAt(stack, player, world, to, EnumFacing.UP, 0, 0, 0, toState);
				} else world.setBlockState(to, toState);

				loadTileSave(world, to, tileSave);

			}
			return ItemStack.EMPTY;
		} else if (stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) != null) {
			// 液体
			IFluidHandlerItem fhi = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
			FluidStack fstack = fhi.drain(1000, true);
			if (fstack != null) {
				IBlockState fluidState = fstack.getFluid().getBlock().getDefaultState();
				world.setBlockState(to, fluidState);
//				world.notifyBlockUpdate(to, fluidState, fluidState, 0);
			}
			return fhi.getContainer();
		} else if (item == Items.BED) {
			// 床
			if (state == null) return stack;
			facing = state.getValue(BlockBed.FACING);
			world.setBlockState(to, state);
			world.setBlockState(to.offset(facing), state.cycleProperty(BlockBed.PART));
			TileEntityBed bed = BlockHelper.getTileEntity(world, to, TileEntityBed.class);
			if (bed != null) bed.setColor(EnumDyeColor.byMetadata(stack.getMetadata()));
			bed = BlockHelper.getTileEntity(world, to.offset(facing), TileEntityBed.class);
			if (bed != null) bed.setColor(EnumDyeColor.byMetadata(stack.getMetadata()));
			return ItemStack.EMPTY;
		} else if (item instanceof ItemDoor) {
			if (state == null) return stack;
			ItemDoor.placeDoor(world, to, facing.rotateY(), state.getBlock(), false);
			return ItemStack.EMPTY;
		} else if (state != null) {
			world.setBlockState(to, state);
			loadTileSave(world, to, tileSave);
			return ItemStack.EMPTY;
		}
		return stack;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		stack = new ItemStack(nbt.getCompoundTag("item"));
		rate = nbt.getFloat("rate");
		flags = nbt.getByte("flags");
		facing = EnumFacing.getHorizontal(nbt.getByte("face"));
		if (nbt.hasKey("color", NBTTag.TAG_NUMBER)) color = nbt.getInteger("color");
		if (nbt.hasKey("trace", NBTTag.TAG_COMPOUND)) trace = new MoveTrace(nbt.getCompoundTag("trace"));
		if (nbt.hasKey("state", NBTTag.TAG_NUMBER)) state = Block.getStateById(nbt.getInteger("state"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setTag("item", stack.serializeNBT());
		nbt.setFloat("rate", rate);
		nbt.setByte("flags", (byte) flags);
		nbt.setByte("face", (byte) facing.getHorizontalIndex());
		if (color > 0) nbt.setInteger("color", color);
		if (trace != null) nbt.setTag("trace", trace.serializeNBT());
		if (state != null) nbt.setInteger("state", Block.getStateId(state));
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeEntityToNBT(nbt);
		ByteBufUtils.writeTag(buffer, nbt);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		readEntityFromNBT(ByteBufUtils.readTag(additionalData));
	}

	public Random getRNG() {
		return this.rand;
	}

	@SideOnly(Side.CLIENT)
	public ItemStack getRenderItem() {
		return stack;
	}

	@SideOnly(Side.CLIENT)
	public float getScale(float partialTicks) {
		if (rate < 0) {
			float r = 1 + RenderHelper.getPartialTicks(rate, rate - 0.1f, partialTicks);
			return MathHelper.sin(r * 3.1415926f / 4 * 3) / 0.7071f;
		}
		return RenderHelper.getPartialTicks(scale, lastTickSacle, partialTicks);
	}

	@SideOnly(Side.CLIENT)
	public float getRoate() {
		return facing == null ? 0 : facing.getHorizontalAngle();
	}
}
