using System;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Windows.Threading;
using Ankor.Core.Action;
using Ankor.Core.Change;
using Ankor.Core.Event;
using Ankor.Core.Messaging;
using Ankor.Core.Messaging.Comm;
using Ankor.Core.Messaging.Json;
using Ankor.Core.Messaging.WebSockets;
using Ankor.Core.Ref;

namespace Ankor.Core.AnkorSystem  {

	public class AnkorClientSystem : IDisposable {

		public class SocketBuilder {
			public string ServerHostName { get; set; }
			public int ServerPort { get; set; }
			public string ClientHostName { get; set; }
			public int ClientPort { get; set; }

			private MessageFactory messageFactory;

			public SocketBuilder() {
				// default values
				ServerHostName = "localhost";
				ServerPort = 8081;
				ClientHostName = "localhost";
				ClientPort = 9090;				
			}

			public AnkorClientSystem Build() {
				string systemName = ClientHostName;
				messageFactory = new MessageFactory(systemName);

				IMessenger messenger = 
					new ClientSocketMessageLoop(messageFactory, ServerHostName, ServerPort, ClientHostName, ClientPort, new JsonMessageMapper());
				messenger.Start();

				return new AnkorClientSystem(messenger, messageFactory);
				
			}
			
		}

		public class WebSocketBuilder {
			public string WebSocketUri { get; set; }

			public string ModelName { get; set; }

			public IDictionary<string, string> ConnectionParams { get; private set; }

			public WebSocketBuilder() {
				ConnectionParams = new Dictionary<string, string>();
				WebSocketUri = "ws://localhost:8080/websocket/ankor";
			}

			public WebSocketBuilder AddConnectParam(string key, string param) {
				ConnectionParams[key] = param;
				return this;
			}

			public AnkorClientSystem StartAnkor() {
				WebSocketMessenger messenger = new WebSocketMessenger(WebSocketUri, new JsonMessageMapper());
				messenger.Start();

				var systemName = messenger.ClientId;

				var factory = new MessageFactory(systemName);
				factory.ModelContextId = ModelName;

				var ankor =  new AnkorClientSystem(messenger, factory);
				//ankor.Dispatcher = Dispatcher;
				return ankor;
			}
		}

		private readonly IMessenger messenger;
		private readonly MessageFactory messageFactory;
		private readonly IInternalModel internalModel;
		private readonly DynaModel dynaModel;
		public Dispatcher Dispatcher { get; set; }

		public IRefModel RefModel {
			get { return this.dynaModel; }
		}

		private AnkorClientSystem(IMessenger messenger, MessageFactory factory) {
			//SystemName = "unknown Client";
			//MessageMapper = new JsonMessageMapper();
			this.messenger = messenger;
			this.messageFactory = factory;

			internalModel = new RModel();
			dynaModel = new DynaModel(internalModel);

			WireUp();

		}

		private void WireUp() {

			messenger.OnMessage += OnServerMessage;

			internalModel.EventRegistry.Add(new ActionEventListener(SendActionToRemote));
			internalModel.EventRegistry.Add(new ChangeEventListener(SendChangeToRemote));

		}

		private void SendChangeToRemote(ChangeEvent modelevent) {
			if (!(modelevent.Source is RemoteSource)) {
				//messageBus.Send(messageFactory.CreateChangeMessage(modelevent.Property.Path, modelevent.Change));
				messenger.Send(WebSocketMessage.CreateChangeMsg(modelevent.Property.Path, modelevent.Change));
			}
		}

		private void SendActionToRemote(ActionEvent modelEvent) {
			// only if remote
			if (!(modelEvent.Source is RemoteSource)) {
				messenger.Send(WebSocketMessage.CreateActionMsg(modelEvent.Property.Path, modelEvent.Action));
			}
		}

		private void OnServerMessage(WebSocketMessage msg) {
			if (msg.Change != null) {
			//if (msg is ChangeMessage) {
				if (Dispatcher != null) {
					//Console.WriteLine("dispatch change msg");
					Dispatcher.Invoke(new Action<WebSocketMessage>(ProcessChangeMessage), msg);
				} else {
					ProcessChangeMessage(msg);
				}
			} else {
				//throw new ApplicationException("unsupported message at this place: " + msg);
				Console.WriteLine("ERRROR ignoring unsupported message at this place: " + msg);
			}
		}

		private void ProcessChangeMessage(WebSocketMessage msg) {
			//Console.WriteLine("process change msg");
			internalModel.UpdateFromExternal(msg.Property, msg.Change);
		}

		public void PrintDebug() {
			internalModel.PrintModelToDebug();
		}

		public void Dispose() {
			try {
				messenger.Send(WebSocketMessage.CreateCloseMsg("root"));
				messenger.Dispose();
			} catch (Exception e) {
				Console.WriteLine(e);
			}
		}

		public void Connect() {
			messenger.Send(WebSocketMessage.CreateConnectMsg("root", 
				new Dictionary<string, object> {{"todoListId", "collaborationTest"}}));
		}
	}
}
