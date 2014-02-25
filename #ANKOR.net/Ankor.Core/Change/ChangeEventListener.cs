using Ankor.Core.Event;

namespace Ankor.Core.Change {
	public class ChangeEventListener : ModelEventListener<ChangeEvent> {

		public ChangeEventListener(ModelEventHandler<ChangeEvent> eventHandler) : base(eventHandler) { }
	}
}