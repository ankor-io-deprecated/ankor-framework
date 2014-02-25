using System.IO;

namespace Ankor.Core.Utils {


	/// <summary>
	/// Extension methods for <see cref="System.IO.FileInfo"/>
	/// </summary>
	public static class FileInfoExtensions {



		/// <summary>
		/// Compares two files against one another
		/// </summary>
		/// <param name="File1">First file</param>
		/// <param name="File2">Second file</param>
		/// <returns>True if the content is the same, false otherwise</returns>
		public static bool CompareTo(this FileInfo File1, FileInfo File2) {
			if (File1.Length != File2.Length)
				return false;
			return File1.Read().Equals(File2.Read());
		}


		/// <summary>
		/// Reads a file to the end as a string
		/// </summary>
		/// <param name="File">File to read</param>
		/// <returns>A string containing the contents of the file</returns>
		public static string Read(this FileInfo File) {
			if (!File.Exists)
				return "";
			using (StreamReader Reader = File.OpenText()) {
				string Contents = Reader.ReadToEnd();
				return Contents;
			}
		}
	}
}
