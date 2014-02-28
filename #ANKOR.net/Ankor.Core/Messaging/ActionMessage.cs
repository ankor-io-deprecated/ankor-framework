
using Ankor.Core.Action;

namespace Ankor.Core.Messaging {
	/// <summary>
	/// Description of ActionMessage.
	/// </summary>
	///
	public class ActionMessage : Message {
				
		// this is not a generic type to enable different json messages, maybe go for a type property and a single msg class
		public AAction Action { get; private set; }

		/// <summary>
		/// For de/serialization libs
		/// </summary>
		private ActionMessage() { }
		
		internal ActionMessage(string senderId, string modelId, string messageId, string actionProperty, AAction action)
			: base(senderId, modelId, messageId, actionProperty) {
			
			this.Action = action;
			
		}

		public override string ToString() {
			return string.Format("{0}, Action: {1}", base.ToString(), Action);
		}
	}
}
