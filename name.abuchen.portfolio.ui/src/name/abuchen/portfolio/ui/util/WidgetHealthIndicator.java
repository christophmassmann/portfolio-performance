package name.abuchen.portfolio.ui.util;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import name.abuchen.portfolio.model.Security;
import name.abuchen.portfolio.money.Values;
import name.abuchen.portfolio.snapshot.PerformanceIndex;

public class WidgetHealthIndicator
{
    protected Canvas control;
    protected PerformanceIndex index;

    public WidgetHealthIndicator(Composite parent, PerformanceIndex index)
    {
        this.index = index;

        this.control = new Canvas(parent, SWT.NONE);
        this.control.setSize(10, 10);
        this.control.setVisible(false);
        Cursor cursor = new Cursor(parent.getDisplay(), SWT.CURSOR_HAND);
        this.control.setCursor(cursor);

        this.control.addPaintListener(new PaintListener()
        {
            @Override
            public void paintControl(PaintEvent e)
            {
                // Canvas canvas = (Canvas) e.widget;
                e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_YELLOW));
                e.gc.fillRoundRectangle(1, 1, 5, 5, 10, 10);
            }
        });
        GridDataFactory.fillDefaults().hint(10, 10).applyTo(this.control);

        InfoToolTip.attach(this.control, this.getTooltip());
    }

    public Control getControl()
    {
        return control;
    }

    public void update()
    {
        this.control.setVisible(!this.getOutdatedSecurities().isEmpty());
        // @todo update tooltip?
    }

    private List<Security> getOutdatedSecurities()
    {
        // @todo make configurable
        LocalDate daysAgo = LocalDate.now().minusDays(7);

        return this.index.getSecurities().stream()
                        .filter(s -> !s.isRetired()
                                        && (s.getLatest() == null || s.getLatest().getDate().isBefore(daysAgo)))
                        .collect(Collectors.toList());
    }

    private String getTooltip()
    {
        return "Die Kurse folgender Wertpapiere sind älter als 7 Tage, das dargestellte Ergebnis gibt daher evtl. nicht den aktuellen Stand wieder:\n\n"
                        + this.getOutdatedSecurities().stream()
                        .map(s -> s.getName() + " (" + Values.Date.format(s.getLatest().getDate()) + ")")
                        .sorted()
                        .collect(Collectors.joining("\n"));
    }
}
