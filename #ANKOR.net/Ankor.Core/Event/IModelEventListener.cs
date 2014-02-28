
namespace Ankor.Core.Event {

	public delegate void ModelEventHandler<in TEventType>(TEventType modelEvent) where TEventType : IModelEvent;

	public interface IModelEventListener<out T> where T : IModelEvent {

	}

	public abstract class ModelEventListener<T> : IModelEventListener<T> where T : IModelEvent {

		public ModelEventHandler<T> EventHandler { get; private set; }

		protected ModelEventListener(ModelEventHandler<T> eventHandler) {
			this.EventHandler = eventHandler;
		}

		public void Process(T modelEvent) {
			if (this.EventHandler != null) {
				EventHandler(modelEvent);
			}
		}
	}
}