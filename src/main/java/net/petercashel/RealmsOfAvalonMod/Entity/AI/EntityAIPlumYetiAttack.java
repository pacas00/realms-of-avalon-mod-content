package net.petercashel.RealmsOfAvalonMod.Entity.AI;

import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.petercashel.RealmsOfAvalonMod.Entity.EntityPlumYeti;

public class EntityAIPlumYetiAttack extends EntityAIAttackMelee {

    private int raiseArmTicks;
    private EntityPlumYeti entPlumYeti;

    public EntityAIPlumYetiAttack(EntityPlumYeti entityPlumYeti, double speedIn, boolean longMemoryIn) {
        super(entityPlumYeti, speedIn, longMemoryIn);
        this.entPlumYeti = entityPlumYeti;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        super.startExecuting();
        this.raiseArmTicks = 0;
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        super.resetTask();
        this.entPlumYeti.setArmsRaised(false);
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        super.updateTask();
        ++this.raiseArmTicks;

        if (this.raiseArmTicks >= 5 && this.attackTick < 10) {
            this.entPlumYeti.setArmsRaised(true);
        } else {
            this.entPlumYeti.setArmsRaised(false);
        }
    }
}
