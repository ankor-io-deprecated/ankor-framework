using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Dynamic;
using System.Linq.Expressions;
using System.Linq;
using Ankor.Core.Action;
using Ankor.Core.Change;
using Ankor.Core.Event;
using Ankor.Core.Messaging.Json;

namespace Ankor.Core.Ref {

	/// <summary>
	/// It is very important for data binding to work that the same ref is always the equal instance
	/// </summary>
	public class DynaRefFactory {

		private static readonly IDictionary<IInternalModel, IDictionary<string, DynaRef>> refRepository
			= new Dictionary<IInternalModel, IDictionary<string, DynaRef>>();

		internal static DynaRef CreateRef(IInternalModel jModel, string path) {
			IDictionary<string, DynaRef> refs;
			lock (refRepository) {
				if (!refRepository.TryGetValue(jModel, out refs)) {
					refs = new Dictionary<string, DynaRef>();
					refRepository[jModel] = refs;
				}
			}
			lock (refs) {
				if (refs.ContainsKey(path)) {
					return refs[path];
				}
				DynaRef dr = new DynaRef(jModel, path);
				refs[path] = dr;
				return dr;
			}
		}
	}

	public class DynaRef : DynamicObject, INotifyPropertyChanged, IDisposable, IEquatable<DynaRef>, IEnumerable<DynaRef>, IEnumerable<KeyValuePair<string, DynaRef>> {
		private readonly IInternalModel model;
		private readonly string path;
		

		internal protected DynaRef(IInternalModel model, string path) {
			this.model = model;
			this.path = path;
		}

		internal static DynaRef CreateRef(IInternalModel jModel, string path) {
			// use reffactory providing same instances OR override equals etc
			//return DynaRefFactory.CreateRef(jModel, path);
			return new DynaRef(jModel, path);
		}

		public object Value {
			get { return InternalGetValue(); }
			set { ApplyChange(new LocalSource(), Change.Change.ValueChange(value)); }
		}

		/// <summary>
		/// Apply a change on this ref, set value and fire event.
		/// </summary>
		public void ApplyChange(IEventSource source, Change.Change change) {
			switch (change.Type) {
				case ChangeType.Value:
					HandleValueChange(change);
					break;
				case ChangeType.Insert:
					HandleInsertChange(change);
					break;
				case ChangeType.Delete:
					HandleDeleteChange(change);
					break;
				case ChangeType.Replace:
					HandleReplaceChange(change);
					break;
				default: throw new ArgumentException("change type not yet supported " + change.Type);
			}
			model.Dispatcher.Dispatch(new ChangeEvent(source, this, change));
		}

		private void HandleReplaceChange(Change.Change change) {
			object val = Value;
			if (!IsList(val)) {
				throw new ArgumentException("replace change is (for now) only supported for arrays");
			}
			IList list = ((IList)val);
			ICollection newElems = ((ICollection) change.Value);
			int startIndex = ToListIndex(change);
			if (startIndex > list.Count) {
				throw new ArgumentException("replace change index " + startIndex + " cannot be bigger than current list size " + list.Count);
			}
			foreach (var newElem in newElems) {
				if (list.Count > startIndex) {
					list[startIndex] = newElem;
				} else {
					list.Add(newElem);
				}

				startIndex++;
			}

		}

		private void HandleDeleteChange(Change.Change change) {
			object val = Value;
			if (!IsList(val)) {
				throw new ArgumentException("delete change is (for now) only supported for arrays");
			}
			((IList) val).RemoveAt(ToListIndex(change));
		}

		private void HandleValueChange(Change.Change change) {
			if (!Equals(change.Value, this.Value)) {
				// is this ok for non skalar types?
				InternalSetValue(change.Value);
			}
		}

		private void HandleInsertChange(Change.Change change) {
			object val = Value;
			if (!IsList(val)) {
				throw new ArgumentException("Cannot insert on non array ref " + ToString());
			}
			var list = ((IList) val);
			var index = ToListIndex(change);
			if (index < 0) {
				throw new ArgumentException("index must be positive " + index);
			} else if (index < list.Count) {
				list.Insert(index, change.Value);
			} else if (index == list.Count) {
				list.Add(change.Value);
			} else {
				throw new ArgumentException("index " + index + " cannot be greater than list size " + list.Count);
			}
		}

		private static int ToListIndex(Change.Change change) {
			if (change.Key is string) {
				return int.Parse((string) change.Key);
			}
			if (change.Key is long) {
				return (int) (long) change.Key;
			}
			return (int)change.Key;
		}

