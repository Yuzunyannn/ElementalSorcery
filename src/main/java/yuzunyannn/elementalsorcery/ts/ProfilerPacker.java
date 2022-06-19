package yuzunyannn.elementalsorcery.ts;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.profiler.Profiler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ProfilerPacker extends Profiler {

	public final Profiler parent;

	public ProfilerPacker(Profiler parent) {
		this.parent = parent;
	}

	@Override
	public void clearProfiling() {
		parent.clearProfiling();
	}

	@Override
	public void startSection(String name) {
		parent.startSection(name);
//		if ("clouds".equals(name)) {
//			Minecraft mc = Minecraft.getMinecraft();
//			ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, mc.entityRenderer, 0.5f, "field_175080_Q");
//			ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, mc.entityRenderer, 0.5f, "field_175081_S");
//			ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, mc.entityRenderer, 0.5f, "field_175082_R");
//		}
		if ("prepareterrain".equals(name)) PocketWatchClient.bindGray();
		if ("hand".equals(name)) PocketWatchClient.unbindGray();
	}

	@Override
	public void endSection() {
		parent.endSection();
	}

	@Override
	public void func_194340_a(Supplier<String> p_194340_1_) {
		parent.func_194340_a(p_194340_1_);
	}

	@Override
	public List<Profiler.Result> getProfilingData(String profilerName) {
		return parent.getProfilingData(profilerName);
	}

	@Override
	public void endStartSection(String name) {
		this.endSection();
		this.startSection(name);
	}

	@Override
	public String getNameOfLastSection() {
		return parent.getNameOfLastSection();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void func_194339_b(Supplier<String> p_194339_1_) {
		parent.func_194339_b(p_194339_1_);
	}

	@Override
	public void startSection(Class<?> profiledClass) {
		parent.startSection(profiledClass);
	}

}
