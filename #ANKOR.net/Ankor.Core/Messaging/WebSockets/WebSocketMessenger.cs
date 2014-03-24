using System;
using System.Threading;
using Ankor.Core.Messaging.Comm;
using SuperSocket.ClientEngine;
using WebSocket4Net;

namespace Ankor.Core.Messaging.WebSockets {

	public class WebSocketMessenger : IMessenger {
		private const int HeartbeatIntervallMs = 5000;

		private readonly WebSocket websocket;
		private volatile string clientId = null;
		protected readonly MessageMapper<string> MessageMapper;
		private Timer heartBeatExecutor;
		private readonly CountdownEvent connectedEvent = new CountdownEvent(1);
		private readonly string finalUri;

		public event MessageHandler OnMessage;

		public string ClientId {
			get { return this.clientId; }
		}

		public WebSocketMessenger(string webSocketUri, MessageMapper<string> messageMapper) {
			MessageMapper = messageMapper;

			this.clientId = Guid.NewGuid().ToString();
			finalUri = webSocketUri + "/" + this.clientId;
			Console.WriteLine("try to connect to websocket " + finalUri);
			websocket = new WebSocket(finalUri);
			websocket.AllowUnstrustedCertificate = true;
			websocket.Opened += OnOpened;
			websocket.Error += OnError;
			websocket.Closed += OnClosed;
			websocket.MessageReceived += OnReceived;
		}

		public void Start() {
			
			websocket.Open();

			if (!connectedEvent.Wait(5000)) {
				throw new ApplicationException("websocket open not successful to " + finalUri);
			}

			heartBeatExecutor = new Timer(SendHeartBeat, state: null, dueTime: HeartbeatIntervallMs, period: HeartbeatIntervallMs);
		}

		private void OnOpened(object sender, EventArgs e) {
			Console.WriteLine("OPENED ws connection");
			connectedEvent.Signal();
		}

		private void OnReceived(object sender, MessageReceivedEventArgs e) {
			string rawMsg = e.Message;
			//Console.WriteLine("RECEIVED: " + ToDebugString(rawMsg));
			var message = MessageMapper.Deserialize<CommMessage>(rawMsg);
			OnMessage(message);
		}

		private void OnClosed(object sender, EventArgs e) {
			Console.WriteLine("CLOSED");
		}

		private void OnError(object sender, ErrorEventArgs e) {
			Console.WriteLine("ERROR " + e.Exception);
		}

		private void SendHeartBeat(object state) {
			websocket.Send("");
		}

		public void Send(CommMessage message) {
			string rawMsg = MessageMapper.Serialize(message);
			//Console.WriteLine("SENDING: " + ToDebugString(rawMsg));			
			websocket.Send(rawMsg);
		}

		public void Dispose() {
			heartBeatExecutor.Dispose();
			connectedEvent.Dispose();
			websocket.Close();
		}

		private string ToDebugString(string rawMsg) {
			if (rawMsg.Length > 1000) {
				return rawMsg.Substring(0, 1000);
			}
			return rawMsg;
		}
	}
}