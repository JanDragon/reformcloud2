/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.permissions.application.command;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;
import systems.reformcloud.reformcloud2.permissions.PermissionManagement;
import systems.reformcloud.reformcloud2.permissions.internal.UUIDFetcher;
import systems.reformcloud.reformcloud2.permissions.nodes.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.nodes.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.objects.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.objects.user.PermissionUser;
import systems.reformcloud.reformcloud2.permissions.util.InternalTimeUnit;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CommandPerms extends GlobalCommand {

    private static final String[] HELP = new String[]{
            "perms groups",
            "perms group [groupname]",
            "perms group [groupname] create",
            "perms group [groupname] create [default]",
            "perms group [groupname] delete",
            "perms group [groupname] clear",
            "perms group [groupname] setdefault [default]",
            "perms group [groupname] setpriority [priority]",
            "perms group [groupname] setprefix [prefix]",
            "perms group [groupname] setsuffix [suffix]",
            "perms group [groupname] setdisplay [display]",
            "perms group [groupname] setcolor [color]",
            "perms group [groupname] addperm [permission] [set]",
            "perms group [groupname] addperm [permission] [set] [timeout] [s/m/h/d/mo]",
            "perms group [groupname] addperm [processgroup] [permission] [set]",
            "perms group [groupname] addperm [processgroup] [permission] [set] [timeout] [s/m/h/d/mo]",
            "perms group [groupname] delperm [permission]",
            "perms group [groupname] delperm [processgroup] [permission]",
            "perms group [groupname] parent add [groupname]",
            "perms group [groupname] parent remove [groupname]",
            "perms group [groupname] parent clear",
            " ",
            "perms user [user]",
            "perms user [user] delete",
            "perms user [user] clear",
            "perms user [user] setprefix [prefix]",
            "perms user [user] setsuffix [suffix]",
            "perms user [user] setdisplay [display]",
            "perms user [user] setcolor [color]",
            "perms user [user] addperm [permission] [set]",
            "perms user [user] addperm [permission] [set] [timeout] [s/m/h/d/mo]",
            "perms user [user] delperm [permission]",
            "perms user [user] addgroup [group]",
            "perms user [user] addgroup [group] [timeout] [s/m/h/d/mo]",
            "perms user [user] delgroup [group]"
    };

    public CommandPerms() {
        super("perms", "reformcloud.command.perms",
                "The main perms command", Arrays.asList("permissions", "cloudperms"));
    }

    @Override
    public boolean handleCommand(@NotNull CommandSource commandSource, String @NotNull [] strings) {
        if (strings.length == 1 && strings[0].equalsIgnoreCase("groups")) {
            List<PermissionGroup> groups = new ArrayList<>(PermissionManagement.getInstance().getPermissionGroups());
            groups.sort(Comparator.comparingInt(PermissionGroup::getPriority));

            commandSource.sendMessage(String.format("Registered groups (%d): \n  -%s", groups.size(), String.join("\n  -",
                    groups
                            .stream()
                            .map(e -> String.format("Name: %s | Priority: %d", e.getName(), e.getPriority()))
                            .toArray(String[]::new))
            ));
            return true;
        }

        if (strings.length == 2 && strings[0].equalsIgnoreCase("user")) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionUser user = PermissionManagement.getInstance().loadUser(uniqueID);
            {
                StringBuilder stringBuilder = new StringBuilder();
                for (NodeGroup group : user.getGroups()) {
                    if (group.isValid()) {
                        stringBuilder.append(group.getGroupName()).append(", ");
                    }
                }
                commandSource.sendMessage("Groups: " + (stringBuilder.length() > 2
                        ? stringBuilder.substring(0, stringBuilder.length() - 2) : "none"));
            }
            {
                StringBuilder stringBuilder = new StringBuilder();
                for (PermissionNode permissionNode : user.getPermissionNodes()) {
                    if (permissionNode.isValid()) {
                        stringBuilder.append(permissionNode.isSet()
                                ? permissionNode.getActualPermission()
                                : "-" + permissionNode.getActualPermission()).append(", ");
                    }
                }
                commandSource.sendMessage("Permissions: " + (stringBuilder.length() > 2
                        ? stringBuilder.substring(0, stringBuilder.length() - 2) : "none"));
            }

            return true;
        }

        if (strings.length == 3
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("delete")
        ) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionManagement.getInstance().deleteUser(uniqueID);
            System.out.println("Deleted user " + strings[1]);
            return true;
        }

        if (strings.length == 3
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("clear")
        ) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionUser user = PermissionManagement.getInstance().loadUser(uniqueID);
            user.getPermissionNodes().clear();
            user.getGroups().clear();
            PermissionManagement.getInstance().updateUser(user);
            System.out.println("Cleared all groups and permissions of user " + strings[1]);
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("setprefix")) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionUser user = PermissionManagement.getInstance().loadUser(uniqueID);
            if (Streams.filterToReference(user.getPermissionNodes(),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3])).isPresent()) {
                System.out.println("The permission " + strings[3] + " is already set");
                return true;
            }

            String prefix = strings[3].replace("_", " ");
            if (prefix.equals("\"\"")) {
                prefix = null;
            }

            user.setPrefix(prefix);
            PermissionManagement.getInstance().updateUser(user);
            System.out.println("The user " + strings[1] + " " + (prefix == null ? "has no longer a prefix" : "has now the prefix " + prefix));
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("setdisplay")) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionUser user = PermissionManagement.getInstance().loadUser(uniqueID);
            if (Streams.filterToReference(user.getPermissionNodes(),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3])).isPresent()) {
                System.out.println("The permission " + strings[3] + " is already set");
                return true;
            }

            String display = strings[3].replace("_", " ");
            if (display.equals("\"\"")) {
                display = null;
            }

            user.setDisplay(display);
            PermissionManagement.getInstance().updateUser(user);
            System.out.println("The user " + strings[1] + " " + (display == null ? "has no longer a display" : "has now the display " + display));
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("setsuffix")) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionUser user = PermissionManagement.getInstance().loadUser(uniqueID);
            if (Streams.filterToReference(user.getPermissionNodes(),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3])).isPresent()) {
                System.out.println("The permission " + strings[3] + " is already set");
                return true;
            }

            String suffix = strings[3].replace("_", " ");
            if (suffix.equals("\"\"")) {
                suffix = null;
            }

            user.setSuffix(suffix);
            PermissionManagement.getInstance().updateUser(user);
            System.out.println("The user " + strings[1] + " " + (suffix == null ? "has no longer a suffix" : "has now the suffix " + suffix));
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("setcolor")) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionUser user = PermissionManagement.getInstance().loadUser(uniqueID);
            if (Streams.filterToReference(user.getPermissionNodes(),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3])).isPresent()) {
                System.out.println("The permission " + strings[3] + " is already set");
                return true;
            }

            String colour = strings[3];
            if (colour.equals("\"\"")) {
                colour = null;
            }

            user.setColour(colour);
            PermissionManagement.getInstance().updateUser(user);
            System.out.println("The user " + strings[1] + " " + (colour == null ? "has no longer a colour" : "has now the colour " + colour));
            return true;
        }

        if (strings.length == 5
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("addperm")
        ) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionUser user = PermissionManagement.getInstance().loadUser(uniqueID);
            if (Streams.filterToReference(user.getPermissionNodes(),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3])).isPresent()) {
                System.out.println("The permission " + strings[3] + " is already set");
                return true;
            }

            Boolean set = CommonHelper.booleanFromString(strings[4]);
            if (set == null) {
                System.out.println("The permission may not be set correctly. Please recheck (use true/false as set argument)");
                return true;
            }

            user.getPermissionNodes().add(new PermissionNode(
                    System.currentTimeMillis(),
                    -1,
                    set,
                    strings[3]
            ));
            PermissionManagement.getInstance().updateUser(user);
            System.out.println("The permission " + strings[3] + " was added to the user " + strings[1] + " with value " + set);
            return true;
        }

        if (strings.length == 7
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("addperm")
        ) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionUser user = PermissionManagement.getInstance().loadUser(uniqueID);
            if (Streams.filterToReference(user.getPermissionNodes(),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3])).isPresent()) {
                System.out.println("The permission " + strings[3] + " is already set");
                return true;
            }

            Boolean set = CommonHelper.booleanFromString(strings[4]);
            if (set == null) {
                System.out.println("The permission may not be set correctly. Please recheck (use true/false as set argument)");
                return true;
            }

            Long givenTimeOut = CommonHelper.longFromString(strings[5]);
            if (givenTimeOut == null) {
                System.out.println("The timout time is not valid");
                return true;
            }

            long timeOut = System.currentTimeMillis()
                    + InternalTimeUnit.convert(parseUnitFromString(strings[6]), givenTimeOut);
            user.getPermissionNodes().add(new PermissionNode(
                    System.currentTimeMillis(),
                    timeOut,
                    set,
                    strings[3]
            ));
            PermissionManagement.getInstance().updateUser(user);
            System.out.println("The permission " + strings[3] + " was added to the user " + strings[1] + " with value " + set);
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("delperm")
        ) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionUser user = PermissionManagement.getInstance().loadUser(uniqueID);
            ReferencedOptional<PermissionNode> perm = Streams.filterToReference(user.getPermissionNodes(),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3]));
            if (!perm.isPresent()) {
                System.out.println("The permission " + strings[3] + " is not set");
                return true;
            }

            user.getPermissionNodes().remove(perm.get());
            PermissionManagement.getInstance().updateUser(user);
            System.out.println("Removed permission " + strings[3] + " from user " + strings[1]);
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("addgroup")
        ) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionGroup group = PermissionManagement.getInstance().getPermissionGroup(strings[3]).orElse(null);
            if (group == null) {
                System.out.println("The group " + strings[3] + " does not exists");
                return true;
            }

            PermissionUser user = PermissionManagement.getInstance().loadUser(uniqueID);
            if (Streams.filterToReference(user.getGroups(), e -> e.getGroupName().equals(strings[3]) && e.isValid()).isPresent()) {
                System.out.println("The user " + strings[1] + " is already in group " + strings[3]);
                return true;
            }

            user.getGroups().add(new NodeGroup(
                    System.currentTimeMillis(),
                    -1,
                    group.getName()
            ));
            PermissionManagement.getInstance().updateUser(user);
            System.out.println("Successfully added user " + strings[1] + " to group " + strings[3]);
            return true;
        }

        if (strings.length == 6
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("addgroup")
        ) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionGroup group = PermissionManagement.getInstance().getPermissionGroup(strings[3]).orElse(null);
            if (group == null) {
                System.out.println("The group " + strings[3] + " does not exists");
                return true;
            }

            PermissionUser user = PermissionManagement.getInstance().loadUser(uniqueID);
            if (Streams.filterToReference(user.getGroups(), e -> e.getGroupName().equals(strings[3]) && e.isValid()).isPresent()) {
                System.out.println("The user " + strings[1] + " is already in group " + strings[3]);
                return true;
            }

            Long givenTimeOut = CommonHelper.longFromString(strings[4]);
            if (givenTimeOut == null) {
                System.out.println("The timout time is not valid");
                return true;
            }

            long timeOut = System.currentTimeMillis()
                    + InternalTimeUnit.convert(parseUnitFromString(strings[5]), givenTimeOut);
            user.getGroups().add(new NodeGroup(
                    System.currentTimeMillis(),
                    timeOut,
                    group.getName()
            ));
            PermissionManagement.getInstance().updateUser(user);
            System.out.println("Successfully added user " + strings[1] + " to group " + strings[3]);
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("delgroup")
        ) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionUser user = PermissionManagement.getInstance().loadUser(uniqueID);
            NodeGroup filter = Streams.filter(user.getGroups(), e -> e.getGroupName().equals(strings[3]));
            if (filter == null) {
                System.out.println("The user " + strings[1] + " is not in group " + strings[3]);
                return true;
            }

            user.getGroups().remove(filter);
            PermissionManagement.getInstance().updateUser(user);
            System.out.println("Successfully removed group " + strings[3] + " from user " + strings[1]);
            return true;
        }

        // ======== Groups ========

        if (strings.length == 2 && strings[0].equalsIgnoreCase("group")) {
            PermissionGroup group = PermissionManagement.getInstance().getPermissionGroup(strings[1]).orElse(null);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            {
                StringBuilder stringBuilder = new StringBuilder();
                for (PermissionNode permissionNode : group.getPermissionNodes()) {
                    if (!permissionNode.isValid()) {
                        continue;
                    }

                    stringBuilder
                            .append(permissionNode.isSet() ? "" : "-")
                            .append(permissionNode.getActualPermission())
                            .append(", ");
                }

                System.out.println("Permissions: " + (stringBuilder.length() > 2
                        ? stringBuilder.substring(0, stringBuilder.length() - 2)
                        : "none"));
            }
            {
                StringBuilder stringBuilder = new StringBuilder();
                group.getPerGroupPermissions().forEach((k, v) -> {
                    stringBuilder.append("Group ").append(k).append(":\n");
                    v.forEach(e -> {
                        if (!e.isValid()) {
                            return;
                        }

                        stringBuilder.append("   - ").append(e.isSet() ? "" : "-").append(e.getActualPermission()).append(", ");
                    });
                    stringBuilder.append("\n");
                });
                System.out.println("Per-Group-Permissions: \n" + (stringBuilder.length() > 3
                        ? stringBuilder.substring(0, stringBuilder.length() - 3) : "none"));
            }
            return true;
        }

        if (strings.length == 3
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("create")
        ) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[1]);
            if (group != null) {
                System.out.println("The group " + strings[1] + " already exists");
                return true;
            }

            PermissionManagement.getInstance().createPermissionGroup(new PermissionGroup(
                    new ArrayList<>(),
                    new HashMap<>(),
                    new ArrayList<>(),
                    strings[1],
                    0
            ));
            System.out.println("The group " + strings[1] + " was created successfully");
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("create")
        ) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[1]);
            if (group != null) {
                System.out.println("The group " + strings[1] + " already exists");
                return true;
            }

            Boolean defaultGroup = CommonHelper.booleanFromString(strings[3]);
            if (defaultGroup == null) {
                System.out.println("Please recheck (use true/false as 4 argument)");
                return true;
            }

            PermissionManagement.getInstance().createPermissionGroup(new PermissionGroup(
                    new ArrayList<>(),
                    new HashMap<>(),
                    new ArrayList<>(),
                    strings[1],
                    0,
                    defaultGroup
            ));

            System.out.println("The group " + strings[1] + " was created successfully");
            return true;
        }

        if (strings.length == 3
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("delete")
        ) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            PermissionManagement.getInstance().deleteGroup(group.getName());
            System.out.println("The group " + strings[1] + " was deleted successfully");
            return true;
        }

        if (strings.length == 3
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("clear")
        ) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            group.getPerGroupPermissions().clear();
            group.getPermissionNodes().clear();
            PermissionManagement.getInstance().updateGroup(group);
            System.out.println("Successfully deleted all permissions and process-group-permissions from group " + strings[1]);
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("setdefault")
        ) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            Boolean defaultGroup = CommonHelper.booleanFromString(strings[3]);
            if (defaultGroup == null) {
                System.out.println("Please recheck (use true/false as 4 argument)");
                return true;
            }

            group.setDefaultGroup(defaultGroup);
            PermissionManagement.getInstance().updateGroup(group);

            System.out.println("The group " + group.getName() + " is now a " + (defaultGroup ? "default" : "normal") + " group");
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("setpriority")
        ) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            Integer priority = CommonHelper.fromString(strings[3]);
            if (priority == null) {
                System.out.println("Please recheck (use an integer as 4 argument)");
                return true;
            }

            group.setPriority(priority);
            PermissionManagement.getInstance().updateGroup(group);

            System.out.println("The group " + group.getName() + " has now the priority: " + priority);
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("setprefix")
        ) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            String prefix = strings[3].replace("_", " ");
            if (prefix.trim().equals("\"\"")) {
                prefix = null;
            }

            group.setPrefix(prefix);
            PermissionManagement.getInstance().updateGroup(group);

            System.out.println("The group " + group.getName() + " " + (prefix == null ? "has no longer a prefix" : "has now the prefix " + prefix));
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("setsuffix")
        ) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            String suffix = strings[3].replace("_", " ");
            if (suffix.trim().equals("\"\"")) {
                suffix = null;
            }

            group.setSuffix(suffix);
            PermissionManagement.getInstance().updateGroup(group);

            System.out.println("The group " + group.getName() + " " + (suffix == null ? "has no longer a suffix" : "has now the suffix " + suffix));
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("setdisplay")
        ) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            String display = strings[3].replace("_", " ");
            if (display.trim().equals("\"\"")) {
                display = null;
            }

            group.setDisplay(display);
            PermissionManagement.getInstance().updateGroup(group);

            System.out.println("The group " + group.getName() + " " + (display == null ? "has no longer a display" : "has now the display " + display));
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("setcolor")
        ) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            String color = strings[3];
            if (color.length() > 2) {
                System.out.println("Please use a colour code as argument. Look at https://minecraft.gamepedia.com/Formatting_codes for a full list");
                return true;
            }

            group.setColour(color);
            PermissionManagement.getInstance().updateGroup(group);

            System.out.println("The group " + group.getName() + " has now the colour " + color);
            return true;
        }

        if (strings.length == 5
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("addperm")
        ) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            if (Streams.filterToReference(group.getPermissionNodes(),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3])).isPresent()) {
                System.out.println("The permission " + strings[3] + " is already set for group " + strings[3]);
                return true;
            }

            Boolean set = CommonHelper.booleanFromString(strings[4]);
            if (set == null) {
                System.out.println("The permission may not be set correctly. Please recheck (use true/false as set argument)");
                return true;
            }

            group.getPermissionNodes().add(new PermissionNode(
                    System.currentTimeMillis(),
                    -1,
                    set,
                    strings[3]
            ));
            PermissionManagement.getInstance().updateGroup(group);
            System.out.println("The permission " + strings[3] + " was added to group " + group.getName());
            return true;
        }

        if (strings.length == 7
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("addperm")
        ) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            if (Streams.filterToReference(group.getPermissionNodes(),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3])).isPresent()) {
                System.out.println("The permission " + strings[3] + " is already set for group " + strings[3]);
                return true;
            }

            Boolean set = CommonHelper.booleanFromString(strings[4]);
            if (set == null) {
                System.out.println("The permission may not be set correctly. Please recheck (use true/false as set argument)");
                return true;
            }

            Long givenTimeOut = CommonHelper.longFromString(strings[5]);
            if (givenTimeOut == null) {
                System.out.println("The timout time is not valid");
                return true;
            }

            long timeOut = System.currentTimeMillis()
                    + InternalTimeUnit.convert(parseUnitFromString(strings[6]), givenTimeOut);
            group.getPermissionNodes().add(new PermissionNode(
                    System.currentTimeMillis(),
                    timeOut,
                    set,
                    strings[3]
            ));
            PermissionManagement.getInstance().updateGroup(group);
            System.out.println("The permission " + strings[3] + " was added to group " + group.getName());
            return true;
        }

        if (strings.length == 6
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("addperm")
        ) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            if (group.getPerGroupPermissions().containsKey(strings[3])
                    && Streams.filterToReference(group.getPerGroupPermissions().get(strings[3]),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3])).isPresent()) {
                System.out.println("The permission " + strings[4] + " is already set for group " + strings[2] + " on " + strings[3]);
                return true;
            }

            Boolean set = CommonHelper.booleanFromString(strings[5]);
            if (set == null) {
                System.out.println("The permission may not be set correctly. Please recheck (use true/false as set argument)");
                return true;
            }

            PermissionManagement.getInstance().addProcessGroupPermission(strings[3], group, new PermissionNode(
                    System.currentTimeMillis(),
                    -1,
                    set,
                    strings[4]
            ));
            System.out.println("The permission " + strings[4] + " was added to group " + group.getName() + " on " + strings[3]);
            return true;
        }

        if (strings.length == 8
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("addperm")
        ) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            if (group.getPerGroupPermissions().containsKey(strings[3])
                    && Streams.filterToReference(group.getPerGroupPermissions().get(strings[3]),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3])).isPresent()) {
                System.out.println("The permission " + strings[4] + " is already set for group " + strings[2] + " on " + strings[3]);
                return true;
            }

            Boolean set = CommonHelper.booleanFromString(strings[5]);
            if (set == null) {
                System.out.println("The permission may not be set correctly. Please recheck (use true/false as set argument)");
                return true;
            }

            Long givenTimeOut = CommonHelper.longFromString(strings[6]);
            if (givenTimeOut == null) {
                System.out.println("The timout time is not valid");
                return true;
            }

            long timeOut = System.currentTimeMillis()
                    + InternalTimeUnit.convert(parseUnitFromString(strings[7]), givenTimeOut);
            PermissionManagement.getInstance().addProcessGroupPermission(strings[3], group, new PermissionNode(
                    System.currentTimeMillis(),
                    timeOut,
                    set,
                    strings[4]
            ));
            System.out.println("The permission " + strings[4] + " was added to group " + group.getName() + " on " + strings[3]);
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("delperm")
        ) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            PermissionNode filter = Streams.filter(group.getPermissionNodes(),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3]));
            if (filter == null) {
                System.out.println("The permission " + strings[3] + " is not set");
                return true;
            }

            group.getPermissionNodes().remove(filter);
            PermissionManagement.getInstance().updateGroup(group);
            System.out.println("The permission " + strings[3] + " was removed from the group " + group.getName());
            return true;
        }

        if (strings.length == 5
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("delperm")
        ) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            if (!group.getPerGroupPermissions().containsKey(strings[3])) {
                System.out.println("There are no server group permission for group " + group.getName() + " on " + strings[3]);
                return true;
            }

            PermissionNode filter = Streams.filter(group.getPerGroupPermissions().get(strings[3]),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[4]));
            if (filter == null) {
                System.out.println("The permission " + strings[4] + " is not set for " + group.getName() + " on " + strings[3]);
                return true;
            }

            group.getPerGroupPermissions().get(strings[3]).remove(filter);
            PermissionManagement.getInstance().updateGroup(group);
            System.out.println("The permission " + strings[4] + " was removed for group " + group.getName() + " on " + strings[3]);
            return true;
        }

        if (strings.length == 5
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("parent")
                && strings[3].equalsIgnoreCase("add")
        ) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            if (group.getSubGroups().contains(strings[4])) {
                System.out.println("The group " + strings[4] + " is already a parent of " + group.getName());
                return true;
            }

            PermissionGroup sub = PermissionManagement.getInstance().getGroup(strings[4]);
            if (sub == null) {
                System.out.println("The group " + strings[4] + " does not exists");
                return true;
            }

            group.getSubGroups().add(sub.getName());
            PermissionManagement.getInstance().updateGroup(group);
            System.out.println("The sub group " + sub.getName() + " was added to " + group.getName());
            return true;
        }

        if (strings.length == 5
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("parent")
                && strings[3].equalsIgnoreCase("remove")
        ) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            if (!group.getSubGroups().contains(strings[4])) {
                System.out.println("The group " + strings[4] + " is not a parent of " + group.getName());
                return true;
            }

            group.getSubGroups().remove(strings[4]);
            PermissionManagement.getInstance().updateGroup(group);
            System.out.println("Removed sub group " + strings[4] + " from " + group.getName());
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("parent")
        ) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            if (group.getSubGroups().isEmpty()) {
                System.out.println("The group " + group.getName() + " does not have any sub-groups");
                return true;
            }

            group.getSubGroups().clear();
            PermissionManagement.getInstance().updateGroup(group);
            System.out.println("Cleared all sub group of " + group.getName());
            return true;
        }

        commandSource.sendMessages(HELP);
        return true;
    }

    private static TimeUnit parseUnitFromString(@NotNull String s) {
        switch (s.toLowerCase()) {
            case "s":
                return TimeUnit.SECONDS;
            case "m":
                return TimeUnit.MINUTES;
            case "h":
                return TimeUnit.HOURS;
            case "d":
                return TimeUnit.DAYS;
            default: {
                return null;
            }
        }
    }
}