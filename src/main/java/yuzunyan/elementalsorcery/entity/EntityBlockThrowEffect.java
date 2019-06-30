package yuzunyan.elementalsorcery.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBlockThrowEffect extends Entity implements IEntityAdditionalSpawnData {

	public ItemStack stack = ItemStack.EMPTY;
	public BlockPos to = BlockPos.ORIGIN;
	public IBlockState state = null;
	private float dyaw = 0;
	private float dpitch = 0;
	private boolean is_break = false;
	private EntityPlayer player = null;

	public EntityBlockThrowEffect(World worldIn) {
		this(worldIn, new Vec3d(0, 0, 0), BlockPos.ORIGIN, ItemStack.EMPTY, null, null);
	}

	public EntityBlockThrowEffect(World worldIn, Vec3d from, BlockPos to, ItemStack stack, IBlockState state,
			EntityPlayer player) {
		super(worldIn);
		if (!world.isRemote) {
			this.stack = stack;
			this.to = to;
		}
		this.player = player;
		this.state = state;
		this.width = 0.25f;
		this.height = 0.25f;
		this.setPosition(from.x, from.y, from.z);

		if (!world.isRemote) {
			double vx = Math.random() - 0.5f;
			double vy = Math.random() - 0.5f;
			double vz = Math.random() - 0.5f;
			Vec3d v = new Vec3d(vx, vy, vz);
			Vec3d oto = new Vec3d(to.getX() + 0.5, to.getY() + 0.5, to.getZ() + 0.5);
			v = v.addVector(0, Math.random() * 0.4 + 0.2, 0).scale(0.75);
			this.motionX = v.x;
			this.motionY = v.y;
			this.motionZ = v.z;
		} else {
			dyaw = (float) (180 * (Math.random() * 0.4 + 0.2) / 20);
			dpitch = (float) (180 * (Math.random() * 0.4 + 0.2) / 20);
			if (Math.random() < 0.5)
				dyaw = -dyaw;
			if (Math.random() < 0.5)
				dpitch = -dpitch;
		}

	}

	@Override
	protected void entityInit() {

	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		ByteBufUtils.writeItemStack(buffer, stack);
		buffer.writeInt(to.getX());
		buffer.writeInt(to.getY());
		buffer.writeInt(to.getZ());
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		stack = ByteBufUtils.readItemStack(additionalData);
		int x = additionalData.readInt();
		int y = additionalData.readInt();
		int z = additionalData.readInt();
		to = new BlockPos(x, y, z);
	}

	public EntityBlockThrowEffect setBreakBlock() {
		this.is_break = true;
		return this;
	}

	static public double G = 1.25;
	static public double f = 0.9;

	@Override
	public void onUpdate() {

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.prevRotationPitch = this.rotationPitch;
		this.prevRotationYaw = this.rotationYaw;

		this.rotationPitch += dpitch;
		this.rotationYaw += dyaw;

		// 移动
		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
		// 引力
		Vec3d oto = new Vec3d(to.getX() + 0.5, to.getY() + 0.5, to.getZ() + 0.5);
		Vec3d tar = oto.subtract(this.posX, this.posY, this.posZ);
		double dis = tar.lengthSquared() + 0.1f;
		if (dis != 0) {
			double F = 1.25f / dis;
			Vec3d a = tar.scale(F);
			this.motionX += a.x;
			this.motionY += a.y;
			this.motionZ += a.z;
		}
		// 阻力
		this.motionX *= 0.9;
		this.motionY *= 0.9;
		this.motionZ *= 0.9;

		if (stack.isEmpty() || to == BlockPos.ORIGIN) {
			this.setDead();
		}
		if (dis <= Math.max(Math.max(this.motionX, this.motionY), this.motionZ)) {
			this.setDead();
			if (!world.isRemote) {
				IBlockState origin = world.getBlockState(to);
				if (this.is_break) {
					world.destroyBlock(to, true);
					this.placedAt();
				} else {
					if (origin.getBlock().isAir(origin, world, to)) {
						this.placedAt();
					} else {
						Block.spawnAsEntity(world, to, stack);
					}
				}

			}
		}
		this.firstUpdate = false;
	}

	private void placedAt() {
		if (stack.getItem() instanceof ItemBlock && Block.getBlockFromItem(stack.getItem()) == state.getBlock()) {
			((ItemBlock) stack.getItem()).placeBlockAt(stack, player, world, to, EnumFacing.DOWN, 0, 0, 0, state);
		} else
			world.setBlockState(to, state);
	}

	@SideOnly(Side.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch,
			int posRotationIncrements, boolean teleport) {
		this.setPosition(x, y, z);
		this.setRotation(yaw, pitch);
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public boolean canBeAttackedWithItem() {
		return false;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
	}

	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;

		if (Double.isNaN(d0)) {
			d0 = 4.0D;
		}

		d0 = d0 * 64.0D;
		return distance < d0 * d0;
	}

}
