using System;
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

			public string ModelContextId { get; set; }

			public Dispatcher Dispatcher { get; set; }

// TODO move to a common builder

			public WebSocketBuilder() {
				WebSocketUri = "ws://localhost:8080/websocket/ankor";
			}

			public AnkorClientSystem Build() {
				WebSocketMessenger messenger = new WebSocketMessenger(WebSocketUri, new JsonMessageMapper());
				messenger.Start();

				var systemName = messenger.ClientId;

				var factory = new MessageFactory(systemName);
				factory.ModelContextId = ModelContextId;

				var ankor =  new AnkorClientSystem(messenger, factory);
				ankor.Dispatcher = Dispatcher;
				return ankor;
			}
		}

		private readonly IMessenger messageBus;
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
			this.messageBus = messenger;
			this.messageFactory = factory;

			internalModel = new RModel();
			dynaModel = new DynaModel(internalModel);

			Connect();

		}

		private void Connect() {

			messageBus.OnMessage += OnServerMessage;

			internalModel.EventRegistry.Add(new ActionEventListener(SendActionToRemote));
			internalModel.EventRegistry.Add(new ChangeEventListener(SendChangeToRemote));

		}

		private void SendChangeToRemote(ChangeEvent modelevent) {
			if (!(modelevent.Source is RemoteSource)) {
				messageBus.Send(messageFactory.CreateChangeMessage(modelevent.Property.Path, modelevent.Change));
			}
		}

		private void SendActionToRemote(ActionEvent modelEvent) {
			// only if remote
			if (!(modelEvent.Source is RemoteSource)) {
				messageBus.Send(messageFactory.CreateActionMessage(modelEvent.Property.Path, modelEvent.Action));
			}
		}

		private void OnServerMessage(Message msg) {
			if (msg is ChangeMessage) {
				if (Dispatcher != null) {
					//Console.WriteLine("dispatch change msg");
					Dispatcher.Invoke(new Action<ChangeMessage>(ProcessChangeMessage), msg);
				} else {
					ProcessChangeMessage((ChangeMessage) msg);
				}
			} else {
				//throw new ApplicationException("unsupported message at this place: " + msg);
				Console.WriteLine("ERRROR ignoring unsupported message at this place: " + msg);
			}
		}

		private void ProcessChangeMessage(ChangeMessage msg) {
			//Console.WriteLine("process change msg");
			internalModel.UpdateFromExternal(msg.Property, msg.Change);
		}

		public void PrintDebug() {
			internalModel.PrintModelToDebug();
		}

		public void Dispose() {
			try {
				messageBus.Dispose();
			} catch (Exception e) {
				Console.WriteLine(e);
			}
		}
	}
}
