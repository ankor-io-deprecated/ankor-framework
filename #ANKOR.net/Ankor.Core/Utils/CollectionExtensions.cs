using System.Collections.Generic;

namespace Ankor.Core.Utils {
	/// <summary>
	/// Description of CollectionExtensions.
	/// </summary>
	public static class CollectionExtensions {
		
		public static IDictionary<K,V> NullToEmpty<K, V>(this IDictionary<K,V> dict) {
			if (dict == null) {
				return new Dictionary<K,V>();				
			} else {
				return dict;
			}
			
		}
		
	}
}
