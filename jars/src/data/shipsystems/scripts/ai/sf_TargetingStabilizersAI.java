package data.shipsystems.scripts.ai;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import org.lazywizard.lazylib.combat.AIUtils;

import java.util.List;

public class sf_TargetingStabilizersAI implements ShipSystemAIScript {

    private ShipSystemAPI system;

    private ShipAPI ship;

    private ShipwideAIFlags flags;
    
    private CombatEngineAPI engine;

    private float nominalRange = 0;
    
    private float activeRange = 0;
    
    private final IntervalUtil tracker = new IntervalUtil(0.35f, 0.6f);
    
    private boolean runOnce = false;
    
    @Override
    public void init(ShipAPI shipAPI, ShipSystemAPI shipSystemAPI, ShipwideAIFlags shipwideAIFlags, CombatEngineAPI combatEngineAPI) {
        this.ship = shipAPI;
        this.system = shipSystemAPI;
        this.flags = shipwideAIFlags;
        this.engine = combatEngineAPI;
    }

    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionVectorDir, ShipAPI target) {
        if (engine.isPaused() || ship.getShipAI() == null) {
            return;
        }
        
        if (!runOnce) {
            runOnce = true;
            List<WeaponAPI> weapons = ship.getAllWeapons();
            int numValidWeapons = 0;
            
            for (WeaponAPI w : weapons) {
                if ((w.getType() == WeaponAPI.WeaponType.BALLISTIC 
                        || w.getType() == WeaponAPI.WeaponType.ENERGY 
                        || w.getType() != WeaponAPI.WeaponType.MISSILE) 
                        && w.getRange() > 200 && !w.hasAIHint(WeaponAPI.AIHints.PD)) {
                    nominalRange = nominalRange + w.getRange();
                    numValidWeapons++;
                }
            }
            
            if (numValidWeapons > 0) {
                nominalRange = nominalRange/numValidWeapons;
            }

            activeRange = nominalRange + 400f;
            
        }
        
        tracker.advance(amount);
        Vector2f shipLoc = new Vector2f(ship.getLocation());
        
        if (tracker.intervalElapsed()) {
            if (!AIUtils.canUseSystemThisFrame(ship)) {
                return;
            }

            boolean shouldUseShipSystem = false;
            
            List<ShipAPI> nearbyShips = CombatUtils.getShipsWithinRange(shipLoc, activeRange);
            for (ShipAPI tracking: nearbyShips) {
                float distance, closestDistance = Float.MAX_VALUE;
                for (ShipAPI s : AIUtils.getNearbyEnemies(ship, activeRange)) {
                    distance = MathUtils.getDistance(s, ship.getLocation());
                    if (distance < closestDistance) {
                        closestDistance = distance;
                    }
                }

                //ignore fighters, allies, and dead ships
                if (tracking.getOwner() == ship.getOwner() || tracking.isHulk() || tracking.isFighter()
                        || closestDistance < nominalRange 
                        || ship.getCurrFlux()/ship.getMaxFlux() > 0.5f) {
                    //do nothing
                } else if (closestDistance > nominalRange 
                        && closestDistance < activeRange) {
                    shouldUseShipSystem = true;
                }
            }
            
            if (ship.getSystem().isActive() ^ shouldUseShipSystem) {
                ship.useSystem();
            }
        }
    }
}