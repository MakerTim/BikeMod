package nl.makertim.bikemod;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BikeItem extends Item {

	public BikeItem() {
		this.setCreativeTab(CreativeTabs.TRANSPORTATION);
		this.setMaxStackSize(1);
		this.setUnlocalizedName("bikes");
		this.setRegistryName(new ResourceLocation(ModInfo.MOD_ID, "bikes"));
	}

	public EnumActionResult onItemUse(ItemStack itemstack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float v0, float v1, float v2) {
		if (facing == EnumFacing.DOWN) {
			return EnumActionResult.FAIL;
		} else {
			boolean isWalkthoughAble = world.getBlockState(pos).getBlock().isReplaceable(world, pos);
			BlockPos placeLocation = isWalkthoughAble ? pos : pos.offset(facing);
			if (!player.canPlayerEdit(placeLocation, facing, itemstack)) {
				return EnumActionResult.FAIL;
			} else {
				double x = (double) placeLocation.getX();
				double y = (double) placeLocation.getY();
				double z = (double) placeLocation.getZ();
				List<Entity> entitiesNear = world.getEntitiesWithinAABBExcludingEntity(null,
					new AxisAlignedBB(x, y, z, x + 1.0D, y + 2.0D, z + 1.0D));
				if (!entitiesNear.isEmpty()) {
					return EnumActionResult.FAIL;
				} else {
					if (!world.isRemote) {
						world.setBlockToAir(placeLocation);
						BikeEntity bike = new BikeEntity(world, x + 0.5D, y, z + 0.5D);
						float lvt_22_1_ = (float) MathHelper
								.floor_float((MathHelper.wrapDegrees(player.rotationYaw - 180.0F) + 22.5F) / 45.0F)
								* 45.0F;
						bike.setLocationAndAngles(x + 0.5D, y, z + 0.5D, lvt_22_1_, 0.0F);
						ItemMonsterPlacer.applyItemEntityDataToEntity(world, player, itemstack, bike);
						world.spawnEntityInWorld(bike);
						world.playSound(null, bike.posX, bike.posY, bike.posZ,
							SoundEvents.ENTITY_ARMORSTAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);
					}

					--itemstack.stackSize;
					return EnumActionResult.SUCCESS;
				}
			}
		}
	}
}