		private bool IsList() {
			return IsList(Value);
		}

		private static bool IsList(object val) {
			return val is IList;
		}

		public string Path {
			get { return this.path; }
		}

		public dynamic Dynamic {
			get { return this; }
		}

		public string PropertyName {
			get { return PathSyntax.GetPropertyName(path); }
		}

		#region EQUALITY
		public bool Equals(DynaRef other) {
			if (ReferenceEquals(null, other)) {
				return false;
			}
			if (ReferenceEquals(this, other)) {
				return true;
			}
			return model.Equals(other.model) && string.Equals(path, other.path);
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
			return Equals((DynaRef) obj);
		}

		public override int GetHashCode() {
			unchecked {
				return (model.GetHashCode()*397) ^ path.GetHashCode();
			}
		}
		#endregion 

		IEnumerator IEnumerable.GetEnumerator() {
			//if (model.IsArray(path)) {
			if (IsList()) {
				return ((IEnumerable<DynaRef>)this).GetEnumerator();
			} else {
				return ((IEnumerable<KeyValuePair<string, DynaRef>>)this).GetEnumerator();
			}
		}

		public IEnumerator<DynaRef> GetEnumerator() {
			//return model.GetRefEnumerator(path);
			var list = this.Value as IList;
			if (list != null) {
				int i = 0;
				var refList = new List<DynaRef>(from object entry in list select CreateRef(model, PathSyntax.AddArrayIndex(path, i++)));
				return refList.GetEnumerator();
			}
			throw new ArgumentException("cannot enumerate on non array ref '" + path + "'");
		}

		IEnumerator<KeyValuePair<string, DynaRef>> IEnumerable<KeyValuePair<string, DynaRef>>.GetEnumerator() {
			return ((IEnumerable<KeyValuePair<string, DynaRef>>) this.InternalGetValue()).GetEnumerator();
		}

		public static bool operator ==(DynaRef left, DynaRef right) {
			return Equals(left, right);
		}

		public static bool operator !=(DynaRef left, DynaRef right) {
			return !Equals(left, right);
		}

		/// <summary>
		/// Do set the value on this ref w.o. any event notifications.
		/// </summary>
		private void InternalSetValue(object value) {
			if (value is DynaRef) { // auto deref if necessary
				value = (value as DynaRef).Value;
			}			
			//Console.WriteLine("SET value " + path + " to " + value);
			model.InternalSetValue(value, path);			
		}

		private object InternalGetValue() {
			//Console.WriteLine("GET value " + path);
			return model.InternalGetValue(path);
		}

		private PathSyntax PathSyntax {
			get { return model.PathSyntax; }
		}
		

		public override string ToString() {
			return String.Format("dref {0}", path);
		}

		public override bool TrySetMember(SetMemberBinder binder, object value) {
			//Console.WriteLine("Try Set Member on " + path + " for " + binder.Name);
			AppendPath(binder.Name).Value = value;
			return true;
		}

		public override bool TryGetMember(GetMemberBinder binder, out object result) {
			
			var dynaRef = AppendPath(binder.Name);
			result = dynaRef;

			//Console.WriteLine("Try Get value at " + path + " for " + binder.Name + " got " + result);
			return result != null;
		}

		public DynaRef this[string subpath] {
			get { return AppendPath(subpath); }			
		}

		public override bool TryGetIndex(GetIndexBinder binder, object[] indexes, out object result) {
			if (indexes.Length != 1) {
				throw new ArgumentException("multiple indexes not supported @" + ToString());
			}
			if (!(indexes[0] is int)) {
				throw new ArgumentException(string.Format("only int index supported (not {0}) @ {1}", indexes[0].GetType(), this));
			}
			result = AppendIndex((int)indexes[0]);
			return result != null;
			//WriteDebug("TryGetIndex " + string.Join(",", indexes));
		}

		public DynaRef AppendPath(string subPath) {
			return DynaRef.CreateRef(model, MakeAbsolutePath(subPath));
		}

		public DynaRef AppendIndex(int index) {
			return CreateRef(model, PathSyntax.AddArrayIndex(path, index));
		}

