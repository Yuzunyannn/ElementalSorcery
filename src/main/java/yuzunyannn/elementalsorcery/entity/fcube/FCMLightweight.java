package yuzunyannn.elementalsorcery.entity.fcube;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class FCMLightweight extends FairyCubeModule {

	@FairyCubeModuleRecipe
	public static boolean matchAndConsumeForCraft(World world, BlockPos pos, IElementInventory inv) {
		if (pos.getY() < 175)return false;
		return matchAndConsumeForCraft(world, pos, inv, ItemHelper.toList(Items.FEATHER, 64, Items.RABBIT_FOOT, 32),
				ElementHelper.toList(ESInit.ELEMENTS.AIR, 32, 50));
	}

	public FCMLightweight(EntityFairyCube fairyCube) {
		super(fairyCube);
		this.setPriority(PriorityType.EXECUTER);
		this.setElementNeedPerExp(new ElementStack(ESInit.ELEMENTS.AIR, 10, 50), 16);
	}

	protected int countdown = 0;

	@Override
	public void onTick(EntityLivingBase master) {
		if (countdown > 0) countdown--;
	}

	@Override
	public void onUpdate(EntityLivingBase master, IFairyCubeMaster fairyCubeMaster) {
		if (countdown > 0) return;
		if (master.ticksExisted % 20 != 0) return;
		if (!master.isSprinting()) return;

		fairyCube.setLookAt(master);
		fairyCube.doExecute(20);
	}

	@Override
	public void onStartExecute(EntityLivingBase master) {
		int level = this.getLevelUsed();
		int lastTime = 4 + (int) Math.pow(level, 1.05f);
		int potionLevel = (int) Math.max(1, level * 0.75f);
		master.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, lastTime * 20, potionLevel));
		master.addPotionEffect(new PotionEffect(MobEffects.SPEED, lastTime * 20, potionLevel));
		if (master.isInWater()) master.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, lastTime * 20, 3));
		countdown = 20 * 8 + lastTime / 2;
//		if (master instanceof EntityPlayer) {
//			EntityPlayerMP player = (EntityPlayerMP) master;
//			//player.capabilities.isFlying = true;
//			player.capabilities.allowFlying = true;
//			player.sendPlayerAbilities();
//		}
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("E", master.getEntityId());
		this.sendToClient(nbt);
	}

	@SideOnly(Side.CLIENT)
	public void onRecv(NBTTagCompound nbt) {
		int[] colors = new int[] { 0xc0fffa, 0xe5ffff, 0x77eaff };
		fairyCube.doClientSwingArm(40, colors);
		Entity entity = fairyCube.world.getEntityByID(nbt.getInteger("E"));
		if (entity == null) return;
		fairyCube.doClientCastingEntity(entity, colors);
	}

	@Override
	public String getStatusUnlocalizedValue(int status) {
		if (status == 1) return "fairy.cube.lightweight.name";
		return super.getStatusUnlocalizedValue(status);
	}

}
