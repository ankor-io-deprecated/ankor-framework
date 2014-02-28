using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Ankor.Core.Messaging.Json;
using Ankor.Core.Ref;
using Newtonsoft.Json.Linq;

namespace Ankor.Core.Test {
	public class TestUtils {
		internal static object ParseSnippet(IInternalModel model, string json) {
			if (model is RModel) {
				return JsonMessageMapper.ParseSnippet(json);
			} else {
				return JObject.Parse(json);
			}
		}
	}
}
