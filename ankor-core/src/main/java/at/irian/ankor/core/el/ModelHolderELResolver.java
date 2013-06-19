package at.irian.ankor.core.el;

import at.irian.ankor.core.application.ModelHolder;

import javax.el.ELContext;
import java.beans.FeatureDescriptor;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Manfred Geiler
 */
public class ModelHolderELResolver extends SingleReadonlyVariableELResolver {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelHolderELResolver.class);


    public ModelHolderELResolver(String modelHolderVarName, ModelHolder modelHolder) {
        super(modelHolderVarName, modelHolder);
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        if (base == null) {
            FeatureDescriptor featureDescriptor = new FeatureDescriptor();
            featureDescriptor.setName(varName);
            featureDescriptor.setDisplayName(varName);
            featureDescriptor.setExpert(true);
            featureDescriptor.setShortDescription("model holder singleton instance");
            return Collections.singleton(featureDescriptor).iterator();
        }
        return null;
    }

}
