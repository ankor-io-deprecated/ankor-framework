package at.irian.ankor.el;

import at.irian.ankor.context.ModelHolder;
import com.typesafe.config.Config;

import javax.el.ELContext;
import java.beans.FeatureDescriptor;
import java.util.Collections;
import java.util.Iterator;

/**
* @author Manfred Geiler
*/
public class ModelHolderELResolver extends SingleReadonlyVariableELResolver {

    public ModelHolderELResolver(Config config, ModelHolder modelHolder) {
        super(config.getString("ankor.variable-names.modelHolder"), modelHolder);
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
