namespace Ankor.Core.Messaging {
	/// <summary>
	/// Description of MessageMapper.
	/// </summary>
	public interface MessageMapper<T> : MessageSerializer<T>, MessageDeserializer<T>  {
		

	}
	
	public interface MessageSerializer<T> {
		T Serialize(object msg);
	}
	
	public interface MessageDeserializer<T> {
		TR Deserialize<TR>(T serializedMsg);
	}
}
