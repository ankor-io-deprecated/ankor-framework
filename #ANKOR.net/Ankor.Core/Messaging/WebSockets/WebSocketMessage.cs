using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Ankor.Core.Action;

namespace Ankor.Core.Messaging.WebSockets {

	public class WebSocketMessage {
		
		public string Property { get; private set; }

		public AAction Action { get; private set; }

		public Change.Change Change { get; private set; }

		public IDictionary<string, object> ConnectParams { get; private set; }

		public bool? Close { get; private set; }

		private WebSocketMessage() {
			Close = null;
		}

		public static WebSocketMessage CreateConnectMsg(string modelName, IDictionary<string, object> connectParams) {
			return new WebSocketMessage { Property = modelName, ConnectParams = connectParams };			
		}

		public static WebSocketMessage CreateActionMsg(string property, AAction action) {
			return new WebSocketMessage {Action = action, Property = property };
		}

		public static WebSocketMessage CreateChangeMsg(string property, Change.Change change) {
			return new WebSocketMessage { Change = change, Property = property };
		}

		public static WebSocketMessage CreateCloseMsg(string modelName) {
			return new WebSocketMessage { Close = true, Property = modelName };
		}

	}
}
