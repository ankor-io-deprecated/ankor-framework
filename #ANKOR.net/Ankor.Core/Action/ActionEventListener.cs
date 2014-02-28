using Ankor.Core.Event;

namespace Ankor.Core.Action {
	public class ActionEventListener : ModelEventListener<ActionEvent> {

		public ActionEventListener(ModelEventHandler<ActionEvent> eventHandler) : base(eventHandler) { }
	}
}