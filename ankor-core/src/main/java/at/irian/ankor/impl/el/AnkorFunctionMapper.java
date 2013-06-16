package at.irian.ankor.impl.el;

import javax.el.FunctionMapper;
import java.lang.reflect.Method;

/**
* @author Manfred Geiler
*/
class AnkorFunctionMapper extends FunctionMapper {
    @Override
    public Method resolveFunction(String prefix, String localName) {
        return null;
    }
}
