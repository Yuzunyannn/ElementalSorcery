package yuzunyan.elementalsorcery.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyan.elementalsorcery.ElementalSorcery;
import yuzunyan.elementalsorcery.crafting.ICraftingLaunch;
import yuzunyan.elementalsorcery.crafting.ICraftingLaunchAnime;
import yuzunyan.elementalsorcery.render.entity.RenderEntityCrafting;

public class EntityCrafting extends Entity {

	public static final DataParameter<NBTTagCompound> NBT = EntityDataManager.createKey(EntityCrafting.class,
			DataSerializers.COMPOUND_TAG);
	public static final DataParameter<Integer> FINISH_TICK = EntityDataManager.createKey(EntityCrafting.class,
			DataSerializers.VARINT);

	private ICraftingLaunch crafting = null;
	private List<ItemStack> item_list = null;
	private BlockPos pos;
	private ICraftingLaunch.CraftingType type;
	private EntityPlayer player;

	@SideOnly(Side.CLIENT)
	private ICraftingLaunchAnime craftingAnime = null;

	public EntityCrafting(World worldIn) {
		this(worldIn, null, null, null);
	}

	public EntityCrafting(World worldIn, BlockPos pos, ICraftingLaunch.CraftingType type, EntityPlayer player) {
		super(worldIn);
		this.width = 0.25f;
		this.height = 0.25f;
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		this.player = player;
		if (!world.isRemote) {
			if (pos == null)
				return;
			this.type = type;
			this.pos = pos;
			this.crafting = (ICraftingLaunch) world.getTileEntity(this.pos);
			this.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			this.item_list = this.crafting.commitItems();
			dataManager.set(NBT, this.getDataNBT());
			dataManager.setDirty(NBT);
		}
	}

	@Override
	protected void entityInit() {
		dataManager.register(NBT, new NBTTagCompound());
		dataManager.register(FINISH_TICK, Integer.valueOf(-1));
	}

