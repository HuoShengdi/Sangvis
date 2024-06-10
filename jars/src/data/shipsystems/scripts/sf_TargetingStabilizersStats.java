package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class sf_TargetingStabilizersStats extends BaseShipSystemScript {

    private static final float RANGE_BONUS = 500f;

    private static final float PROJ_SPEED_BONUS = 100f;

    private float SPEED_MALUS;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        CombatEntityAPI entity = stats.getEntity();
        ShipAPI ship = null;
        if (!(entity instanceof ShipAPI)) {
            return;
        } else {
            ship = (ShipAPI) stats.getEntity();
        }

        if (ship != null) {
            if (ship.getVariant().hasHullMod("safetyoverrides")) {
                SPEED_MALUS = -75f;
            } else {
                SPEED_MALUS = -50f;
            }
        }

        stats.getTurnAcceleration().modifyFlat(id, 40f * effectLevel);
        stats.getMaxTurnRate().modifyFlat(id, 30f);

        stats.getEnergyWeaponRangeBonus().modifyFlat(id, RANGE_BONUS * effectLevel);
        stats.getBallisticWeaponRangeBonus().modifyFlat(id, RANGE_BONUS * effectLevel);
        stats.getProjectileSpeedMult().modifyPercent(id, PROJ_SPEED_BONUS * effectLevel);

        stats.getMaxSpeed().modifyFlat(id, SPEED_MALUS * effectLevel);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0 ) {
            return new StatusData("engine power redirected", false);
        } else if (index == 1) {
            return new StatusData("weapon range +" + (int) (RANGE_BONUS * effectLevel), false);
        } else if (index == 2) {
            return new StatusData("projectile speed +" + (int) (PROJ_SPEED_BONUS * effectLevel) + "%", false);
        }

        return null;
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getMaxSpeed().unmodify(id);
        stats.getProjectileSpeedMult().unmodify(id);
        stats.getBallisticWeaponRangeBonus().unmodify(id);
        stats.getEnergyWeaponRangeBonus().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
    }
}