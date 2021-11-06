package yuzunyannn.elementalsorcery.entity;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.block.BlockElfFruit;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class EntityFallingElfFruit extends Entity implements IEntityAdditionalSpawnData {

	protected IBlockState fallTile;
	public int fallTime = 0;
	public BlockPos originPos;

	public EntityFallingElfFruit(World worldIn) {
		super(worldIn);
	}

	public EntityFallingElfFruit(World worldIn, BlockPos pos) {
		super(worldIn);
		this.fallTile = worldIn.getBlockState(pos);
		this.preventEntitySpawning = true;
		this.setSize(0.98F, 0.98F);
		this.setPosition(pos.getX() + 0.5, pos.getY() + (double) ((1.0F - this.height) / 2.0F), pos.getZ() + 0.5);
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevPosX = pos.getX() + 0.5;
		this.prevPosY = pos.getY();
		this.prevPosZ = pos.getZ() + 0.5;
		this.setOrigin(pos);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(Block.getStateId(fallTile));
		BlockPos pos = getOrigin();
		buffer.writeInt(pos.getX());
		buffer.writeInt(pos.getY());
		buffer.writeInt(pos.getZ());
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		try {
			fallTile = Block.getStateById(buffer.readInt());
		} catch (Exception e) {
			fallTile = ESInit.BLOCKS.ELF_FRUIT.getDefaultState();
		}
		setOrigin(new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt()));
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	protected void entityInit() {

	}

	public void setOrigin(BlockPos pos) {
		originPos = pos;
	}

	public BlockPos getOrigin() {
		if (originPos == null) originPos = new BlockPos(this);
		return originPos;
	}

	@Override
	public void onUpdate() {
		try {
			this.onFallingUpdate();
		} catch (Exception e) {
			this.setDead();
		}
	}

	public void onFallingUpdate() {
		Block block = this.fallTile.getBlock();

		if (block != ESInit.BLOCKS.ELF_FRUIT) {
			this.setDead();
			return;
		}

		if (this.fallTile.getMaterial() == Material.AIR) {
			this.setDead();
			return;
		}

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (this.fallTime++ == 0) {
			BlockPos blockpos = new BlockPos(this);
			if (world.getBlockState(blockpos).getBlock() == block) world.setBlockToAir(blockpos);
			else if (!world.isRemote) {
				this.setDead();
				return;
			}
		}

		if (!this.hasNoGravity()) this.motionY -= 0.03999999910593033D;

		float fallDistance = this.fallDistance;
		this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

		this.motionX *= 0.9800000190734863D;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= 0.9800000190734863D;

		if (this.world.isRemote) return;
		BlockPos pos = new BlockPos(this);

		if (!this.onGround) {
			if (this.fallTime > 100 && !this.world.isRemote && (pos.getY() < 1 || pos.getY() > 256)
					|| this.fallTime > 600) {
				this.smash();
				this.setDead();
			}
			return;
		}

		IBlockState state = world.getBlockState(pos);

		boolean isSmash = false;

		BlockPos downMovePos = new BlockPos(posX, posY - 0.00999D, posZ);
		if (this.world.isAirBlock(downMovePos)) {
			if (BlockFalling.canFallThrough(world.getBlockState(downMovePos))) {
				isSmash = true;
			}
		}
		int stage = fallTile.getValue(BlockElfFruit.STAGE);
		if (state.getBlock() == Blocks.PISTON_EXTENSION) isSmash = true;
		if (stage != BlockElfFruit.MAX_STATE) isSmash = true;
		if (fallDistance > 8) isSmash = isSmash || (fallDistance - 8) / 200f > rand.nextFloat();

		this.setDead();

		if (!isSmash) {
			if (world.mayPlace(block, pos, true, EnumFacing.UP, (Entity) null)
					&& world.setBlockState(pos, this.fallTile, 3))
				return;
		}

		if (fallDistance < 4 && stage == BlockElfFruit.MAX_STATE) {
			if (world.getGameRules().getBoolean("doEntityDrops")) {
				entityDropItem(new ItemStack(block, 1, block.damageDropped(this.fallTile)), 0.0F);
				return;
			}
		}

		this.smash();

	}

	public void smash() {
		Vec3d vec = fallTile.getOffset(world, getOrigin());
		((WorldServer) this.world).spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX + vec.x,
				this.posY + this.height / 2, this.posZ + vec.z, 20, this.width / 4.0F, this.height / 4.0F,
				this.width / 4.0F, 0.05D, Block.getStateId(fallTile));

		List<PotionEffect> list = new LinkedList<>();
		int stage = fallTile.getValue(BlockElfFruit.STAGE);
		if (stage < BlockElfFruit.MAX_STATE) {
			int duration = stage == 0 ? 20 * 60 : 20 * 30;
			switch (rand.nextInt(3)) {
			case 0:
				list.add(new PotionEffect(MobEffects.HUNGER, duration, stage == 0 ? 1 : 0));
				break;
			case 1:
				list.add(new PotionEffect(MobEffects.POISON, duration, stage == 0 ? 1 : 0));
				break;
			case 2:
				list.add(new PotionEffect(MobEffects.BLINDNESS, duration, 0));
				break;
			}
		} else {
			int duration = 20 * 30;
			switch (rand.nextInt(5)) {
			case 0:
				list.add(new PotionEffect(MobEffects.SPEED, duration, 0));
				break;
			case 1:
				list.add(new PotionEffect(MobEffects.REGENERATION, duration, 0));
				break;
			case 2:
				list.add(new PotionEffect(MobEffects.RESISTANCE, duration, 0));
				break;
			case 3:
				list.add(new PotionEffect(MobEffects.POISON, duration, 0));
				break;
			}
		}

		if (list.isEmpty()) return;
		WorldHelper.applySplash(world, this.getPositionVector(), list, (entity, effect) -> {
			if (entity instanceof EntityElfBase) return !effect.getPotion().isBadEffect();
			return true;
		});
		world.playEvent(2002, new BlockPos(this), list.get(0).getPotion().getLiquidColor());
	}

	@Override
	public void fall(float distance, float damageMultiplier) {

	}

	protected void writeEntityToNBT(NBTTagCompound nbt) {
		Block block = this.fallTile != null ? this.fallTile.getBlock() : Blocks.AIR;
		ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(block);
		nbt.setString("Block", resourcelocation == null ? "" : resourcelocation.toString());
		nbt.setByte("Data", (byte) block.getMetaFromState(this.fallTile));
		nbt.setInteger("Time", this.fallTime);
		NBTHelper.setBlockPos(nbt, "origin", originPos);
	}

	protected void readEntityFromNBT(NBTTagCompound nbt) {
		int i = nbt.getByte("Data") & 255;

		if (nbt.hasKey("Block", 8)) fallTile = Block.getBlockFromName(nbt.getString("Block")).getStateFromMeta(i);
		else if (nbt.hasKey("TileID", 99)) fallTile = Block.getBlockById(nbt.getInteger("TileID")).getStateFromMeta(i);
		else fallTile = Block.getBlockById(nbt.getByte("Tile") & 255).getStateFromMeta(i);

		this.fallTime = nbt.getInteger("Time");
		Block block = fallTile.getBlock();

		if (block == null || block.getDefaultState().getMaterial() == Material.AIR)
			this.fallTile = ESInit.BLOCKS.ELF_FRUIT.getDefaultState();

		originPos = NBTHelper.getBlockPos(nbt, "origin");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean canRenderOnFire() {
		return false;
	}

	@Nullable
	public IBlockState getBlock() {
		return this.fallTile;
	}

	@Override
	public boolean ignoreItemEntityData() {
		return true;
	}

}
