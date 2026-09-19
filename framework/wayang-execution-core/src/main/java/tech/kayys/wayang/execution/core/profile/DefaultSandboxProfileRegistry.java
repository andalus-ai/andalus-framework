package tech.kayys.wayang.execution.core.profile;

import tech.kayys.wayang.execution.filesystem.AccessDecision;
import tech.kayys.wayang.execution.filesystem.FilesystemOperation;
import tech.kayys.wayang.execution.filesystem.FilesystemPolicy;
import tech.kayys.wayang.execution.limits.CpuLimit;
import tech.kayys.wayang.execution.limits.MemoryLimit;
import tech.kayys.wayang.execution.limits.ProcessLimit;
import tech.kayys.wayang.execution.limits.ResourceLimits;
import tech.kayys.wayang.execution.limits.StorageLimit;
import tech.kayys.wayang.execution.network.NetworkMode;
import tech.kayys.wayang.execution.network.NetworkPolicy;
import tech.kayys.wayang.execution.network.NetworkRule;
import tech.kayys.wayang.execution.process.ProcessDecision;
import tech.kayys.wayang.execution.process.ProcessPolicy;
import tech.kayys.wayang.execution.sandbox.SandboxContext;
import tech.kayys.wayang.execution.sandbox.SandboxLifecycle;
import tech.kayys.wayang.execution.sandbox.SandboxProfile;
import tech.kayys.wayang.execution.sandbox.SandboxSpec;
import tech.kayys.wayang.execution.secrets.SecretPolicy;
import tech.kayys.wayang.execution.workspace.MountSpec;
import tech.kayys.wayang.execution.workspace.WorkspaceMode;
import tech.kayys.wayang.execution.workspace.WorkspaceSpec;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry and factory for standard and custom sandbox profiles.
 */
public class DefaultSandboxProfileRegistry {

    private final Map<String, SandboxProfile> profiles = new ConcurrentHashMap<>();

    public DefaultSandboxProfileRegistry() {
        register(createMinimalProfile());
        register(createReadOnlyProfile());
        register(createCodingProfile());
        register(createResearchProfile());
        register(createDataProcessingProfile());
        register(createNetworkedProfile());
        register(createPrivilegedProfile());
    }

    public void register(SandboxProfile profile) {
        Objects.requireNonNull(profile, "profile cannot be null");
        profiles.put(profile.name().toUpperCase(), profile);
    }

    public Optional<SandboxProfile> find(String profileName) {
        if (profileName == null) return Optional.empty();
        return Optional.ofNullable(profiles.get(profileName.trim().toUpperCase()));
    }

    public SandboxSpec resolve(String profileName, SandboxContext context) {
        return find(profileName)
                .orElseThrow(() -> new IllegalArgumentException("Unknown sandbox profile: " + profileName))
                .resolve(context);
    }

    private SandboxProfile createMinimalProfile() {
        return new SandboxProfile() {
            @Override
            public String name() {
                return PROFILE_MINIMAL;
            }

            @Override
            public SandboxSpec resolve(SandboxContext context) {
                return new SandboxSpec(
                        WorkspaceSpec.ephemeral(),
                        FilesystemPolicy.readOnly(),
                        request -> ProcessDecision.deny("Processes forbidden in MINIMAL profile"),
                        NetworkPolicy.disabled(),
                        SecretPolicy.denyAll(),
                        new ResourceLimits(CpuLimit.of(1.0), MemoryLimit.megabytes(512), StorageLimit.gigabytes(1), ProcessLimit.of(1, 4), Duration.ofMinutes(5)),
                        SandboxLifecycle.standard()
                );
            }
        };
    }

    private SandboxProfile createReadOnlyProfile() {
        return new SandboxProfile() {
            @Override
            public String name() {
                return PROFILE_READ_ONLY;
            }

            @Override
            public SandboxSpec resolve(SandboxContext context) {
                return new SandboxSpec(
                        new WorkspaceSpec(WorkspaceMode.READ_ONLY, Optional.empty(), Set.of()),
                        FilesystemPolicy.readOnly(),
                        ProcessPolicy.allowAll(),
                        NetworkPolicy.disabled(),
                        SecretPolicy.denyAll(),
                        ResourceLimits.standard(),
                        SandboxLifecycle.standard()
                );
            }
        };
    }

