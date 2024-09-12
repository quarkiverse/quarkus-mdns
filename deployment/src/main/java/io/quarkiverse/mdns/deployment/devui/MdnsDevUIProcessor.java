package io.quarkiverse.mdns.deployment.devui;

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

        card.setCustomCard("qwc-mdns-card.js");

        cardPageBuildItemBuildProducer.produce(card);
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    JsonRPCProvidersBuildItem createJsonRPCService() {
        return new JsonRPCProvidersBuildItem(MdnsJsonRPCService.class);
    }
}