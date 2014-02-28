
namespace Ankor.Core.Event {

	public interface IModelEvent {
		bool IsAppropriateListener(IModelEventListener<IModelEvent> listener);
		void ProcessBy(IModelEventListener<IModelEvent> listener);
	}

	public abstract class ModelEvent : IModelEvent {
		public IEventSource Source { get; private set; }

		protected ModelEvent(IEventSource source) {
			Source = source;
		}

		public abstract bool IsAppropriateListener(IModelEventListener<IModelEvent> listener);
		public abstract void ProcessBy(IModelEventListener<IModelEvent> listener);
	}

}