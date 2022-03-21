package name.abuchen.portfolio.json;

import java.time.LocalDate;

import name.abuchen.portfolio.model.SecurityPrice;
import name.abuchen.portfolio.money.Values;

public class JSecurityPrice
{
    private LocalDate date;
    private double price;
    
    public LocalDate getDate()
    {
        return date;
    }

    public double getPrice()
    {
        return price;
    }
    
    public static JSecurityPrice fromSecurityPrice(SecurityPrice securityPrice)
    {
        JSecurityPrice p = new JSecurityPrice();
        p.date = securityPrice.getDate();
        p.price = securityPrice.getValue() / Values.Share.divider();
        
        return p;
    }
}
