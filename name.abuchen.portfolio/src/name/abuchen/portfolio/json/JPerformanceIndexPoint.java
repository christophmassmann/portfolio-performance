package name.abuchen.portfolio.json;

import java.time.LocalDate;

import name.abuchen.portfolio.model.SecurityPrice;
import name.abuchen.portfolio.money.Values;

public class JPerformanceIndexPoint
{
    private LocalDate date;
    private double percent;
    
    public LocalDate getDate()
    {
        return date;
    }

    public double getPercent()
    {
        return percent;
    }
    
    public static JPerformanceIndexPoint from(LocalDate date, double percent)
    {
        JPerformanceIndexPoint p = new JPerformanceIndexPoint();
        p.date = date;
        p.percent = percent*100;
        
        return p;
    }
}
