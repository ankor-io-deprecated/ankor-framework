using System;
using System.Collections;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;

namespace Ankor.Core.Test.Ref {

	//public class Evaluator {

	//  public object Eval(object contextObject, string expression) {
	//    IEnumerable<ExpressionPart> expressionParts = new SimpleParser(expression).CompilePath();

	//    foreach (ExpressionPart part in expressionParts) {
	//      if (part.EType == ElementType.Property) {
	//        var key = (string)part.Value;
	//        contextObject = EvalProperty(contextObject, key);
	//      } else if (part.EType == ElementType.Indexer) {
	//        int index = (int)part.Value;
	//        contextObject = EvalListIndex(contextObject, index);
	//      }
	//    }
	//    return contextObject;
	//  }

	//  private static object EvalListIndex(object contextObject, int index) {
	//    if (contextObject is IList) {
	//      contextObject = ((IList) contextObject)[index];
	//    } else {
	//      throw new ArgumentException("unsupported context object type " + contextObject.GetType());
	//    }
	//    return contextObject;
	//  }

	//  private static object EvalProperty(object contextObject, string propName) {
			
	//    if (contextObject is IDictionary) {
	//      contextObject = ((IDictionary)contextObject)[propName];
	//    } else if (contextObject is IDictionary<string, object>) {
	//      contextObject = ((IDictionary<string, object>)contextObject)[propName];
	//    } else {
	//      throw new ArgumentException("unsupported context object type " + contextObject.GetType());
	//    }
	//    return contextObject;
	//  }

	//  internal enum ElementType {
	//    Indexer,
	//    Property
	//  }

	//  internal class ExpressionPart {
	//    internal ElementType EType { get; private set; }
	//    internal object Value { get; private set; }

	//    private ExpressionPart(ElementType type, object value) {
	//      this.EType = type;
	//      this.Value = value;
	//    }

	//    internal static ExpressionPart Indexer(int index) {
	//      return new ExpressionPart(ElementType.Indexer, index);
	//    }

	//    internal static ExpressionPart Property(string name) {
	//      return new ExpressionPart(ElementType.Property, name);
	//    }
	//  }

	//  class SimpleParser {
	//    private readonly char[] characters;
	//    private int pos = 0;
	//    private char current = '\0';
	//    private readonly IList<ExpressionPart> compiledParts = new List<ExpressionPart>();

	//    private static readonly char[] PropertyEndTokens = new[] { '[', '.', '\0' };

	//    internal SimpleParser(string expression) {
	//      if (!expression.StartsWith("[")) {
	//        expression = "." + expression; // this simplifies the grammar 
	//      }
	//      this.characters = (expression + "\0").ToCharArray();				
	//    }

	//    private char NextChar() {
	//      current = characters[pos++];
	//      return current;
	//    }

	//    internal IEnumerable<ExpressionPart> CompilePath() {
	//      NextChar();
	//      while (current != '\0') {
	//        switch (current) {
	//          case '.':
	//            ParseProperty();
	//            break;
	//          case '[':
	//            ParseIndexer();
	//            break;
	//          default:
	//            throw new ArgumentException("unexpected token: " + current);
	//        }
	//      }
	//      return this.compiledParts;
	//    }

	//    private void ParseIndexer() {
	//      StringBuilder sb = new StringBuilder();
	//      for (NextChar(); !IsIndexerEndToken(); NextChar()) {
	//        sb.Append(current);
	//      }
	//      NextChar();
	//      compiledParts.Add(ExpressionPart.Indexer(int.Parse(sb.ToString())));
	//    }

	//    private bool IsIndexerEndToken() {
	//      return current == ']';
	//    }

	//    private void ParseProperty() {
	//      StringBuilder sb = new StringBuilder();

	//      for (NextChar(); !IsPropertyEndToken(); NextChar()) {
	//        sb.Append(current);
	//      }
	//      compiledParts.Add(ExpressionPart.Property(sb.ToString()));
	//    }

	//    private bool IsPropertyEndToken() {
	//      return PropertyEndTokens.Contains(current);
	//    }

	//  }

	//  class Parser {
	//    private readonly char[] characters;
	//    private int pos = 0;

	//    private readonly IList<ExpressionPart> compiledParts = new List<ExpressionPart>();
	//    private string currentToken;
	//    private TokenType currentTokenType;

	//    private static readonly char[] TerminalTokens = new[] { '[', ']', '.', '\0' };
			
	//    internal Parser(string expression) {
	//      if (!expression.StartsWith("[")) {
	//        expression = "." + expression; // this simplifies the grammar 
	//      }
	//      this.characters = (expression+"\0").ToCharArray();
	//    }

	//    enum TokenType {
	//      BracketOpen,
	//      BracketClose,
	//      Dot,
	//      Ident,
	//      Digit,
	//      Eof
	//    }

	//    private void Unread() {
	//      pos--;
	//    }

	//    private char NextChar() {
	//      return characters[pos++];
	//    }

	//    private void ReadNext() {
	//      char c = NextChar();

	//      if (c == '\0') {
	//        currentTokenType = TokenType.Eof;

	//      } else if (c == '[') {
	//        currentTokenType = TokenType.BracketOpen;

	//      } else if (c == ']') {
	//        currentTokenType = TokenType.BracketClose;

	//      } else if (c == '.') {
	//        currentTokenType = TokenType.Dot;

	//      } else if (char.IsNumber(c)) {
	//        currentTokenType = TokenType.Digit;
	//        currentToken = c.ToString(CultureInfo.InvariantCulture);

	//      } else {
	//        StringBuilder ident = new StringBuilder(c);
	//        for (; !TerminalTokens.Contains(c); c = NextChar()) {
	//          ident.Append(c);
	//        }
	//        Unread();
	//        currentTokenType = TokenType.Ident;
	//        currentToken = ident.ToString();
	//      }
	//    }


	//    internal IList<ExpressionPart> CompilePath() {
	//      ReadNext();
	//      while (currentTokenType != TokenType.Eof) {
	//        Elem();
	//      }
	//      return compiledParts;				
	//    }

	//    private void Elem() {
	//      if (currentTokenType == TokenType.BracketOpen) {
	//        Indexer();
	//      } else if (currentTokenType == TokenType.Dot) {
	//        Property();
	//      } else {
	//        throw new ArgumentException();
	//      }
	//    }

	//    private void Property() {
	//      Expect(TokenType.Dot);
	//      Expect(TokenType.Ident);
	//      compiledParts.Add(ExpressionPart.Property(currentToken));
	//    }

	//    private void Indexer() {
	//      Expect(TokenType.BracketOpen);
	//      int num = IntegerNumber();
	//      compiledParts.Add(ExpressionPart.Indexer(num));
	//      Expect(TokenType.BracketClose);
	//    }

	//    private int IntegerNumber() {
	//      StringBuilder num = new StringBuilder();
	//      do {
	//        num.Append(currentToken);
	//        ReadNext();
	//      } while (Accept(TokenType.Digit));				
	//      return int.Parse(num.ToString());
	//    }

	//    private void Expect(TokenType expected) {
	//      if (!Accept(expected)) {
	//        throw new ArgumentException();
	//      }
	//    }

	//    private bool Accept(TokenType accepted) {
	//      if (currentTokenType == accepted) {
	//        ReadNext();
	//        return true;
	//      }
	//      return false;
	//    }
	//  }
	//}
}