define([
    "dojo/_base/declare",
    //"dijit/layout/ContentPane"
    "dijit/_Widget",
    "dijit/_TemplatedMixin",
    "dijit/_WidgetsInTemplateMixin"
], function(declare, _Widget, _TemplatedMixin, _WidgetsInTemplateMixin) {
    return declare([_Widget, _TemplatedMixin, _WidgetsInTemplateMixin], {
        closable: true,
        panelRef: null,
        ankorPanelId: null,

        onClose: function() {
            this.panelRef.del();
            this.inherited(arguments);
        }
    });
});
