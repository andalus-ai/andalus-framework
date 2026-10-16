package tech.kayys.andalus.a2ui.andalus;

import tech.kayys.andalus.aqli.sdk.WayangStandardAlignmentPortfolio;
import tech.kayys.andalus.aqli.sdk.WayangStandardAlignmentProvider;

public final class WayangA2uiStandardAlignmentProvider implements WayangStandardAlignmentProvider {

    @Override
    public String providerId() {
        return "andalus-a2ui-standard-alignment";
    }

    @Override
    public WayangStandardAlignmentPortfolio portfolio() {
        return WayangA2uiStandardAlignment.portfolio();
    }
}
