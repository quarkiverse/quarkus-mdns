package io.quarkiverse.mdns.deployment;

import java.util.Optional;
import java.util.stream.Stream;

import org.jboss.logging.Logger;

import io.quarkiverse.mdns.runtime.JmDNSProducer;
import io.quarkiverse.mdns.runtime.MdnsRecorder;
import io.quarkiverse.mdns.runtime.MdnsRuntimeConfig;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Consume;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedPackageBuildItem;
import io.quarkus.runtime.LaunchMode;

class MdnsProcessor {

    private static final Logger LOG = Logger.getLogger(MdnsProcessor.class);

    private static final String FEATURE = "mdns";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    public ExtensionEnabledBuildItem checkExtensionEnabled(MdnsRecorder recorder, LaunchModeBuildItem launchMode,
            MdnsRuntimeConfig runtimeConfig) {
        Optional<Boolean> enabled = runtimeConfig.enabled();
        if (enabled != null && enabled.isPresent() && !enabled.get()) {
            LOG.info("mDNS is explicitly disabled.");
            return null;
        }
        ExtensionEnabledBuildItem item = new ExtensionEnabledBuildItem();
        if (enabled != null && enabled.isPresent()) {
            LOG.info("mDNS is enabled.");
            return item;
        }

        // always start in DEV mode if enabled property is not found
        if (launchMode.getLaunchMode() == LaunchMode.DEVELOPMENT) {
            LOG.info("mDNS started automatically in DEV mode.");
            return item;
        }

        return null;
    }

    @BuildStep
    @Consume(ExtensionEnabledBuildItem.class)
    AdditionalBeanBuildItem registerBean() {
        return AdditionalBeanBuildItem.unremovableOf(JmDNSProducer.class);
    }

    @BuildStep
    @Consume(ExtensionEnabledBuildItem.class)
    @Record(ExecutionTime.RUNTIME_INIT)
    void initializeMdns(BeanContainerBuildItem beanContainer, MdnsRecorder recorder,
            MdnsRuntimeConfig runtimeConfig, ShutdownContextBuildItem shutdownContextBuildItem) {
        recorder.initMdns(beanContainer.getValue(), runtimeConfig, shutdownContextBuildItem);
    }

    @BuildStep
    @Consume(ExtensionEnabledBuildItem.class)
    void runtimeInitializedClasses(BuildProducer<RuntimeInitializedPackageBuildItem> runtimeInitializedPackages) {
        //@formatter:off
        Stream.of(javax.jmdns.impl.JmDNSImpl.class.getName(),
                  javax.jmdns.impl.JmmDNSImpl.class.getName())
                .map(RuntimeInitializedPackageBuildItem::new)
                .forEach(runtimeInitializedPackages::produce);
        //@formatter:on
    }

}