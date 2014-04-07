Note: Do not modify the `.js` files in the `build` directory.

Edit the `.jsx` files in `src` instead. 
Use the JSX compiler to compile the `.jsx` files into `.js` files.

    jsx -w -x jsx src/ build/
    
If you don't have the `jsx` program you can get it via:

    npm install -g react-tools
