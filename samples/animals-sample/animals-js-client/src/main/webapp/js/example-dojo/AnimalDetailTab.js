define([
    "dojo/_base/declare",
    "dijit/layout/ContentPane",
    "dojo/text!./templates/AnimalDetailTab.html"
], function(declare, ContentPane, template) {
    return declare([ContentPane], {
        title: "Test Tab",
        content: template
    });
});
