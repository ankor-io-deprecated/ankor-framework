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
using Ankor.Core.Messaging.Socket;
using Ankor.Core.Messaging.WebSockets;
using Ankor.Core.Ref;

namespace Ankor.Core.AnkorSystem {

	public abstract class AnkorClientSystemBuilder {
		public Uri ServerAddress { get; set; }
		public string ModelName { get; set; }

		public IDictionary<string, object> ConnectionParams { get; protected set; }

		public AnkorClientSystemBuilder AddConnectParam(string key, string param) {
			ConnectionParams[key] = param;
			return this;
		}

		public abstract AnkorClientSystem StartAnkor();

		protected internal AnkorClientSystemBuilder() {
			ConnectionParams = new Dictionary<string, object>();
		}
	}

	public class AnkorClientSystem : IDisposable {

		public class SocketBuilder : AnkorClientSystemBuilder {

			public Uri ClientAddress { get; set; }

			public SocketBuilder() {
				// default values
				ServerAddress = new Uri("//localhost:8080");
				ClientAddress = new Uri("//localhost:9090");
			}

			public override AnkorClientSystem StartAnkor() {

				IMessenger messenger = new ClientSocketMessageLoop(ServerAddress, ClientAddress, new JsonMessageMapper());
				messenger.Start();

				return new AnkorClientSystem(messenger, this);

			}

		}

		public class WebSocketBuilder : AnkorClientSystemBuilder {

			public WebSocketBuilder() {
				ServerAddress = new Uri("ws://localhost:8080/websocket/ankor");
			}

			public override AnkorClientSystem StartAnkor() {
				WebSocketMessenger messenger = new WebSocketMessenger(ServerAddress.ToString(), new JsonMessageMapper());
				messenger.Start();

				return new AnkorClientSystem(messenger, this);
			}
		}


		private readonly IMessenger messenger;
		private readonly IInternalModel internalModel;
		private readonly DynaModel dynaModel;
		public Dispatcher Dispatcher { get; set; }

		private readonly AnkorClientSystemBuilder builder;

		public IRefModel RefModel {
			get { return this.dynaModel; }
		}

		internal AnkorClientSystem(IMessenger messenger, AnkorClientSystemBuilder builder) {
			this.messenger = messenger;
			this.builder = builder;

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
				messenger.Send(CommMessage.CreateChangeMsg(modelevent.Property.Path, modelevent.Change));
			}
		}

		private void SendActionToRemote(ActionEvent modelEvent) {
			// only if remote
			if (!(modelEvent.Source is RemoteSource)) {
				messenger.Send(CommMessage.CreateActionMsg(modelEvent.Property.Path, modelEvent.Action));
			}
		}

		private void OnServerMessage(CommMessage msg) {
			if (msg.Change != null) {
				if (Dispatcher != null) {
					Dispatcher.Invoke(new Action<CommMessage>(ProcessChangeMessage), msg);
				} else {
					ProcessChangeMessage(msg);
				}
			} else {
				//throw new ApplicationException("unsupported message at this place: " + msg);
				Console.WriteLine("ERRROR ignoring unsupported message at this place: " + msg);
			}
		}

		private void ProcessChangeMessage(CommMessage msg) {
			internalModel.UpdateFromExternal(msg.Property, msg.Change);
		}

		public void PrintDebug() {
			internalModel.PrintModelToDebug();
		}

		public void Dispose() {
			try {
				messenger.Send(CommMessage.CreateCloseMsg("root"));
				messenger.Dispose();
			} catch (Exception e) {
				Console.WriteLine(e);
			}
		}

		public void Connect() {
			messenger.Send(CommMessage.CreateConnectMsg(builder.ModelName, builder.ConnectionParams));
		}
	}
}
