package name.abuchen.portfolio.util;

import java.time.Clock;
import java.time.LocalDate;

import name.abuchen.portfolio.model.Security;

public final class SecurityTimeliness
{
    private Security security;
    private TradeCalendar tradeCalendar;
    private Clock clock;

    public SecurityTimeliness(Security security, Clock clock)
    {
        this.security = security;
        this.clock = clock;
        this.tradeCalendar = TradeCalendarManager.getInstance(security);
    }

    public boolean isStale()
    {
        // @todo make configurable
        final LocalDate daysAgo = this.getStartDate(7);

        return !this.security.isRetired()
                        && (this.security.getLatest() == null || this.security.getLatest().getDate().isBefore(daysAgo));
    }

    private LocalDate getStartDate(int numberOfBusinessDaysToLookBack)
    {
        LocalDate currentDay = LocalDate.now(this.clock);
        while (numberOfBusinessDaysToLookBack > 0)
        {
            currentDay = currentDay.minusDays(1);

            if (this.tradeCalendar.isHoliday(currentDay) || this.tradeCalendar.isWeekend(currentDay))
            {
                continue;
            }

            numberOfBusinessDaysToLookBack--;
        }

        return currentDay;
    }
}