		public event PropertyChangedEventHandler PropertyChanged {
			add {
				model.EventRegistry.Add(value, new ChangeEventListener(e => {
					PropertyChangedEventArgs args = null;
					if (this == e.Property || this.IsDescendantOf(e.Property)) {
						args = new PropertyChangedEventArgs("Value");  // my Value changed or a parent of me changed
					} else if (this.IsAncestorOf(e.Property)) {
						// change is somewhere deeper below me, need this for manual pattern matching handlers
						// maybe remove this and use another event (generic) or the java like annotation patterns
						string relativePath = MakeRelativePath(e.Property.Path);
						args = new PropertyChangedEventArgs(relativePath);
					}

					if (args != null) {
						value(this, args);						
					}

				}));
				//Console.WriteLine("Registered PropertyChanged at '" + Path + "' from " + value.GetHashCode());
			}

			remove {
				model.EventRegistry.RemoveByKey(value);
			}
		}

		private bool IsAncestorOf(DynaRef property) {
			return PathSyntax.IsAncestor(this.path, property.path);
		}

		public bool IsDescendantOf(DynaRef property) {
			return PathSyntax.IsDescendant(this.path, property.path);
		}

		//private void OnModelPropertyChanged(object sender, PropertyChangedEventArgs e) {
		//  var handler = propertyChanged;
		//  string changePath = e.PropertyName;
		//  if (handler != null) {
		//    if (changePath.Equals(path)) { // exactly myself
		//      handler(this, new PropertyChangedEventArgs("Value"));

		//    } else if (PathSyntax.IsDescendent(changePath, path)) { // change is somewhere deeper below me (is a descendant) ATTENTION the >.< for startswith
		//      // this is for manual pattern matching change handlers. maybe go for another event here
		//      string relativePath = MakeRelativePath(changePath);
		//      handler(this, new PropertyChangedEventArgs(relativePath));

		//    } else if (PathSyntax.IsDescendent(path, changePath)) { // i am descendent, maybe i have changed
		//      // TODO should check against old value?! but where to get it from, model is already new
		//      handler(this, new PropertyChangedEventArgs("Value"));
		//      //handler(this, new PropertyChangedEventArgs(""));
		//    }
		//  }
		//}

		public event ActionReceivedEventHandler ActionReceived {
			add {
				model.EventRegistry.Add(new ActionEventListener(e => {
					if (e.Property.Path == this.path) { // only actions that are for this ref
						value(this, e);
					}
				}));
			}

			remove { throw new NotImplementedException(); }
		}

		private string MakeRelativePath(string absolutePath) {
			return PathSyntax.MakeRelativePath(path, absolutePath);
		}

		private string MakeAbsolutePath(string subPath) {
			return PathSyntax.MakeAbsolutePath(path, subPath);
		}

		public void Dispose() {
			// jModel.PropertyChanged -= OnModelPropertyChanged;
			// TODO remove the listeners
		}

		public void Fire(AAction aAction) {
			//jModel.FireActionFromInternal(path, aAction);
			//jModel.Dispatcher.Dispatch(new ActionEvent(this, aAction));
			Fire(aAction, new LocalSource());
		}
		public void Fire(string actionName) {
			Fire(new AAction(actionName));
		}

		public void Fire(AAction action, IEventSource remoteSource) {
			model.Dispatcher.Dispatch(new ActionEvent(new LocalSource(), this, action));

		}

		//private void OnModelAction(object sender, ActionEventArgs e) {
		//  if (e.Path == path) {
		//    OnActionReceived(e);
		//  }
		//}

		//public void OnActionReceived(ActionEventArgs e) {
		//  ActionReceivedEventHandler handler = ActionReceived;
		//  if (handler != null) handler(this, e);
		//}

		#region DEBUG_OVERRIDES		

		public override System.Collections.Generic.IEnumerable<string> GetDynamicMemberNames() {
			WriteDebug("GetDynamicMemberNames");
			return base.GetDynamicMemberNames();
		}
		public override DynamicMetaObject GetMetaObject(System.Linq.Expressions.Expression parameter) {
			WriteDebug("GetMetaObject ");
			var target = base.GetMetaObject(parameter);
			return target;
			//return new MyDynamicMetaObject(parameter, BindingRestrictions.Empty, this, target);
		}
		public override bool TryBinaryOperation(BinaryOperationBinder binder, object arg, out object result) {
			WriteDebug("03");
			return base.TryBinaryOperation(binder, arg, out result);
		}
		public override bool TryConvert(ConvertBinder binder, out object result) {
			WriteDebug("TryConvert() to " + binder.Type);
			return base.TryConvert(binder, out result);
		}
		public override bool TryCreateInstance(CreateInstanceBinder binder, object[] args, out object result) {
			WriteDebug("05");
			return base.TryCreateInstance(binder, args, out result);
		}

