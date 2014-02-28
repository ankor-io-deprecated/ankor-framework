
namespace Ankor.Core.Messaging {
	
	/// <summary>
	/// Description of Message.
	/// </summary>
	public abstract class Message {
		
		public string SenderId {get;  set;}
		public string ModelId {get; private set;}
		public string MessageId {get; private set;}
		public string Property { get; private set; }
		
		protected Message(string senderId, string modelId, string messageId, string property) {
			this.SenderId = senderId;
			this.ModelId = modelId;
			this.MessageId = messageId;
			this.Property = property;
		}

		protected internal Message() { }

		public override string ToString() {
			return string.Format("SenderId: {0}, ModelId: {1}, MessageId: {2}, Property: {3}", SenderId, ModelId, MessageId, Property);
		}
	}
}
