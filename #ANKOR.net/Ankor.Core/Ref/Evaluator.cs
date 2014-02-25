using System;
using System.Collections;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;

namespace Ankor.Core.Ref {

	public class Evaluator {
		private readonly object context;

		public Evaluator(object context) {
			this.context = context;
		}

		public Evaluator() {}

		public object GetValueSafe(object contextObject, string expression) {
			try {
				return GetValue(contextObject, expression);
			} catch (KeyNotFoundException e) {
				return null;
			}			
		}

		public object GetValueSafe(string expression) {
			return GetValueSafe(this.context, expression);
		}

		public object GetValue(string expression) {
			return GetValue(this.context, expression);
		}

		public object GetValue(object contextObject, string expression) {
			CompiledExpression compiledExpression = new SimpleParser(expression).CompilePath();

			return GetValueFromExpression(contextObject, compiledExpression);
		}

		private object GetValueFromExpression(object contextObject, CompiledExpression compiledExpression) {
			foreach (ExpressionPart part in compiledExpression.compiledParts) {
				if (part.EType == ElementType.Property) {
					var key = (string) part.Value;
					contextObject = GetPropertyValue(contextObject, key, compiledExpression);
				} else if (part.EType == ElementType.Indexer) {
					int index = (int) part.Value;
					contextObject = GetListIndexValue(contextObject, index, compiledExpression);
				}
			}
			return contextObject;
		}

		private static object GetListIndexValue(object contextObject, int index, CompiledExpression expression) {
			if (contextObject is IList) {
				contextObject = ((IList) contextObject)[index];
			} else {
				//Console.WriteLine("expected IList, unsupported object type: " + contextObject.GetType());
				throw new ArgumentException("expected IList, unsupported object type: " + contextObject.GetType());
				//return contextObject;
			}
			return contextObject;
		}

		private static object GetPropertyValue(object contextObject, string propName, CompiledExpression expression) {
			
			if (contextObject is IDictionary) {
				var dict = ((IDictionary)contextObject);
				if (!dict.Contains(propName)) {
					throw CreateKeyNotFoundException(propName, expression.ToString());
				}
				contextObject = dict[propName];
			} else if (contextObject is IDictionary<string, object>) {
				var dict = ((IDictionary<string, object>)contextObject);
				if (!dict.ContainsKey(propName)) {
					throw CreateKeyNotFoundException(propName, expression.ToString());
				}
				contextObject = dict[propName];
			} else {
				throw new ArgumentException(string.Format(
					"expected IDictionary.. for property {0} unsupported type {1} ", propName, contextObject.GetType()));
			}
			return contextObject;
		}

		private static KeyNotFoundException CreateKeyNotFoundException(string propName, string expression) {
			return new KeyNotFoundException(string.Format("error eval '{0}', key not found '{1}'",expression, propName));
		}

		public void SetValue(object contextObject, string expression, object newValue) {
			CompiledExpression compiledExpression = new SimpleParser(expression).CompilePath();

			object parent = GetValueFromExpression(contextObject, compiledExpression.GetParent());

			ExpressionPart thisPart = compiledExpression.compiledParts.Last();

			if (parent is IDictionary) {
				string key = (string) thisPart.Value;
				(parent as IDictionary)[key] = newValue;
			} else if (parent is IDictionary<string, object>) {
				(parent as IDictionary<string, object>)[(string) thisPart.Value] = newValue;
			} else if (parent is IList) {
				(parent as IList)[(int) thisPart.Value] = newValue;
			} else {
				throw new ArgumentException(string.Format("try set '{0}' unsupported object type: {1} ", expression, contextObject.GetType()));
			}

		}

		public void SetValue(string expression, object newValue) {
			SetValue(this.context, expression, newValue);
		}

		internal enum ElementType {
			Indexer,
			Property
			
		}

		internal class CompiledExpression {
			internal readonly List<ExpressionPart> compiledParts = new List<ExpressionPart>();

			public CompiledExpression GetParent() {
				CompiledExpression parent = new CompiledExpression();
				parent.compiledParts.AddRange(this.compiledParts.Take(this.compiledParts.Count-1));
				return parent;
			}

			public override string ToString() {
				return string.Join("", compiledParts.Select(e => e.ToString()));
			}
		}



		internal class ExpressionPart {
			internal ElementType EType { get; private set; }
			internal object Value { get; private set; }

			private ExpressionPart(ElementType type, object value) {
				this.EType = type;
				this.Value = value;
			}

			internal static ExpressionPart Indexer(int index) {
				return new ExpressionPart(ElementType.Indexer, index);
			}

			internal static ExpressionPart Property(string name) {
				return new ExpressionPart(ElementType.Property, name);
			}

