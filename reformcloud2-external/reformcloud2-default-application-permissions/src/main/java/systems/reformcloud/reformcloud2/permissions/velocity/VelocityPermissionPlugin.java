package systems.reformcloud.reformcloud2.permissions.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import systems.reformcloud.reformcloud2.permissions.util.PermissionPluginUtil;
import systems.reformcloud.reformcloud2.permissions.velocity.command.CommandCloudPerms;
import systems.reformcloud.reformcloud2.permissions.velocity.listener.VelocityPermissionListener;

@Plugin(
        id = "reformcloud_2_perms",
        name = "VelocityPermissionPlugin",
        version = "2",
        description = "The reformcloud permission plugin",
        url = "https://reformcloud.systems",
        authors = {"derklaro"},
        dependencies = {@Dependency(id = "reformcloud_2_api_executor")}
)
public class VelocityPermissionPlugin {

    @Inject
    public VelocityPermissionPlugin(ProxyServer proxyServer) {
        proxy = proxyServer;
    }

    private static ProxyServer proxy;

    @Subscribe
    public void handleInit(ProxyInitializeEvent event) {
        proxy.getEventManager().register(this, new VelocityPermissionListener());
        proxy.getCommandManager().register(new CommandCloudPerms(), "perms", "permissions", "cloudperms");

        PermissionPluginUtil.awaitConnection();
    }

    public static ProxyServer getProxy() {
        return proxy;
    }
}
