package yuzunyannn.elementalsorcery.dungeon;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncJsonCreateContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncTimes;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.util.helper.MantraHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class DungeonFuncMantra extends GameFuncTimes {

	protected Mantra mantra;
	protected List<ElementStack> eStacks;
	protected Vec3d offset = Vec3d.ZERO;
	protected Vec3d orient = Vec3d.ZERO;
	protected Vec3d moveVec = Vec3d.ZERO;
	protected boolean isVertical = false;
	protected double moveSpeed = 0;
	protected double potent = 0;
	protected DungeonIntegerLoader duration = DungeonIntegerLoader.of(40);
	protected String targetGetter;

	@Override
	public void loadFromJson(JsonObject json, GameFuncJsonCreateContext context) {
		super.loadFromJson(json, context);
		mantra = Mantra.REGISTRY.getValue(new ResourceLocation(json.needString("mantra")));
		if (mantra == null) throw new RuntimeException("cannot find mantra " + json.needString("mantra"));
		eStacks = json.needElements("elements");
		offset = DungeonFuncEntity.getVec3d(json, "offset");
		orient = DungeonFuncEntity.getVec3d(json, "orient");
		if (offset.x + offset.y + offset.z == 0) offset = Vec3d.ZERO;
		if (orient.x + orient.y + orient.z == 0) orient = Vec3d.ZERO;
		moveVec = DungeonFuncEntity.getVec3d(json, "moveVec");
		if (moveVec == Vec3d.ZERO) {
			moveSpeed = json.hasNumber("moveSpeed") ? json.getNumber("moveSpeed").doubleValue() : 0.02;
			moveVec = new Vec3d(0, moveSpeed, 0);
		} else moveSpeed = moveVec.length();
		isVertical = json.hasBoolean("isVertical") ? json.getBoolean("isVertical") : false;
		potent = json.hasNumber("potent") ? json.getNumber("potent").doubleValue() : 0;
		duration = DungeonIntegerLoader.get(json, "duration", 40);
		targetGetter = json.hasString("targetGetter") ? json.getString("targetGetter") : null;
	}

	protected EntityLivingBase getTarget(GameFuncExecuteContext context) {
		if (targetGetter == null) return null;
		if ("killer".equals(targetGetter)) {
			Event evevt = context.getEvent();
			if (evevt instanceof LivingDeathEvent) {
				DamageSource ds = ((LivingDeathEvent) evevt).getSource();
				if (ds != null) {
					Entity entity = ds.getTrueSource();
					if (entity instanceof EntityLivingBase) return (EntityLivingBase) entity;
				}
			}
		}
		return null;
	}

	@Override
	protected void execute(GameFuncExecuteContext context) {
		if (mantra == null) return;
		Random rand = getCurrRandom();
		Vec3d vec = context.getSrcObj().getObjectPosition();
		World world = context.getWorld();

		EntityLivingBase caster = context.getSrcObj().asEntityLivingBase();
		int duration = this.duration.getInteger(rand.nextInt());
		Vec3d orient = this.orient;
		Vec3d castVec = vec.add(offset);
		EntityLivingBase target = getTarget(context);
		if (caster != null) {
			if (orient == Vec3d.ZERO) orient = caster.getLookVec();
		} else {
			if (orient == Vec3d.ZERO) orient = new Vec3d(0, 1, 0);
		}

		if (target != null) MantraHelper.autoTrace(world, caster, castVec, orient, target, moveSpeed, duration, potent,
				mantra, eStacks);
		else if (isVertical) {
			boolean isRev = orient.y > 0 ? true : false;
			MantraHelper.autoArea(world, caster, castVec, isRev, duration, potent, mantra, eStacks);
		} else MantraHelper.autoDirect(world, caster, castVec, orient, moveVec, duration, mantra, eStacks);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		if (mantra == null) return nbt;
		if (offset != Vec3d.ZERO) NBTHelper.setVec3d(nbt, "offset", offset);
		if (orient != Vec3d.ZERO) NBTHelper.setVec3d(nbt, "orient", orient);
		if (moveVec != Vec3d.ZERO) NBTHelper.setVec3d(nbt, "moveVec", moveVec);
		NBTHelper.setElementList(nbt, "elements", eStacks);
		nbt.setString("mantra", mantra.getRegistryName().toString());
		nbt.setTag("duration", duration.serializeNBT());
		nbt.setBoolean("isVertical", isVertical);
		nbt.setFloat("potent", (float) potent);
		if (targetGetter != null) nbt.setString("tg", targetGetter);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);

		if (nbt.hasKey("offset")) offset = NBTHelper.getVec3d(nbt, "offset");
		else offset = Vec3d.ZERO;

		if (nbt.hasKey("orient")) orient = NBTHelper.getVec3d(nbt, "orient");
		else orient = Vec3d.ZERO;

		if (nbt.hasKey("moveVec")) moveVec = NBTHelper.getVec3d(nbt, "moveVec");
		else moveVec = Vec3d.ZERO;
		moveSpeed = moveVec.length();

		duration = DungeonIntegerLoader.get(nbt.getTag("duration"), 40);
		potent = nbt.getDouble("potent");
		isVertical = nbt.getBoolean("isVertical");
		eStacks = NBTHelper.getElementList(nbt, "elements");
		mantra = Mantra.REGISTRY.getValue(new ResourceLocation(nbt.getString("mantra")));
		targetGetter = nbt.getString("tg");
		if (targetGetter.isEmpty()) targetGetter = null;
	}

	@Override
	public String toString() {
		return "<Mantra> mantra:" + mantra;
	}

}