			public override string ToString() {
				switch (EType) {
					case ElementType.Indexer: return "[" + Value + "]";
					case ElementType.Property: return "." + Value;
				}
				throw new ArgumentException("unknown type " + EType);
			}
		}

		class SimpleParser {
			private readonly char[] characters;
			private int pos = 0;
			private char current = '\0';
			private CompiledExpression compiledExpression;

			private static readonly char[] PropertyEndTokens = new[] { '[', '.', '\0' };

			internal SimpleParser(string expression) {
				compiledExpression = new CompiledExpression();
				if (!string.IsNullOrEmpty(expression) && !expression.StartsWith("[")) {
					expression = "." + expression; // this simplifies the grammar 
				}
				this.characters = (expression + "\0").ToCharArray();

			}

			private char NextChar() {
				current = characters[pos++];
				return current;
			}

			internal CompiledExpression CompilePath() {
				NextChar();
				while (current != '\0') {
					switch (current) {
						case '.':
							ParseProperty();
							break;
						case '[':
							ParseIndexer();
							break;
						default:
							throw new ArgumentException("unexpected token: " + current);
					}
				}
				return this.compiledExpression;
			}

			private void ParseIndexer() {
				StringBuilder sb = new StringBuilder();
				for (NextChar(); !IsIndexerEndToken(); NextChar()) {
					sb.Append(current);
				}
				NextChar();
				compiledExpression.compiledParts.Add(ExpressionPart.Indexer(int.Parse(sb.ToString())));
			}

			private bool IsIndexerEndToken() {
				return current == ']';
			}

			private void ParseProperty() {
				StringBuilder sb = new StringBuilder();

				for (NextChar(); !IsPropertyEndToken(); NextChar()) {
					sb.Append(current);
				}
				compiledExpression.compiledParts.Add(ExpressionPart.Property(sb.ToString()));
			}

			private bool IsPropertyEndToken() {
				return PropertyEndTokens.Contains(current);
			}

		}

		#region OLD_PARSER

		class Parser {
			private readonly char[] characters;
			private int pos = 0;

			private readonly IList<ExpressionPart> compiledParts = new List<ExpressionPart>();
			private string currentToken;
			private TokenType currentTokenType;

			private static readonly char[] TerminalTokens = new[] { '[', ']', '.', '\0' };
			
			internal Parser(string expression) {
				if (!expression.StartsWith("[")) {
					expression = "." + expression; // this simplifies the grammar 
				}
				this.characters = (expression+"\0").ToCharArray();
			}

			enum TokenType {
				BracketOpen,
				BracketClose,
				Dot,
				Ident,
				Digit,
				Eof
			}

			private void Unread() {
				pos--;
			}

			private char NextChar() {
				return characters[pos++];
			}

			private void ReadNext() {
				char c = NextChar();

				if (c == '\0') {
					currentTokenType = TokenType.Eof;

				} else if (c == '[') {
					currentTokenType = TokenType.BracketOpen;

				} else if (c == ']') {
					currentTokenType = TokenType.BracketClose;

				} else if (c == '.') {
					currentTokenType = TokenType.Dot;

				} else if (char.IsNumber(c)) {
					currentTokenType = TokenType.Digit;
					currentToken = c.ToString(CultureInfo.InvariantCulture);

				} else {
					StringBuilder ident = new StringBuilder(c);
					for (; !TerminalTokens.Contains(c); c = NextChar()) {
						ident.Append(c);
					}
					Unread();
					currentTokenType = TokenType.Ident;
					currentToken = ident.ToString();
				}
			}


			internal IList<ExpressionPart> CompilePath() {
				ReadNext();
				while (currentTokenType != TokenType.Eof) {
					Elem();
				}
				return compiledParts;				
			}

			private void Elem() {
				if (currentTokenType == TokenType.BracketOpen) {
					Indexer();
				} else if (currentTokenType == TokenType.Dot) {
					Property();
				} else {
					throw new ArgumentException();
				}
			}

			private void Property() {
				Expect(TokenType.Dot);
				Expect(TokenType.Ident);
				compiledParts.Add(ExpressionPart.Property(currentToken));
			}

			private void Indexer() {
				Expect(TokenType.BracketOpen);
				int num = IntegerNumber();
				compiledParts.Add(ExpressionPart.Indexer(num));
				Expect(TokenType.BracketClose);
			}

			private int IntegerNumber() {
				StringBuilder num = new StringBuilder();
				do {
					num.Append(currentToken);
					ReadNext();
				} while (Accept(TokenType.Digit));				
				return int.Parse(num.ToString());
			}

			private void Expect(TokenType expected) {
				if (!Accept(expected)) {
					throw new ArgumentException();
				}
			}

			private bool Accept(TokenType accepted) {
				if (currentTokenType == accepted) {
					ReadNext();
					return true;
				}
				return false;
			}
		}

		#endregion

	}
}