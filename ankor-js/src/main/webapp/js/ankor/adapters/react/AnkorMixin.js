/**
 * Simple Mixin for React components that are supported by a Ankor model.
 * Simply provide a `ankorRef` property to a component and this mixin will set the properties of the Ankor model
 * as `props.ankor` in the React component.
 * 
 * <MyComponent ankorRef={...} />
 *
 * Example:
 * React.createClass({
 *   mixins: [AnkorMixin],
 *   render: function () {
 *      <label>{this.props.ankor.myProp}</label>
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

            // This is bad for performance
            var model = this.ankorRef.getValue();
            Object.keys(model).forEach(function (key) {
                if (!newProps[key]) {
                    newProps[key] = model[key];
                }
            });
            
            // new api
            // TODO: remove old one (above) before 0.4 release
            newProps.ankor = this.ankorRef.getValue();
        }
    }
});
