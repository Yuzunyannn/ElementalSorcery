package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunch;
import yuzunyannn.elementalsorcery.entity.EntityCrafting;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.ICasterObject;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.MantraEffectFlags;
import yuzunyannn.elementalsorcery.grimoire.WantedTargetResult;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicCircle;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicCircleMantra;
import yuzunyannn.elementalsorcery.tile.altar.TileStaticMultiBlock;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class MantraLaunch extends MantraCommon {

	protected String type;
	protected int spellTick = 20;

	public MantraLaunch(String type, int color) {
		this.type = type;
		this.setTranslationKey(TextHelper.castToCamel("launch_" + type));
		this.setColor(color);
		this.setIcon(TextHelper.toESResourceLocation("textures/mantras/launch_" + type + ".png"));
		this.setRarity(150);
		this.setOccupation(1);
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {

		WantedTargetResult wr = caster.iWantBlockTarget();
		BlockPos pos = wr.getPos();
		if (pos == null) return;

		ICraftingLaunch tile = BlockHelper.getTileEntity(world, pos, ICraftingLaunch.class);
		if (tile == null) return;

		MantraDataCommon dataEffect = (MantraDataCommon) data;
		dataEffect.markContinue(true);
		dataEffect.set(POS, pos);
	}

	@Override
	public void onSpelling(World world, IMantraData data, ICaster caster) {

		MantraDataCommon mData = (MantraDataCommon) data;
		if (!mData.isMarkContinue()) return;

		int tick = caster.iWantKnowCastTick();
		if (tick >= spellTick) {
			if (world.isRemote) this.onSpellingEffect(world, mData, caster);
			this.endSpelling(world, mData, caster);
			return;
		}

		BlockPos pos = mData.get(POS);
		if (pos.equals(BlockPos.ORIGIN)) {
			mData.markContinue(false);
			return;
		}

		ICasterObject co = caster.iWantCaster();
		if (co.getPositionVector().squareDistanceTo(new Vec3d(pos)) > 16 * 16) {
			mData.markContinue(false);
			return;
		}

		if (world.isRemote) {
			this.onSpellingEffect(world, mData, caster);
			return;
		}

		TileStaticMultiBlock tile = BlockHelper.getTileEntity(world, pos, TileStaticMultiBlock.class);
		if (tile == null) return;

		tile.isAndCheckIntact();
	}

	@Override
	public void endSpelling(World world, IMantraData data, ICaster caster) {

		int tick = caster.iWantKnowCastTick();
		if (tick < spellTick) return;

		MantraDataCommon mData = (MantraDataCommon) data;
		if (!mData.isMarkContinue()) return;
		mData.markContinue(false);

		BlockPos pos = mData.get(POS);
		if (pos.equals(BlockPos.ORIGIN)) return;

		ICraftingLaunch tile = BlockHelper.getTileEntity(world, pos, ICraftingLaunch.class);
		if (tile == null || tile.isWorking()) return;

		if (world.isRemote) return;
		
		EntityPlayer player = caster.iWantCaster().asPlayer();

		if (!tile.canCrafting(type, player)) {
			world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1.0f, false);
			return;
		}

		EntityCrafting ecrafting = new EntityCrafting(world, pos, type, player);
		world.spawnEntity(ecrafting);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getProgressRate(World world, IMantraData data, ICaster caster) {
		int tick = Math.min(caster.iWantKnowCastTick(), spellTick);
		return tick / (float) spellTick;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData data, ICaster caster) {
		super.onSpellingEffect(world, data, caster);
		if (!hasEffectFlags(world, data, caster, MantraEffectFlags.DECORATE)) return;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EffectMagicCircle getEffectMagicCircle(World world, EntityLivingBase entity, IMantraData data) {
		MantraDataCommon mData = (MantraDataCommon) data;
		BlockPos pos = mData.get(POS);
		EffectMagicCircleMantra emc = new EffectMagicCircleMantra(world, pos, this.getIconResource());
		emc.setColor(this.getColor(mData));
		return emc;
	}

}
