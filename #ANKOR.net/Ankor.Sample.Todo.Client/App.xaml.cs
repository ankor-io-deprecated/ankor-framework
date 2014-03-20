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

			var builder = new AnkorClientSystem.WebSocketBuilder() {
				WebSocketUri = "ws://localhost:8080/websocket/ankor",
				//builder.WebSocketUri = "wss://ankor-todo-sample.irian.at/websocket/ankor";
				ModelName = "root",
			};
			builder.AddConnectParam("todoListId", "collaborationTest");

			builder.WebSocketUri = ConfigurationManager.AppSettings["serverUrl"];

			try {
				Ankor = builder.StartAnkor();

				base.OnStartup(e);
			} catch (Exception ex) {
				MessageBox.Show("error starting with server '" + builder.WebSocketUri + "'\n" + ex.ToString(), "error");
				throw;
			}
		}


		protected override void OnExit(ExitEventArgs e) {

			Ankor.Dispose();
			base.OnExit(e);
		}

	}
}
