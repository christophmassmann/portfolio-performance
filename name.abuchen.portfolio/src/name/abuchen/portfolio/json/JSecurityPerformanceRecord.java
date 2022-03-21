package name.abuchen.portfolio.json;

import name.abuchen.portfolio.money.Money;
import name.abuchen.portfolio.money.Quote;
import name.abuchen.portfolio.snapshot.security.SecurityPerformanceRecord;

public class JSecurityPerformanceRecord
{
    private Quote fifoCostPerSharesHeld;
    private Money capitalGainsOnHoldings;
    private Quote quote;
    private Money marketValue;
    private double twror;
    private double twrorpa;
    private double volatility;
    private double drawdown;
    private double drawdownDuration;
    private double irr;
    private Money delta;
    private double deltaPercent;
    private long sharesHeld;
    private Money fees;
    private Money taxes;
    private Money sumOfDividends;
    private double rateOfReturnPerYear;
    private double realizedCapitalGains;
    private double unrealizedCapitalGains;
    
    public static JSecurityPerformanceRecord from(SecurityPerformanceRecord record)
    {
        JSecurityPerformanceRecord s = new JSecurityPerformanceRecord();
        s.fifoCostPerSharesHeld = record.getFifoCostPerSharesHeld();
        s.capitalGainsOnHoldings = record.getCapitalGainsOnHoldings();
        s.quote = record.getQuote();
        s.marketValue = record.getMarketValue();
        s.twror = record.getTrueTimeWeightedRateOfReturn();
        s.twrorpa = record.getTrueTimeWeightedRateOfReturnAnnualized();
        s.volatility = record.getVolatility();
        s.drawdown = record.getMaxDrawdown();
        s.drawdownDuration = record.getMaxDrawdownDuration();
        s.irr = record.getIrr();
        s.delta = record.getDelta();
        s.deltaPercent = record.getDeltaPercent();
        s.sharesHeld = record.getSharesHeld();
        s.fees = record.getFees();
        s.taxes = record.getTaxes();
        s.sumOfDividends = record.getSumOfDividends();
        s.rateOfReturnPerYear = record.getRateOfReturnPerYear();
                
        return s;
    }

    public Quote getFifoCostPerSharesHeld()
    {
        return fifoCostPerSharesHeld;
    }

    public Money getCapitalGainsOnHoldings()
    {
        return capitalGainsOnHoldings;
    }

    public Quote getQuote()
    {
        return quote;
    }

    public Money getMarketValue()
    {
        return marketValue;
    }
    
    public double getTwror()
    {
        return twror;
    }

    public double getTwrorpa()
    {
        return twrorpa;
    }

    public double getVolatility()
    {
        return volatility;
    }

    public double getDrawdown()
    {
        return drawdown;
    }

    public double getDrawdownDuration()
    {
        return drawdownDuration;
    }

    public double getIrr()
    {
        return irr;
    }

    public Money getDelta()
    {
        return delta;
    }

    public double getDeltaPercent()
    {
        return deltaPercent;
    }

    public long getSharesHeld()
    {
        return sharesHeld;
    }

    public Money getFees()
    {
        return fees;
    }

    public Money getTaxes()
    {
        return taxes;
    }

    public Money getSumOfDividends()
    {
        return sumOfDividends;
    }

    public double getRateOfReturnPerYear()
    {
        return rateOfReturnPerYear;
    }
}
