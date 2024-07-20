package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;

@SideOnly(Side.CLIENT)
public interface IMantraProgressable {

	public double getProgressRate(World world, IMantraData data, ICaster caster);

}
