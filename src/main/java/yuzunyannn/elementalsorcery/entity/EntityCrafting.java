package yuzunyannn.elementalsorcery.entity;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.crafting.ICraftingCommit;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunch;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunchAnime;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityCrafting;
import yuzunyannn.elementalsorcery.util.helper.ExceptionHelper;

public class EntityCrafting extends Entity implements IEntityAdditionalSpawnData {

	public static final DataParameter<Integer> FINISH_TICK = EntityDataManager.createKey(EntityCrafting.class,
			DataSerializers.VARINT);

	private ICraftingLaunch crafting = null;
	private ICraftingCommit commit = null;
	private NBTTagCompound commitNBT = null;
	private BlockPos pos;
	private String type;
	private EntityLivingBase player;
	/** 客户端动画 */
	private ICraftingLaunchAnime craftingAnime = null;

	public EntityCrafting(World worldIn) {
		this(worldIn, null, null, null);
	}

	public EntityCrafting(World worldIn, BlockPos pos, String type, EntityLivingBase player) {
		super(worldIn);
		this.width = 0.25f;
		this.height = 0.25f;
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		this.player = player;
		if (!world.isRemote) {
			if (pos == null) return;
			this.type = type;
			this.pos = pos;
			this.crafting = (ICraftingLaunch) world.getTileEntity(this.pos);
			this.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			this.commit = this.crafting.craftingBegin(type, player);
			if (this.commit == null) this.nullCommitException();
			else this.commit.setWorldInfo(this.world, this.pos, this.player);
		}
	}

	// 令人痛恨的情况
	private void nullCommitException() {
		this.setDead();
		String msg = "EntityCrafting的commit为null，发生于:" + this.pos + "合成类型：" + this.type;
		ESAPI.logger.warn(msg);
		ExceptionHelper.warnSend(world, msg);
	}

	@Override
	protected void entityInit() {
		dataManager.register(FINISH_TICK, Integer.valueOf(-1));
	}

