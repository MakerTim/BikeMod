package nl.makertim.bikemod;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BikeEntity extends Entity {

	private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.createKey(EntityBoat.class,
		DataSerializers.VARINT);
	private static final DataParameter<Integer> FORWARD_DIRECTION = EntityDataManager.createKey(EntityBoat.class,
		DataSerializers.VARINT);
	private static final DataParameter<Float> DAMAGE_TAKEN = EntityDataManager.createKey(EntityBoat.class,
		DataSerializers.FLOAT);

	private float prefMotion;
	private boolean forwardInputDown;
	private boolean backInputDown;

	public BikeEntity(World worldIn) {
		super(worldIn);
		this.preventEntitySpawning = true;
		this.setSize(1, 1);
	}

	public BikeEntity(World worldIn, double x, double y, double z) {
		this(worldIn);
		this.setPosition(x, y, z);
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevPosX = x;
		this.prevPosY = y;
		this.prevPosZ = z;
	}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they
	 * walk on. used for spiders and wolves to prevent them from trampling crops
	 */
	protected boolean canTriggerWalking() {
		return false;
	}

	protected void entityInit() {
		this.dataManager.register(TIME_SINCE_HIT, 0);
		this.dataManager.register(FORWARD_DIRECTION, 1);
		this.dataManager.register(DAMAGE_TAKEN, 0F);
	}

	/**
	 * Returns a boundingBox used to collide the entity with other entities and
	 * blocks. This enables the entity to be pushable on contact, like boats or
	 * minecarts.
	 */
	public AxisAlignedBB getCollisionBox(Entity entityIn) {
		return entityIn.getEntityBoundingBox();
	}

	/**
	 * Returns the collision bounding box for this entity
	 */
	public AxisAlignedBB getCollisionBoundingBox() {
		return this.getEntityBoundingBox();
	}

	/**
	 * Returns true if this entity should push and be pushed by other entities
	 * when colliding.
	 */
	public boolean canBePushed() {
		return false;
	}

	/**
	 * Returns the Y offset from the entity's position for any entity riding
	 * this one.
	 */
	public double getMountedYOffset() {
		return 0.55D;
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isEntityInvulnerable(source)) {
			return false;
		} else if (!this.worldObj.isRemote && !this.isDead) {
			if (source instanceof EntityDamageSourceIndirect && source.getEntity() != null
					&& this.isPassenger(source.getEntity())) {
				return false;
			} else {
				this.setForwardDirection(-this.getForwardDirection());
				this.setTimeSinceHit(10);
				this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);
				this.setBeenAttacked();
				boolean flag = source.getEntity() instanceof EntityPlayer
						&& ((EntityPlayer) source.getEntity()).capabilities.isCreativeMode;
				if (flag || this.getDamageTaken() > 40.0F) {
					if (!flag && this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
						this.dropItemWithOffset(Bikes.item, 1, 0.0F);
					}
					this.setDead();
				}
				return true;
			}
		} else {
			return true;
		}
	}

	/**
	 * Applies a velocity to the entities, to push them away from eachother.
	 */
	public void applyEntityCollision(Entity entityIn) {
		if (entityIn instanceof BikeEntity) {
			if (prefMotion > 1) {
				this.setDead();
			} else if (entityIn.getEntityBoundingBox().minY < this.getEntityBoundingBox().maxY) {
				super.applyEntityCollision(entityIn);
			}
		} else if (entityIn.getEntityBoundingBox().minY <= this.getEntityBoundingBox().minY) {
			super.applyEntityCollision(entityIn);
		}
	}

	/**
	 * Setups the entity to do the hurt animation. Only used by packets in
	 * multiplayer.
	 */
	@SideOnly(Side.CLIENT)
	public void performHurtAnimation() {
		this.setForwardDirection(-this.getForwardDirection());
		this.rotationYaw += (Math.random() * 10) - 5;
		this.setTimeSinceHit(10);
		this.setDamageTaken(this.getDamageTaken() * 11.0F);
	}

	/**
	 * Returns true if other Entities should be prevented from moving through
	 * this Entity.
	 */
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	/*
	 * Gets the horizontal facing direction of this Entity, adjusted to take
	 * specially-treated entity types into account.
	 */
	@Override
	public EnumFacing getAdjustedHorizontalFacing() {
		return this.getHorizontalFacing().rotateY();
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		if (this.getTimeSinceHit() > 0) {
			this.setTimeSinceHit(this.getTimeSinceHit() - 1);
		}

		if (this.getDamageTaken() > 0.0F) {
			this.setDamageTaken(this.getDamageTaken() - 1.0F);
		}

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		super.onUpdate();

		this.updateMotion();
		if (this.worldObj.isRemote) {
			this.controlBoat();
			this.worldObj.sendPacketToServer(new CPacketVehicleMove(this));
		}

		this.moveEntity(this.motionX, this.motionY, this.motionZ);

		this.doBlockCollisions();
		List<Entity> list = this.worldObj.getEntitiesInAABBexcluding(this,
			this.getEntityBoundingBox().expand(0.2D, -0.01D, 0.2D), EntitySelectors.getTeamCollisionPredicate(this));

		if (!list.isEmpty()) {
			boolean flag = !this.worldObj.isRemote && !(this.getControllingPassenger() instanceof EntityPlayer);

			for (Entity entity : list) {
				if (!entity.isPassenger(this)) {
					if (flag && this.getPassengers().size() < 2 && !entity.isRiding() && entity.width < this.width
							&& entity instanceof EntityLivingBase && !(entity instanceof EntityWaterMob)
							&& !(entity instanceof EntityPlayer)) {
						entity.startRiding(this);
					} else {
						this.applyEntityCollision(entity);
					}
				}
			}
		}
	}

	/**
	 * Update the boat's speed, based on momentum.
	 */
	private void updateMotion() {
		double d1 = this.func_189652_ae() ? 0.0D : -0.04D;
		/* How much of current speed to retain. Value zero to one. */
		float momentum = 0.9F;
		this.motionX *= (double) momentum;
		this.motionZ *= (double) momentum;
		this.motionY += d1;
	}

	private void controlBoat() {
		float f = (float) Math.pow(prefMotion, 2) / 1.5F;
		if (this.isBeingRidden()) {
			if (this.forwardInputDown) {
				f += 0.075F;
			}
			if (this.backInputDown) {
				f -= 0.075F;
			}

			if (!getPassengers().isEmpty()) {
				Entity rider = getPassengers().get(0);
				float riderYaw = rider.rotationYaw % 360;
				if (Math.round(rotationYaw) != Math.round(riderYaw)) {
					// TODO: Fix als je roteerd = sliprem
					// TODO: Fix draaien
					// TODO: hoe groter bocht = meer remmen
					updateMotion();
					System.out.printf("%f != %f\n", rotationYaw, riderYaw);
					float yawDiff = rotationYaw - riderYaw;
					float max = 50;
					yawDiff = MathHelper.clamp_float(yawDiff, Math.min(-1F, -1F * f * max), Math.max(1F, 1F * f * max))
							* (Math.abs(yawDiff) / 12);
					if (yawDiff > max && !(yawDiff > max - 360)) {
						yawDiff = rotationYaw - riderYaw;
					}
					prevRotationYaw = rotationYaw;
					rotationYaw -= yawDiff;
					rotationYaw %= 360;
				}
			}
			if (f < 0) {
				f = 0;
			}

			this.motionX += (double) (MathHelper.sin(-this.rotationYaw * 0.0175F) * f);
			this.motionZ += (double) (MathHelper.cos(this.rotationYaw * 0.0175F) * f);
			this.prefMotion = f;
		} else {
			f /= 3;
		}
		if (f > 2.5F) {
			f = 2.5F;
		}
		this.motionX += (double) (MathHelper.sin(-this.rotationYaw * 0.0175F) * f);
		this.motionZ += (double) (MathHelper.cos(this.rotationYaw * 0.0175F) * f);
		this.prefMotion = f;
	}

	public void updatePassenger(Entity passenger) {
		if (this.isPassenger(passenger)) {
			float f = 0.0F;
			float f1 = (float) ((this.isDead ? 0.001D : this.getMountedYOffset()) + passenger.getYOffset());

			Vec3d vec3d = (new Vec3d((double) f, 0.0D, 0.0D))
					.rotateYaw(-this.rotationYaw * 0.0175F - ((float) Math.PI / 2F));
			passenger.setPosition(this.posX + vec3d.xCoord, this.posY + (double) f1, this.posZ + vec3d.zCoord);
			this.applyYawToEntity(passenger);

			if (passenger instanceof EntityAnimal && this.getPassengers().size() > 1) {
				int j = passenger.getEntityId() % 2 == 0 ? 90 : 270;
				passenger.setRenderYawOffset(((EntityAnimal) passenger).renderYawOffset + (float) j);
				passenger.setRotationYawHead(passenger.getRotationYawHead() + (float) j);
			}
		}
	}

	/**
	 * Applies this boat's yaw to the given entity. Used to update the
	 * orientation of its passenger.
	 */
	protected void applyYawToEntity(Entity entityToUpdate) {
		entityToUpdate.setRenderYawOffset(this.rotationYaw);
		float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
		float f1 = MathHelper.clamp_float(f, -60.0F, 60.0F);
		entityToUpdate.prevRotationYaw += f1 - f;
		entityToUpdate.rotationYaw += f1 - f;
		entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
	}

	/**
	 * Applies this entity's orientation (pitch/yaw) to another entity. Used to
	 * update passenger orientation.
	 */
	@SideOnly(Side.CLIENT)
	public void applyOrientationToEntity(Entity entityToUpdate) {
		this.applyYawToEntity(entityToUpdate);
	}

	protected void writeEntityToNBT(NBTTagCompound compound) {
	}

	protected void readEntityFromNBT(NBTTagCompound compound) {
	}

	public boolean processInitialInteract(EntityPlayer player, @Nullable ItemStack stack, EnumHand hand) {
		if (!this.worldObj.isRemote && !player.isSneaking()) {
			player.startRiding(this);
		}
		return true;
	}

	/**
	 * Sets the damage taken from the last hit.
	 */
	public void setDamageTaken(float damageTaken) {
		this.dataManager.set(DAMAGE_TAKEN, damageTaken);
	}

	/**
	 * Gets the damage taken from the last hit.
	 */
	public float getDamageTaken() {
		return this.dataManager.get(DAMAGE_TAKEN);
	}

	/**
	 * Sets the time to count down from since the last time entity was hit.
	 */
	public void setTimeSinceHit(int timeSinceHit) {
		this.dataManager.set(TIME_SINCE_HIT, timeSinceHit);
	}

	/**
	 * Gets the time since the last hit.
	 */
	public int getTimeSinceHit() {
		return this.dataManager.get(TIME_SINCE_HIT);
	}

	/**
	 * Sets the forward direction of the entity.
	 */
	public void setForwardDirection(int forwardDirection) {
		this.dataManager.set(FORWARD_DIRECTION, forwardDirection);
	}

	/**
	 * Gets the forward direction of the entity.
	 */
	public int getForwardDirection() {
		return this.dataManager.get(FORWARD_DIRECTION);
	}

	protected boolean canFitPassenger(Entity passenger) {
		return this.getPassengers().size() < 2;
	}

	/**
	 * For vehicles, the first passenger is generally considered the controller
	 * and "drives" the vehicle. For example, Pigs, Horses, and Boats are
	 * generally "steered" by the controlling passenger.
	 */
	@Nullable
	public Entity getControllingPassenger() {
		List<Entity> list = this.getPassengers();
		return list.isEmpty() ? null : list.get(0);
	}

	public void updateForward(boolean key) {
		this.forwardInputDown = key;
	}

	public void updateBrake(boolean key) {
		this.backInputDown = key;
	}
}
