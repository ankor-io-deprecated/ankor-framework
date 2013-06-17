package at.irian.ankor.core.el;

import javax.el.FunctionMapper;
import java.lang.reflect.Method;

/**
* @author Manfred Geiler
*/
class StandardFunctionMapper extends FunctionMapper {
    @Override
    public Method resolveFunction(String prefix, String localName) {
        return null;
    }
}
