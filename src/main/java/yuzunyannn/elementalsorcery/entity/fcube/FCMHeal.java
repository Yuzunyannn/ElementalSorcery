package yuzunyannn.elementalsorcery.entity.fcube;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.entity.FairyCubeModule;
import yuzunyannn.elementalsorcery.api.entity.FairyCubeModuleRecipe;
import yuzunyannn.elementalsorcery.api.entity.IFairyCubeMaster;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.elf.ElfTime;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class FCMHeal extends FairyCubeModule {

	@FairyCubeModuleRecipe
	public static boolean matchAndConsumeForCraft(World world, BlockPos pos, IElementInventory inv) {
		ElfTime time = new ElfTime(world);
		if (!time.at(ElfTime.Period.AFTERNOON)) return false;
		return FairyCubeModuleInGame.matchAndConsumeForCraft(world, pos, inv, ItemHelper.toList(Items.GOLDEN_APPLE, 4),
				ElementHelper.toList(ESObjects.ELEMENTS.WOOD, 10, 400));
	}

	public FCMHeal(EntityFairyCube fairyCube) {
		super(fairyCube);
		this.setPriority(PriorityType.EXECUTER);
		this.setElementNeedPerExp(new ElementStack(ESObjects.ELEMENTS.WOOD, 12, 100), 16);
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

		float hp = master.getHealth();
		if (hp > 10) return;
		fairyCube.setLookAt(master);
		fairyCube.doExecute(20);
	}

	@Override
	public void onStartExecute(EntityLivingBase master) {
		int cdSec = 20;
		int level = this.getLevelUsed();
		cdSec = Math.max(cdSec - (int) (level * 0.9f), 8);
		countdown = 20 * cdSec;

		float healPoint = 4 + (float) Math.pow(level, 1.4f);
		float realPoint = master.getMaxHealth() - master.getHealth();
		float overflow = healPoint - realPoint;
		master.heal(Math.min(realPoint, healPoint));
		if (overflow > 0) {
			int time = MathHelper.floor(overflow / 20f * 6);
			master.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, time * 20, 3));
		}

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("E", master.getEntityId());
		this.sendToClient(nbt);
	}

	@Override
	public String getStatusUnlocalizedValue(int status) {
		if (status == 1) return "effect.regeneration";
		return super.getStatusUnlocalizedValue(status);
	}

	@SideOnly(Side.CLIENT)
	public void onRecv(NBTTagCompound nbt) {
		int[] colors = new int[] { 0xe61818, 0xfa6f6f, 0xa00a0a };
		fairyCube.doClientSwingArm(20, colors);
		Entity entity = fairyCube.getWorld().getEntityByID(nbt.getInteger("E"));
		if (entity == null) return;
		fairyCube.doClientCastingEffect(entity, colors);
	}

}
