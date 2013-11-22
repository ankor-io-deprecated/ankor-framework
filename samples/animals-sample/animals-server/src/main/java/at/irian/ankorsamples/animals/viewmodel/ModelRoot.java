package at.irian.ankorsamples.animals.viewmodel;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.big.AnkorBigMap;
import at.irian.ankor.i18n.ResourceBundleMap;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankorsamples.animals.domain.animal.AnimalRepository;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * @author Thomas Spiegl
 */
@SuppressWarnings("UnusedDeclaration")
public class ModelRoot {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelRoot.class);

    private final Ref rootRef;
    private final Ref userNameRef;
    private final Ref serverStatusRef;
    private final Ref localeRef;
    private final ContentPane contentPane;

    private String userName = "";
    private String serverStatus = "";

    private Locale locale = null;
    private Locale[] supportedLocales = {Locale.GERMAN, Locale.ENGLISH};

    @AnkorBigMap(initialSize = 10, missingValueSubstitute = String.class)
    private Map<String,String> resources = Collections.emptyMap();

    public ModelRoot(Ref rootRef, AnimalRepository animalRepository) {
        this.rootRef = rootRef;
        this.userNameRef = rootRef.appendPath("userName");
        this.serverStatusRef = rootRef.appendPath("serverStatus");
        this.localeRef = rootRef.appendPath("locale");
        this.contentPane = new ContentPane(rootRef.appendPath("contentPane"),
                                           rootRef.appendPath("serverStatus").<String>toTypedRef(),
                                           rootRef.appendPath("resources"),
                                           animalRepository);
        AnkorPatterns.initViewModel(this, rootRef);
    }

    @ActionListener
    public void init() {
        userNameRef.setValue("John Doe");
        serverStatusRef.setValue("");
        localeRef.setValue(Locale.ENGLISH);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(String serverStatus) {
        this.serverStatus = serverStatus;
    }

    public ContentPane getContentPane() {
        return contentPane;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale[] getSupportedLocales() {
        return supportedLocales;
    }

    @ChangeListener(pattern = ".locale")
    public void onLocaleChanged() {
        LOG.info("Locale changed to {}", locale);

        ResourceBundleMap labels = ResourceBundleMap.getBundleMap("at.irian.ankorsamples.animals.viewmodel.i18n.Resources",
                                                                  locale);
        rootRef.appendPath("resources").setValue(labels);
    }

    public Map<String, String> getResources() {
        return resources;
    }

    public void setResources(Map<String, String> resources) {
        this.resources = resources;
    }

}
