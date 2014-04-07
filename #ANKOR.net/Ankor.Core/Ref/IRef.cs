using System;
using System.Collections.Generic;
using System.ComponentModel;
using Ankor.Core.Action;

namespace Ankor.Core.Ref {

	public interface IRef : INotifyPropertyChanged, IDisposable, IEquatable<IRef>, IEnumerable<IRef>, IEnumerable<KeyValuePair<string, IRef>> {
		object Value { get; set; }
		string Path { get; }
		string PropertyName { get; }
		dynamic Dynamic { get; }
		IRef this[string subpath] { get; }

		IRef AppendPath(string subPath);
		IRef AppendIndex(int index);

		void Fire(AAction aAction);
		void Fire(string actionName);

		event ActionReceivedEventHandler ActionReceived;
	}
}