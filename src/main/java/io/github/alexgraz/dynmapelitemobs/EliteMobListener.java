package io.github.alexgraz.dynmapelitemobs;

import com.magmaguy.elitemobs.api.EliteMobDeathEvent;
import com.magmaguy.elitemobs.api.EliteMobSpawnEvent;
import com.magmaguy.elitemobs.custombosses.CustomBossEntity;
import com.magmaguy.elitemobs.mobconstructor.EliteMobEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EliteMobListener implements Listener {

    private DynmapUpdater dynmapUpdater = new DynmapUpdater();

    @EventHandler
    public void onEliteMobSpawn(EliteMobSpawnEvent event) {

        EliteMobEntity eliteMob = event.getEliteMobEntity();

        if (!(eliteMob instanceof CustomBossEntity)) return;

        dynmapUpdater.addEliteMob(eliteMob);
    }

    @EventHandler
    public void onEliteMobDeath(EliteMobDeathEvent event) {

        EliteMobEntity eliteMob = event.getEliteMobEntity();

        if (!(eliteMob instanceof CustomBossEntity)) return;

        dynmapUpdater.removeEliteMob(eliteMob);
    }
}
