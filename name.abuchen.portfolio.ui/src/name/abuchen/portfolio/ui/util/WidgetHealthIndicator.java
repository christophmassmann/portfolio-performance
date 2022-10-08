package name.abuchen.portfolio.ui.util;

import java.time.Clock;
import java.util.ArrayList;
import java.util.HashSet;
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
import name.abuchen.portfolio.util.SecurityTimeliness;

public class WidgetHealthIndicator
{
    protected Canvas control;
    protected List<Security> securities;

    public WidgetHealthIndicator(Composite parent)
    {
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
                e.gc.fillRoundRectangle(1, 5, 5, 5, 10, 10);
            }
        });
        GridDataFactory.fillDefaults().hint(10, 10).applyTo(this.control);

        InfoToolTip.attach(this.control, this::getTooltip);
    }

    public Control getControl()
    {
        return control;
    }

    public void setSecurities(List<Security> securities)
    {
        // remove duplicates
        this.securities = new ArrayList<>(new HashSet<>(securities));
        this.update();
    }

    public void update()
    {
        this.control.setVisible(!this.getStaleSecurities().isEmpty());
    }

    private List<Security> getStaleSecurities()
    {
        return this.securities.stream()
                        .filter(s -> (new SecurityTimeliness(s, Clock.systemDefaultZone())).isStale())
                        .collect(Collectors.toList());
    }

    private String getTooltip()
    {
        if (this.securities == null)
            return ""; //$NON-NLS-1$

        return "Die aktuellen Kurse folgender Wertpapiere sind älter als X Tage, das dargestellte Ergebnis gibt daher evtl. nicht den momentanen Stand wieder:\n\n"
                        + this.getStaleSecurities().stream()
                                        .map(s -> s.getName() + " (" + Values.Date.format(s.getLatest().getDate()) //$NON-NLS-1$
                                                        + ")") //$NON-NLS-1$
                        .sorted()
                                        .collect(Collectors.joining("\n")); //$NON-NLS-1$
    }
}
