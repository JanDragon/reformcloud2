package systems.reformcloud.reformcloud2.executor.controller.network.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

public final class ControllerPacketOutStartProcess extends JsonPacket {

    public ControllerPacketOutStartProcess(ProcessInformation processInformation, boolean start) {
        super(NetworkUtil.CONTROLLER_INFORMATION_BUS + 2, new JsonConfiguration().add("info", processInformation).add("start", start));
    }
}
