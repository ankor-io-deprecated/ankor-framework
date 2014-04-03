using Ankor.Core.Event;
using Ankor.Core.Ref;

namespace Ankor.Core.Change {
	public class ChangeEvent : ModelEvent, IModelEvent {
		public IRef Property { get; private set; }
		public Change Change { get; private set; }

		public ChangeEvent(IEventSource source, IRef property, Change change) : base(source) {
			Change = change;
			Property = property;
		}

		public override bool IsAppropriateListener(IModelEventListener<IModelEvent> listener) {
			return listener is ModelEventListener<ChangeEvent>;
		}

		public override void ProcessBy(IModelEventListener<IModelEvent> listener) {
			((ModelEventListener<ChangeEvent>)listener).Process(this);
		}
	}
}