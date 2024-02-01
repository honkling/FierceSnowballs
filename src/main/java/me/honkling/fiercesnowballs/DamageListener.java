package me.honkling.fiercesnowballs;

import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import static me.honkling.fiercesnowballs.FierceSnowballs.instance;

public class DamageListener implements Listener {
    @EventHandler
    public void onSnowballHit(ProjectileHitEvent event) {
        var projectile = event.getEntity();
        var entity = event.getHitEntity();

        if (!(projectile instanceof Snowball) || !(entity instanceof Player player))
            return;

        var damage = instance.getConfig().getDouble("damage", 0);
        player.damage(damage, projectile);
    }
}
