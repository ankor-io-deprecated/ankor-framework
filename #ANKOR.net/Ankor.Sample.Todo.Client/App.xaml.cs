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
			var builder = InitWithWebSocket();

			//var builder = InitWithSocket();

			try {
				Ankor = builder.Build();

				App.Ankor.Connect();

				Thread.Sleep(400);

				base.OnStartup(e);
			} catch (Exception ex) {
				MessageBox.Show("error starting with server '" + builder.ServerAddress + "'\n" + ex.ToString(), "error");
				throw;
			}
		}

		private static AnkorClientSystemBuilder InitWithSocket() {
			var builder = new AnkorClientSystem.SocketBuilder() {
				ClientAddress = new Uri("//localhost:9091"),
				ServerAddress = new Uri("//localhost:8089"),
				ModelName = "root"
			};
			return builder;
		}

		private static AnkorClientSystemBuilder InitWithWebSocket() {
			var builder = new AnkorClientSystem.WebSocketBuilder() {
				ServerAddress = new Uri("ws://localhost:8080/websocket/ankor"),
				//builder.WebSocketUri = "wss://ankor-todo-sample.irian.at/websocket/ankor";
				ModelName = "root",
			};
			builder.AddConnectParam("todoListId", "collaborationTest");
			builder.ServerAddress = new Uri(ConfigurationManager.AppSettings["serverUrl"]);
			return builder;
		}

		protected override void OnExit(ExitEventArgs e) {
			Ankor.Dispose();
			base.OnExit(e);
		}

	}
}
