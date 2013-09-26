grammar RefPattern;

pattern: (relPattern | absPattern) EOF ;

relPattern: '.' absPattern;
absPattern: node (('.' node) | ('[' node ']'))* ;

node: (property | backref) ;

backref: '(' property ')' ;

property: (PROPERTY_ID
           | singleWildcard
           | multiWildcard
           | typeWildcard
           | contextWildcard) ;

singleWildcard:  '*' ;
multiWildcard:   '**' ;
typeWildcard:    TYPE_ID ;
contextWildcard: '@' ;

PROPERTY_ID: ID ;
TYPE_ID:     '<' ID ('.' ID)* '>';
ID:          [a-zA-Z] [a-zA-Z0-9]* ;
