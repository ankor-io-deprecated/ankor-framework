package at.irian.ankor.api.model.deprecated;

/**
 */
public class ModelPropertyPathFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelPropertyPathFactory.class);

    private static final ModelPropertyPath ROOT_INSTANCE = new ModelPropertyPath() {
        @Override
        public String getName() {
            return "/";
        }

        @Override
        public String toString() {
            return getName();
        }

        @Override
        public ModelPropertyPath getRoot() {
            return this;
        }

        @Override
        public ModelPropertyPath getParent() {
            return null;
        }

        @Override
        public String getProperty() {
            return null;
        }

        @Override
        public ModelPropertyPath withChild(String property) {
            return create(this, property);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ModelPropertyPath && isRoot((ModelPropertyPath)obj);
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean isChildOf(ModelPropertyPath p) {
            return false;
        }

        @Override
        public boolean isDescendantOf(ModelPropertyPath p) {
            return false;
        }
    };

    public ModelPropertyPath getRootPath() {
        return ROOT_INSTANCE;
    }

    private static ModelPropertyPath create(final ModelPropertyPath parent, final String property) {
        final String name = isRoot(parent)
                            ? "/" + property
                            : parent.getName() + "/" + property;
        return new ModelPropertyPath() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String toString() {
                return getName();
            }

            @Override
            public ModelPropertyPath getRoot() {
                return parent.getRoot();
            }

            @Override
            public ModelPropertyPath getParent() {
                return parent;
            }

            @Override
            public String getProperty() {
                return property;
            }

            @Override
            public ModelPropertyPath withChild(String property) {
                return create(this, property);
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof ModelPropertyPath
                       && isEqual(((ModelPropertyPath)obj).getParent(), parent)
                       && isEqual(((ModelPropertyPath)obj).getProperty(), property);
            }

            @Override
            public int hashCode() {
                return parent.hashCode() + 31 * property.hashCode();
            }

            @Override
            public boolean isChildOf(ModelPropertyPath p) {
                return p.equals(parent);
            }

            @Override
            public boolean isDescendantOf(ModelPropertyPath p) {
                return isChildOf(p) || parent.isDescendantOf(p);
            }
        };
    }

    private static boolean isRoot(ModelPropertyPath path) {
        return path.getParent() == null;
    }

    private static boolean isEqual(Object p1, Object p2) {
        return (p1 == null && p2 == null) || (p1 != null && p2 != null && p1.equals(p2));
    }
}
