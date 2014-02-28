namespace Ankor.Core.Messaging {
	/// <summary>
	/// Description of MessageMapper.
	/// </summary>
	public interface MessageMapper<T> : MessageSerializer<T>, MessageDeserializer<T>  {
		

	}
	
	public interface MessageSerializer<T> {
		T Serialize(Message msg);
	}
	
	public interface MessageDeserializer<T> {
		Message Deserialize(T serializedMsg);
	}
}