	private NBTTagCompound getDataNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		int[] pos = new int[] { this.pos.getX(), this.pos.getY(), this.pos.getZ() };
		nbt.setIntArray("pos", pos);
		if (item_list != null) {
			NBTTagList list = new NBTTagList();
			for (ItemStack stack : item_list) {
				if (!stack.isEmpty())
					list.appendTag(stack.serializeNBT());
			}
			nbt.setTag("item_list", list);
		}
		nbt.setInteger("type", type.ordinal());
		return nbt;

	}

	private void recoveryDataFromNBT(NBTTagCompound nbt) {
		if (nbt == null)
			return;
		// 回复位置
		int[] pos = nbt.getIntArray("pos");
		this.pos = new BlockPos(pos[0], pos[1], pos[2]);
		this.setPosition(this.pos.getX() + 0.5, this.pos.getY(), this.pos.getZ() + 0.5);
		// 恢复物品
		if (nbt.hasKey("item_list")) {
			NBTTagList list = (NBTTagList) nbt.getTag("item_list");
			item_list = new ArrayList<ItemStack>();
			for (NBTBase base : list) {
				item_list.add(new ItemStack((NBTTagCompound) base));
			}
		}
		// 恢复类型
		this.type = ICraftingLaunch.CraftingType.values()[nbt.getInteger("type")];
	}

	private int finish_tick = -1;

	@Override
	public void onUpdate() {
		if (crafting == null) {
			// 恢复状态
			if (world.isRemote) {
				NBTTagCompound nbt = dataManager.get(NBT);
				if (nbt.hasKey("pos")) {
					this.recoveryDataFromNBT(nbt);
					TileEntity tile = world.getTileEntity(this.pos);
					if (tile instanceof ICraftingLaunch) {
						this.crafting = (ICraftingLaunch) tile;
						this.crafting.craftingRecovery(type, this.player);
						this.crafting.commitItems();
						this.craftingAnime = this.crafting.getAnime();
						if (this.craftingAnime == null)
							this.craftingAnime = RenderEntityCrafting.getDefultAnime();
						return;
					}
				}
				ElementalSorcery.logger.warn("EntityCrafting的客户端没有收到正确的位置");
			} else if (this.pos != null) {
				// 这段正常情况下，在服务器启动时候才会调用
				TileEntity tile = world.getTileEntity(this.pos);
				if (tile instanceof ICraftingLaunch) {
					this.crafting = (ICraftingLaunch) tile;
					this.crafting.craftingRecovery(type, this.player);
					return;
				}
			}
			this.setDead();
			return;
		}

		if (world.isRemote) {
			this.updateClient();
			this.crafting.craftingUpdateClient();
		} else {
			TileEntity tile = world.getTileEntity(this.pos);
			// 方块被打掉了
			if (this.crafting != tile) {
				this.drop();
				this.setDead();
				return;
			}
			// 完成时间判定
			if (this.finish_tick >= 0) {
				if (this.finish_tick == 0) {
					this.drop();
					this.setDead();
					return;
				}
				this.finish_tick--;
			} else {
				// 结束运行判定
				if (!this.crafting.canContinue()) {
					int flags = this.crafting.craftingEnd(item_list);
					if (flags == ICraftingLaunch.FAIL) {
						this.drop();
						this.setDead();
					} else {
						this.finish_tick = this.crafting.getEndingTime();
						dataManager.set(FINISH_TICK, finish_tick);
						dataManager.setDirty(FINISH_TICK);
					}
					return;
				}
				crafting.craftingUpdate();
			}
		}
		this.firstUpdate = false;
	}

	private void drop() {
		if (this.item_list == null)
			return;
		if(this.type == ICraftingLaunch.CraftingType.ELEMENT_DECONSTRUCT){
			item_list.clear();
			return;
		}
		for (ItemStack stack : item_list) {
			if (stack.isEmpty())
				continue;
			Block.spawnAsEntity(world, this.getPosition().up(), stack);
		}
		item_list.clear();
	}

	private void updateClient() {
		if (item_list == null)
			return;
		if (this.finish_tick < 0) {
			this.finish_tick = dataManager.get(FINISH_TICK);
		} else {
			if (finish_tick > 0) {
				this.finish_tick--;
			}
		}
		this.craftingAnime.update(this, finish_tick);
	}

	@Override
	public void setDead() {
		super.setDead();
		if (world.isRemote) {
			// 客户端的完成特效
			if (this.finish_tick >= 0) {
				if (this.craftingAnime != null)
					this.craftingAnime.endEffect(this, this.world, this.pos);
			}
		}
	}

	public void defaultEndEffect() {
		Overlay effect = new Overlay(this.world, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
		effect.setRBGColorF(1.0f, 1.0f, 1.0f);
		Minecraft.getMinecraft().effectRenderer.addEffect(effect);
	}

	// 临时的例子效果
	public static class Overlay extends ParticleFirework.Overlay {
		protected Overlay(World p_i46466_1_, double p_i46466_2_, double p_i46466_4_, double p_i46466_6_) {
			super(p_i46466_1_, p_i46466_2_, p_i46466_4_, p_i46466_6_);
		}
	}

	public List<ItemStack> getItemList() {
		return item_list;
	}

	//private UUID uuidPlayer = null;

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		// if (compound.hasUniqueId("cPlayer")) {
		// uuidPlayer = compound.getUniqueId("cPlayer");
		// }
		this.recoveryDataFromNBT(compound.getCompoundTag("ecrafting"));
		dataManager.set(NBT, this.getDataNBT());
		dataManager.setDirty(NBT);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		// if (this.player != null) {
		// compound.setUniqueId("cPlayer", this.player.getUniqueID());
		// System.out.println("uuid已经设置！！！！！！！！！！！");
		// }
		compound.setTag("ecrafting", this.getDataNBT());
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
