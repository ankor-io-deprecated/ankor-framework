using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Dynamic;
using System.Linq;
using System.Text;
using Ankor.Core.Action;
using Ankor.Core.Change;
using Ankor.Core.Event;
using Ankor.Core.Event.Dispatch;
using Ankor.Core.Messaging.Json;
using Newtonsoft.Json.Linq;

namespace Ankor.Core.Ref {
	public class RModel : IInternalModel {

		public PathSyntax PathSyntax { get; private set; }
		public EventDispatcher Dispatcher { get; private set; }
		public EventRegistry EventRegistry { get; private set; }

		private object root = new ExpandoObject();
		private Evaluator evaluator;

		public RModel() {
			PathSyntax = new PathSyntax();
			this.evaluator = new Evaluator(root);
			EventRegistry = new EventRegistry();
			Dispatcher = new EventDispatcher(EventRegistry);
		}

		public dynamic Root {
			get { return DynaRef.CreateRef(this, ""); }
		}

		public void UpdateFromExternal(string path, object newValue) {
			UpdateFromExternal(path, Change.Change.ValueChange(newValue));
		}

		public void UpdateFromExternal(string path, Change.Change change) {
			var refToChange = DynaRef.CreateRef(this, path);
			refToChange.ApplyChange(new RemoteSource(), change);
		}

		public void InternalSetValue(object newValue, string path) {
			
			if (string.IsNullOrEmpty(path)) {
				// is root change
				root = newValue;
				this.evaluator = new Evaluator(root);
				return;
			}

			evaluator.SetValue(path, newValue);

		}

		//private object CreateObjectsForPath(IDictionary<string, object> parentObj, string childPath) {
		//  string[] localNames = childPath.Split('.');
		//  for (int i = 0; i < localNames.Length - 1; i++) {
		//    string localName = localNames[i];
		//    parentObj.Add(localName, new ExpandoObject());
		//    parentObj = (IDictionary<string, object>) parentObj[localName];
		//  }
		//  return parentObj;
		//}

		//private object FindLastExistingParent(string path, out string parantPath) {
		//  object parent = null;
		//  parantPath = path;
		//  while (parent == null) {
		//    parantPath = PathSyntax.GetParentPath(parantPath);
		//    parent = evaluator.GetValueSafe(parantPath);					
		//  }
		//  return parent;
		//}


		public object InternalGetValue(string path) {
			return evaluator.GetValueSafe(path);
		}

		public void PrintModelToDebug() {
			StringBuilder sb = new StringBuilder();
			AppendDataTreeFormatted(sb, root, "");
			Console.WriteLine(sb.ToString());
			
		}

		private void AppendDataTreeFormatted(StringBuilder sb, object obj, string indent) {
			if (obj is IDictionary<string, object>) {
				sb.Append("{\n");
				foreach (var entry in (obj as IDictionary<string, object>)) {
					sb.Append(indent).Append("  '").Append(entry.Key).Append("': ");
					AppendDataTreeFormatted(sb, entry.Value, indent + "  ");
				}
				sb.Append(indent).Append("}\n");
			} else if (obj is IList) {
				sb.Append("[\n");
				foreach (var entry in (obj as IList)) {
					sb.Append(indent).Append("  ");
					AppendDataTreeFormatted(sb, entry, indent + "  ");
				}
				sb.Append(indent).Append("]\n");
				
			} else {
				if (obj == null) {
					sb.Append("null\n");
				} else {
					sb.Append("'").Append(obj).Append("'\n");
				}
			}
			
		}

	}
}
