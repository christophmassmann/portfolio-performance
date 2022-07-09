package name.abuchen.portfolio.ui.views.dashboard;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.swt.widgets.Composite;

import name.abuchen.portfolio.model.AttributeType;
import name.abuchen.portfolio.model.Dashboard.Widget;
import name.abuchen.portfolio.model.LimitPrice;
import name.abuchen.portfolio.model.Security;
import name.abuchen.portfolio.model.SecurityPrice;
import name.abuchen.portfolio.ui.util.Colors;
import name.abuchen.portfolio.ui.util.InfoToolTip;

public class SingleLimitExceededWidget extends AbstractIndicatorWidget<SingleLimitExceededWidget.LimitItem>
{
    public static class LimitItem extends AbstractSecurityListWidget.Item
    {
        private LimitPrice limit;
        private SecurityPrice price;

        public LimitItem(Security security, LimitPrice limit, SecurityPrice price)
        {
            super(security);
            this.limit = limit;
            this.price = price;
        }

    }

    private String toolTip = ""; //$NON-NLS-1$

    public SingleLimitExceededWidget(Widget widget, DashboardData dashboardData)
    {
        super(widget, dashboardData, true);

        addConfig(new AttributesConfig(this, t -> t.getTarget() == Security.class && t.getType() == LimitPrice.class));
    }

    public String getToolTip()
    {
        return toolTip;
    }

    @Override
    public Composite createControl(Composite parent, DashboardResources resources)
    {
        Composite composite = super.createControl(parent, resources);

        InfoToolTip.attach(indicator, this::getToolTip);

        return composite;
    }

    @Override
    public Supplier<LimitItem> getUpdateTask()
    {
        return () -> {

            List<AttributeType> types = get(AttributesConfig.class).getTypes();

            for (Security security : getClient().getSecurities())
            {
                for (AttributeType t : types)
                {
                    Object attribute = security.getAttributes().get(t);
                    if (!(attribute instanceof LimitPrice))
                        continue;

                    LimitPrice limit = (LimitPrice) attribute;

                    SecurityPrice latest = security.getSecurityPrice(LocalDate.now());
                    if (latest != null && limit.isExceeded(latest))
                    { return new LimitItem(security, limit, latest); }
                }

            }
            return null;
        };
    }

    @Override
    public void update(LimitItem item)
    {
        super.update(item);

        DecimalFormat df = new DecimalFormat("+#.#%;-#.#%"); //$NON-NLS-1$

        double limit = item.limit.calculateRelativeDistance(item.price.getValue());

        indicator.setText(df.format(limit));
        indicator.setTextColor(limit > 0 ? Colors.theme().redForeground() : Colors.theme().greenForeground());
    }

}