    private SandboxProfile createCodingProfile() {
        return new SandboxProfile() {
            @Override
            public String name() {
                return PROFILE_CODING;
            }

            @Override
            public SandboxSpec resolve(SandboxContext context) {
                Set<String> codingTools = Set.of("git", "mvn", "gradle", "node", "npm", "cargo", "rustc", "python", "python3", "pytest", "dart", "flutter", "sh", "bash");
                return new SandboxSpec(
                        WorkspaceSpec.withMounts(WorkspaceMode.PERSISTENT, Set.of(
                                MountSpec.readWrite("workspace", "/workspace"),
                                MountSpec.readWrite("artifacts", "/artifacts"),
                                MountSpec.readWrite("cache", "/cache")
                        )),
                        FilesystemPolicy.readWrite(),
                        ProcessPolicy.allowList(codingTools),
                        NetworkPolicy.allowlist(Set.of(
                                NetworkRule.allowHost("github.com", 443),
                                NetworkRule.allowHost("repo.maven.apache.org", 443),
                                NetworkRule.allowHost("registry.npmjs.org", 443)
                        )),
                        SecretPolicy.denyAll(),
                        new ResourceLimits(CpuLimit.of(4.0), MemoryLimit.gigabytes(8), StorageLimit.gigabytes(20), ProcessLimit.of(256, 1024), Duration.ofHours(2)),
                        SandboxLifecycle.standard()
                );
            }
        };
    }

    private SandboxProfile createResearchProfile() {
        return new SandboxProfile() {
            @Override
            public String name() {
                return PROFILE_RESEARCH;
            }

            @Override
            public SandboxSpec resolve(SandboxContext context) {
                return new SandboxSpec(
                        WorkspaceSpec.ephemeral(),
                        FilesystemPolicy.readWrite(),
                        ProcessPolicy.allowAll(),
                        NetworkPolicy.full(),
                        SecretPolicy.denyAll(),
                        new ResourceLimits(CpuLimit.of(2.0), MemoryLimit.gigabytes(4), StorageLimit.gigabytes(10), ProcessLimit.of(64, 256), Duration.ofHours(1)),
                        SandboxLifecycle.standard()
                );
            }
        };
    }

    private SandboxProfile createDataProcessingProfile() {
        return new SandboxProfile() {
            @Override
            public String name() {
                return PROFILE_DATA_PROCESSING;
            }

            @Override
            public SandboxSpec resolve(SandboxContext context) {
                return new SandboxSpec(
                        WorkspaceSpec.ephemeral(),
                        FilesystemPolicy.readWrite(),
                        ProcessPolicy.allowAll(),
                        NetworkPolicy.loopbackOnly(),
                        SecretPolicy.denyAll(),
                        new ResourceLimits(CpuLimit.of(8.0), MemoryLimit.gigabytes(16), StorageLimit.gigabytes(50), ProcessLimit.of(512, 2048), Duration.ofHours(4)),
                        SandboxLifecycle.standard()
                );
            }
        };
    }

    private SandboxProfile createNetworkedProfile() {
        return new SandboxProfile() {
            @Override
            public String name() {
                return PROFILE_NETWORKED;
            }

            @Override
            public SandboxSpec resolve(SandboxContext context) {
                return new SandboxSpec(
                        WorkspaceSpec.ephemeral(),
                        FilesystemPolicy.readWrite(),
                        ProcessPolicy.allowAll(),
                        NetworkPolicy.full(),
                        SecretPolicy.denyAll(),
                        ResourceLimits.standard(),
                        SandboxLifecycle.standard()
                );
            }
        };
    }

    private SandboxProfile createPrivilegedProfile() {
        return new SandboxProfile() {
            @Override
            public String name() {
                return PROFILE_PRIVILEGED;
            }

            @Override
            public SandboxSpec resolve(SandboxContext context) {
                return new SandboxSpec(
                        WorkspaceSpec.ephemeral(),
                        FilesystemPolicy.readWrite(),
                        ProcessPolicy.allowAll(),
                        NetworkPolicy.full(),
                        (ref, ctx) -> true,
                        ResourceLimits.unlimited(),
                        SandboxLifecycle.standard()
                );
            }
        };
    }
}
