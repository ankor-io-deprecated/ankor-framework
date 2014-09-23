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
    function setAnkorProps(newProps) {
        if (!(newProps.ankorRef instanceof Ref)) {
            throw Error("Ankor React component must have a property `ankorRef` of type `Ref`");
        }
        this.ankorRef = newProps.ankorRef;
        newProps.ankor = this.ankorRef.getValue();
    }
    
    return {
        componentWillMount: function () {
            setAnkorProps.call(this, this.props);
        },
        
        componentWillReceiveProps: function (newProps) {
            setAnkorProps.call(this, newProps);
        }
    }
});
