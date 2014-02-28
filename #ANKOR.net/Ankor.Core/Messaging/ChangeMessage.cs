﻿
namespace Ankor.Core.Messaging {

	public class ChangeMessage : Message {

		public Change.Change Change { get; private set; }

		/// <summary>
		/// For de/serialization libs
		/// </summary>
		private ChangeMessage() {}

		internal ChangeMessage(string senderId, string modelId, string messageId, string actionProperty, Change.Change change)
			: base(senderId, modelId, messageId, actionProperty) {

			this.Change = change;
		}

		public override string ToString() {
			return string.Format("{0}, Change: {1}", base.ToString(), Change);
		}
	}
}
