package at.irian.ankor.el;

import at.irian.ankor.ref.RefFactory;

import javax.el.ELContext;
import javax.el.ExpressionFactory;

/**
 * @author Manfred Geiler
 */
public interface ELSupport {

    ExpressionFactory getExpressionFactory();

    ELContext getELContextFor(RefFactory refFactory);
}
