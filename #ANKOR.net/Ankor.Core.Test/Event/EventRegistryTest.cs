using System;
using System.Linq;
using System.Text;
using Ankor.Core.Action;
using Ankor.Core.Change;
using Ankor.Core.Event;
using Ankor.Core.Event.Dispatch;
using NUnit.Framework;
using FluentAssertions;

namespace Ankor.Core.Test.Event {

	[TestFixture]
	public class EventRegistryTest {
		private EventRegistry registry;
		private EventDispatcher dispatcher;

		[SetUp]
		public void SetUp() {
			registry = new EventRegistry();
			dispatcher = new EventDispatcher(registry);
		}

		[Test]
		public void RegisterOneListener() {
			var actionEventListener = new ActionEventListener(e => Console.WriteLine(""));
			registry.Add(actionEventListener);

			registry.First().Should().Be(actionEventListener);
			registry.Count().Should().Be(1);
		}

		[Test]
		public void RemoveListener() {
			var actionEventListener = new ActionEventListener(e => Console.WriteLine(""));
			registry.Add(actionEventListener);

			registry.First().Should().Be(actionEventListener);
			registry.Count().Should().Be(1);

			registry.Remove(actionEventListener);
			registry.Count().Should().Be(0);
			
		}

		/// <summary>
		/// A listener could be added with a key object to be removed later by key.
		/// Needed if no instance of the listener itself can be stored.
		/// </summary>
		[Test]
		public void RemoveListenerByKey() {
			object key = new object();
			registry.Add(key, new ActionEventListener(e => Console.WriteLine("")));

			registry.Count().Should().Be(1);

			registry.RemoveByKey(key);  // remove by key should work
			registry.Count().Should().Be(0);
		}

		[Test]
		public void IterateListeners() {

			int called = 0;

			registry.Add(new ActionEventListener(delegate(ActionEvent modelEvent) { called++; }));

			IModelEventListener<IModelEvent> l = new ActionEventListener(eventHandler: e => called++);

			registry.Add(l);

			foreach (var listener in registry) {
				new ActionEvent(new LocalSource(), null, new AAction("test")).ProcessBy(listener);
			}
			called.Should().Be(2);
		}
	
		[Test]
		public void FireEventTriggersListeners() {
			AAction fired = null;

			registry.Add(new ActionEventListener( e => fired = e.Action));

			dispatcher.Dispatch(new ActionEvent(new LocalSource(), null, new AAction("test")));

			fired.Should().NotBeNull();
			fired.Name.Should().Be("test");

			dispatcher.Dispatch(new ActionEvent(new LocalSource(), null, new AAction("test2")));

			fired.Name.Should().Be("test2");
		}

		[Test]
		public void FireEventNotTriggersWrongListener() {
			int called = 0;

			registry.Add(new ChangeEventListener(e => called++));

			dispatcher.Dispatch(new ActionEvent(new LocalSource(), null, new AAction("test")));

			called.Should().Be(0);

		}
	}
}
