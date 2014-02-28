namespace Ankor.Core.Event.Dispatch {

	public class EventDispatcher {
		private readonly EventRegistry registry;

		public EventDispatcher(EventRegistry registry) {
			this.registry = registry;
		}

		public void Dispatch<T>(T modelEvent) where T : IModelEvent {
			foreach (var listener in registry) {
				if (modelEvent.IsAppropriateListener(listener)) {
					modelEvent.ProcessBy(listener);
				}
			}
		}
	}
}