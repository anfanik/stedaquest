package me.anfanik.steda.quest.api.npc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.mojang.authlib.GameProfile;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.val;
import me.anfanik.steda.api.utility.ChatUtility;
import me.anfanik.steda.api.utility.Skin;
import me.anfanik.steda.api.utility.TextUtility;
import me.anfanik.steda.quest.StedaQuest;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbstractNPC {

    private static Map<Integer, AbstractNPC> npcs = new HashMap<>(); static {
        val plugin = StedaQuest.get().getPlugin();
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> Bukkit.getOnlinePlayers().forEach(AbstractNPC::showNearestNPCs), 30 * 20L, 30 * 20L);
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void showNearestNPCs(PlayerJoinEvent event) {
                val player = event.getPlayer();
                AbstractNPC.showNearestNPCs(player);
            }

        }, plugin);
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                            Player player = event.getPlayer();
                            PacketContainer packet = event.getPacket();

                            int id = packet.getIntegers().read(0);
                            AbstractNPC npc = npcs.get(id);
                            if (npc == null) {
                                return;
                            }

                            EnumWrappers.EntityUseAction action = event.getPacket().getEntityUseActions().read(0);
                            if (action != EnumWrappers.EntityUseAction.INTERACT_AT) {
                                return;
                            }

                            EnumWrappers.Hand hand = event.getPacket().getHands().read(0);
                            npc.getDelegate().handleInteract(player, hand);
                            if (hand == EnumWrappers.Hand.MAIN_HAND) {
                                npc.getDelegate().handleMainHandInteract(player);
                            } else {
                                npc.getDelegate().handleOffHandInteract(player);
                            }
                        }
                    }
                });
    }

    private static void showNearestNPCs(Player player) {
        val location = player.getLocation();
        npcs.values().stream()
                .filter(npc -> npc.getLocation().distanceSquared(location) <= 64 * 64)
                .filter(npc -> npc.getDelegate().isVisible(player))
                .forEach(npc -> npc.show(player));
    }

    private final EntityNPC entity;

    @Getter
    private Location location;

    @Setter
    private int customId = -1;

    public AbstractNPC(String name, Skin skin, Location location) {
        this.location = location;
        WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        GameProfile profile = new GameProfile(UUID.randomUUID(), TextUtility.colorize(name));
        skin.applyToGameProfile(profile);
        entity = new EntityNPC(nmsWorld, profile);
        teleport(location);
        npcs.put(entity.getId(), this);
        chatDelegate = new ChatDelegateImpl(name);
    }

    public void teleport(Location location) {
        this.location = location;
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        entity.setHeadRotation(location.getYaw(), location.getPitch());
        Bukkit.getOnlinePlayers().forEach(player -> {
            val connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutEntityTeleport(entity));
            float headYaw = MathHelper.d(entity.getHeadRotation() * 256.0F / 360.0F);
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(entity, (byte) headYaw));
        });
    }

    @SneakyThrows
    public void show(Player player) {
        val connection = ((CraftPlayer) player).getHandle().playerConnection;
        if (customId == -1) {
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entity));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(entity));
        } else {
            connection.sendPacket(new PacketPlayOutSpawnEntity());
            val spawn = new PacketPlayOutNamedEntitySpawn(entity);
            val id = spawn.getClass().getDeclaredField("a");
            id.setAccessible(true);
            id.set(spawn, customId);
            connection.sendPacket(spawn);
        }
    }

    @Getter
    @Setter
    protected Delegate delegate = new DelegateImpl();

    @Getter
    @Setter
    protected ChatDelegate chatDelegate;

    public interface Delegate {

        void handleInteract(Player player, EnumWrappers.Hand hand);

        void handleMainHandInteract(Player player);

        void handleOffHandInteract(Player player);

        boolean isVisible(Player player);

    }

    public static class DelegateImpl implements Delegate {

        @Override
        public void handleInteract(Player player, EnumWrappers.Hand hand) {
        }

        @Override
        public void handleMainHandInteract(Player player) {
        }

        @Override
        public void handleOffHandInteract(Player player) {
        }

        @Override
        public boolean isVisible(Player player) {
            return true;
        }

    }

    public interface ChatDelegate {

        String getChatName();

        String getChannelPrefix();

        void sendMessage(Player player, String message, Object... arguments);

    }

    public static class ChatDelegateImpl implements ChatDelegate {

        @Getter
        @Setter
        private String chatName;

        @Getter
        @Setter
        private String channelPrefix = "&7[&cНПС&7] ";

        public ChatDelegateImpl(String chatName) {
            this.chatName = chatName;
        }

        @Override
        public void sendMessage(Player player, String message, Object... arguments) {
            ChatUtility.colored().send(player, getChannelPrefix() + getChatName() + "&f: " + message, arguments);
        }

    }

    protected static class EntityNPC extends EntityPlayer {

        public EntityNPC(WorldServer worldserver, GameProfile gameprofile) {
            super(MinecraftServer.getServer(), worldserver, gameprofile, new PlayerInteractManager(worldserver));
        }

        public void setHeadRotation(float yaw, float pitch) {
            setYawPitch(yaw, pitch);
        }

    }

}