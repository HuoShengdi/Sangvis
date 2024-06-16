package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.Misc;

public class sf_NeuralCloudInterface extends BaseHullMod {
    
    public static float MAX_CR_PENALTY = 0.15f;
    
    public static float MIN_CREW_MULT = 0.5f;
    
    public static float MAX_CREW_MULT = 0.5f;
    
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullsize, MutableShipStatsAPI stats, String id) {
        stats.getMinCrewMod().modifyMult(id, MIN_CREW_MULT);
        stats.getMaxCrewMod().modifyMult(id, MAX_CREW_MULT);

        if (isInPlayerFleet(stats)) {
            stats.getMaxCombatReadiness().modifyFlat(id, -MAX_CR_PENALTY, "Neural Cloud Interface Penalty");
        }
    }
    
    public String getDescriptionParam(int index, ShipAPI.HullSize hullsize, ShipAPI ship) {
        if (index == 0) return Math.round(MAX_CR_PENALTY * 100f) + "%";
        return null;
    }
    
    public boolean isApplicableToShip(ShipAPI ship) {
        return !Misc.isAutomated(ship);
    }
    
}