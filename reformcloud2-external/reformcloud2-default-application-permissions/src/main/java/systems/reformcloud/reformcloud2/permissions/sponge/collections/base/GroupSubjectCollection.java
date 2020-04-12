package systems.reformcloud.reformcloud2.permissions.sponge.collections.base;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.sponge.collections.DefaultSubjectCollection;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.base.group.GroupSubject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class GroupSubjectCollection extends DefaultSubjectCollection {

    public GroupSubjectCollection(PermissionService service) {
        super(PermissionService.SUBJECTS_GROUP, service);
    }

    @NotNull
    @Override
    protected Subject load(String id) {
        return new GroupSubject(id, service, this);
    }

    @Override
    @NotNull
    public CompletableFuture<Boolean> hasSubject(@NotNull String identifier) {
        return CompletableFuture.completedFuture(PermissionAPI.getInstance().getPermissionUtil().getGroup(identifier) != null);
    }

    @Override
    @NotNull
    public Collection<Subject> getLoadedSubjects() {
        return new ArrayList<>();
    }
}
