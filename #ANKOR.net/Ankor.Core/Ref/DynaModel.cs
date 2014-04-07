using Ankor.Core.Messaging.Json;

namespace Ankor.Core.Ref {

	public interface IRefModel {
		dynamic Root { get; }
		dynamic DRef { get; }

		IRef RootRef { get; }
		
	}

	public class DynaModel :IRefModel {
		private IInternalModel jModel;

		public dynamic Root { get; private set; }

		public dynamic DRef { get; private set; }

		public IRef RootRef { get; private set; }

		public DynaModel(IInternalModel jModel) {
			this.jModel = jModel;
			RootRef = DynaRef.CreateRef(jModel, "root");
			Root = RootRef;
			DRef = DynaRef.CreateRef(jModel, "");
		}

	}
}