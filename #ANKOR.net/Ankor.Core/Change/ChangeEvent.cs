using Ankor.Core.Event;
using Ankor.Core.Ref;

namespace Ankor.Core.Change {
	public class ChangeEvent : ModelEvent, IModelEvent {
		public DynaRef Property { get; private set; }
		public Change Change { get; private set; }

		public ChangeEvent(IEventSource source, DynaRef property, Change change) : base(source) {
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