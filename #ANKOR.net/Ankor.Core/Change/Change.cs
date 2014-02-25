﻿using System.Collections;

namespace Ankor.Core.Change {

	public class Change {
		public ChangeType Type { get; private set; }
		public object Key { get; private set; }
		public object Value { get; private set; }

		// for json serializing
		private Change() {}

		private Change(ChangeType type, object key, object value) {
			this.Type = type;
			this.Key = key;
			this.Value = value;
		}

		public static Change ValueChange(object val) {
			return new Change(ChangeType.Value, null, val);
		}

		public static Change InsertChange(long idx, object insertedElement) {
				return new Change(ChangeType.Insert, idx, insertedElement);
		}

		public static Change DeleteChange(object key) {
				return new Change(ChangeType.Delete, key, null);
		}

		public static Change ReplaceChange(long fromIdx, ICollection newElements) {
			return new Change(ChangeType.Replace, fromIdx, newElements);
		}

		protected bool Equals(Change other) {
			return Type.Equals(other.Type) && Equals(Key, other.Key) && Equals(Value, other.Value);
		}

		public static bool operator ==(Change left, Change right) {
			return Equals(left, right);
		}

		public static bool operator !=(Change left, Change right) {
			return !Equals(left, right);
		}

		public override string ToString() {
			return string.Format("Type: {0}, Key: {1}, Value: {2}", Type, Key, Value);
		}

		public override bool Equals(object obj) {
			if (ReferenceEquals(null, obj)) {
				return false;
			}
			if (ReferenceEquals(this, obj)) {
				return true;
			}
			if (obj.GetType() != this.GetType()) {
				return false;
			}
			return Equals((Change) obj);
		}

		public override int GetHashCode() {
			unchecked {
				int hashCode = Type.GetHashCode();
				hashCode = (hashCode*397) ^ (Key != null ? Key.GetHashCode() : 0);
				hashCode = (hashCode*397) ^ (Value != null ? Value.GetHashCode() : 0);
				return hashCode;
			}
		}

	}

	public enum ChangeType {
		Value,
		Insert,
		Delete,
		Replace
	}
}
