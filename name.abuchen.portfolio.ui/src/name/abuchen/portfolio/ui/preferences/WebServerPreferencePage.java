package name.abuchen.portfolio.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;

import name.abuchen.portfolio.ui.UIConstants;

public class WebServerPreferencePage extends FieldEditorPreferencePage
{

    public WebServerPreferencePage()
    {
        super(GRID);
        setTitle("Web Server [Experimental]"); //$NON-NLS-1$
    }

    @Override
    public void createFieldEditors()
    {
        addField(new BooleanFieldEditor(UIConstants.Preferences.RUN_WEB_SERVER, //
                        "Start embedded web server", getFieldEditorParent())); //$NON-NLS-1$

        addField(new IntegerFieldEditor(UIConstants.Preferences.WEB_SERVER_PORT, //
                        "Web server port", getFieldEditorParent())); //$NON-NLS-1$

        addField(new StringFieldEditor(UIConstants.Preferences.WEB_SERVER_TOKEN, //
                        "Access token", getFieldEditorParent())); //$NON-NLS-1$

    }
}
