using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Ankor.Core.Ref {
	public class PathSyntax {

		public string MakeAbsolutePath(string basePath, string subPath) {
			if (subPath.StartsWith("[")) {
				return basePath + subPath;
			}
			return JoinAllNotNullOrEmpty(".", basePath, subPath);
		}

		private static string JoinAllNotNullOrEmpty(string separator, params string[] elems) {
			return string.Join(separator, elems.Where(e => !String.IsNullOrEmpty(e)));
		}

		public string MakeRelativePath(string basePath, string absolutePath) {
			if (absolutePath.Equals(basePath)) {
				return "";
			}
			if (!String.IsNullOrEmpty(basePath) && absolutePath.StartsWith(basePath)) {
				int len = basePath.Length;
				if (absolutePath[basePath.Length] != '[') {
					len += 1; // remove the . too
				}
				return absolutePath.Substring(len);
			}
			return absolutePath;
		}

		public string GetPropertyName(string path) {
			if (path.EndsWith("]")) {
				int startIndex = path.LastIndexOf('[') +1;
				int indexLen = path.Length - 1 - startIndex;
				return path.Substring(startIndex, indexLen);
			}
			int lastSeparator = path.LastIndexOf('.');
			if (lastSeparator < 0) {
				return path;
			}
			return path.Substring(lastSeparator + 1);
		}

		public string GetParentPath(string path) {
			if (path.EndsWith("]")) {
				int startIndexer = path.LastIndexOf('[');				
				return path.Substring(0, startIndexer);
			}
			int lastSeparatorIndex = path.LastIndexOf('.');
			if (lastSeparatorIndex < 0) {
				return "";
			}
			return path.Substring(0, lastSeparatorIndex);
		}

		public bool IsDescendant(string descendant, string ancestor) {
			if (descendant == null || ancestor == null) {
				return false;
			}
			if (ancestor != String.Empty) {
				return descendant.StartsWith(ancestor + ".") || descendant.StartsWith(ancestor + "[");
			}
			// empty ancestor is the parent of everything except itself
			return descendant != String.Empty;
		}

		public bool IsAncestor(string ancestor, string descendant) {
			return IsDescendant(descendant, ancestor);
		}

		public string AddArrayIndex(string path, int i) {
			return path + "[" + i + "]";
		}
		
	}
}
