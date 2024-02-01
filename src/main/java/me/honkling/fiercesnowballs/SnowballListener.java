package me.honkling.fiercesnowballs;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static me.honkling.fiercesnowballs.FierceSnowballs.instance;

public class SnowballListener implements Listener {
    private Map<UUID, Integer> tasks = new HashMap<>();
    private boolean warned = false;

    @EventHandler
    public void onSnowballHit(ProjectileHitEvent event) {
        var projectile = event.getEntity();
        var entity = event.getHitEntity();

        if (!(projectile instanceof Snowball))
            return;

        var scheduler = Bukkit.getScheduler();
        scheduler.cancelTask(tasks.get(projectile.getUniqueId()));
        tasks.remove(projectile.getUniqueId());

        if (!(entity instanceof Player player))
            return;

        var config = instance.getConfig();
        var damage = config.getDouble("damage", 2);

        if (projectile.hasMetadata("critical"))
            damage *= config.getDouble("critical.multiplier", 1.5);

        player.damage(damage, projectile);
    }

    @EventHandler
    public void onSnowballThrow(ProjectileLaunchEvent event) {
        var projectile = event.getEntity();

        if (!(projectile instanceof Snowball snowball))
            return;

        var config = instance.getConfig();
        var chance = Math.ceil(Math.random() * 100);
        var requirement = config.getDouble("critical.chance", 10);

        if (chance <= requirement)
            snowball.setMetadata("critical", new FixedMetadataValue(instance, true));

        Particle particle = null;

        try {
            var normalParticle = Particle.valueOf(config.getString("trails.normal-particle", "END_ROD"));
            var criticalParticle = Particle.valueOf(config.getString("trails.critical-particle", "VILLAGER_ANGRY"));

            particle = snowball.hasMetadata("critical") ? criticalParticle : normalParticle;
        } catch (IllegalArgumentException ignored) {
            if (!warned) {
                instance.getLogger().warning("Invalid trail particle.");
                warned = true;
                return;
            }
        }

        if (config.getBoolean("trails.enabled", true)) {
            var scheduler = Bukkit.getScheduler();
            var ticks = new AtomicInteger();

            final var finalParticle = particle;

            var taskId = scheduler.scheduleSyncRepeatingTask(instance, () -> {
                ticks.addAndGet(2);

                if (ticks.get() >= config.getInt("trails.timer", 400) || finalParticle == null) {
                    scheduler.cancelTask(tasks.get(snowball.getUniqueId()));
                    tasks.remove(snowball);
                    return;
                }

                snowball.getWorld().spawnParticle(finalParticle, snowball.getLocation(), 1);
            }, 2L, 2L);

            tasks.put(snowball.getUniqueId(), taskId);
        }
    }
}
