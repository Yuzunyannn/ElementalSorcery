package yuzunyannn.elementalsorcery.util.helper;

import java.util.Collection;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.entity.EntityAutoMantra;
import yuzunyannn.elementalsorcery.entity.EntityAutoMantra.AutoMantraConfig;

public class MantraHelper {

	static public void autoDirect(World world, EntityLivingBase caster, Vec3d start, Vec3d orient, Vec3d move, int tick,
			Mantra mantra, Collection<ElementStack> elements) {
		AutoMantraConfig config = new EntityAutoMantra.AutoMantraConfig();
		config.setMoveVec(move);
		EntityAutoMantra mantraEntity = new EntityAutoMantra(world, config, caster, mantra, null);
		mantraEntity.setPosition(start.x, start.y, start.z);
		mantraEntity.setSpellingTick(tick);
		mantraEntity.setOrient(orient);
		IElementInventory elementInv = mantraEntity.getElementInventory();
		for (ElementStack eStack : elements) elementInv.insertElement(eStack, false);
		world.spawnEntity(mantraEntity);
	}

	static public void autoArea(World world, EntityLivingBase caster, Vec3d start, boolean isRev, int tick,
			double potent, Mantra mantra, Collection<ElementStack> elements) {
		AutoMantraConfig config = new EntityAutoMantra.AutoMantraConfig();
		config.setMoveVec(Vec3d.ZERO);
		if (isRev) config.blockTrack = AutoMantraConfig.BLOCKTRACK_DIRECT_REVERSE;
		EntityAutoMantra mantraEntity = new EntityAutoMantra(world, config, caster, mantra, null);
		mantraEntity.setPosition(start.x, start.y, start.z);
		mantraEntity.setSpellingTick(tick);
		mantraEntity.setOrient(new Vec3d(0, isRev ? 1 : -1, 0));
		if (potent > 0) mantraEntity.iWantGivePotent((float) potent, 100);
		IElementInventory elementInv = mantraEntity.getElementInventory();
		for (ElementStack eStack : elements) elementInv.insertElement(eStack, false);
		world.spawnEntity(mantraEntity);
	}

	static public void autoTrace(World world, EntityLivingBase caster, Vec3d start, Vec3d orient,
			EntityLivingBase target, double move, int tick, double potent, Mantra mantra,
			Collection<ElementStack> elements) {
		AutoMantraConfig config = new EntityAutoMantra.AutoMantraConfig();
		config.setTarget(target, move);
		EntityAutoMantra mantraEntity = new EntityAutoMantra(world, config, caster, mantra, null);
		mantraEntity.setPosition(start.x, start.y, start.z);
		mantraEntity.setSpellingTick(tick);
		mantraEntity.setOrient(orient);
		if (potent > 0) mantraEntity.iWantGivePotent((float) potent, 100);
		IElementInventory elementInv = mantraEntity.getElementInventory();
		for (ElementStack eStack : elements) elementInv.insertElement(eStack, false);
		world.spawnEntity(mantraEntity);
	}
}
