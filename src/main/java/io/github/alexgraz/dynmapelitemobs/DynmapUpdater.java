package io.github.alexgraz.dynmapelitemobs;

import com.magmaguy.elitemobs.mobconstructor.EliteMobEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;

import static org.bukkit.Bukkit.getServer;

class DynmapUpdater {

    private List<EliteMobEntity> eliteMobsDisplayed = new ArrayList<>();
    private List<EliteMobEntity> eliteMobsRemovalQueue = new LinkedList<>();

    private MarkerSet eliteMobsBossesSet;

    DynmapUpdater() {
        Plugin dynmap = getServer().getPluginManager().getPlugin("dynmap");

        DynmapAPI dynmapAPI = (DynmapAPI) dynmap;
        MarkerAPI markerAPI = dynmapAPI != null ? dynmapAPI.getMarkerAPI() : null;

        this.eliteMobsBossesSet = markerAPI.getMarkerSet("eliteMobs.markerSet");

        if (this.eliteMobsBossesSet == null) {
            this.eliteMobsBossesSet = markerAPI.createMarkerSet("eliteMobs.markerSet", "EliteMobs Bosses", null, false);
        }

        String markerLabel = "EliteMobs Boss";
        String markerID = "eliteMobsBoss";

        MarkerIcon markerIcon = markerAPI.getMarkerIcon(markerID);

        if (markerIcon == null) {
            InputStream inputStream = getClass().getResourceAsStream("/crowned-skull.png");
            markerIcon = markerAPI.createMarkerIcon(markerID, markerLabel, inputStream);
        }

        this.eliteMobsBossesSet.setDefaultMarkerIcon(markerIcon);
    }

    void startRepeatingTask() {

        Runnable runnable = () -> {

            eliteMobsRemovalQueue.forEach(mob -> {

                String mobID = mob.getLivingEntity().getUniqueId().toString();
                Marker marker = this.eliteMobsBossesSet.findMarker(mobID);
                marker.deleteMarker();

                eliteMobsDisplayed.remove(mob);
                eliteMobsRemovalQueue.remove(mob);

            });

            eliteMobsDisplayed.forEach(mob -> {

                String mobID = mob.getLivingEntity().getUniqueId().toString();
                Location mobLocation = mob.getLivingEntity().getLocation();
                World world = mob.getLivingEntity().getWorld();

                Marker marker = this.eliteMobsBossesSet.findMarker(mobID);

                if (marker == null) {
                    marker = this.eliteMobsBossesSet.createMarker(mobID, null, world.getName(), mobLocation.getX(), mobLocation.getY(), mobLocation.getZ(), null, false);

                } else {
                    marker.setLocation(world.getName(), mobLocation.getX(), mobLocation.getY(), mobLocation.getZ());
                }

                int healthPercentage = (int) (mob.getHealth() / mob.getMaxHealth() * 100);

                marker.setLabel(this.getLabelHTML(mob.getName().substring(2), healthPercentage), true);
            });

            if (eliteMobsDisplayed.size() == 0) {
                Bukkit.getScheduler().cancelTasks(DynmapEliteMobs.getInstance());
            }
        };

        Bukkit.getScheduler().scheduleSyncRepeatingTask(DynmapEliteMobs.getInstance(), runnable, 20, 20);
    }

    void addEliteMob(EliteMobEntity eliteMob) {
        eliteMobsDisplayed.add(eliteMob);

        if (eliteMobsDisplayed.size() == 1) {
            startRepeatingTask();
        }
    }

    void removeEliteMob(EliteMobEntity eliteMob) {
        eliteMobsRemovalQueue.add(eliteMob);
    }

    private static <T> Predicate<T> not(Predicate<T> predicate) {
        return predicate.negate();
    }

    private String getLabelHTML(String label, int health) {
        return "<div style=\"padding: 0.5em\">" +
                "<p style=\"font-size: small;" +
                "text-align: center;" +
                "margin: 0 0 0.35em;\">" + label + "</p>" +
                "<div style=\"" +
                "height: 0.8em;" +
                "width: 9em;" +
                "position: relative;" +
                "background: #555;" +
                "-moz-border-radius: 25px;" +
                "-webkit-border-radius: 25px;" +
                "border-radius: 10px;" +
                "padding: 0.3em;" +
                "box-shadow: inset 0 -1px 1px rgba(255,255,255,0.3);\">" +
                "<span style=\"" +
                String.format("width: %d%%; ", health) +
                "display: block; " +
                "height: 100%;" +
                "border-radius: 10px; " +
                "background-color: rgb(43,194,83);  " +
                "background-image: -webkit-gradient(\n" +
                "linear, left bottom, left top, " +
                "color-stop(0, rgb(43,194,83)), color-stop(1, rgb(84,240,84)));" +
                "box-shadow: " +
                "    inset 0 2px 9px  rgba(255,255,255,0.3)," +
                "    inset 0 -2px 6px rgba(0,0,0,0.4);" +
                "position: relative;" +
                "overflow: hidden;\"></span>" +
                "</div></div>";
    }

}
