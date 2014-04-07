using Ankor.Core.Event;
using Ankor.Core.Ref;

namespace Ankor.Core.Action {
	public class ActionEvent : ModelEvent {
		public AAction Action {get; private set; }
		public IRef Property { get; private set; }

		public ActionEvent(IEventSource source, IRef property, AAction aAction) : base(source) {
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