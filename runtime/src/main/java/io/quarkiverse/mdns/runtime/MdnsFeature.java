package io.quarkiverse.mdns.runtime;

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;

public class MdnsFeature implements Feature {

    private final static String REASON = "mDNS runtime initialization";

    @Override
    public void afterRegistration(AfterRegistrationAccess access) {
        RuntimeClassInitialization.initializeAtRunTime("javax.jmdns.impl.JmDNSImpl");
    }

    @Override
    public String getDescription() {
        return REASON;
    }
}