		public override bool TryDeleteMember(DeleteMemberBinder binder) {
			WriteDebug("06");
			return base.TryDeleteMember(binder);
		}

		public override bool TryInvokeMember(InvokeMemberBinder binder, object[] args, out object result) {
			WriteDebug("07");
			return base.TryInvokeMember(binder, args, out result);
		}

		public override bool TryInvoke(InvokeBinder binder, object[] args, out object result) {
			WriteDebug("08");
			return base.TryInvoke(binder, args, out result);
		}

		public override bool TryUnaryOperation(UnaryOperationBinder binder, out object result) {
			WriteDebug("09");
			return base.TryUnaryOperation(binder, out result);
		}


		public override bool TrySetIndex(SetIndexBinder binder, object[] indexes, object value) {
			WriteDebug("11");
			return base.TrySetIndex(binder, indexes, value);
		}

		public override bool TryDeleteIndex(DeleteIndexBinder binder, object[] indexes) {
			WriteDebug("12");
			return base.TryDeleteIndex(binder, indexes);
		}

		private void WriteDebug(string id) {			
			//Console.WriteLine("CALLED " + id + " p: " + path );
		}
		#endregion

		#region  DEBUG_META_OBJECT
		
		public class MyDynamicMetaObject : DynamicMetaObject {
			private string path;
			private DynamicMetaObject target;
			public MyDynamicMetaObject(Expression expression, BindingRestrictions restrictions) : base(expression, restrictions) { }
			public MyDynamicMetaObject(Expression expression, BindingRestrictions restrictions, DynaRef dobj, DynamicMetaObject target)
				: base(expression, restrictions, dobj) {
				this.path = dobj.path;
				this.target = target;
			}

			private void WriteDebug(string id) {
				//Console.WriteLine("CALLED " + id + " p: " + path);
			}

			public override DynamicMetaObject BindConvert(ConvertBinder binder) {
				WriteDebug("BindConvert " + binder.Type);
				return target.BindConvert(binder);
			}

			public override DynamicMetaObject BindGetMember(GetMemberBinder binder) {
				WriteDebug("BindGetMember "+ binder.Name);
				return target.BindGetMember(binder);
			}

			public override DynamicMetaObject BindSetMember(SetMemberBinder binder, DynamicMetaObject value) {
				WriteDebug("003");
				return target.BindSetMember(binder, value);
			}

			public override DynamicMetaObject BindDeleteMember(DeleteMemberBinder binder) {
				WriteDebug("004");
				return target.BindDeleteMember(binder);
			}

			public override DynamicMetaObject BindGetIndex(GetIndexBinder binder, DynamicMetaObject[] indexes) {
				WriteDebug("005");
				return target.BindGetIndex(binder, indexes);
			}

			public override DynamicMetaObject BindSetIndex(SetIndexBinder binder, DynamicMetaObject[] indexes, DynamicMetaObject value) {
				WriteDebug("006");
				return target.BindSetIndex(binder, indexes, value);
			}

			#region Overrides of DynamicMetaObject

			public override DynamicMetaObject BindDeleteIndex(DeleteIndexBinder binder, DynamicMetaObject[] indexes) {
				WriteDebug("007");
				return target.BindDeleteIndex(binder, indexes);
			}

			public override DynamicMetaObject BindInvokeMember(InvokeMemberBinder binder, DynamicMetaObject[] args) {
				WriteDebug("008");
				return target.BindInvokeMember(binder, args);
			}

			public override DynamicMetaObject BindInvoke(InvokeBinder binder, DynamicMetaObject[] args) {
				WriteDebug("009");
				return target.BindInvoke(binder, args);
			}

			public override DynamicMetaObject BindCreateInstance(CreateInstanceBinder binder, DynamicMetaObject[] args) {
				WriteDebug("010");
				return target.BindCreateInstance(binder, args);
			}

			public override DynamicMetaObject BindUnaryOperation(UnaryOperationBinder binder) {
				WriteDebug("011");
				return target.BindUnaryOperation(binder);
			}

			public override DynamicMetaObject BindBinaryOperation(BinaryOperationBinder binder, DynamicMetaObject arg) {
				WriteDebug("012");
				return target.BindBinaryOperation(binder, arg);
			}

			public override IEnumerable<string> GetDynamicMemberNames() {
				WriteDebug("013");
				return target.GetDynamicMemberNames();
			}

			#endregion 
		}

		#endregion


	}

	
}