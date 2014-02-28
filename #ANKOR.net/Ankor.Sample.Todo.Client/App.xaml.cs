using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Threading;
using System.Windows;
using Ankor.Core.AnkorSystem;
using SuperSocket.ClientEngine;
using WebSocket4Net;

namespace Ankor.Sample.Todo.Client {

	public partial class App : Application {

		public static AnkorClientSystem Ankor { get; private set; }

		protected override void OnStartup(StartupEventArgs e) {
			var builder = new AnkorClientSystem.WebSocketBuilder();
			//builder.WebSocketUri = "ws://localhost:8080/websocket/ankor";
			builder.WebSocketUri = "wss://ankor-todo-sample.irian.at/websocket/ankor";
			builder.ModelContextId = "collabTest";
			//builder.SystemName = "wpfTestClient";// + System.IO.Path.GetRandomFileName();
			//builder.Dispatcher = this.Dispatcher;
			Ankor = builder.Build();

			base.OnStartup(e);			
		}

		protected override void OnExit(ExitEventArgs e) {

			Ankor.Dispose();
			base.OnExit(e);
		}

	}
}
