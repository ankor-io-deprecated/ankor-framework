/**
 * Simple Mixin for React components that are supported by a Ankor model.
 * Simply provide a `ankorRef` property to a component and this mixin will set the properties of the Ankor model
 * as `props` in the React component.
 *
 * Example:
 * React.createClass({
 *   mixins: [AnkorMixin],
 *   render: function () {
 *      <label>{this.props.myAnkorProp}</label>
 *   }
 * })
 *
 * Usage:
 * <App ankorRef={rootRef} />
 */
define([
    '../../Ref'
], function (Ref) {
    return {
        componentWillReceiveProps: function (newProps) {
            if (!(this.props.ankorRef instanceof Ref)) {
                throw Error("Ankor React component must have a property `ankorRef` of type `Ref`");
            }

            // TODO: This is bad for performance, maybe switch back to old model?

            var model = this.ankorRef.getValue();
            Object.keys(model).forEach(function (key) {
                if (!newProps[key]) {
                    newProps[key] = model[key];
                }
            });
        }
    }
});
