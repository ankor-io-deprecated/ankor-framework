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
		private readonly CountdownEvent initMessageEvent = new CountdownEvent(1);
		private readonly string finalUri;

		public event MessageHandler OnMessage;

		public string ClientId {
			get { return this.clientId; }
		}

		public WebSocketMessenger(string webSocketUri, MessageMapper<string> messageMapper) {
			MessageMapper = messageMapper;

			this.clientId = System.Guid.NewGuid().ToString();
			finalUri = webSocketUri + "/" + this.clientId;
			Console.WriteLine("try to connect to websocket " + finalUri);
			websocket = new WebSocket(finalUri);
			websocket.AllowUnstrustedCertificate = true;
			websocket.Opened += new EventHandler(OnOpened);
			websocket.Error += new EventHandler<ErrorEventArgs>(OnError);
			websocket.Closed += new EventHandler(OnClosed);
			websocket.MessageReceived += new EventHandler<MessageReceivedEventArgs>(OnReceived);
			
		}

		public void Start() {
			
			websocket.Open();

			if (!initMessageEvent.Wait(5000)) {
				throw new ApplicationException("websocket open not successful to " + finalUri);
			}

			heartBeatExecutor = new Timer(SendHeartBeat, state: null, dueTime: HeartbeatIntervallMs, period: HeartbeatIntervallMs);
		}

		private void OnOpened(object sender, EventArgs e) {
			Console.WriteLine("OPENED ws connection");
			initMessageEvent.Signal();
		}

		private void OnReceived(object sender, MessageReceivedEventArgs e) {
			string rawMsg = e.Message;
			//Console.WriteLine("RECEIVED: " + ToDebugString(rawMsg));
//			if (clientId == null) {
//				clientId = rawMsg;
//				initMessageEvent.Signal();
//			} else {
				var message = MessageMapper.Deserialize<WebSocketMessage>(rawMsg);
				OnMessage(message);
			//}
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

		public void Send(WebSocketMessage message) {
			string rawMsg = MessageMapper.Serialize(message);
			//Console.WriteLine("SENDING: " + ToDebugString(rawMsg));			
			websocket.Send(rawMsg);
		}

		public void Dispose() {
			heartBeatExecutor.Dispose();
			initMessageEvent.Dispose();
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