using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using Ankor.Core.Messaging.Comm;

namespace Ankor.Core.Messaging.Socket {
	public abstract class SocketMessageLoop : IDisposable {
		private const int MaxDebugLen = 1000;

		protected readonly MessageMapper<string> MessageMapper;
		protected IPEndPoint RemoteEndPoint;
		protected readonly IPEndPoint ListenEndPoint;
		protected readonly Uri LocalHost;

		private TcpListener listener;
		private Thread receiveThread;
		private bool run = true;

		public event MessageHandler OnMessage;

		protected SocketMessageLoop(Uri localHost, MessageMapper<string> messageMapper) {
			this.MessageMapper = messageMapper;
			this.LocalHost = localHost;
			ListenEndPoint = new IPEndPoint(Dns.GetHostAddresses(localHost.Host)[1], localHost.Port);
		}

		public void Send(CommMessage msg) {
			msg.SenderId = LocalHost.ToString();
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
			                var message = MessageMapper.Deserialize<CommMessage>(rawMsg);
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

		public virtual void Dispose() {
			run = false;
			if (listener != null) {
				listener.Stop();
			}
		}

	}
}
