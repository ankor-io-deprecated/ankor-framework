using System;
using System.Threading;
using Ankor.Core.Messaging.Comm;
using SuperSocket.ClientEngine;
using WebSocket4Net;

namespace Ankor.Core.Messaging.WebSockets {

	public class WebSocketMessenger : IMessenger {
		private const int HeartbeatIntervallMs = 5000;

		private volatile WebSocket websocket;
		private volatile string clientId = null;
		protected readonly MessageMapper<string> MessageMapper;
		private Timer heartBeatExecutor;
		private CountdownEvent connectedEvent;
		private readonly string finalUri;

		public event MessageHandler OnMessage;
		public event System.Action OnReconnect;

		public string ClientId {
			get { return this.clientId; }
		}

		public WebSocketMessenger(string webSocketUri, MessageMapper<string> messageMapper) {
			MessageMapper = messageMapper;

			this.clientId = Guid.NewGuid().ToString();
			finalUri = webSocketUri + "/" + this.clientId;
			NewWebSocket();
		}

		private void NewWebSocket() {
			if (websocket != null) {
				websocket.Opened -= OnOpened;
				websocket.Error -= OnError;
				websocket.Closed -= OnClosed;
				websocket.MessageReceived -= OnReceived;
			}
			Console.WriteLine("try to connect to websocket " + finalUri);
			websocket = new WebSocket(finalUri);
			websocket.AllowUnstrustedCertificate = true;
			websocket.Opened += OnOpened;
			websocket.Error += OnError;
			websocket.Closed += OnClosed;
			websocket.MessageReceived += OnReceived;
		}

		public void Start() {
			InternalConnect();
		}

		private void InternalConnect() {
			Console.WriteLine("try to open websocket");
			websocket.Open();
			connectedEvent = new CountdownEvent(1);
			if (!connectedEvent.Wait(5000)) {
				throw new ApplicationException("websocket open not successful to " + finalUri);
			}

			if (heartBeatExecutor != null) {
				heartBeatExecutor.Dispose();
			}
			heartBeatExecutor = new Timer(SendHeartBeat, state: null, dueTime: HeartbeatIntervallMs, period: HeartbeatIntervallMs);
		}

		private void OnOpened(object sender, EventArgs e) {
			Console.WriteLine("OPENED ws connection");
			connectedEvent.Signal();
		}

		private void OnReceived(object sender, MessageReceivedEventArgs e) {
			string rawMsg = e.Message;
			Console.WriteLine("RECEIVED: " + ToDebugString(rawMsg));
			var message = MessageMapper.Deserialize<CommMessage>(rawMsg);
			OnMessage(message);
		}

		private void OnClosed(object sender, EventArgs e) {
			Console.WriteLine("CLOSED");
			heartBeatExecutor.Dispose();
			heartBeatExecutor = null;
			ReConnect();
		}

		private void OnError(object sender, ErrorEventArgs e) {
			Console.WriteLine("ERROR " + e.Exception);
		}

		private void SendHeartBeat(object state) {
			websocket.Send("");
		}

		public void Send(CommMessage message) {
			string rawMsg = MessageMapper.Serialize(message);
			Console.WriteLine("SENDING: " + ToDebugString(rawMsg));			
			websocket.Send(rawMsg);
		}

		public void ReConnect() {
			NewWebSocket();
			new Timer(ExecuteReconnect, null, 1, 1000);
		}

		private void ExecuteReconnect(object state) {
			InternalConnect();
			if (OnReconnect != null) {
				OnReconnect();
			}
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