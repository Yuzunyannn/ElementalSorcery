package yuzunyannn.elementalsorcery.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.particle.FirwrokShap;

public class EntityParticleEffect extends Entity {

	static public void spawnParticleEffect(World worldIn, double x, double y, double z, NBTTagCompound nbt) {
		EntityParticleEffect effect = new EntityParticleEffect(worldIn, x, y, z, nbt);
		effect.world.spawnEntity(effect);
		effect.setDead();
	}

	private static final DataParameter<ItemStack> PARTICLE_DATA = EntityDataManager
			.<ItemStack>createKey(EntityFireworkRocket.class, DataSerializers.ITEM_STACK);

	public EntityParticleEffect(World worldIn) {
		super(worldIn);
		this.setSize(0.0F, 0.0F);
	}

	private EntityParticleEffect(World worldIn, double x, double y, double z, NBTTagCompound nbt) {
		super(worldIn);
		this.setSize(0.0f, 0.0f);
		this.setPosition(x, y, z);
		ItemStack stack = new ItemStack(Items.AIR);
		stack.setTagCompound(nbt);
		this.dataManager.set(PARTICLE_DATA, stack);
		this.dataManager.setDirty(PARTICLE_DATA);
	}

	@Override
	public void setDead() {
		this.world.setEntityState(this, (byte) 17);
		super.setDead();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		if (id == 17 && this.world.isRemote) {
			ItemStack itemstack = this.dataManager.get(PARTICLE_DATA);
			NBTTagCompound nbt = itemstack.getTagCompound();
			this.showParticle(nbt == null ? new NBTTagCompound() : nbt);
		}
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
	protected void entityInit() {
		this.dataManager.register(PARTICLE_DATA, ItemStack.EMPTY);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
	}

	@SideOnly(Side.CLIENT)
	protected void showParticle(NBTTagCompound nbt) {
		if (nbt.hasKey("Explosions", 9)) {
			NBTTagList list = nbt.getTagList("Explosions", 10);
			for (NBTBase base : list) {
				nbt = (NBTTagCompound) base;
				byte type = nbt.getByte("Type");
				int size = nbt.getInteger("Size");
				double speed = nbt.getDouble("Speed");
				boolean trail = nbt.getBoolean("Trail");
				boolean flicker = nbt.getBoolean("Flicker");
				int[] colors = nbt.getIntArray("Colors");
				int[] fadeColors = nbt.getIntArray("FadeColors");
				if (colors.length == 0)
					colors = new int[] { ItemDye.DYE_COLORS[0] };
				if (fadeColors.length == 0)
					fadeColors = new int[] { ItemDye.DYE_COLORS[0] };
				switch (type) {
				case 0:
					FirwrokShap.createBall(this.world, new Vec3d(this.posX, this.posY, this.posZ), speed, size, colors,
							fadeColors, trail, flicker);
					break;
				case 1:
					FirwrokShap.createCircle(this.world, new Vec3d(this.posX, this.posY, this.posZ), speed, size,
							colors, fadeColors, trail, flicker);
					break;
				default:
					break;
				}
			}
		}
	}
}
