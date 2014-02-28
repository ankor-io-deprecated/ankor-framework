namespace Ankor.Core.Event {
	public interface IEventSource {}

	public class RemoteSource : IEventSource { }

	public class LocalSource : IEventSource { }
}