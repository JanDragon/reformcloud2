package systems.reformcloud.reformcloud2.permissions.sponge.subject.factory;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.AbstractSpongeSubject;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.base.system.SystemSubjectData;

public class FactorySubject extends AbstractSpongeSubject {

    private static final SubjectData DATA = new SystemSubjectData();

    public FactorySubject(String id, PermissionService service, SubjectCollection source) {
        this.id = id;
        this.service = service;
        this.source = source;
    }

    private final String id;

    private final PermissionService service;

    private final SubjectCollection source;

    @Override
    protected PermissionService service() {
        return service;
    }

    @Override
    protected boolean has(String permission) {
        return true;
    }

    @Override
    @NotNull
    public SubjectCollection getContainingCollection() {
        return this.source;
    }

    @Override
    @NotNull
    public SubjectData getSubjectData() {
        return DATA;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return id;
    }
}
