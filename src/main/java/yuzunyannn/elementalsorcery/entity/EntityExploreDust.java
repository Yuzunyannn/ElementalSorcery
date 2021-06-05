package yuzunyannn.elementalsorcery.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.explore.ExploreManagement;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.FirewrokShap;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;

public class EntityExploreDust extends Entity implements IEntityAdditionalSpawnData {

	protected int level;
	protected ItemStack stack = new ItemStack(ESInit.ITEMS.NATURE_CRYSTAL);
	/** 当前处理记录的数据 */
	protected NBTTagCompound data = new NBTTagCompound();

	public EntityExploreDust(World worldIn) {
		super(worldIn);
		this.setSize(1, 1);
		checkColor();
	}

	public EntityExploreDust(World worldIn, int level) {
		super(worldIn);
		this.level = level;
		checkColor();
	}

	@Override
	public void setDead() {
		if (!this.isDead) this.onDead();
		super.setDead();
	}

	@Override
	protected void entityInit() {

	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeByte(level);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		level = additionalData.readByte();
		checkColor();
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		level = compound.getByte("lev");
		data = compound.getCompoundTag("data");
		checkColor();
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setByte("lev", (byte) level);
		compound.setTag("data", data);
	}

	public int tick;

	@Override
	public void onUpdate() {
		if (world.isRemote) {
			this.onUpdateClient();
			return;
		}
		tick++;
		// 开始进行扫描
		if (tick % 10 != 0) return;
		boolean ok = ExploreManagement.instance.explore(data, world, this.getPosition(), level, null, null);
		if (ok) this.setDead();
	}

	public int[] DEAFULT_COLOR;

	public void checkColor() {
		switch (level) {
		case 1:
			DEAFULT_COLOR = new int[] { 0x179a4e, 0x056b30, 0x00856f };
			break;
		case 2:
			DEAFULT_COLOR = new int[] { 0x179a4e, 0x056b30, 0x00856f, 0x9eaa01, 0x7e3096 };
			break;
		default:
			DEAFULT_COLOR = new int[] { 0x179a4e, 0x056b30 };
			break;
		}
	}

	public void onDead() {
		if (world.isRemote) {
			Vec3d pos = this.getPositionVector().addVector(0, this.height / 2, 0);
			FirewrokShap.createECircle(world, pos, 0.25, 3, DEAFULT_COLOR);
			FirewrokShap.createECircle(world, pos, 0.5, 4, DEAFULT_COLOR);
			return;
		}
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("natureData", data);
		stack.setTagCompound(nbt);
		Block.spawnAsEntity(world, this.getPosition(), stack);
	}

	public ItemStack getStack() {
		return stack;
	}

	@SideOnly(Side.CLIENT)
	public void onUpdateClient() {
		if (EventClient.tick % 4 != 0) return;
		Vec3d pos = this.getPositionVector().addVector(rand.nextDouble() * 1.5 - 0.75, 0.1,
				rand.nextDouble() * 1.5 - 0.75);
		EffectElementMove effect = new EffectElementMove(world, pos);
		effect.lifeTime = rand.nextInt(10) + 20;
		effect.dalpha = 1.0f / effect.lifeTime;
		effect.g = 0.005f;
		effect.setVelocity(0, 0.075f, 0);
		effect.setColor(DEAFULT_COLOR[rand.nextInt(DEAFULT_COLOR.length)]);
		Effect.addEffect(effect);
	}

}
