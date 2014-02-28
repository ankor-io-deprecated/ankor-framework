using System.Collections.Generic;
using System.ComponentModel;
using Ankor.Core.Action;
using Ankor.Core.Event.Dispatch;

namespace Ankor.Core.Ref {
	public interface IInternalModel {
		
		//event ActionFiredHandler ActionForRemote;
//		event PropertyChangedEventHandler PropertyChanged;
//		event ActionReceivedEventHandler ActionFired;

		PathSyntax PathSyntax { get; }
		void UpdateFromExternal(string path, object newValue);
		void UpdateFromExternal(string path, Change.Change change);
		void InternalSetValue(object newValue, string path);
		object InternalGetValue(string path);		
		void PrintModelToDebug();
		dynamic Root { get; }

		// move to a ref/model context?
		EventDispatcher Dispatcher { get; }
		EventRegistry EventRegistry { get; }
		
		
		// dont want these:
		//void FireActionFromInternal(string path, AAction aAction);		
		//void FireActionFromRemote(string path, AAction aAction);
		//IEnumerator<DynaRef> GetRefEnumerator(string path);
		//event ModelChangedHandler ChangeForRemote;

		
	}
}