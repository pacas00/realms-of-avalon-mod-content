package net.petercashel.RealmsOfAvalonMod.Entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.petercashel.RealmsOfAvalonMod.Entity.AI.EntityAIPlumYetiAttack;
import net.petercashel.RealmsOfAvalonMod.RealmsOfAvalonMod;

public class EntityPlumYeti extends EntityMob {

    // We reuse the zombie model which has arms that need to be raised when the zombie is attacking:
    private static final DataParameter<Boolean> ARMS_RAISED = EntityDataManager.createKey(EntityPlumYeti.class, DataSerializers.BOOLEAN);

    public static final ResourceLocation LOOT = new ResourceLocation(RealmsOfAvalonMod.MODID, "entities/plumyeti");

    public EntityPlumYeti(World worldIn) {
        super(worldIn);
        //Boundingbox
        setSize(0.6F * 2, 0.95F * 2);
        this.stepHeight = 1F;
        this.height = 2.5f;

    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(ARMS_RAISED, Boolean.valueOf(false));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        // Here we set various attributes for our mob. Like maximum health, armor, speed, ...
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
    }

    public void setArmsRaised(boolean armsRaised) {
        this.getDataManager().set(ARMS_RAISED, Boolean.valueOf(armsRaised));
    }

    @SideOnly(Side.CLIENT)
    public boolean isArmsRaised() {
        return this.getDataManager().get(ARMS_RAISED).booleanValue();
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIPlumYetiAttack(this, 2.25D, false));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.5D));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.applyEntityAI();
    }

    private void applyEntityAI() {
        this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[]{EntityPigZombie.class}));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, false));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntitySnowman.class, true));
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (super.attackEntityAsMob(entityIn)) {
            //
            try {
                entityIn.addVelocity(0, 0.75, 0);
            } catch (java.lang.NoSuchMethodError nsme) {
                System.out.println(nsme);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    protected ResourceLocation getLootTable() {
        return LOOT;
    }

    @Override
    protected boolean isValidLightLevel() {
        return true;
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 1;
    }

    @Override
    public float getRenderSizeModifier()
    {
        return 2F;
    }

    /**
     * Returns the Y Offset of this entity.
     */
    @Override
    public double getYOffset()
    {
        return 6.0D;
    }

    @Override
    public float getEyeHeight()
    {
        return this.height * 0.85F;
    }

    @Override
    public boolean startRiding(Entity entityIn, boolean force)
    {
        return false;
    }
}
