package io.quarkiverse.mdns.deployment.devui;

import java.util.Objects;

import javax.jmdns.JmmDNS;

import io.quarkiverse.mdns.runtime.MdnsJsonRPCService;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.devui.spi.JsonRPCProvidersBuildItem;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.ExternalPageBuilder;
import io.quarkus.devui.spi.page.Page;
import io.quarkus.devui.spi.page.PageBuilder;

/**
 * Dev UI card for displaying important details such as the exposed mDNS service.
 */
public class MdnsDevUIProcessor {

    @BuildStep(onlyIf = IsDevelopment.class)
    void createCard(BuildProducer<CardPageBuildItem> cardPageBuildItemBuildProducer) {
        final CardPageBuildItem card = new CardPageBuildItem();

        final PageBuilder<ExternalPageBuilder> sessionPage = Page.externalPageBuilder("Service")
                .icon("font-awesome-solid:tower-broadcast")
                .dynamicLabelJsonRPCMethodName("getService");
        card.addPage(sessionPage);

        final PageBuilder<ExternalPageBuilder> versionPage = Page.externalPageBuilder("Version")
                .icon("font-awesome-solid:book")
                .url("https://github.com/jmdns/jmdns")
                .doNotEmbed()
                .staticLabel(Objects.toString(JmmDNS.class.getPackage().getImplementationVersion(), "?"));
        card.addPage(versionPage);

        card.setCustomCard("qwc-mdns-card.js");

        cardPageBuildItemBuildProducer.produce(card);
    }

    @BuildStep
    JsonRPCProvidersBuildItem createJsonRPCService() {
        return new JsonRPCProvidersBuildItem(MdnsJsonRPCService.class);
    }
}
