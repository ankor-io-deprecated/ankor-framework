using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using Ankor.Core.Messaging.WebSockets;

namespace Ankor.Core.Messaging.Comm {
	public abstract class SocketMessageLoop : IDisposable {
		protected const string SocketConnectToken = "$socketConnect";
		private const int MaxDebugLen = 1000;


		protected readonly MessageMapper<string> MessageMapper;
		protected IPEndPoint RemoteEndPoint;

		protected readonly IPEndPoint ListenEndPoint;
		private TcpListener listener;
		private Thread receiveThread;		

		public event MessageHandler OnMessage;

		protected SocketMessageLoop(string localHost, int listenPort, MessageMapper<string> messageMapper) {
			this.MessageMapper = messageMapper;
			ListenEndPoint = new IPEndPoint(Dns.GetHostAddresses(localHost)[1], listenPort);
		}


		public void Send(WebSocketMessage msg) {
			using (var client = new TcpClient()) {
				client.Connect(RemoteEndPoint);
				using (var writer = new StreamWriter(client.GetStream(), new UTF8Encoding(false))) {
					string rawMsg = MessageMapper.Serialize(msg);
					Console.WriteLine("SENDING: " + rawMsg);
					writer.WriteLine(rawMsg);
				}
			}
		}

		public virtual void Start() {
			listener = new TcpListener(ListenEndPoint);
			listener.Start();
			receiveThread = new Thread(Receive);
			receiveThread.Start();

		}

		private void Receive() {
			Console.WriteLine("Start receiving @" + ListenEndPoint.Address + ":" + ListenEndPoint.Port);
			while (run) {

			    try {
			        using (var client = listener.AcceptTcpClient()) {
			            using (var reader = new StreamReader(client.GetStream(), new UTF8Encoding(false))) {
			                string rawMsg = reader.ReadToEnd();
			                Console.WriteLine("RECEIVED: " + ToDebugString(rawMsg));
			                var message = MessageMapper.Deserialize<WebSocketMessage>(rawMsg);
			                OnMessage(message);
			            }
			        }
			    } catch (SocketException e) {
			        Console.WriteLine(e.ToString());
			        return;
			    }
			}
		}

		private string ToDebugString(string rawMsg) {
			if (rawMsg.Length > MaxDebugLen) {
				return rawMsg.Substring(0, MaxDebugLen);
			}
			return rawMsg;
		}

		private bool run = true;

		public virtual void Dispose() {
			run = false;
			if (listener != null) {
				listener.Stop();
			}
		}

	}
}
