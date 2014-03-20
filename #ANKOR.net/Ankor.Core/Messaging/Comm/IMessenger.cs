using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Ankor.Core.Messaging.WebSockets;

namespace Ankor.Core.Messaging.Comm {

	public delegate void MessageHandler(WebSocketMessage msg);

	public interface IMessenger : IDisposable {

		event MessageHandler OnMessage;

		void Start();

		void Send(WebSocketMessage message);
	}
}

