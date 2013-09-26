package at.irian.ankorsamples.fxrates.server;

import java.math.BigDecimal;

/**
 * @author Thomas Spiegl
 */
public class Rate {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Rate.class);
    private final String symbol;
    private final BigDecimal bid;
    private final BigDecimal ask;
    private final BigDecimal high;
    private final BigDecimal low;
    private final Short direction;
    private final String last;

    public Rate(String symbol, BigDecimal bid, BigDecimal ask, BigDecimal high, BigDecimal low, Short direction, String last) {
        this.symbol = symbol;
        this.bid = bid;
        this.ask = ask;
        this.high = high;
        this.low = low;
        this.direction = direction;
        this.last = last;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public Short getDirection() {
        return direction;
    }

    public String getLast() {
        return last;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rate rate = (Rate) o;

        if (!ask.equals(rate.ask)) return false;
        if (!bid.equals(rate.bid)) return false;
        if (!high.equals(rate.high)) return false;
        if (last != null ? !last.equals(rate.last) : rate.last != null) return false;
        if (!low.equals(rate.low)) return false;
        if (!symbol.equals(rate.symbol)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return symbol.hashCode();
    }
}
