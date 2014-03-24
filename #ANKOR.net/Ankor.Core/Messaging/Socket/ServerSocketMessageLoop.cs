using System;
using System.Net;
using Ankor.Core.Messaging.Comm;

namespace Ankor.Core.Messaging.Socket {
	public class ServerSocketMessageLoop : SocketMessageLoop, IDisposable {

		public ServerSocketMessageLoop(string localHost, int listenPort, MessageMapper<string> messageMapper)
			: base(new Uri("//" + localHost + ":" + listenPort), messageMapper) {

			//OnMessage += HandleConnectMessage;
			throw new NotImplementedException();
		}

//		private void HandleConnectMessage(Message msg) {
//			var actionMsg = msg as ActionMessage;
//			if (actionMsg != null) {
//				if (actionMsg.Action.Name.Equals(SocketConnectToken)) {
//					Console.WriteLine("handleSocketConnect " + actionMsg.Action.Params);
//					long port = (long) actionMsg.Action.Params["port"];
//					string host = (string) actionMsg.Action.Params["host"];
//
//					RemoteEndPoint = new IPEndPoint(IPAddress.Parse(host),(int) port);
//
//				}
//			}
//		}
	}
}
