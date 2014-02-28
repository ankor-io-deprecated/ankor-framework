//using System;
//using System.Collections.Generic;
//using System.ComponentModel;
//using System.Linq;
//using Ankor.Core.Action;
//using Ankor.Core.Event.Dispatch;
//using Newtonsoft.Json.Linq;
//
//namespace Ankor.Core.Ref.Json {
//
//	//public delegate void ModelChangedHandler(string path, Change.Change change);
//	//public delegate void ActionFiredHandler(string path, AAction action);
//
//	// this is the json model actually
//	public class JsonModel : IInternalModel {
//
//		private readonly JObject root = new JObject();
//		public PathSyntax PathSyntax { get; private set; }
//
//		//public string ModelId { get; private set; }
//
//
//		public dynamic Root {
//			get { return DynaRef.CreateRef(this, ""); }
//		}
//
//		public EventDispatcher Dispatcher { get; private set; }
//		public EventRegistry EventRegistry { get; private set; }
//
//	//	public event ModelChangedHandler ChangeForRemote = delegate { };
//	//	public event ActionFiredHandler ActionForRemote = delegate { };
//
//		public event PropertyChangedEventHandler PropertyChanged = delegate {};
//		public event ActionReceivedEventHandler ActionFired = delegate {};
//
//		public JsonModel() {
//			PathSyntax = new PathSyntax();
//			// TODO get model id from server
//			//ModelId = "1";
//		}
//
//		public void UpdateFromExternal(string path, object newValue) {
//			UpdateFromExternal(path, Change.Change.ValueChange(newValue));
//		}
//
//		public void UpdateFromExternal(string path, Change.Change change) {
//			object newValue = change.Value;
//			String strippedPath = RemoveRootQualifier(path);
//			lock (this) {
//				// simple try to avoid double change events
//
//				var oldToken = root.SelectToken(strippedPath); 
//				if (oldToken == null) {
//
//					var parentObj = FindDeepestExistingParent(strippedPath);
//					string childPath = PathSyntax.MakeRelativePath(parentObj.Path, strippedPath);
//					
//					parentObj = CreateJObjectsForPath(parentObj, childPath);
//
//					if (!(newValue is JToken)) {
//						// this is only Ok for primitives... (but what else to handle, Lists?)
//						newValue = new JValue(newValue);
//					}
//
//					parentObj.Add(PathSyntax.GetPropertyName(childPath), (JToken) newValue);
//
//				} else {
//					UpdateValue(newValue, oldToken);
//				}
//				//FirePropertyChangedEvents(strippedPath);
//			}
//			FirePropertyChangedEvents(strippedPath); // ? inside or outside the lock?
//
//
//			//Console.WriteLine("NEW ROOT: " + root);
//		}
//
//		private JObject FindDeepestExistingParent(string elemPath) {
//			JObject parentObj = null;
//			while (parentObj == null) {
//				elemPath = PathSyntax.GetParentPath(elemPath);
//				parentObj = (JObject)root.SelectToken(elemPath);
//			}
//			return parentObj;
//		}
//
//		private static JObject CreateJObjectsForPath(JObject parentObj, string childPath) {
//			string[] localNames = childPath.Split('.');
//			for (int i = 0; i < localNames.Length - 1; i++) {
//				string localName = localNames[i];
//				parentObj.Add(localName, new JObject());
//				parentObj = (JObject) parentObj[localName];
//			}
//			return parentObj;
//		}		
//
//		private void FirePropertyChangedEvents(string path) {
//			PropertyChanged(this, new PropertyChangedEventArgs(path));
//		}
//
//		private static string RemoveRootQualifier(string path) {
//			if (path.Equals("root")) {
//				path = "";
//			} else {
//				path = path.Substring("root.".Length);
//			}
//			return path;
//		}
//
//		private static string AddRootQualifier(string path) {
//			if (String.IsNullOrEmpty(path)) {
//				return "root";
//			}
//			return "root." + path;
//		}
//
//		private static void UpdateValue(object newValue, JToken oldToken) {
//			if (oldToken is JObject) {
//				var oldObject = (JObject) oldToken;
//				if (newValue is JObject) {
//					oldObject.ReplaceAll(((JObject) newValue).Children());
//				} else {
//					throw new ArgumentException("unsupported new val type " + newValue.GetType());
//				}
//			} else if (oldToken is JValue) {
//				JValue oldValue = (JValue) oldToken;
//				oldValue.Value = newValue;
//			} else if (oldToken is JArray) {				
//				((JProperty)oldToken.Parent).Value = (JToken)newValue;			
//			} else {
//				throw new ArgumentException("unsupported token type " + oldToken.GetType());
//			}
//		}
//
//		public void InternalSetValue(object newValue, string path) {
//			var token = root.SelectToken(path, false);
//			if (token is JValue) {
//				JValue jVal = ((JValue)token);
//				object oldValue = jVal.Value;
//				if (!Equals(oldValue, newValue)) {
//					jVal.Value = newValue;
//					PropertyChanged(this, new PropertyChangedEventArgs(path));
//				//	ChangeForRemote(AddRootQualifier(path), Change.Change.ValueChange(newValue));
//				}
//			} else {
//				throw new ArgumentException("cannot set value on non-leaf property");
//			}
//		}
//
//		public object InternalGetValue(string path) {
//			var token = root.SelectToken(path, false);
//
//			if (token != null) {
//				return UnwrapValue(token, path);
//			}
//			//throw new ArgumentException("path evaluates to NULL: ");
//			Console.WriteLine("path evaluates to NULL: " + path);
//			return null;
//		}
//
//		private object UnwrapValue(JToken token, string path, bool valuesOnly = false) {
//			if (token is JValue) {
//				return ((JValue)token).Value;
//			}
//			if (valuesOnly) return DynaRef.CreateRef(this, path);
//			if (token is JArray) {
//				int i = 0;
//				return new List<object>(token.Select(token1 => UnwrapValue(token1, PathSyntax.AddArrayIndex(path, i++), true)));
//			}
//			if (token is JObject) {
//				JObject jo = ((JObject)token);
//				IDictionary<string, DynaRef> dict = new SortedDictionary<string, DynaRef>(new InsertionOrderComparer(jo));
//				foreach (var entry in jo) {
//					dict[entry.Key] = DynaRef.CreateRef(this, PathSyntax.MakeAbsolutePath(path, entry.Key));
//				}
//				return dict;
//			}
//			Console.WriteLine("error unwrapping " + token +" type not supported " + token.GetType());
//			return null;
//			//throw new ArgumentException("error unwrapping " + token + " type not supported " + token.GetType());
//			
//		}
//
//		// TODO a bad idea, just works as long as readonly
//		private class InsertionOrderComparer : IComparer<string> {
//			private readonly IDictionary<string, int> childNameOrder;
//			public InsertionOrderComparer(JObject node) {
//				childNameOrder = new Dictionary<string, int>();
//				int i = 0;
//				foreach (var entry in node) {
//					childNameOrder[entry.Key] = i++;
//				}
//			}
//
//			public int Compare(string x, string y) {
//				return childNameOrder[x].CompareTo(childNameOrder[y]);
//			}
//		}
//
//		public void PrintModelToDebug() {
//			Console.WriteLine("Current root\n" + root);
//		}
//
//		public void FireActionFromInternal(string path, AAction aAction) {
//		//	ActionForRemote(AddRootQualifier(path), aAction);
//			//ActionFired(this, new ActionEventArgs(aAction, path));
//		}
//
//		public void FireActionFromRemote(string path, AAction aAction) {
//			//ActionFired(this, new ActionEventArgs(aAction, RemoveRootQualifier(path)));
//		}		
//
//		public IEnumerator<DynaRef> GetRefEnumerator(string path) {
//			var token = root.SelectToken(path, false);
//			if (token == null) {
//				throw new ArgumentException("cannot enumerate at path evaluating to null: " + path);
//			}
//			if (token is JArray) {
//				int i = 0;
//				return new List<DynaRef>(token.Select(token1 => DynaRef.CreateRef(this, PathSyntax.AddArrayIndex(path, i++)))).GetEnumerator();
//			}
//			throw new ArgumentException("cannot enumerate on non array ref '" + path + "' of type " + token.Type);
//		}
//
//		public bool IsArray(string path) {
//			var token = root.SelectToken(path, false);
//			return token is JArray;
//		}
//	}
//
//}
