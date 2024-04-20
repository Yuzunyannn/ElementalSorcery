package yuzunyannn.elementalsorcery.dungeon;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncJsonCreateContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncTimes;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.util.json.JsonObject;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class DungeonFuncPotion extends GameFuncTimes {

	protected List<PotionEffect> effects = new ArrayList<>();
	protected boolean isLingering;

	@Override
	public void loadFromJson(JsonObject json, GameFuncJsonCreateContext context) {
		super.loadFromJson(json, context);
		effects = json.needPotionEffects("effects");
		isLingering = json.hasBoolean("isLingering") ? json.getBoolean("isLingering") : false;
	}

	@Override
	protected void execute(GameFuncExecuteContext context) {
		Vec3d vec = context.getSrcObj().getObjectPosition();
		World world = context.getWorld();
		WorldHelper.applyPotion(world, vec, context.getSrcObj().toEntityLiving(), effects, isLingering);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		if (isLingering) nbt.setBoolean("isLingering", true);
		NBTTagList list = new NBTTagList();
		nbt.setTag("effects", list);
		for (PotionEffect effect : effects) {
			NBTTagCompound edata = new NBTTagCompound();
			list.appendTag(edata);
			edata.setString("id", effect.getPotion().getRegistryName().toString());
			edata.setShort("duration", (short) effect.getDuration());
			edata.setShort("amplifier", (short) effect.getAmplifier());
		}
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		isLingering = nbt.getBoolean("isLingering");
		effects.clear();
		NBTTagList list = nbt.getTagList("effects", NBTTag.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound edata = list.getCompoundTagAt(i);
			Potion potion = Potion.getPotionFromResourceLocation(edata.getString("id"));
			if (potion == null) continue;
			effects.add(new PotionEffect(potion, edata.getInteger("duration"), edata.getInteger("amplifier")));
		}
	}

	@Override
	public String toString() {
		return "<Potion> potions:" + effects;
	}

}
