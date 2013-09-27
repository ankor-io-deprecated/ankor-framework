package at.irian.ankor.annotation;

/**
 * @author Manfred Geiler
 */
public class MethodParamInfo {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MethodParamInfo.class);

    private final String name;
    private final Class<?> type;
    private final boolean backRef;

    private MethodParamInfo(String name, Class<?> type, boolean backRef) {
        this.name = name;
        this.type = type;
        this.backRef = backRef;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Class<?> getType() {
        return type;
    }

    public boolean isBackRef() {
        return backRef;
    }


    public static MethodParamInfo namedActionParam(String name, Class<?> type) {
        return new MethodParamInfo(name, type, false);
    }

    public static MethodParamInfo backRefParam(Class<?> type) {
        return new MethodParamInfo(null, type, true);
    }

}
