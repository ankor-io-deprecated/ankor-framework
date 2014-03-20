using System;
using System.Collections.Generic;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using Newtonsoft.Json.Serialization;
using Newtonsoft.Json.Linq;
using System.Collections;

namespace Ankor.Core.Messaging.Json {
	/// <summary>
	/// Description of JsonMessageMapper.
	/// </summary>
	public class JsonMessageMapper : MessageMapper<string> {
		public JsonMessageMapper() {
		}

		public string Serialize(Object msg) {			
			JsonSerializerSettings settings = new JsonSerializerSettings() {
				NullValueHandling = NullValueHandling.Ignore,
				ContractResolver = new CamelCasePropertyNamesContractResolver(),
				DateTimeZoneHandling = DateTimeZoneHandling.Local,
				DateFormatString = "yyyy-MM-dd'T'HH:mm:ss.SSSZ",								
			};
			settings.Converters.Add(new StringEnumConverter(){CamelCaseText = true});
			
			return JsonConvert.SerializeObject(msg, settings);
			
//			StringWriter stringWriter = new StringWriter();
//			JsonTextWriter writer = new JsonTextWriter(stringWriter) {
//				QuoteName = false
//			};
//			
//			//return JsonConvert.SerializeObject(msg, settings);
//			JsonSerializer ser = new JsonSerializer();
//			ser.NullValueHandling = NullValueHandling.Ignore;
//			ser.ContractResolver = new CamelCasePropertyNamesContractResolver();
//			ser.Serialize(writer, msg);
//			
//			writer.Close();
//			return stringWriter.ToString();
			
//			DataContractJsonSerializer ser = new DataContractJsonSerializer(typeof(Message));
//			MemoryStream m = new MemoryStream();
//			ser.WriteObject(m, msg);
//			string json = new StreamReader(m).ReadToEnd();
//			
//			return json;
		}

		public static object ParseSnippet(string json) {
			return JsonConvert.DeserializeObject<object>(json, new ExpandoForObjectConverter());
		}

		public TR Deserialize<TR>(string serializedMsg) {
			var contractResolver = new DefaultContractResolver(true);
			contractResolver.DefaultMembersSearchFlags |= System.Reflection.BindingFlags.NonPublic;
			var settings = new JsonSerializerSettings() {
					 ContractResolver = contractResolver
			};
			settings.Converters.Add(new StringEnumConverter() { CamelCaseText = true });
			//settings.Converters.Add(new ExpandoForObjectConverter());
			settings.Converters.Add(new DictionaryForObjectConverter());

			JObject json = JObject.Parse(serializedMsg);

			return JsonConvert.DeserializeObject<TR>(serializedMsg, settings);
//			if (json.Property("action") != null) {
//				return JsonConvert.DeserializeObject<ActionMessage>(serializedMsg, settings);
//			}
//			if (json.Property("change") != null) {
//				return JsonConvert.DeserializeObject<ChangeMessage>(serializedMsg, settings);
//			}
			throw new ArgumentException("unable to determine message type of json message " + serializedMsg);
		}

	}

	class ExpandoForObjectConverter : ExpandoObjectConverter {
		public override bool CanConvert(Type objectType) {
			return objectType == typeof(object);
		}
	}

}
