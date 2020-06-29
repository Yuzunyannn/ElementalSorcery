package yuzunyannn.elementalsorcery.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.event.ITickTask;
import yuzunyannn.elementalsorcery.render.particle.FirwrokShap;

public class EntityParticleEffect extends Entity implements IEntityAdditionalSpawnData {

	static public void spawnParticleEffect(World worldIn, double x, double y, double z, NBTTagCompound nbt) {
		EntityParticleEffect effect = new EntityParticleEffect(worldIn, x, y, z, nbt);
		effect.world.spawnEntity(effect);
		effect.setDead();
	}

	ItemStack stack = ItemStack.EMPTY;

	public EntityParticleEffect(World worldIn) {
		super(worldIn);
		this.setSize(0.0F, 0.0F);
	}

	private EntityParticleEffect(World worldIn, double x, double y, double z, NBTTagCompound nbt) {
		super(worldIn);
		this.setSize(0.0f, 0.0f);
		this.setPosition(x, y, z);
		stack = new ItemStack(Items.DIAMOND);
		stack.setTagCompound(nbt);
	}

	@Override
	public void setDead() {
		super.setDead();
		if (this.world.isRemote) {
			NBTTagCompound nbt = stack.getTagCompound();
			this.showParticle(nbt == null ? new NBTTagCompound() : nbt);
		}
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		ByteBufUtils.writeItemStack(buffer, stack);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		stack = ByteBufUtils.readItemStack(additionalData);
	}

	@Override
	protected void entityInit() {
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
				if (colors.length == 0) colors = new int[] { ItemDye.DYE_COLORS[0] };
				if (fadeColors.length == 0) fadeColors = new int[] { ItemDye.DYE_COLORS[0] };
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
