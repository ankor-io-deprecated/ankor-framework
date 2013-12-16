define([
    "dojo/_base/declare",
    "dijit/_Widget",
    "dijit/_TemplatedMixin",
    "dijit/_WidgetsInTemplateMixin",
    "ankor/adapters/dojo/AnkorStatefulBinding"
], function(declare, _Widget, _TemplatedMixin, _WidgetsInTemplateMixin, AnkorStatefulBinding) {
    return declare([_Widget, _TemplatedMixin, _WidgetsInTemplateMixin], {
        closable: true,
        panelRef: null,
        ankorPanelId: null,

        postCreate: function() {
            this.inherited(arguments);

            this.own(
                new AnkorStatefulBinding(this, "title", this.panelRef.append("name"))
            );
        },
        onClose: function() {
            this.panelRef.del();
            this.inherited(arguments);
        }
    });
});
