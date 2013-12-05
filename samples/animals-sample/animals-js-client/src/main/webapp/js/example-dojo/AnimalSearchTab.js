define([
    "dojo/_base/declare",
    "./BaseTab",
    "dojo/text!./templates/AnimalSearchTab.html"
], function(declare, BaseTab, template) {
    return declare([BaseTab], {
        templateString: template
    });
});
