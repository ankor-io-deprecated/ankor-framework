using System;
using System.Net;
using Ankor.Core.Messaging.Comm;

namespace Ankor.Core.Messaging.Socket {
	public class ClientSocketMessageLoop : SocketMessageLoop, IMessenger {

		public ClientSocketMessageLoop(Uri serverAddress, Uri clientAddress, MessageMapper<string> messageMapper) 
			: base(clientAddress, messageMapper) {
			RemoteEndPoint = new IPEndPoint(Dns.GetHostAddresses(serverAddress.Host)[1], serverAddress.Port);
		}

//		public override void Start() {
//			base.Start();
//
//			// TODO close is just send for testing now
//			//var closeMsg = MessageFactory.CreateActionMessage("root", new AAction("$close"));
//			//Send(closeMsg);
//
//			//var connectMsg = messageFactory.CreateGlobalActionMessage(
//			//  new AAction(SocketConnectToken,
//			//    new Dictionary<string, object>() {
//			//      {"host", ListenEndPoint.Address.ToString()},
//			//      {"port", ListenEndPoint.Port}
//			//    }));
//
//			//CommMessage.CreateConnectMsg()
//			//Send(connectMsg);
//			//throw new NotImplementedException("change sockets back again");
//
//
//		}


	}
}