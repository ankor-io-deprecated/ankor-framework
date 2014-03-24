using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Ankor.Core.Action;
using Ankor.Core.Messaging.WebSockets;

namespace Ankor.Core.Messaging.Comm {
	public class CommMessage {
		public string Property { get; private set; }

		public AAction Action { get; private set; }

		public Change.Change Change { get; private set; }

		public IDictionary<string, object> ConnectParams { get; private set; }

		public bool? Close { get; private set; }

		public string SenderId { get; set; }

		private CommMessage() {
			Close = null;
		}

		public static CommMessage CreateConnectMsg(string modelName, IDictionary<string, object> connectParams) {
			return new CommMessage { Property = modelName, ConnectParams = connectParams };			
		}

		public static CommMessage CreateActionMsg(string property, AAction action) {
			return new CommMessage { Action = action, Property = property };
		}

		public static CommMessage CreateChangeMsg(string property, Change.Change change) {
			return new CommMessage { Change = change, Property = property };
		}

		public static CommMessage CreateCloseMsg(string modelName) {
			return new CommMessage { Close = true, Property = modelName };
		}
	}
}
