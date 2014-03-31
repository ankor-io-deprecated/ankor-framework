package at.irian.ankor.serialization.json.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * MixIn dummy class to mix in @JsonIgnoreProperties to all types.
 *
 * @author Manfred Geiler
 */
@JsonIgnoreProperties({"CGLIB$BOUND", "CGLIB$CALLBACK_0", "CGLIB$CALLBACK_1", "CGLIB$CALLBACK_2", "CGLIB$CALLBACK_3", "CGLIB$CALLBACK_4", "CGLIB$CALLBACK_5"})
interface DefaultMixIn {}
