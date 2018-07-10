package net.perfectdreams.impossiblelatency;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.ScheduledPacket;
import org.bukkit.plugin.java.JavaPlugin;

public class ImpossibleLatency extends JavaPlugin {
	@Override
	public void onEnable() {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Status.Server.SERVER_INFO) {
			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer pongPacket = new PacketContainer(
						PacketType.Status.Server.PONG
				);

				pongPacket.getLongs().write(0, 0L); // The client doesn't care if you send a different time than what it expects

				// We are going to send the pong packet right after the server info packet.
				// Vanilla sends the "ping" packet right after processing the server info packet, because we are sending it right after the
				// server info packet (instead of waiting for the client to send the packet) when the client finishes processing the
				// server info packet, it will process the our pong packet shortly after, making the client believe the server
				// has very low latency! wow, such low latency, very impossible.
				event.schedule(
						new ScheduledPacket(
								pongPacket,
								event.getPlayer(),
								false
						)
				);
			}
		});
	}
}