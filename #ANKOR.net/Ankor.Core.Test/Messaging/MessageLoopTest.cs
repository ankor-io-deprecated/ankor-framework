//using System;
//using System.Collections.Generic;
//using System.Linq;
//using System.Linq.Expressions;
//using System.Text;
//using System.Threading;
//using Ankor.Core.Action;
//using Ankor.Core.Messaging;
//using Ankor.Core.Messaging.Comm;
//using Ankor.Core.Messaging.Json;
//using NUnit.Framework;
//using FluentAssertions;
//
//namespace Ankor.Core.Test.Messaging {
//
//	[System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1001:TypesThatOwnDisposableFieldsShouldBeDisposable"), TestFixture]
//	public class MessageLoopTest {
//		private const int ListenPort = 18080;
//		private const int LocalPort = 19090;
//
//		private readonly MessageFactory messageFactory = new MessageFactory("testSystem") {
//			CreateNextId = () => "testClient#1",
//			ModelContextId = "mcid"
//		};
//
//		private volatile Message lastMessage = null;
//		
//		private ServerSocketMessageLoop serverLoop;
//		private ClientSocketMessageLoop clientLoop;
//		
//		[SetUp]
//		public void SetUp() {
//			
//		}
//		
//		[TearDown]
//		public void TearDown() {
//			if (clientLoop != null) {
//				clientLoop.Dispose();
//			}
//			if (serverLoop != null) {
//				serverLoop.Dispose();
//			}
//		}
//
//		[Test]
//		public void TestOneWay() {
//			serverLoop = new ServerSocketMessageLoop("localhost", ListenPort, new JsonMessageMapper());
//			serverLoop.OnMessage += msg => { this.lastMessage = msg; };
//			serverLoop.Start();			
//
//			clientLoop = new ClientSocketMessageLoop(messageFactory, "localhost", ListenPort, "localhost", LocalPort, new JsonMessageMapper());
//			var message = messageFactory.CreateActionMessage("path", new AAction("test"));
//			clientLoop.Send(message);
//
//			Thread.Sleep(500);
//
//			message.MessageId.Should().Be(lastMessage.MessageId);
//			message.ModelId.Should().Be(lastMessage.ModelId);
//			
//		}
//
//		[Test]
//		public void TestTwoWays() {
//			serverLoop = new ServerSocketMessageLoop("localhost", ListenPort, new JsonMessageMapper());
//			serverLoop.OnMessage += msg => { this.lastMessage = msg; };
//			serverLoop.Start();
//
//			clientLoop = new ClientSocketMessageLoop(messageFactory, "localhost", ListenPort, "localhost", LocalPort, new JsonMessageMapper());
//			clientLoop.Start();
//			clientLoop.OnMessage += msg => { this.lastMessage = msg; };
//
//			Thread.Sleep(500);
//			
//			var message = messageFactory.CreateActionMessage("path", new AAction("fromClient"));
//			clientLoop.Send(message);
//
//			Thread.Sleep(500);			
//			message.MessageId.Should().Be(lastMessage.MessageId);
//			message.ModelId.Should().Be(lastMessage.ModelId);
//			lastMessage.As<ActionMessage>().Action.Name.Should().Be("fromClient");
//
//			message = messageFactory.CreateActionMessage("path2", new AAction("fromServer"));
//			serverLoop.Send(message);
//
//			Thread.Sleep(500);
//
//			message.ModelId.Should().Be(lastMessage.ModelId);
//			lastMessage.As<ActionMessage>().Action.Name.Should().Be("fromServer");
//
//		}
//
//		[Test][Ignore] // manual only
//		public void TestTalkToServer() {
//			clientLoop = new ClientSocketMessageLoop(messageFactory, "localhost", ListenPort, "localhost", LocalPort, new JsonMessageMapper());
//			clientLoop.Start();
//			clientLoop.OnMessage += msg => Console.WriteLine("Received: " + msg.ToString());
//
//			Thread.Sleep(500);
//
//			var message = messageFactory.CreateActionMessage("path", new AAction("fromClient"));
//			clientLoop.Send(message);
//
//			Thread.Sleep(500);
//			
//			//clientLoop.Dispose();
//		}
//
//
//
//	}
//}
