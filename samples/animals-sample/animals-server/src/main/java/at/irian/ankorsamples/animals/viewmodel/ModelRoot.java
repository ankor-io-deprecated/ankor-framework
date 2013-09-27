package at.irian.ankorsamples.animals.viewmodel;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankorsamples.animals.domain.animal.AnimalRepository;

/**
 * @author Thomas Spiegl
 */
@SuppressWarnings("UnusedDeclaration")
public class ModelRoot {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TestModel.class);

    private final Ref myRef;
    private final ContentPane contentPane;

    private String userName = "";
    private String serverStatus = "";

    public ModelRoot(Ref myRef, AnimalRepository animalRepository) {
        this.myRef = myRef;
        this.contentPane = new ContentPane(myRef.appendPath("contentPane"),
                                           myRef.appendPath("serverStatus").<String>toTypedRef(),
                                           animalRepository);
        AnkorPatterns.initViewModel(this, myRef);
    }

    @ActionListener
    public void init() {
        myRef.appendPath("userName").setValue("John Doe");
        myRef.appendPath("serverStatus").setValue("");
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
}
