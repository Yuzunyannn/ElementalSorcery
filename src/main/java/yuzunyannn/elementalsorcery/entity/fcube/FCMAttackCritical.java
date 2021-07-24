package yuzunyannn.elementalsorcery.entity.fcube;

import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.elf.ElfTime;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class FCMAttackCritical extends FairyCubeModule {

	@FairyCubeModuleRecipe
	public static boolean matchAndConsumeForCraft(World world, BlockPos pos, IElementInventory inv) {
		ElfTime time = new ElfTime(world);
		if (!time.at(ElfTime.Period.NIGHT)) return false;

		return matchAndConsumeForCraft(world, pos, inv, ItemHelper.toList(Items.DIAMOND_AXE, 1),
				ElementHelper.toList(ESInit.ELEMENTS.FIRE, 75, 50));
	}

	protected float absorbRate = 0;

	public FCMAttackCritical(EntityFairyCube fairyCube) {
		super(fairyCube);
		this.setPriority(PriorityType.MODIFY_MULTI);
		this.setStatusCount(3);
	}

	public float getIncreased() {
		int level = this.getLevelUsed();
		return 0.05f + (float) Math.pow(level / 3f, 1.3) / 10f;
	}

	@Override
	public float modifyAttribute(String attribute, float value) {
		if ("attack:damage".equals(attribute)) {
			int status = this.getCurrStatus();
			float incr = this.getIncreased();
			float chance = 1;
			if (status == 3) chance = 0.04f;
			else if (status == 2) chance = 0.33f;
			else chance = 0.75f;
			if (fairyCube.getRNG().nextFloat() > chance) return value;
			return value * (1 + incr / chance);
		}
		return value;
	}

	@Override
	public boolean absorbElements(IElementInventory einv) {
		int dCount = (int) (8 * (1 + Math.abs(absorbRate)));
		if (absorbRate > 0) {
			ElementStack metal = new ElementStack(ESInit.ELEMENTS.FIRE, dCount, 100);
			if (absorbElementToExp(metal, einv, onceExpGetMax)) {
				absorbRate -= 0.5f;
				return true;
			}
			ElementStack wood = new ElementStack(ESInit.ELEMENTS.METAL, dCount, 100);
			if (absorbElementToExp(wood, einv, onceExpGetMax)) {
				absorbRate += 0.5f;
				return true;
			}
		} else {
			ElementStack wood = new ElementStack(ESInit.ELEMENTS.METAL, dCount, 100);
			if (absorbElementToExp(wood, einv, onceExpGetMax)) {
				absorbRate += 0.5f;
				return true;
			}
			ElementStack metal = new ElementStack(ESInit.ELEMENTS.FIRE, dCount, 125);
			if (absorbElementToExp(metal, einv, onceExpGetMax)) {
				absorbRate -= 0.5f;
				return true;
			}
		}
		return false;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		nbt.setFloat("abRa", absorbRate);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		absorbRate = nbt.getFloat("abRa");
	}

	@Override
	public String getStatusUnlocalizedValue(int status) {
		if (status == 1) return "fairy.cube.attackCritical.steady";
		else if (status == 2) return "fairy.cube.attackCritical.lucky";
		else if (status == 3) return "fairy.cube.attackCritical.unparalleled";
		return super.getStatusUnlocalizedValue(status);
	}

}
