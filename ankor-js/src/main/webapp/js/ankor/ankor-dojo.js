/**
 * See `ankor.js`
 */
define([
    './adapters/dojo/AnkorDgridBinding',
    './adapters/dojo/AnkorDijitSelectBinding',
    './adapters/dojo/AnkorListStore',
    './adapters/dojo/AnkorStatefulBinding',
    './adapters/dojo/BaseConverter',
    './adapters/dojo/StringConverter',
    './utils/DojoUtils',
    './ankor'
], function (AnkorDgridBinding, 
             AnkorDijitSelectBinding, 
             AnkorListStore,
             AnkorStatefulBinding, 
             BaseConverter,
             StringConverter,
             DojoUtils,
             ankor) {
    ankor.adapters.dojo = {};
    ankor.adapters.dojo.AnkorDgridBinding = AnkorDgridBinding;
    ankor.adapters.dojo.AnkorDijitSelectBinding = AnkorDijitSelectBinding;
    ankor.adapters.dojo.AnkorListStore = AnkorListStore;
    ankor.adapters.dojo.AnkorStatefulBinding = AnkorStatefulBinding;
    ankor.adapters.dojo.BaseConverter = BaseConverter;
    ankor.adapters.dojo.StringConverter = StringConverter;
    ankor.utils.DojoUtils = DojoUtils;
    return ankor;
});

