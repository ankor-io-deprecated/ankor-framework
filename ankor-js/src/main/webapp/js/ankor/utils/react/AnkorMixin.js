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
    '../../Ref',
    'react'
], function (Ref, React) {
    return {
        componentWillMount: function () {
            if (!(this.props.ankorRef instanceof Ref)) {
                throw Error("Ankor React component must have a property `ankorRef` of type `Ref`");
            }

            this.ankorRef = this.props.ankorRef;
            this.props = React.addons.update(this.props, {
                $merge: this.ankorRef.getValue()
            });
        }
    }
});
