package at.irian.ankor.ref.match.pattern;

import at.irian.ankor.ref.match.RefMatcher;
import at.irian.ankor.ref.match.RefMatcherFactory;
import org.antlr.v4.runtime.*;

/**
 * @author Manfred Geiler
 */
public class AntlrRefMatcherFactory implements RefMatcherFactory {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AntlrRefMatcherFactory.class);

    @Override
    public RefMatcher getRefMatcher(String strPattern) {
        Pattern pattern = createPatternFrom(strPattern);
        return new PatternRefMatcher(pattern);
    }


    protected Pattern createPatternFrom(String strPattern) {

        RefPatternParser.PatternContext pattern;
        try {
            CharStream cs = new ANTLRInputStream(strPattern);
            Lexer lexer = new RefPatternLexer(cs);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            RefPatternParser parser = new RefPatternParser(tokens);
            parser.setErrorHandler(new BailErrorStrategy());
            pattern = parser.pattern();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid pattern " + strPattern, e);
        }

        Pattern refPattern;
        RefPatternParser.AbsPatternContext absPattern;
        if (pattern.relPattern() != null) {
            absPattern = pattern.relPattern().absPattern();
            refPattern = new Pattern(null, createRelativeRootNode());
        } else {
            absPattern = pattern.absPattern();
            refPattern = null;
        }

        for (RefPatternParser.NodeContext node : absPattern.node()) {
            if (refPattern == null) {
                refPattern = createRootPattern(node);
            } else {
                refPattern = createChildPattern(node, refPattern);
            }
        }

        return refPattern;
    }

    private Pattern createRootPattern(RefPatternParser.NodeContext node) {
        return createChildPattern(node, null);
    }

    private Pattern createChildPattern(RefPatternParser.NodeContext node,
                                          final Pattern parentPattern) {
        return new Pattern(parentPattern, createNode(node));
    }

    private Node createNode(final RefPatternParser.NodeContext node) {

        RefPatternParser.PropertyContext property;
        RefPatternParser.BackrefContext backref = node.backref();
        if (backref != null) {
            property = backref.property();
        } else {
            property = node.property();
        }

        String propertyName = property.PROPERTY_ID() != null ? property.PROPERTY_ID().getText() : null;

        String javaType = null;
        WildcardType wildcardType;
        if (property.singleWildcard() != null) {
            wildcardType = WildcardType.singleNode;
        } else if (property.multiWildcard() != null) {
            wildcardType = WildcardType.multiNode;
        } else if (property.typeWildcard() != null) {
            wildcardType = WildcardType.javaType;
            String typeIdToken = property.typeWildcard().TYPE_ID().getText();
            javaType = typeIdToken.substring(1, typeIdToken.length() - 1);
        } else if (property.contextWildcard() != null) {
            wildcardType = WildcardType.context;
        } else {
            wildcardType = null;
        }

        return new Node(propertyName, wildcardType, backref != null, javaType);
    }

    private Node createRelativeRootNode() {
        return new Node(null, WildcardType.context, false, null);
    }
}
