﻿using System;
using System.Collections.Generic;

namespace Ankor.Core.Action {

	public delegate void ActionReceivedEventHandler(object sender, ActionEvent e);

	//public class ActionEventArgs : EventArgs {
	//  public AAction Action { get; private set; }
	//  public string Path { get; private set; } // not here?!

	//  public ActionEventArgs(AAction action, string path) {
	//    this.Action = action;
	//    this.Path = path;
	//  }

	//}

	/// <summary>
	/// Description of AAction.
	/// </summary>
	public class AAction {
		
		public string Name { get; private set; }
		public IDictionary<string, object> Params { get; private set; }

		private AAction() {}
		
		public AAction(string name) : this(name, null) {		}
		
		public AAction(string name, IDictionary<string, object> parameters) {
			Name = name;
			Params = parameters;// parameters.nullToEmpty();		
		}

		public AAction(string name, string paramKey, object paramVal) : this(name, new Dictionary<string, object> {{ paramKey, paramVal}}) {			
		}
		

		public AAction AddParam(string name, object val) {
			if (Params == null) {
				this.Params = new Dictionary<string, object>();
			}
			Params[name] = val;
			return this;
		}

		public override string ToString() {
			return string.Format("Name: {0}, Params: {1}", Name, Params);
		}
	}
}
