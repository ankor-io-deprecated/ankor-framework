using Ankor.Core.Event;
using Ankor.Core.Ref;

namespace Ankor.Core.Action {
	public class ActionEvent : ModelEvent {
		public AAction Action {get; private set; }
		public DynaRef Property { get; private set; }

		public ActionEvent(IEventSource source, DynaRef property, AAction aAction) : base(source) {
			Property = property;
			Action = aAction;
		}

		public override bool IsAppropriateListener(IModelEventListener<IModelEvent> listener) {
			return listener is ModelEventListener<ActionEvent>;
		}

		public override void ProcessBy(IModelEventListener<IModelEvent> listener) {
			((ModelEventListener<ActionEvent>)listener).Process(this);
		}
	}
}