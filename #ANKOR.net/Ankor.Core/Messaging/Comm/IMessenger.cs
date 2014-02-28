using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Ankor.Core.Messaging.Comm {

	public delegate void MessageHandler(Message msg);

	public interface IMessenger : IDisposable {

		event MessageHandler OnMessage;

		void Start();

		void Send(Message message);
	}
}

