using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Ankor.Core.Messaging.WebSockets;

namespace Ankor.Core.Messaging.Comm {

	public delegate void MessageHandler(CommMessage msg);

	public interface IMessenger : IDisposable {

		event MessageHandler OnMessage;

		event System.Action OnReconnect;

		void Start();

		void Send(CommMessage message);
	}
}