	/** 存入数据 */
	private NBTTagCompound getDataNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		int[] pos = new int[] { this.pos.getX(), this.pos.getY(), this.pos.getZ() };
		nbt.setIntArray("pos", pos);
		nbt.setString("type", type);
		if (this.player != null) nbt.setInteger("enid", this.player.getEntityId());
		if (this.commit != null) nbt.setTag("cnbt", this.commit.serializeNBT());
		return nbt;
	}

	/** 恢复数据 */
	private void recoveryDataFromNBT(NBTTagCompound nbt) {
		if (nbt == null) return;
		// 回复位置
		int[] pos = nbt.getIntArray("pos");
		this.pos = new BlockPos(pos[0], pos[1], pos[2]);
		this.setPosition(this.pos.getX() + 0.5, this.pos.getY(), this.pos.getZ() + 0.5);
		// 恢复NBT
		if (nbt.hasKey("cnbt")) this.commitNBT = nbt.getCompoundTag("cnbt");
		// 恢复类型
		this.type = nbt.getString("type");
		// 恢复玩家
		Entity entity = this.world.getEntityByID(nbt.getInteger("enid"));
		if (entity instanceof EntityLivingBase) this.player = (EntityLivingBase) entity;
		else this.player = null;
	}

	private void recovery() {
		if (this.pos == null) return;
		// 恢复状态
		TileEntity tile = world.getTileEntity(this.pos);
		if (tile instanceof ICraftingLaunch) {
			this.crafting = (ICraftingLaunch) tile;
			this.commit = this.crafting.recovery(type, this.player, this.commitNBT);
			if (this.commit == null) this.nullCommitException();
			else this.commit.setWorldInfo(this.world, this.pos, this.player);
		}
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		NBTTagCompound nbt = this.getDataNBT();
		if (this.commitNBT != null && !nbt.hasKey("cnbt")) nbt.setTag("cnbt", this.commitNBT);
		ByteBufUtils.writeTag(buffer, nbt);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		if (!this.world.isRemote) return;
		NBTTagCompound nbt = ByteBufUtils.readTag(additionalData);
		this.clientRecovery(nbt);
	}

	@SideOnly(Side.CLIENT)
	private boolean clientRecovery(NBTTagCompound nbt) {
		if (nbt.hasKey("pos")) {
			this.recoveryDataFromNBT(nbt);
			TileEntity tile = world.getTileEntity(this.pos);
			if (tile instanceof ICraftingLaunch) {
				this.crafting = (ICraftingLaunch) tile;
				this.commit = this.crafting.recovery(type, this.player, this.commitNBT);
				if (this.commit == null) {
					this.nullCommitException();
					return false;
				}
				this.commit.setWorldInfo(this.world, this.pos, this.player);
				this.craftingAnime = this.crafting.getAnime(this.commit);
				if (this.craftingAnime == null) this.craftingAnime = RenderEntityCrafting.getDefultAnime();
				return true;
			}
		}
		String msg = "EntityCrafting的客户端没有收到正确的位置";
		ESAPI.logger.warn(msg);
		ExceptionHelper.warnSend(world, msg);
		return false;
	}

	private int finishTick = -1;

	@Override
	public void onUpdate() {
		try {
			this.updateOnce();
		} catch (Exception e) {
			String report = "";
			report += "crafting:" + this.crafting + "\n";
			report += "commit:" + this.crafting + "\n";
			report += "pos:" + this.pos + "\n";
			String msg = "EntityCrafting在运行的时候出现异常！报告如下：\n" + report;
			ESAPI.logger.warn(msg, e);
			ExceptionHelper.warnSend(world, msg);
			this.setDead();
		}
	}

	public void updateOnce() {
		if (crafting == null) {
			if (!world.isRemote) this.recovery();
			if (crafting == null) this.setDead();
			return;
		}
		if (world.isRemote) {
			this.updateClient();
			this.crafting.craftingUpdateClient(this.commit);
		} else {
			TileEntity tile = world.getTileEntity(this.pos);
			// 方块被打掉了
			if (this.crafting != tile) {
				this.commit.CraftingDisappear(this.world, this.pos);
				this.drop();
				this.setDead();
				return;
			}
			// 完成时间判定
			if (this.finishTick >= 0) {
				if (this.finishTick == 0) {
					this.drop();
					this.setDead();
					return;
				}
				this.finishTick--;
			} else {
				// 结束运行判定
				if (!this.crafting.canContinue(this.commit)) {
					int flags = this.crafting.craftingEnd(this.commit);
					if (flags == ICraftingLaunch.FAIL) {
						this.drop();
						this.setDead();
					} else {
						this.finishTick = this.crafting.getEndingTime(this.commit);
						dataManager.set(FINISH_TICK, finishTick);
						dataManager.setDirty(FINISH_TICK);
					}
					return;
				}
				crafting.craftingUpdate(this.commit);
			}
		}
		this.firstUpdate = false;
	}

	private void drop() {
		if (this.commit == null) return;
		List<ItemStack> itemList = this.commit.getItems();
		if (itemList == null) return;
		for (ItemStack stack : itemList) {
			if (stack.isEmpty()) continue;
			Block.spawnAsEntity(world, this.getPosition().up(), stack);
		}
	}

	private void updateClient() {
		if (this.commit == null) return;
		if (this.finishTick < 0) {
			this.finishTick = dataManager.get(FINISH_TICK);
		} else {
			if (finishTick > 0) {
				this.finishTick--;
			}
		}
		BlockPos pos = this.pos == null ? this.getPosition() : this.pos;
		this.craftingAnime.update(this.commit, this.world, pos, finishTick);
	}

	@Override
	public void setDead() {
		super.setDead();
		if (world.isRemote) {
			// 客户端的完成特效
			if (this.craftingAnime != null) this.craftingAnime.endEffect(this.commit, this.world, this.pos,
					this.finishTick >= 0 ? ICraftingLaunch.SUCCESS : ICraftingLaunch.FAIL);
		}
	}

	@SideOnly(Side.CLIENT)
	public ICraftingCommit getCommit() {
		return this.commit;
	}

	@SideOnly(Side.CLIENT)
	public static void defaultEndEffect(World world, BlockPos pos) {
		Overlay effect = new Overlay(world, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
		effect.setRBGColorF(1.0f, 1.0f, 1.0f);
		Minecraft.getMinecraft().effectRenderer.addEffect(effect);
	}

	// 临时的例子效果
	@SideOnly(Side.CLIENT)
	public static class Overlay extends ParticleFirework.Overlay {
		protected Overlay(World p_i46466_1_, double p_i46466_2_, double p_i46466_4_, double p_i46466_6_) {
			super(p_i46466_1_, p_i46466_2_, p_i46466_4_, p_i46466_6_);
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		this.recoveryDataFromNBT(compound.getCompoundTag("ecraft"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setTag("ecraft", this.getDataNBT());
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public boolean canBeAttackedWithItem() {
		return false;
	}

	// 客户端获取用于调用渲染用
	@SideOnly(Side.CLIENT)
	public ICraftingLaunchAnime getCraftingLaunchAnime() {
		return this.craftingAnime;
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
