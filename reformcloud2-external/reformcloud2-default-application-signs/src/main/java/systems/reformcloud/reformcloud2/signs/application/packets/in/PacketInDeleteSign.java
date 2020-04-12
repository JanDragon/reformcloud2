package systems.reformcloud.reformcloud2.signs.application.packets.in;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.signs.application.ReformCloudApplication;
import systems.reformcloud.reformcloud2.signs.application.packets.out.PacketOutDeleteSign;
import systems.reformcloud.reformcloud2.signs.packets.PacketUtil;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

import java.util.Collection;
import java.util.function.Consumer;

public class PacketInDeleteSign extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return PacketUtil.SIGN_BUS + 1;
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        if (packet.content().has("sign")) {
            CloudSign sign = packet.content().get("sign", CloudSign.TYPE);
            if (sign == null) {
                return;
            }

            ReformCloudApplication.delete(sign);
            DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new PacketOutDeleteSign(sign)));
        } else {
            Collection<CloudSign> cloudSigns = packet.content().get("signs", new TypeToken<Collection<CloudSign>>() {
            });
            if (cloudSigns == null) {
                return;
            }

            for (CloudSign cloudSign : cloudSigns) {
                ReformCloudApplication.delete(cloudSign);
                DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new PacketOutDeleteSign(cloudSign)));
            }
        }
    }
}
