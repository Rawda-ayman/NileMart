package org.route;

import java.math.BigDecimal;

public enum Zone
{
    GREATER_CAIRO(50, true),
    ALEX_DELTA(65, true),
    CANAL(70, true),
    UPPER_EGYPT(85, true),
    REMOTE(120, false);

    private final BigDecimal shippingFee;
    private final boolean freeShipping;

    Zone(double shippingFee, boolean freeShipping) {
        this.shippingFee = BigDecimal.valueOf(shippingFee);
        this.freeShipping = freeShipping;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public boolean isFreeShipping() {
        return freeShipping;
    }
}
