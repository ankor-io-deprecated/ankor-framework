using System;
using System.Globalization;
using Ankor.Core.Action;

namespace Ankor.Core.Messaging {
	/// <summary>
	/// Description of MessageFactory.
	/// </summary>
	public class MessageFactory {
		public string SystemName { get; private set; }
		public NextId CreateNextId { get; set; }

		// TODO add modelcontext

		public string ModelContextId { get; set; }

		private long currentId = 1;
		public delegate string NextId();
		
		public MessageFactory(string systemName) {
			this.SystemName = systemName;
			CreateNextId = () => SystemName + "#" + currentId++;
			ModelContextId = "1";
		}

		public Message CreateActionMessage(string actionPropertyPath, AAction action) {
			return new ActionMessage(SystemName, ModelContextId, CreateNextId(), actionPropertyPath, action);
		}

		public Message CreateChangeMessage(string changedPropertyPath, Change.Change change) {
			return new ChangeMessage(SystemName, ModelContextId, CreateNextId(), changedPropertyPath, change);
		}

		public Message CreateGlobalActionMessage(AAction action) {
			return new ActionMessage(SystemName, null, CreateNextId(), null, action);
		}


	}
}
