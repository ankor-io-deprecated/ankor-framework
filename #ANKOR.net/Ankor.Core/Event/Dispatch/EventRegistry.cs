using System.Collections;
using System.Collections.Generic;
using System.Collections.Immutable;
using Ankor.Core.Action;

namespace Ankor.Core.Event.Dispatch {
	public class EventRegistry : IEnumerable<IModelEventListener<IModelEvent>> {

		private volatile ImmutableList<IModelEventListener<IModelEvent>> listeners = ImmutableList<IModelEventListener<IModelEvent>>.Empty;
		private readonly IDictionary<object, IModelEventListener<IModelEvent>> listenerKeyMap = new Dictionary<object, IModelEventListener<IModelEvent>>(); 

		public void Add(IModelEventListener<IModelEvent> eventListener) {
			listeners = listeners.Add(eventListener);
		}

		public void Add(object listenerKey, IModelEventListener<IModelEvent> eventListener) {
			Add(eventListener);
			listenerKeyMap[listenerKey] = eventListener;
		}

		public IEnumerator<IModelEventListener<IModelEvent>> GetEnumerator() {
			return listeners.GetEnumerator();
		}

		IEnumerator IEnumerable.GetEnumerator() {
			return GetEnumerator();
		}

		public void Remove(IModelEventListener<IModelEvent> eventListener) {
			listeners = listeners.Remove(eventListener);
		}

		public void RemoveByKey(object listenerKey) {
			IModelEventListener<IModelEvent> eventListener = null;
			if (listenerKeyMap.TryGetValue(listenerKey, out eventListener)) {
				Remove(eventListener);
				listenerKeyMap.Remove(listenerKey);
			}
		}
	}
}