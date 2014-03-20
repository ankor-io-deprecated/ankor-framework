using System;
using System.Collections.Generic;
using System.Net;
using Ankor.Core.Action;
using Ankor.Core.Messaging.WebSockets;

namespace Ankor.Core.Messaging.Comm {
	public class ClientSocketMessageLoop : SocketMessageLoop, IMessenger {
		private readonly MessageFactory messageFactory;

		public ClientSocketMessageLoop(MessageFactory messageFactory, string serverHost, int serverPort, string localHost, int localPort,
			MessageMapper<string> messageMapper) : base(localHost, localPort, messageMapper) {			
			RemoteEndPoint = new IPEndPoint(Dns.GetHostAddresses(serverHost)[1], serverPort);
			this.messageFactory = messageFactory;
		}

		public override void Start() {
			base.Start();

			// TODO close is just send for testing now
			//var closeMsg = MessageFactory.CreateActionMessage("root", new AAction("$close"));
			//Send(closeMsg);

			var connectMsg = messageFactory.CreateGlobalActionMessage(
				new AAction(SocketConnectToken,
					new Dictionary<string, object>() {
						{"host", ListenEndPoint.Address.ToString()},
						{"port", ListenEndPoint.Port}
					}));

			//Send(connectMsg);
			throw new NotImplementedException("change sockets back again");


		}

		public new void Dispose() {
			//Send(messageFactory.CreateActionMessage("root", new AAction("$close")));
			base.Dispose();			
		}



	}
}