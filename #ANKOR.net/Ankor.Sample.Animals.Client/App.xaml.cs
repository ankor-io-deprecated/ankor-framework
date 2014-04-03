using System;
using System.Threading;
using System.Windows;
using Ankor.Core.AnkorSystem;

namespace Ankor.Sample.Animals.Client {
	/// <summary>
	/// Interaction logic for App.xaml
	/// </summary>
	public partial class App : Application {
		//private AnkorClientSystem ankorSystem;

		public new static App Current { get; private set; }
		public static AnkorClientSystem Ankor { get; private set; }
		//internal dynamic Root { get; private set; }

		public App() {
			Current = this;
		}
		
		protected override void OnStartup(StartupEventArgs e) {
			var builder = new AnkorClientSystem.SocketBuilder();
			builder.ServerAddress = new Uri("//localhost:8081");
			//builder.SystemName = "wpfTestClient";// + System.IO.Path.GetRandomFileName();
			Ankor = builder.Build();

			//Resources.Add("ankor", Ankor);
			//Resources.Add("root", Ankor.RefModel.Root);
			//Root = Ankor.RefModel.Root;

			//Ankor.RefModel.RootRef.Fire("init");

			Thread.Sleep(500);

			base.OnStartup(e);
		}
		
		protected override void OnExit(ExitEventArgs e) {

			Ankor.Dispose();
			base.OnExit(e);
		}
	}
}
