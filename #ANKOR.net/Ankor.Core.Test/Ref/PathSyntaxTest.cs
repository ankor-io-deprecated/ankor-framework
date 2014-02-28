using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Ankor.Core.Ref;
using NUnit.Framework;
using FluentAssertions;

namespace Ankor.Core.Test.Ref {

	// TODO add tests for array syntax?
	[TestFixture]
	public class PathSyntaxTest {
		private readonly PathSyntax pathSyntax = new PathSyntax();

		[Test]
		public void TestIsDescendent() {
			// TODO maybe the: empty == root of everything is strange sometimes?
			pathSyntax.IsDescendant("1.2.3", "1.2").Should().BeTrue();			
			pathSyntax.IsDescendant("1.2.3", "1").Should().BeTrue();
			pathSyntax.IsDescendant("123.4", "123").Should().BeTrue();
			pathSyntax.IsDescendant("1", "").Should().BeTrue();

			pathSyntax.IsDescendant("1234", "123").Should().BeFalse();
			pathSyntax.IsDescendant("aa", "aa").Should().BeFalse();
			pathSyntax.IsDescendant("", "").Should().BeFalse();
			pathSyntax.IsDescendant("1.2.3.4", "1.2.2").Should().BeFalse();

			pathSyntax.IsDescendant("a.b[0]", "a.b").Should().BeTrue();
			pathSyntax.IsDescendant("a.b[0].c", "a.b[0]").Should().BeTrue();
			
		}

		[Test]
		public void TestIsAncestor() {
			pathSyntax.IsAncestor("1", "1.2.3").Should().BeTrue();
			pathSyntax.IsAncestor("", "1.2.3").Should().BeTrue();
			pathSyntax.IsAncestor("123", "123.4").Should().BeTrue();
			pathSyntax.IsAncestor("1.2.3", "1.2.3.4.superferdl").Should().BeTrue();

			pathSyntax.IsAncestor("1.2.3", "1").Should().BeFalse();
			pathSyntax.IsAncestor("1", "1").Should().BeFalse();
			pathSyntax.IsAncestor("", "").Should().BeFalse();

			pathSyntax.IsAncestor("a", "a[1]").Should().BeTrue();			
		}

		// TODO add the [] part.., use for todo sample "get current index in main window" impl afterwards



		[Test]
		public void TestMakeAbsolutePath() {
			pathSyntax.MakeAbsolutePath("", "1").Should().Be("1");
			pathSyntax.MakeAbsolutePath("1", "1").Should().Be("1.1");
			pathSyntax.MakeAbsolutePath("1.2", "1.2").Should().Be("1.2.1.2");
			pathSyntax.MakeAbsolutePath("1.2", "").Should().Be("1.2");

			pathSyntax.MakeAbsolutePath("1.2", "a[1]").Should().Be("1.2.a[1]");
			pathSyntax.MakeAbsolutePath("1.2", "[1]").Should().Be("1.2[1]");
			pathSyntax.MakeAbsolutePath("", "[1]").Should().Be("[1]");
			pathSyntax.MakeAbsolutePath("[a]", "").Should().Be("[a]");
		}

		[Test]
		public void TestMakeRelativePath() {
			pathSyntax.MakeRelativePath("", "1").Should().Be("1");
			pathSyntax.MakeRelativePath("1", "1").Should().Be("");
			pathSyntax.MakeRelativePath("1.2", "1.2.3.4").Should().Be("3.4");
			pathSyntax.MakeRelativePath("12345.1", "1234.1").Should().Be("1234.1");
			pathSyntax.MakeRelativePath("1234.1", "12345.1").Should().Be("12345.1");

			pathSyntax.MakeRelativePath("a", "a[1].b.c").Should().Be("[1].b.c");
			pathSyntax.MakeRelativePath("a[0]", "a[0].b.c").Should().Be("b.c");
		}

		[Test]
		public void TestGetParentPath() {
			pathSyntax.GetParentPath("1.2.3.4").Should().Be("1.2.3");
			pathSyntax.GetParentPath("1234").Should().Be("");
			pathSyntax.GetParentPath("").Should().Be(""); // is this cool?
			pathSyntax.GetParentPath("123.4").Should().Be("123");

			pathSyntax.GetParentPath("a.b.c[3]").Should().Be("a.b.c");
			pathSyntax.GetParentPath("c[3][1]").Should().Be("c[3]");
			pathSyntax.GetParentPath("a[3].c").Should().Be("a[3]");
		}

		[Test]
		public void TestGetPropertyName() {
			pathSyntax.GetPropertyName("1.2.3").Should().Be("3");
			pathSyntax.GetPropertyName("1.123").Should().Be("123");
			pathSyntax.GetPropertyName("1").Should().Be("1");
			pathSyntax.GetPropertyName("").Should().Be("");

			pathSyntax.GetPropertyName("a[1]").Should().Be("1");
			pathSyntax.GetPropertyName("a.b.c[11101]").Should().Be("11101");
			pathSyntax.GetPropertyName("[1]").Should().Be("1");
			pathSyntax.GetPropertyName("a[1].a[2]").Should().Be("2");

			pathSyntax.GetPropertyName("a[1].a[i2]").Should().Be("i2");
		}

		[Test]
		public void TestAddArrayIndex() {
			pathSyntax.AddArrayIndex("1.2.3", 0).Should().Be("1.2.3[0]");
			pathSyntax.AddArrayIndex("bla", 50).Should().Be("bla[50]");
			pathSyntax.AddArrayIndex("", 0).Should().Be("[0]");
		}

	}
}
